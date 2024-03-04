package com.example.weathercast

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weathercast.databinding.ActivityDetailsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailsActivity : AppCompatActivity() {
    private val binding: ActivityDetailsBinding by lazy {
        ActivityDetailsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Bengaluru")

        binding.save.setOnClickListener {
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(this,"Home",Toast.LENGTH_LONG).show()
        }
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
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    binding.condition.text = condition
                    binding.humidity.text = "$humidity %"
                    binding.wind.text = "$windSpeed m/s"
                    binding.sunrise.text = "${time(sunRise)} am"
                    binding.sunset.text = "${time(sunSet)} pm"
                    binding.sea.text = "$seaLevel hPa"
                    changeBackgroundWithCondition(condition)
                }

            }

            override fun onFailure(call: Call<WeatherCast>, t: Throwable) {

            }

        })


    }

    private fun changeBackgroundWithCondition(conditions: String) {

        when (conditions) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.conditionImage.setImageResource(R.drawable.sunny)
            }

            "Partly Cloudy", "Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.conditionImage.setImageResource(R.drawable.cloudy)
            }

            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy" -> {
                binding.conditionImage.setImageResource(R.drawable.stormy)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.conditionImage.setImageResource(R.drawable.snowy)
            }

            "Haze" -> {
                binding.conditionImage.setImageResource(R.drawable.hazy)
            }

            else -> {
                binding.conditionImage.setImageResource(R.drawable.sunny)
            }

        }
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