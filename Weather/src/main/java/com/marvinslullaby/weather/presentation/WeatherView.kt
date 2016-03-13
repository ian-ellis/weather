package com.marvinslullaby.weather.presentation

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import butterknife.bindView
import com.marvinslullaby.weather.R
import com.marvinslullaby.weather.domain.WeatherViewModel


class WeatherView :RelativeLayout {

  var retryAction:(()->Unit)? = null

  private val loadingView: ProgressBar by bindView(R.id.progress)
  private val weatherInfo: WeatherInfoView by bindView(R.id.view_weather_info)
  private val errorView: WeatherErrorView by bindView(R.id.view_weather_error)

  constructor (context: Context) : super(context, null) {}
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

  init {
    this.isFocusable = true
    this.isFocusableInTouchMode = true
    this.requestFocus()
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    inflater.inflate(R.layout.layout_weather_view, this)
  }

  fun showLoading(){
    loadingView.visibility = View.VISIBLE
    errorView.visibility = View.GONE
    weatherInfo.visibility = View.GONE
  }

  fun showError(){
    errorView.visibility = View.VISIBLE
    loadingView.visibility = View.GONE
    weatherInfo.visibility = View.GONE
    errorView.message = context.getString(R.string.error)
    errorView.buttonText = context.getString(R.string.error_btn)
    errorView.buttonAction = retryAction
  }

  fun showNoWeather(){
    errorView.visibility = View.VISIBLE
    loadingView.visibility = View.GONE
    weatherInfo.visibility = View.GONE
    errorView.message = context.getString(R.string.error_no_weather)
    errorView.buttonText = null
    errorView.buttonAction = null
  }

  fun showDetails(details: WeatherViewModel.Weather.WeatherDetails){
    weatherInfo.visibility = View.VISIBLE
    errorView.visibility = View.GONE
    loadingView.visibility = View.GONE
    weatherInfo.setDetails(details)
  }


}