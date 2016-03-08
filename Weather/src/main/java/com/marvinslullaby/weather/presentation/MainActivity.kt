package com.marvinslullaby.weather.presentation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.marvinslullaby.weather.R
import com.marvinslullaby.weather.data.weather.WeatherDataLayer
import rx.android.schedulers.AndroidSchedulers


class MainActivity : AppCompatActivity() {

  protected val dataLayer: WeatherDataLayer = WeatherDataLayer()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val toolbar = findViewById(R.id.toolbar) as Toolbar
    setSupportActionBar(toolbar)


    dataLayer.getWeatherForCity("Sydney").observeOn(AndroidSchedulers.mainThread()).subscribe({
      val woot = ""
    },{
      val yeah = ""
    },{
      val damn = ""
    })
  }


  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    val id = item.itemId

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true
    }

    return super.onOptionsItemSelected(item)
  }
}