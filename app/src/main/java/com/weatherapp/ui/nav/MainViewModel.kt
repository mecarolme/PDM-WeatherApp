package com.weatherapp.ui.nav

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.weatherapp.db.fb.FBDatabase
import com.weatherapp.ui.model.City
import com.weatherapp.ui.model.User

class MainViewModel (private val db: FBDatabase): ViewModel(),
    FBDatabase.Listener {
    private val _cities = mutableStateListOf<City>()
    val cities
        get() = _cities.toList()

    private val _user = mutableStateOf<User?>(null)
    val user: User?
        get() = _user.value

    init {
        db.setListener(this)
    }

    fun remove(city: City) {
        db.remove(city)
        _cities.remove(city)
    }

    fun add(name: String, location: LatLng? = null) {
        val newCity = City(name = name, location = location)
        if (_cities.none { it.name == newCity.name }) {
            db.add(newCity)
            _cities.add(newCity)
        }
    }

    override fun onUserLoaded(user: User) {
        _user.value = user
    }

    override fun onCityAdded(city: City) {
        if (_cities.none { it.name == city.name }) {
            _cities.add(city)
        }
    }

    override fun onCityUpdate(city: City) {
        val index = _cities.indexOfFirst { it.name == city.name }
        if (index != -1) {
            _cities[index] = city
        }
    }

    override fun onCityRemoved(city: City) {
        _cities.remove(city)
    }
}

private fun generateCities(): List<City> {
    return List(20) { i ->
        City(name = "Cidade $i", weather = "Carregando clima...")
    }
}