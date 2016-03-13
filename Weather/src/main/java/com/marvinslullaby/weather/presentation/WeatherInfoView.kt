package com.marvinslullaby.weather.presentation

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.bindView
import com.marvinslullaby.weather.R
import com.marvinslullaby.weather.domain.WeatherViewModel


class WeatherInfoView : LinearLayout {

  private val temp: TextView by bindView(R.id.txt_temp)
  private val description:TextView by bindView(R.id.txt_description)

  constructor (context: Context) : super(context, null) {}
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

  init {
    orientation = LinearLayout.VERTICAL
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    inflater.inflate(R.layout.layout_weather_info, this)
  }

  fun setDetails(details: WeatherViewModel.Weather.WeatherDetails){
    temp.text = "${details.temperatureMin} - ${details.temperatureMax}"
    description.text = details.description
  }
}