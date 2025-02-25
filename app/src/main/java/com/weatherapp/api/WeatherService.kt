package com.weatherapp.api

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import com.squareup.picasso.Picasso
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class WeatherService {
    private var weatherAPI: WeatherServiceAPI

    init {
        val retrofitAPI = Retrofit.Builder()
            .baseUrl(WeatherServiceAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        weatherAPI = retrofitAPI.create(WeatherServiceAPI::class.java)
    }

    fun getName(lat: Double, lng: Double, onResponse: (String?) -> Unit) {
        search("$lat,$lng") { loc -> onResponse(loc?.name) }
    }

    fun getLocation(name: String, onResponse: (lat: Double?, long: Double?) -> Unit) {
        search(name) { loc -> onResponse(loc?.lat, loc?.lon) }
    }

    private fun search(query: String, onResponse: (APILocation?) -> Unit) {
        Log.d("WeatherApp DEBUG", "Query received: $query")
        if (query.isBlank()) {
            Log.w("WeatherApp WARNING", "Query is empty")
            onResponse(null)
            return
        }

        val call: Call<List<APILocation>?> = weatherAPI.search(query = query)
        enqueue(call) { response ->
            onResponse(response?.firstOrNull())
        }
    }

    private fun <T> enqueue(call : Call<T?>, onResponse : ((T?) -> Unit)? = null){
        call.enqueue(object : Callback<T?> {
            override fun onResponse(call: Call<T?>, response: Response<T?>) {
                val obj: T? = response.body()
                onResponse?.invoke(obj)
            }
            override fun onFailure(call: Call<T?>, t: Throwable) {
                Log.w("WeatherApp WARNING", "" + t.message)
            }
        })
    }

    fun getCurrentWeather(name: String, onResponse: (APICurrentWeather?) -> Unit){
        val call: Call<APICurrentWeather?> = weatherAPI.currentWeather(name)
        enqueue(call) { onResponse.invoke(it) }
    }

    fun getForecast(name: String, onResponse : (APIWeatherForecast?) -> Unit) {
        val call: Call<APIWeatherForecast?> = weatherAPI.forecast(name)
        enqueue(call) { onResponse.invoke(it) }
    }

    fun getBitmap(imgUrl: String, onResponse: (Bitmap?) -> Unit) {
        Picasso.get().load(imgUrl).into(object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?,
                                        from: Picasso.LoadedFrom?) {
                onResponse.invoke(bitmap)
            }
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Log.w("WeatherApp WARNING", "" + e?.message)
                e?.printStackTrace()
            }
        })
    }

}
