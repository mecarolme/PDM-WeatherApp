package com.weatherapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.weatherapp.R
import com.weatherapp.ui.model.Forecast
import com.weatherapp.ui.model.MainViewModel
import java.text.DecimalFormat

@Composable
fun HomePage(viewModel: MainViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (viewModel.city == null) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(colorResource(id = R.color.teal_700))
                    .wrapContentSize(Alignment.Center)
            ) {
                Text(
                    text = "Selecione uma cidade na lista de favoritas.",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }
        } else {
            val city = viewModel.city!!

            if (city.weather == null) {
                viewModel.loadWeather(city)
            }
            if (city.forecast == null) {
                viewModel.loadForecast(city)
            }

            Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                AsyncImage(
                    model = city.weather?.imgUrl,
                    modifier = Modifier.size(100.dp),
                    error = painterResource(id = R.drawable.loading),
                    contentDescription = "Imagem"
                )

                Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Text(
                            text = city.name,
                            fontSize = 28.sp
                        )

                        val isMonitored = city.isMonitored
                        val icon = if (isMonitored) Icons.Filled.Notifications else Icons.Outlined.Notifications

                        Icon(
                            imageVector = icon, contentDescription = "Monitorada?",
                            modifier = Modifier.size(32.dp).clickable(enabled=viewModel.city != null){
                                viewModel.update(viewModel.city!!
                                    .copy(isMonitored = !isMonitored))
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = city.weather?.desc ?: "...", fontSize = 22.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Temp: ${city.weather?.temp ?: "?"}℃", fontSize = 22.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            city.forecast?.let { list ->
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(list) { forecast ->
                        ForecastItem(forecast, onClick = {})
                    }
                }
            }
        }
    }
}

@Composable
fun ForecastItem(
    forecast: Forecast,
    onClick: (Forecast) -> Unit,
    modifier: Modifier = Modifier
) {
    val format = DecimalFormat("#.0")
    val tempMin = format.format(forecast.tempMin)
    val tempMax = format.format(forecast.tempMax)
    Row(
        modifier = modifier.fillMaxWidth().padding(12.dp)
            .clickable( onClick = { onClick(forecast) }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = forecast.imgUrl,
            modifier = Modifier.size(40.dp),
            error = painterResource(id = R.drawable.loading),
            contentDescription = "Imagem"
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column {
            Text(modifier = Modifier, text = forecast.weather, fontSize = 24.sp)
            Row {
                Text(modifier = Modifier, text = forecast.date, fontSize = 20.sp)
                Spacer(modifier = Modifier.size(12.dp))
                Text(modifier = Modifier, text = "Min: $tempMin℃", fontSize = 16.sp)
                Spacer(modifier = Modifier.size(12.dp))
                Text(modifier = Modifier, text = "Max: $tempMax℃", fontSize = 16.sp)
            }
        }
    }
}