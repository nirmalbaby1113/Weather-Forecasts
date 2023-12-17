package nirmal.baby.weatherforecasts.Data

data class WeatherApiResponse(
    val location: Location,
    val current: Current,
    val forecast: Forecast
)

data class Location(
    val name: String,
    val region: String,
    val country: String,
    val lat: Float,
    val lon: Float
)

data class Current(
    val temp_c: Float,
    val temp_f: Float,
    val feelslike_c: Float,
    val condition: WeatherCondition
)

data class WeatherCondition(
    val text: String,
    val icon: String,
    val code: Int
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val day: Day
)

data class Day(
    val maxtemp_c: Float,
    val maxtemp_f: Float,
    val mintemp_c: Float,
    val mintemp_f: Float,
    val condition: WeatherCondition
)
