package com.rugved.weatherapp

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var weatherUrl = ""
    var apiID = "d88f4a2c24c041f7b4e5c6621bb834e2"

    private lateinit var textViewTemp: TextView
    private lateinit var textViewCity: TextView
    private lateinit var textViewRain: TextView
    private lateinit var textViewSnow: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewTemp = findViewById(R.id.tv_temp)
        textViewCity = findViewById(R.id.tv_city)
        textViewRain = findViewById(R.id.tv_raining)
        textViewSnow = findViewById(R.id.tv_snowing)

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

                textViewTemp.text = obj2.getString("temp") + "℃"
                textViewCity.text = obj2.getString("city_name")
                textViewRain.text = obj4.toString()
                textViewSnow.text = obj2.getString("snow")
            },
            { textViewTemp.text = "That did not work!" })

        queue.add(stringReq)
    }

}
