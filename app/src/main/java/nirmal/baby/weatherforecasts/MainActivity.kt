package nirmal.baby.weatherforecasts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import nirmal.baby.weatherforecasts.HttpCalls.WeatherApiManager

class MainActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val apiUrl = "forecast.json?key=aef73390620d4eb080912325231611&q=London&days=1&aqi=no&alerts=no"
        WeatherApiManager().fetchData(apiUrl)

    }
}