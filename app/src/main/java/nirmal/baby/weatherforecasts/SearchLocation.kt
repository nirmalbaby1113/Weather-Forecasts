package nirmal.baby.weatherforecasts

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import nirmal.baby.weatherforecasts.Adapters.AdapterLocationWeather
import nirmal.baby.weatherforecasts.Data.WeatherApiResponse
import nirmal.baby.weatherforecasts.HttpCalls.WeatherApiManager
import nirmal.baby.weatherforecasts.ItemModels.ItemModelLocationWeather

class SearchLocation : AppCompatActivity() {

    private val weatherApiManager = WeatherApiManager()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterLocationWeather: AdapterLocationWeather
    private lateinit var backTextView: TextView
    private lateinit var textSearchLocationEditText: EditText
    private lateinit var relativeLayoutSearchedData: RelativeLayout
    private lateinit var searchedTemperatureTxtViewMain: TextView
    private lateinit var searchedTemperatureDescTxtViewMain: TextView
    private lateinit var searchedLocationTxtViewMain: TextView
    private lateinit var searchedImageViewMain: ImageView
    private lateinit var searchedShowDetailsButton: TextView
    private lateinit var prefs: SharedPreferences
    private lateinit var gson: Gson
    private var itemListForLocal = mutableListOf<ItemModelLocationWeather>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_location)

        init()
        initializeRecyclerview()
        goBackToMainActivity()
        searchLocation()
    }

    private fun init(){
        recyclerView = findViewById(R.id.recyclerLocationWeatherDetails)
        backTextView = findViewById(R.id.txtViewBack)
        textSearchLocationEditText = findViewById(R.id.editTextSearchLocation)
        relativeLayoutSearchedData = findViewById(R.id.searchedWeatherLayout)
        searchedTemperatureTxtViewMain = findViewById(R.id.txtViewTemperatureInLocationMain)
        searchedTemperatureDescTxtViewMain = findViewById(R.id.txtViewTemperatureDescInLocationMain)
        searchedLocationTxtViewMain = findViewById(R.id.txtViewLocationInLocationMain)
        searchedImageViewMain = findViewById(R.id.imageViewWeatherConditionInLocationMain)
        searchedShowDetailsButton = findViewById(R.id.textViewShowDetailsWeatherMain)
        prefs = getSharedPreferences("WeatherSearchSharedPreference", Context.MODE_PRIVATE)
        gson = Gson()
        itemListForLocal = getItemList().asReversed()
    }

    private fun searchLocation(){
        if (textSearchLocationEditText.text != null)
        {
            textSearchLocationEditText.setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH || event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                    fetchAPI(textSearchLocationEditText.text.toString())
                    hideSoftKeyboard()
                    return@setOnEditorActionListener true
                }
                false
            }
        }
    }

    private fun initializeRecyclerview(){
        adapterLocationWeather = AdapterLocationWeather(this,itemListForLocal)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapterLocationWeather
    }

    private fun goBackToMainActivity() {
        backTextView.setOnClickListener {
            finish()
        }
    }

    private fun hideSoftKeyboard() {
        val inputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun fetchAPI(locationName: String) {
        val apiUrl = "forecast.json?key=aef73390620d4eb080912325231611&q=${locationName}&days=1&aqi=no&alerts=no"
        weatherApiManager.fetchData(apiUrl) { weatherApiResponse ->
            if (weatherApiResponse != null) {
                //Log.d("MainActivity", weatherApiResponse.forecast.forecastday.count().toString())
                updateUI(weatherApiResponse)
            }else{
                Log.e("MainActivity", "API Error: Unable to fetch data")
            }
        }
    }

    private fun updateUI(weatherApiResponse: WeatherApiResponse) {

        itemListForLocal = getItemList()
        if (itemListForLocal.size > 4) {
            itemListForLocal.removeAt(0)
            itemListForLocal.add(ItemModelLocationWeather(weatherApiResponse.current.temp_c,weatherApiResponse.current.condition.icon,weatherApiResponse.current.condition.text,weatherApiResponse.location.name, weatherApiResponse.location.country))
        }else{
            itemListForLocal.add(ItemModelLocationWeather(weatherApiResponse.current.temp_c,weatherApiResponse.current.condition.icon,weatherApiResponse.current.condition.text,weatherApiResponse.location.name, weatherApiResponse.location.country))
        }

        saveItemList(itemListForLocal)
        //Log.d("SearchLocation","Object: ${itemListForLocal[0]}")
        relativeLayoutSearchedData.visibility = View.VISIBLE

        val temperatureStringBuilder = StringBuilder()
        temperatureStringBuilder.append(weatherApiResponse.current.temp_c)
        temperatureStringBuilder.append("°")
        searchedTemperatureTxtViewMain.text = temperatureStringBuilder.toString()

        searchedTemperatureDescTxtViewMain.text = weatherApiResponse.current.condition.text

        val locationStringBuilder = StringBuilder()
        locationStringBuilder.append(weatherApiResponse.location.name)
        locationStringBuilder.append(", ")
        locationStringBuilder.append(weatherApiResponse.location.country)
        searchedLocationTxtViewMain.text = locationStringBuilder.toString()

        Picasso
            .get()
            .load("https:${weatherApiResponse.current.condition.icon}")
            .into(searchedImageViewMain)

        searchedShowDetailsButton.setOnClickListener {
            goToMainActivity("${weatherApiResponse.location.name}, ${weatherApiResponse.location.country}")
        }



    }

    private fun goToMainActivity(locationName: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("locationName", locationName)
        startActivity(intent)
    }

    private fun saveItemList(itemList: MutableList<ItemModelLocationWeather>) {
        val itemListJson = gson.toJson(itemList)
        val editor = prefs.edit()
        editor.putString("weatherJsonString", itemListJson)
        editor.apply()
    }

    private fun getItemList(): MutableList<ItemModelLocationWeather> {
        val itemListJson = prefs.getString("weatherJsonString", null)
        return if (itemListJson != null) {
            val itemType = object : TypeToken<MutableList<ItemModelLocationWeather>>() {}.type
            gson.fromJson(itemListJson, itemType)
        } else {
            mutableListOf()
        }
    }
}