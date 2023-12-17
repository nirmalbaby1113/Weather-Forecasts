package nirmal.baby.weatherforecasts.Interfaces

import nirmal.baby.weatherforecasts.Data.WeatherApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface WeatherApiService {
    @GET
    fun fetchData(@Url url: String): Call<WeatherApiResponse>
}