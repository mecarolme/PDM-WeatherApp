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

    override fun onUserLoaded(user: User) {
        _user.value = user
    }

    override fun onCityAdded(city: City) {
        _cities[city.name] = city
    }

    override fun onCityUpdate(city: City) {
        _cities[city.name] = city.copy()
    }

    override fun onCityRemoved(city: City) {
        _cities.remove(city.name)
    }
}
