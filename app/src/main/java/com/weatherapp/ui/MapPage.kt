package com.weatherapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.weatherapp.ui.model.MainViewModel
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings

@Composable
fun MapPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val camPosState = rememberCameraPositionState()

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        onMapClick = { location ->
            viewModel.add(location)
        },
        cameraPositionState = camPosState,
        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
        uiSettings = MapUiSettings(myLocationButtonEnabled = true)
    ) {
        viewModel.cities.forEach { city ->
            city.location?.let { location ->
                if (city.weather == null) {
                    viewModel.loadWeather(city)
                }

                Marker(
                    state = MarkerState(position = location),
                    title = city.name,
                    snippet = city.weather?.desc ?: "Carregando..."
                )
            }
        }
    }
}