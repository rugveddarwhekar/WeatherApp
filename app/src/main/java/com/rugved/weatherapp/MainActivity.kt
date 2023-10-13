package com.rugved.weatherapp

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    var day_night = "d"
    private var weatherUrl = ""
    var apiID = "d88f4a2c24c041f7b4e5c6621bb834e2"

    private lateinit var imageViewWeather: ImageView
    private lateinit var textViewTemp: TextView
    private lateinit var textViewCity: TextView
    private lateinit var textViewRain: TextView
    private lateinit var textViewSnow: TextView
    private lateinit var textViewWeather: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewTemp = findViewById(R.id.tv_temp)
        textViewCity = findViewById(R.id.tv_city)
        textViewRain = findViewById(R.id.tv_raining)
        textViewSnow = findViewById(R.id.tv_snowing)
        textViewWeather = findViewById(R.id.tv_weather)
        imageViewWeather = findViewById(R.id.imageView_weather)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Log.e("lat", weatherUrl)

        obtainLocation()

        val weatherButton: Button = findViewById(R.id.button_getweather)

        weatherButton.setOnClickListener {
            Log.e(TAG, "onCreate: onClick")
            obtainLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtainLocation(){
        Log.e(TAG, "obtainLocation: ")

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                weatherUrl = "https://api.weatherbit.io/v2.0/current?" + "lat=" + location?.latitude + "&lon="+ location?.longitude + "&key=" + apiID
                Log.e(TAG, "obtainLocation: ${weatherUrl.toString()}")
                getTemp()
            }
    }

    @SuppressLint("SetTextI18n")
    private fun getTemp(){
        val queue = Volley.newRequestQueue(this)
        val url: String = weatherUrl
        Log.e(TAG, "getTemp: $url")

        val stringReq = StringRequest(Request.Method.GET, url,
            { response ->
                Log.e(TAG, "getTemp: ${response.toString()}")

                val obj = JSONObject(response)

                val arr = obj.getJSONArray("data")
                Log.e(TAG, "getTemp: ${arr.toString()}")

                val obj2 = arr.getJSONObject(0)
                Log.e(TAG, "getTemp: ${obj2.toString()}")

                val obj3 = obj2.getJSONObject("weather")
                Log.e(TAG, "getTemp: ${obj3.toString()}")

                val obj4 = obj3.getString("description")
                Log.e(TAG, "getTemp: ${obj4.toString()}")

                day_night = obj2.getString("pod").toString()
                val weather_code: Int = obj3.getInt("code")
                when (weather_code) {
                    200,201,202,230,231,232,233,300,301,302,500,501,502,511,520,521,522 -> imageViewWeather.setImageResource(R.drawable.rainy)
                    600,601,602,610,611,612,621,622,623 -> imageViewWeather.setImageResource(R.drawable.snowy)
                    700,711,721,731,741,751,802,803,804,900 -> {
                        if(day_night == "n"){
                            imageViewWeather.setImageResource(R.drawable.cloudy_night)
                        } else {
                            imageViewWeather.setImageResource(R.drawable.cloudy_day)
                        }
                    }
                    800 -> {
                        if(day_night == "n"){
                            imageViewWeather.setImageResource(R.drawable.clear_night)
                        } else {
                            imageViewWeather.setImageResource(R.drawable.clear_day)
                        }
                    }
                    else -> {
                        if(day_night == "n"){
                            imageViewWeather.setImageResource(R.drawable.clear_night)
                        } else {
                            imageViewWeather.setImageResource(R.drawable.clear_day)
                        }
                    }
                }
                val apparent_temp = obj2.getString("app_temp")
                textViewTemp.text = obj2.getString("temp") + "℃" +  " \n(Feels like: " + apparent_temp + "℃)"
                textViewCity.text = obj2.getString("city_name")
                textViewRain.text = obj2.getString("precip")
                textViewSnow.text = obj2.getString("snow")
                val condition = obj4.toString()
                textViewWeather.text = condition
            },
            { textViewTemp.text = "That did not work!" })


        queue.add(stringReq)
    }

}
