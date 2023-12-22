package nirmal.baby.weatherforecasts.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import nirmal.baby.weatherforecasts.ItemModels.ItemModelHourlyWeather
import nirmal.baby.weatherforecasts.ItemModels.ItemModelLocationWeather
import nirmal.baby.weatherforecasts.MainActivity
import nirmal.baby.weatherforecasts.R

class AdapterLocationWeather(private val context: Context, private val itemList: MutableList<ItemModelLocationWeather>) :
    RecyclerView.Adapter<AdapterLocationWeather.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.card_view_location_weather, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]

        Picasso.get().load("https:${item.imageUrl}").into(holder.imageView)
        holder.textViewTempDes.text = item.temperatureDesc

        val temperatureStringBuilder = StringBuilder()
        temperatureStringBuilder.append(item.temperature.toString())
        temperatureStringBuilder.append("°")
        holder.textViewTemp.text = temperatureStringBuilder.toString()

        val locationCountryStringBuilder = StringBuilder()
        locationCountryStringBuilder.append(item.locationName)
        locationCountryStringBuilder.append(", ")
        locationCountryStringBuilder.append(item.countryName)
        holder.textViewLocation.text = locationCountryStringBuilder.toString()

        holder.showDetailsTextView.setOnClickListener {
            goToMainActivity("${item.locationName}, ${item.countryName}")
        }

    }



    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var textViewTemp: TextView
        var textViewTempDes: TextView
        var textViewLocation: TextView
        var showDetailsTextView: TextView

        init {
            imageView = itemView.findViewById(R.id.imageViewWeatherConditionInLocationCard)
            textViewTemp = itemView.findViewById(R.id.txtViewTemperatureInLocationCard)
            textViewTempDes = itemView.findViewById(R.id.txtViewTemperatureDescInLocationCard)
            textViewLocation = itemView.findViewById(R.id.txtViewLocationInLocationCard)
            showDetailsTextView = itemView.findViewById(R.id.textViewShowDetailsWeather)
        }
    }

    private fun goToMainActivity(locationName: String) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("locationName", locationName)
        context.startActivity(intent)
    }
}