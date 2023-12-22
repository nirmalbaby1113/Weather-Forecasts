package nirmal.baby.weatherforecasts.HttpCalls

import android.util.Log
import nirmal.baby.weatherforecasts.Data.WeatherApiResponse
import nirmal.baby.weatherforecasts.Interfaces.WeatherApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherApiManager {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.weatherapi.com/v1/") // Base URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(WeatherApiService::class.java)

    fun fetchData(apiUrl: String, uiUpdateCallback: (WeatherApiResponse?) -> Unit) {
        val call: Call<WeatherApiResponse> = apiService.fetchData(apiUrl)


        Log.d("WeatherApiManager", "URL in Fetch: ${apiUrl}")
        call.enqueue(object : Callback<WeatherApiResponse> {
            override fun onResponse(call: Call<WeatherApiResponse>, response: Response<WeatherApiResponse>) {
                if (response.isSuccessful) {
                    //val data: String? = response.body()?.location?.name
                    val weatherApiResponse: WeatherApiResponse? = response.body()
                    uiUpdateCallback.invoke(weatherApiResponse)
                    //Log.d("WeatherApiManager", "Response: ${data}")
                } else {
                    uiUpdateCallback.invoke(null)
                    Log.d("WeatherApiManager", "${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeatherApiResponse>, t: Throwable) {
                println("Error: ${t.message}")
                uiUpdateCallback(null)
            }
        })
    }
}

