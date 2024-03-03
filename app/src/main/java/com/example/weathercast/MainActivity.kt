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
//                    val humidity = responseBody.main.humidity
//                    val windSpeed = responseBody.wind.speed
//                    val sunRise = responseBody.sys.sunrise.toLong()
//                    val sunSet = responseBody.sys.sunset.toLong()
//                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val max = responseBody.main.temp_max
                    val min = responseBody.main.temp_min

                    Log.d("TAG", "onResponse:$temperature")
                    binding.todayTemp.text = "$temperature °C"
                    binding.weatherText.text = condition
//                    binding.condition.text = condition
                    binding.maxText.text = "Max Temp: $max °C"
                    binding.minText.text = "Min Temp: $min °C"
//                    binding.humidity.text = "$humidity %"
//                    binding.wind.text = "$windSpeed m/s"
//                    binding.sunrise.text = "${time(sunRise )} am"
//                    binding.sunset.text = "${time(sunSet)} pm"
//                    binding.sea.text = "$seaLevel hPa"
//                    binding.dateText.text = date()
//                    binding.dayText.text = day(currentTimeMillis())
                    binding.locationText.text = "$cityName"

                    changeBackgroundWithWeather(condition)
                }

            }

            override fun onFailure(call: Call<WeatherCast>, t: Throwable) {

            }

        })

        binding.detailsButton.setOnClickListener {
            val intent = Intent(this,DetailsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun changeBackgroundWithWeather(conditions: String) {

        when (conditions) {
            "Clear Sky", "Sunny", "Clear" -> {
//                binding.root.setBackgroundResource(R.drawable.sun)
                binding.imageView.setBackgroundResource(R.drawable.sun)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Partly Cloudy", "Clouds", "Overcast", "Mist", "Foggy" -> {

                binding.imageView.setBackgroundResource(R.drawable.cloud)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy" -> {
                binding.root.setBackgroundResource(R.drawable.rain)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow", "Moderate Snow", "Hesavy Snow", "Blizzard" -> {
                binding.imageView.setBackgroundResource(R.drawable.snow)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            "Haze" -> {
                binding.imageView.setBackgroundResource(R.drawable.haze)
                binding.lottieAnimationView.setAnimation(R.raw.haze)
            }

            else -> {
                binding.imageView.setBackgroundResource(R.drawable.sun)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

        }
        binding.lottieAnimationView.playAnimation()
    }
}