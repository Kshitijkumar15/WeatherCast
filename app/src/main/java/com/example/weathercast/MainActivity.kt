package com.example.weathercast

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.weathercast.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Bengaluru")
        searchCity()


    }

    private fun searchCity() {

        val serarchView = binding.search
        serarchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null) {
                    fetchWeatherData(query)
                }


                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(" https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response =
            retrofit.getWeatherData(cityName, "a98912cc29d1dbaab93a33ff323902dd", "metric")
        response.enqueue(object : Callback<WeatherCast> {
            override fun onResponse(call: Call<WeatherCast>, response: Response<WeatherCast>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val max = responseBody.main.temp_max
                    val min = responseBody.main.temp_min

                    Log.d("TAG", "onResponse:$temperature")
                    binding.todayTemp.text = "$temperature°C"
                    binding.weatherText.text = condition
                    binding.maxText.text = "$max °C"
                    binding.minText.text = "$min °C"
                    binding.locationText.text = "$cityName"
                    binding.dateText.text = date()
                    binding.dayText.text = day(currentTimeMillis())

                    changeBackgroundWithWeather(condition)
                }

            }

            override fun onFailure(call: Call<WeatherCast>, t: Throwable) {

            }

        })

        binding.detailsButton.setOnClickListener {
            val intent = Intent(this, DetailsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun changeBackgroundWithWeather(conditions: String) {

        when (conditions) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.icon.setBackgroundResource(R.drawable.sunny)
                binding.bgImage.setImageResource(R.drawable.sun)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Partly Cloudy", "Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.icon.setBackgroundResource(R.drawable.cloudy)
                binding.bgImage.setImageResource(R.drawable.cloud)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy" -> {
                binding.icon.setBackgroundResource(R.drawable.stormy)
                binding.bgImage.setImageResource(R.drawable.rain)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.icon.setBackgroundResource(R.drawable.snowy)
                binding.bgImage.setImageResource(R.drawable.snow)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            "Haze" -> {
                binding.icon.setBackgroundResource(R.drawable.hazy)
                binding.bgImage.setImageResource(R.drawable.haze)
                binding.lottieAnimationView.setAnimation(R.raw.haze)
            }

            else -> {
                binding.icon.setBackgroundResource(R.drawable.sunny)
                binding.bgImage.setImageResource(R.drawable.sun)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

        }
        binding.lottieAnimationView.playAnimation()
    }
    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp * 1000)))
    }

    fun day(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}