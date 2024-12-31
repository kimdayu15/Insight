package com.gems.insight.api

import retrofit2.Call
import com.gems.insight.api.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {

    @GET("everything")
    fun getNewsList(@Query("q") q: String, @Query("apiKey") apiKey: String): Call<NewsResponse>
}

