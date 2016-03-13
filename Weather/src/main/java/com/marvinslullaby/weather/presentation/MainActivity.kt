package com.marvinslullaby.weather.presentation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import butterknife.bindView
import com.marvinslullaby.weather.R
import com.marvinslullaby.weather.data.location.LocationDataLayer
import com.marvinslullaby.weather.data.location.LocationDataLayerImplementation
import com.marvinslullaby.weather.data.weather.WeatherDataLayer
import com.marvinslullaby.weather.data.weather.WeatherService
import com.marvinslullaby.weather.domain.NoSearchTermsException
import com.marvinslullaby.weather.domain.WeatherViewModel
import com.marvinslullaby.weather.network.ServiceFactory
import com.marvinslullaby.weather.utils.RxBinderUtil
import rx.android.schedulers.AndroidSchedulers


class MainActivity : AppCompatActivity() {


  private lateinit var viewModel:WeatherViewModel
  private val binder = RxBinderUtil(this)

  private val searchView:SearchView by bindView(R.id.view_search)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val toolbar = findViewById(R.id.toolbar) as Toolbar
    setSupportActionBar(toolbar)

    retainState({
      viewModel = it
    },{
      WeatherViewModel.newInstance(this)
    })

    searchView.submitHandler = {
      viewModel.search(it)
    }
  }

  override fun onResume() {
    binder.bindProperty(viewModel.getCurrentWeather(),{

    },{
      if(it is NoSearchTermsException){

      }
      Log.d("Weather","WTF?! something went wrong with getting the weather")
    })

    binder.bindProperty(viewModel.getPreviousSearchTerms(),{
      searchView.update(it)
    },{
      Log.d("Weather","WTF?! somthing went wrong with getting search terms")
    })
  }

  override fun onPause() {
    binder.clear()
  }

}