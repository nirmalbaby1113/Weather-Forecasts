package nirmal.baby.weatherforecasts

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import nirmal.baby.weatherforecasts.Adapters.AdapterHourlyWeather
import nirmal.baby.weatherforecasts.ItemModels.ItemModelHourlyWeather
import nirmal.baby.weatherforecasts.Data.Hour
import nirmal.baby.weatherforecasts.Data.WeatherApiResponse
import nirmal.baby.weatherforecasts.HttpCalls.WeatherApiManager
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private val weatherApiManager = WeatherApiManager()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterHourlyWeather: AdapterHourlyWeather
    private lateinit var locationTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var temperatureDescTextView: TextView
    private lateinit var tempMinMaxTextView: TextView
    private lateinit var dayOneThreeDayForecast: TextView
    private lateinit var dayTwoThreeDayForecast: TextView
    private lateinit var dayThreeThreeDayForecast: TextView
    private lateinit var dayOneTemperatureForecast: TextView
    private lateinit var dayTwoTemperatureForecast: TextView
    private lateinit var dayThreeTemperatureForecast: TextView
    private lateinit var dateTextView: TextView
    private lateinit var imageWeatherConditionMain: ImageView
    private lateinit var locationOnTopLinearLayout: LinearLayout
    private lateinit var currentWeatherLinearLayout: LinearLayout
    private lateinit var extrasLinearLayout: LinearLayout
    private lateinit var forecastLayoutHours: LinearLayout
    private lateinit var forecastLayoutThreeDay: LinearLayout
    private lateinit var mainProgressBar: ProgressBar
    private lateinit var changeLocationTextView: TextView
    private lateinit var locationName: String
    private val itemList = mutableListOf<ItemModelHourlyWeather>()
    private val locationPermissionCode = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        // Check and request location permissions
        if (checkLocationPermission()) {
            // Permissions already granted, get user's location
            getUserLocation()
        } else {
            // Request location permissions
            requestLocationPermission()
        }

        getLocationNameFromSearchActivity()
        fetchAPI(locationName)


        moveToSearchLocationActivity()

    }

    // Check if the app has location permissions
    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Request location permissions
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            locationPermissionCode
        )
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get user's location
                getUserLocation()
            }
        }
    }

    // Get user's location
    private fun getUserLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (checkLocationPermission()) {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            // Use the location data (latitude and longitude)
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude

                locationName = getLocationName(latitude,longitude).toString()

            } else {
                // Handle the case where the location is null
            }
        } else {
            // Location permissions not granted, request again
            requestLocationPermission()
        }
    }

    fun getLocationName(latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(this, Locale.getDefault())

        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses != null && addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                // You can extract other information like city, state, etc. from the 'address' object
                return address.getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    private fun getLocationNameFromSearchActivity() {
        locationName = intent.getStringExtra("locationName").toString()
    }

    private fun fetchAPI(locationName: String) {
        val apiUrl = "forecast.json?key=aef73390620d4eb080912325231611&q=${locationName}&days=3&aqi=no&alerts=no"
        weatherApiManager.fetchData(apiUrl) { weatherApiResponse ->
            if (weatherApiResponse != null) {
                //Log.d("MainActivity", weatherApiResponse.forecast.forecastday.count().toString())
                updateUI(weatherApiResponse)
            }else{
                Log.e("MainActivity", "API Error: Unable to fetch data")
            }
        }
    }

    private fun init(){

        //Initialize each UI elements with its proper IDs
        recyclerView =findViewById(R.id.recyclerViewHourlyWeather)
        locationTextView = findViewById(R.id.txtViewLocationName)
        temperatureTextView = findViewById(R.id.txtViewWeatherTemperature)
        temperatureDescTextView = findViewById(R.id.txtViewTemperatureDescription)
        tempMinMaxTextView = findViewById(R.id.txtViewTemperatureMinMax)
        dayOneThreeDayForecast = findViewById(R.id.txtViewDayForecast1)
        dayTwoThreeDayForecast = findViewById(R.id.txtViewDayForecast2)
        dayThreeThreeDayForecast = findViewById(R.id.txtViewDayForecast3)
        dayOneTemperatureForecast = findViewById(R.id.txtViewForecastTemperature1)
        dayTwoTemperatureForecast = findViewById(R.id.txtViewForecastTemperature2)
        dayThreeTemperatureForecast = findViewById(R.id.txtViewForecastTemperature3)
        dateTextView = findViewById(R.id.txtViewDate)
        imageWeatherConditionMain = findViewById(R.id.imageWeatherCondition)
        locationOnTopLinearLayout = findViewById(R.id.linearLayoutLocationOnTop)
        currentWeatherLinearLayout = findViewById(R.id.linearLayoutCurrentWeather)
        extrasLinearLayout = findViewById(R.id.linearLayoutExtras)
        forecastLayoutHours = findViewById(R.id.hourlyForecastLayout)
        forecastLayoutThreeDay = findViewById(R.id.threeDayForcastLayout)
        mainProgressBar = findViewById(R.id.mainLoadingIndicator)
        changeLocationTextView = findViewById(R.id.txtViewChangeLocation)


    }
    private fun updateUI(weatherApiResponse: WeatherApiResponse) {

        //Changing the visibility
        mainProgressBar.visibility = View.GONE
        locationOnTopLinearLayout.visibility = View.VISIBLE
        currentWeatherLinearLayout.visibility = View.VISIBLE
        extrasLinearLayout.visibility = View.VISIBLE
        forecastLayoutHours.visibility = View.VISIBLE
        forecastLayoutThreeDay.visibility = View.VISIBLE

        //Initializing the UI elements with proper data
        val locationStringBuilder = StringBuilder()
        locationStringBuilder.append(weatherApiResponse.location.name)
        locationStringBuilder.append(", ")
        locationStringBuilder.append(weatherApiResponse.location.country)
        locationTextView.text = locationStringBuilder.toString()

        val temperatureStringBuilder = StringBuilder()
        temperatureStringBuilder.append(weatherApiResponse.current.temp_c)
        temperatureStringBuilder.append(" °C")
        temperatureTextView.text = temperatureStringBuilder.toString()

        val tempMinMaxStringBuilder = StringBuilder()
        tempMinMaxStringBuilder.append("High: ")
        tempMinMaxStringBuilder.append(weatherApiResponse.forecast.forecastday[0].day.maxtemp_c)
        tempMinMaxStringBuilder.append("° , Low: ")
        tempMinMaxStringBuilder.append(weatherApiResponse.forecast.forecastday[0].day.mintemp_c)
        tempMinMaxStringBuilder.append("°")
        tempMinMaxTextView.text = tempMinMaxStringBuilder.toString()

        temperatureDescTextView.text = weatherApiResponse.current.condition.text

        dateTextView.text = formatDate(weatherApiResponse.forecast.forecastday[0].date)

        Picasso
            .get()
            .load("https:${weatherApiResponse.current.condition.icon}")
            .into(imageWeatherConditionMain)

        dayOneThreeDayForecast.text = getDayOfWeek(weatherApiResponse.forecast.forecastday[0].date)

        dayTwoThreeDayForecast.text = getDayOfWeek(weatherApiResponse.forecast.forecastday[1].date)

        dayThreeThreeDayForecast.text = getDayOfWeek(weatherApiResponse.forecast.forecastday[2].date)

        // Clear the StringBuilder for reuse
        temperatureStringBuilder.setLength(0)

        // Day One
        temperatureStringBuilder.append(weatherApiResponse.current.temp_c)
        temperatureStringBuilder.append(" °C")
        dayOneTemperatureForecast.text = temperatureStringBuilder.toString()

        // Clear the StringBuilder for reuse
        temperatureStringBuilder.setLength(0)

        // Day Two
        temperatureStringBuilder.append(weatherApiResponse.forecast.forecastday[1].day.avgtemp_c)
        temperatureStringBuilder.append(" °C")
        dayTwoTemperatureForecast.text = temperatureStringBuilder.toString()

        // Clear the StringBuilder for reuse
        temperatureStringBuilder.setLength(0)

        // Day Three
        temperatureStringBuilder.append(weatherApiResponse.forecast.forecastday[2].day.avgtemp_c)
        temperatureStringBuilder.append(" °C")
        dayThreeTemperatureForecast.text = temperatureStringBuilder.toString()

        //Initializing recyclerview with adapterHourlyWeather
        initializeRecyclerView(weatherApiResponse)
    }

    private fun initializeRecyclerView(weatherApiResponse: WeatherApiResponse){

        val timeZone = ZoneId.of(weatherApiResponse.location.tz_id)
        val currentHour = ZonedDateTime.now(timeZone).hour
        //val currentHour = LocalDateTime.now().hour
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        Log.d("MainActivity", weatherApiResponse.forecast.forecastday[0].hour[0].time)
        for (item in weatherApiResponse.forecast.forecastday[0].hour){
            val jsonDateAndTime = LocalDateTime.parse(item.time, formatter)
            if(currentHour <= jsonDateAndTime.hour) {
                Log.d("MainActivity","Filtered Time: ${jsonDateAndTime.hour}")
                val newItem = generateItem(item)
                if (newItem != null) {
                    itemList.add(newItem)
                }
            }
        }

        adapterHourlyWeather = AdapterHourlyWeather(this,itemList)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapterHourlyWeather

    }

    private fun getDayOfWeek(dateString: String): String {
        // Parse the string to LocalDate
        val date = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE)

        val dayToSmallCase = date.dayOfWeek.toString().lowercase()

        // Capitalize the first letter and Return the day of the week
        return dayToSmallCase.substring(0, 1)
            .uppercase(Locale.getDefault()) + dayToSmallCase.substring(1)
    }

    private fun formatDate(dateString: String): String {

        Log.d("MainActivity","Inside Format: ${dateString}")
        // Define the formatter
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // Parse the string to LocalDateTime
        val dateTime = LocalDate.parse(dateString, formatter)

        //Return the string with proper format
        return "${dateTime.month.getDisplayName(TextStyle.SHORT,Locale.getDefault())}, ${dateTime.dayOfMonth}"
    }

    // Function to generate a single ItemModelHourlyWeather object based on the JSON data
    @SuppressLint("SimpleDateFormat")
    private fun generateItem(hourItem: Hour): ItemModelHourlyWeather? {

        // Define the formatter
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")

        try {
            // Parse the string to Date
            val date: Date? = formatter.parse(hourItem.time)

            // Extract hour and minute
            val calendar = java.util.Calendar.getInstance()
            if (date != null) {
                calendar.time = date
            }

            val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)

            return ItemModelHourlyWeather("${hour}:00", hourItem.condition.icon, hourItem.temp_c.toString())
        } catch (e: ParseException) {
            e.printStackTrace()
            return null
        }
    }

    private fun moveToSearchLocationActivity(){
        changeLocationTextView.setOnClickListener {
            val intent = Intent(this,SearchLocation::class.java)
            startActivity(intent)
        }
    }
}