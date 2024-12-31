package com.gems.insight.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.gems.insight.api.model.News
import coil.compose.AsyncImage
import com.gems.insight.api.RetrofitClient
import com.gems.insight.api.RetrofitService
import com.gems.insight.api.model.NewsResponse
import com.gems.insight.navigation.NavigationItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen(itemTitle: String?, navController: NavHostController, query: String) {

    var isLoading by remember { mutableStateOf(true) }

    var news by remember { mutableStateOf<List<News>>(emptyList()) }
    val retrofitService: RetrofitService =
        RetrofitClient.getClient().create(RetrofitService::class.java)

    LaunchedEffect(itemTitle) {
        isLoading = true
        retrofitService.getNewsList(query, apiKey = "02c00a9da5de44e3a59300cb6bc9b828")
            .enqueue(object : Callback<NewsResponse> {
                override fun onResponse(
                    call: Call<NewsResponse?>,
                    response: Response<NewsResponse?>
                ) {
                    if (response.isSuccessful) {
                        news = (response.body())!!.articles
                    }
                    isLoading = false
                }

                override fun onFailure(
                    call: Call<NewsResponse?>,
                    t: Throwable
                ) {
                    isLoading = false
                }
            })
    }

    if (isLoading) {
        Text(
            text = "Loading...",
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
            fontSize = 18.sp,
            color = Color.Gray
        )
    } else {
        val news = news.find { it.title == itemTitle }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("News Details") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate(NavigationItem.HomeScreen.route) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(13.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                news?.urlToImage?.let { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                val publishedAt = news?.publishedAt
                val formattedDate = if (!publishedAt.isNullOrEmpty()) {
                    try {
                        val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                        val outputFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                        val date = inputFormatter.parse(publishedAt)
                        date?.let { outputFormatter.format(it) } ?: "Invalid date"
                    } catch (e: ParseException) {
                        e.printStackTrace()
                        "Invalid date format"
                    }
                } else {
                    "Published date not available"
                }
                Text(
                    text = news?.title ?: "Title not available",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(0.dp, 7.dp)
                )
                Text(
                    text = news?.description ?: "Description not available",
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(0.dp, 7.dp)
                )
                Text(
                    text = news?.content ?: "Content not available",
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(0.dp, 7.dp)
                )
                Text(
                    text = formattedDate,
                    modifier = Modifier.padding(0.dp, 7.dp)
                )
                Text(text = news?.source?.name ?: "Source not available", color = Color(0xFFF98121))
            }
        }
    }
}
