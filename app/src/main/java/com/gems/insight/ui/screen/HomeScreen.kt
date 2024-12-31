package com.gems.insight.ui.screen

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gems.insight.R
import com.gems.insight.api.RetrofitClient
import com.gems.insight.api.RetrofitService
import com.gems.insight.api.model.News
import com.gems.insight.api.model.NewsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val news = remember { mutableStateOf<List<News>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val retrofitService: RetrofitService =
        RetrofitClient.getClient().create(RetrofitService::class.java)
    val apiKey = "02c00a9da5de44e3a59300cb6bc9b828"
    val query = remember { mutableStateOf("top") }
    val searchQuery = remember { mutableStateOf("") }
    val isSearching = remember { mutableStateOf(false) }
    val selectedTab = remember { mutableStateOf("Top") }

    LaunchedEffect(query.value) {
        isLoading.value = true
        val call = retrofitService.getNewsList(query.value, apiKey)
        call.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(
                call: Call<NewsResponse?>,
                response: Response<NewsResponse?>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    news.value = response.body()!!.articles.filter { article ->
                        val title = article.title
                        val imageUrl = article.urlToImage
                        !title.isNullOrEmpty() && !imageUrl.isNullOrEmpty()
                    }
                    isLoading.value = false
                } else {
                    Log.d("TAG", "Response failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(
                call: Call<NewsResponse?>,
                t: Throwable
            ) {
                Log.d("TAG", "API failed: ${t.message}")
                isLoading.value = false
            }
        })
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                if (isSearching.value) {
                    OutlinedTextField(
                        value = searchQuery.value,
                        onValueChange = { searchQuery.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                        placeholder = {
                            Text(
                                text = "Search news...",
                                style = TextStyle(color = Color.Gray)
                            )
                        },
                        singleLine = true,
                        trailingIcon = {
                            if (isSearching.value) {
                                IconButton(onClick = {
                                    isSearching.value = false
                                    searchQuery.value = ""
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close Search",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        }
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 0.dp, 20.dp, 0.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier
                                .size(110.dp)
                                .clickable {}
                        )
                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { isSearching.value = true }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Icon(
                                painter = painterResource(R.drawable.notification),
                                contentDescription = null,
                                tint = Color(0xFFF98121),
                                modifier = Modifier
                                    .padding(5.dp)
                                    .clickable {}
                            )
                        }
                    }
                }
            },
            actions = {
                if (isSearching.value) {
                    IconButton(onClick = {
                        if (searchQuery.value.isNotEmpty()) {
                            query.value = searchQuery.value.lowercase()
                            selectedTab.value = "Search"
                        }
                        isSearching.value = false
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFFF98121)
                        )
                    }
                }
            }
        )
    }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            val tabs = listOf(
                "Search",
                "Top",
                "Latest",
                "Popular",
                "Technology",
                "Cricket",
                "Sports",
                "Science",
                "Health"
            )
            LazyRow {
                items(tabs.size) { index ->
                    Tabs(
                        title = tabs[index],
                        selectedTab = selectedTab,
                        onTabSelected = { selected ->
                            if (selected == "Search") {
                                isSearching.value = true
                                searchQuery.value = ""
                            } else {
                                isSearching.value = false
                                query.value = selected.lowercase()
                            }
                        }
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp, 0.dp, 10.dp, 0.dp)) {
                if (isLoading.value) {
                    Text("Loading....")
                } else if (news.value.isEmpty()) {
                    Text("No news found")
                } else {
                    LazyColumn {
                        items(news.value.size) { index ->
                            News(
                                news.value[index].source.name,
                                news.value[index].title,
                                news.value[index].publishedAt,
                                news.value[index].urlToImage,
                                navController,
                                query.value
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Tabs(
    title: String,
    selectedTab: MutableState<String>,
    onTabSelected: (String) -> Unit
) {
    val isSelected = selectedTab.value == title

    val tabColor = if (isSelected) Color(0xFFF98121) else Color.Gray
    val textColor = if (isSelected) Color.White else Color.Black

    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(tabColor)
            .clickable {
                onTabSelected(title)
                selectedTab.value = title
            }
            .padding(12.dp)
    ) {
        Text(
            text = title,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Composable
fun News(source: String, title: String, publishedAt: String, imageUrl: String?, navController: NavHostController, query: String) {
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val formattedDate = formatter.format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(publishedAt))

    Card(
        onClick = {navController.navigate("item/$query/$title")},
        modifier = Modifier
            .size(380.dp)
            .padding(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(Color(0xFFEFEFEF))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontWeight = FontWeight.Medium, overflow = TextOverflow.Ellipsis)
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        source,
                        fontSize = 13.sp,
                        color = Color(0xFFF98121),
                        modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 0.dp)
                    )
                    Text(formattedDate)
                }
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(16.dp))
                ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

