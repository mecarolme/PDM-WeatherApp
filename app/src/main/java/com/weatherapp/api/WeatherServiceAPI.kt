package com.weatherapp.api

import com.weatherapp.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherServiceAPI {
    companion object {
        const val BASE_URL = "https://api.weatherapi.com/v1/"
        const val API_KEY = BuildConfig.WEATHER_API_KEY
    }

    @GET("search.json")
    fun search(
        @Query("key") apiKey: String = API_KEY,
        @Query("q") query: String
    ): Call<List<APILocation>?>
}