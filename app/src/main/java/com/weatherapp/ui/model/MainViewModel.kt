package com.weatherapp.ui.model

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.weatherapp.api.WeatherService
import com.weatherapp.db.fb.FBDatabase

class MainViewModel(
    private val db: FBDatabase,
    private val service: WeatherService
) : ViewModel(), FBDatabase.Listener {

    private val _cities = mutableStateMapOf<String, City>()
    val cities: List<City>
        get() = _cities.values.toList()

    private var _city = mutableStateOf<City?>(null)
    var city: City?
        get() = _city.value
        set(tmp) { _city = mutableStateOf(tmp?.copy()) }

    private val _user = mutableStateOf<User?>(null)
    val user: User?
        get() = _user.value

    init {
        db.setListener(this)
    }

    fun remove(city: City) {
        db.remove(city)
        _cities.remove(city.name)
    }

    fun add(name: String) {
        service.getLocation(name) { lat, lng ->
            if (lat != null && lng != null) {
                val newCity = City(name = name, location = LatLng(lat, lng))
                if (!_cities.containsKey(newCity.name)) {
                    db.add(newCity)
                    _cities[newCity.name] = newCity
                }
            }
        }
    }

    fun add(location: LatLng) {
        service.getName(location.latitude, location.longitude) { name ->
            if (name != null) {
                val newCity = City(name = name, location = location)
                if (!_cities.containsKey(newCity.name)) {
                    db.add(newCity)
                    _cities[newCity.name] = newCity
                }
            }
        }
    }

    fun loadWeather(city: City) {
        service.getCurrentWeather(city.name) { apiWeather ->
            val updatedCity = city.copy(
                weather = Weather(
                    date = apiWeather?.current?.last_updated ?: "...",
                    desc = apiWeather?.current?.condition?.text ?: "...",
                    temp = apiWeather?.current?.temp_c ?: -1.0,
                    imgUrl = "https:" + (apiWeather?.current?.condition?.icon ?: "")
                )
            )

            _cities[city.name] = updatedCity
        }
    }

    fun loadForecast(city : City) {
        service.getForecast(city.name) { result ->
            city.forecast = result?.forecast?.forecastday?.map {
                Forecast(
                    date = it.date?:"00-00-0000",
                    weather = it.day?.condition?.text?:"Erro carregando!",
                    tempMin = it.day?.mintemp_c?:-1.0,
                    tempMax = it.day?.maxtemp_c?:-1.0,
                    imgUrl = ("https:" + it.day?.condition?.icon)
                )
            }
            _cities.remove(city.name)
            _cities[city.name] = city.copy()
        }
    }

    override fun onUserLoaded(user: User) {
        _user.value = user
    }

    override fun onCityAdded(city: City) {
        _cities[city.name] = city
    }

    override fun onCityUpdate(city: City) {
        _cities.remove(city.name)
        _cities[city.name] = city.copy()

        if (_city.value?.name == city.name) {
            _city.value = city.copy()
        }
    }

    override fun onCityRemoved(city: City) {
        _cities.remove(city.name)
    }
}
