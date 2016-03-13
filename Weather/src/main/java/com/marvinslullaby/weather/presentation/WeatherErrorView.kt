package com.marvinslullaby.weather.presentation

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.bindView
import com.marvinslullaby.weather.R


class WeatherErrorView :LinearLayout {

  var message:String? = null
    set(value){
      messageText.text = value
    }

  var buttonAction:(()->Unit)? = null

  var buttonText:String? = null
    set(value){
      if(value == null){
        button.visibility = View.GONE
      }else{
        button.visibility = View.VISIBLE
        button.text = value
      }
    }

  private val messageText: TextView by bindView(R.id.txt_error_message)
  private val button: Button by bindView(R.id.btn_error_action)

  constructor (context: Context) : super(context, null) {}
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

  init {
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    inflater.inflate(R.layout.layout_weather_error, this)
  }

  override fun onFinishInflate() {
    super.onFinishInflate()
    button.setOnClickListener {
      buttonAction?.invoke()
    }
  }
}