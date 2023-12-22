package nirmal.baby.weatherforecasts.Adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import nirmal.baby.weatherforecasts.ItemModels.ItemModelHourlyWeather
import nirmal.baby.weatherforecasts.R


class AdapterHourlyWeather(private val context: Context, private val itemList: List<ItemModelHourlyWeather>) :
    RecyclerView.Adapter<AdapterHourlyWeather.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.card_view_hourly_weather, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        Picasso.get().load("https:${item.imageUrl}").into(holder.imageView)
        holder.textViewTemp.text = item.itemTemperature
        holder.textViewTime.text = item.itemTimeHour
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var textViewTemp: TextView
        var textViewTime: TextView

        init {
            imageView = itemView.findViewById(R.id.imageViewWeatherImageInCardView)
            textViewTemp = itemView.findViewById(R.id.txtViewTemperatureInCardView)
            textViewTime = itemView.findViewById(R.id.txtViewTimeHourInCardView)
        }
    }
}

