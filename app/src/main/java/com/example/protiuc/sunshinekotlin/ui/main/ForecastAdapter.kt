package com.example.protiuc.sunshinekotlin.ui.main

import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.protiuc.sunshinekotlin.R
import com.example.protiuc.sunshinekotlin.data.database.WeatherEntry
import com.example.protiuc.sunshinekotlin.utils.SunshineDateUtils
import com.example.protiuc.sunshinekotlin.utils.SunshineWeatherUtils
import kotlinx.android.synthetic.main.forecast_list_item.view.*
import java.util.*

internal class ForecastAdapter(
        private val context: Context,
        private val clickHandler: ForecastAdapterOnItemClickHandler)
    : RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder>() {
    /*
     * Flag to determine if we want to use a separate view for the list item that represents
     * today. This flag will be true when the phone is in portrait mode and false when the phone
     * is in landscape. This flag will be set in the constructor of the adapter by accessing
     * boolean resources.
     */
    private val mUseTodayLayout: Boolean = context.resources.getBoolean(R.bool.use_today_layout)
    private var mForecast: List<WeatherEntry>? = null

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (like ours does) you
     * can use this viewType integer to provide a different layout. See
     * [android.support.v7.widget.RecyclerView.Adapter.getItemViewType]
     * for more details.
     * @return A new ForecastAdapterViewHolder that holds the View for each list item
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ForecastAdapterViewHolder {

        val layoutId = getLayoutIdByType(viewType)
        val view = LayoutInflater.from(context).inflate(layoutId, viewGroup, false)
        view.isFocusable = true
        return ForecastAdapterViewHolder(view)
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param forecastAdapterViewHolder The ViewHolder which should be updated to represent the
     * contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(forecastAdapterViewHolder: ForecastAdapterViewHolder, position: Int) {
        val currentWeather = mForecast!![position]

        /****************
         * Weather Icon *
         */
        val weatherIconId = currentWeather.weatherIconId
        val weatherImageResourceId = getImageResourceId(weatherIconId, position)
        forecastAdapterViewHolder.iconView.setImageResource(weatherImageResourceId)

        /****************
         * Weather Date *
         */
        val dateInMillis = currentWeather.date!!.time
        /* Get human readable string using our utility method */
        val dateString = SunshineDateUtils.getFriendlyDateString(context, dateInMillis, false)

        /* Display friendly date string */
        forecastAdapterViewHolder.dateView.setText(dateString)

        /***********************
         * Weather Description *
         */
        val description = SunshineWeatherUtils.getStringForWeatherCondition(context, weatherIconId)
        /* Create the accessibility (a11y) String from the weather description */
        val descriptionA11y = context.getString(R.string.a11y_forecast, description)

        /* Set the text and content description (for accessibility purposes) */
        forecastAdapterViewHolder.descriptionView.setText(description)
        forecastAdapterViewHolder.descriptionView.contentDescription = descriptionA11y

        /**************************
         * High (max) temperature *
         */
        val highInCelsius = currentWeather.max
        /*
         * If the user's preference for weather is fahrenheit, formatTemperature will convert
         * the temperature. This method will also append either °C or °F to the temperature
         * String.
         */
        val highString = SunshineWeatherUtils.formatTemperature(context, highInCelsius)
        /* Create the accessibility (a11y) String from the weather description */
        val highA11y = context.getString(R.string.a11y_high_temp, highString)

        /* Set the text and content description (for accessibility purposes) */
        forecastAdapterViewHolder.highTempView.setText(highString)
        forecastAdapterViewHolder.highTempView.contentDescription = highA11y

        /*************************
         * Low (min) temperature *
         */
        val lowInCelsius = currentWeather.min
        /*
         * If the user's preference for weather is fahrenheit, formatTemperature will convert
         * the temperature. This method will also append either °C or °F to the temperature
         * String.
         */
        val lowString = SunshineWeatherUtils.formatTemperature(context, lowInCelsius)
        val lowA11y = context.getString(R.string.a11y_low_temp, lowString)

        /* Set the text and content description (for accessibility purposes) */
        forecastAdapterViewHolder.lowTempView.setText(lowString)
        forecastAdapterViewHolder.lowTempView.contentDescription = lowA11y
    }

    /**
     * Converts the weather icon id from Open Weather to the local image resource id. Returns the
     * correct image based on whether the forecast is for today(large image) or the future(small image).
     *
     * @param weatherIconId Open Weather icon id
     * @param position      Position in list
     * @return Drawable image resource id for weather
     */
    private fun getImageResourceId(weatherIconId: Int, position: Int): Int {
        val viewType = getItemViewType(position)

        when (viewType) {

            VIEW_TYPE_TODAY -> return SunshineWeatherUtils
                    .getLargeArtResourceIdForWeatherCondition(weatherIconId)

            VIEW_TYPE_FUTURE_DAY -> return SunshineWeatherUtils
                    .getSmallArtResourceIdForWeatherCondition(weatherIconId)

            else -> throw IllegalArgumentException("Invalid view type, value of $viewType")
        }
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    override fun getItemCount(): Int {
        return if (null == mForecast) 0 else mForecast!!.size
    }

    /**
     * Returns an integer code related to the type of View we want the ViewHolder to be at a given
     * position. This method is useful when we want to use different layouts for different items
     * depending on their position. In Sunshine, we take advantage of this method to provide a
     * different layout for the "today" layout. The "today" layout is only shown in portrait mode
     * with the first item in the list.
     *
     * @param position index within our RecyclerView and list
     * @return the view type (today or future day)
     */
    override fun getItemViewType(position: Int): Int {
        return if (mUseTodayLayout && position == 0) {
            VIEW_TYPE_TODAY
        } else {
            VIEW_TYPE_FUTURE_DAY
        }
    }

    /**
     * Swaps the list used by the ForecastAdapter for its weather data. This method is called by
     * [MainActivity] after a load has finished. When this method is called, we assume we have
     * a new set of data, so we call notifyDataSetChanged to tell the RecyclerView to update.
     *
     * @param newForecast the new list of forecasts to use as ForecastAdapter's data source
     */
    fun swapForecast(newForecast: List<WeatherEntry>) {
        if (mForecast == null) {
            mForecast = newForecast
            notifyDataSetChanged()
        } else {

            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return mForecast!!.size
                }

                override fun getNewListSize(): Int {
                    return newForecast.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return mForecast!![oldItemPosition].id == newForecast[newItemPosition].id
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val newWeather = newForecast[newItemPosition]
                    val oldWeather = mForecast!![oldItemPosition]
                    return newWeather.id == oldWeather.id && newWeather.date!! == oldWeather.date
                }
            })

            mForecast = newForecast
            result.dispatchUpdatesTo(this)
        }
    }

    /**
     * Returns the the layout id depending on whether the list item is a normal item or the larger
     * "today" list item.
     *
     * @param viewType
     * @return
     */
    private fun getLayoutIdByType(viewType: Int): Int {
        when (viewType) {

            VIEW_TYPE_TODAY -> {
                return R.layout.list_item_forecast_today
            }

            VIEW_TYPE_FUTURE_DAY -> {
                return R.layout.forecast_list_item
            }

            else -> throw IllegalArgumentException("Invalid view type, value of $viewType")
        }
    }

    /**
     * The interface that receives onItemClick messages.
     */
    interface ForecastAdapterOnItemClickHandler {
        fun onItemClick(date: Date)
    }

    /**
     * A ViewHolder is a required part of the pattern for RecyclerViews. It mostly behaves as
     * a cache of the child views for a forecast item. It's also a convenient place to set an
     * OnClickListener, since it has access to the adapter and the views.
     */
    internal inner class ForecastAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val iconView: ImageView = view.weather_icon

        val dateView: TextView = view.date
        val descriptionView: TextView = view.weather_description
        val highTempView: TextView = view.high_temperature
        val lowTempView: TextView = view.low_temperature

        init {
            view.setOnClickListener(this)
        }

        /**
         * This gets called by the child views during a click. We fetch the date that has been
         * selected, and then call the onItemClick handler registered with this adapter, passing that
         * date.
         *
         * @param v the View that was clicked
         */
        override fun onClick(v: View) {
            val adapterPosition = adapterPosition
            val date = mForecast!![adapterPosition].date
            if (date != null) {
                clickHandler.onItemClick(date)
            }
        }
    }

    companion object {

        private const val VIEW_TYPE_TODAY = 0
        private const val VIEW_TYPE_FUTURE_DAY = 1
    }
}