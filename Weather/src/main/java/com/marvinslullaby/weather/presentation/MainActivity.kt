package com.marvinslullaby.weather.presentation

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import butterknife.bindView
import com.marvinslullaby.weather.R
import com.marvinslullaby.weather.data.location.LocationDataLayer
import com.marvinslullaby.weather.data.location.LocationDataLayerImplementation
import com.marvinslullaby.weather.data.location.LocationPermissionsDeniedException
import com.marvinslullaby.weather.data.search.SearchTerm
import com.marvinslullaby.weather.data.weather.WeatherDataLayer
import com.marvinslullaby.weather.data.weather.WeatherService
import com.marvinslullaby.weather.domain.NoSearchTermsException
import com.marvinslullaby.weather.domain.WeatherViewModel
import com.marvinslullaby.weather.network.ServiceFactory
import com.marvinslullaby.weather.utils.RxBinderUtil
import com.marvinslullaby.weather.utils.hideKeyboard
import rx.android.schedulers.AndroidSchedulers


class MainActivity : AppCompatActivity() {
  companion object {
    const val REQUEST_LOCATION_PERMISSIONS = 1
  }

  private lateinit var viewModel:WeatherViewModel
  private val binder = RxBinderUtil(this)

  private val searchView:SearchView by bindView(R.id.view_search)
  private val weatherView:WeatherView by bindView(R.id.view_weather)
  private val appBar:AppBarLayout by bindView(R.id.appbar)
  private val collapsingToolbar:CollapsingToolbarLayout by bindView(R.id.collapsing_toolbar)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val toolbar = findViewById(R.id.toolbar) as Toolbar
    setSupportActionBar(toolbar)

    retainState({
      viewModel = it
    },{
      val vm = WeatherViewModel.newInstance(this)
      vm.go()
      vm
    })

    searchView.submitHandler = {
      hideKeyboard(this,searchView)
      collapsingToolbar.title = it
      viewModel.search(it)
    }

    searchView.searchingHandler = { searching ->
      appBar.setExpanded(!searching)
    }

    searchView.deleteHandler = {searchTerm->
      viewModel.delete(searchTerm)
    }

    searchView.selectHandler = { searchTerm->
      hideKeyboard(this,searchView)
      when(searchTerm){
        is SearchTerm.City -> collapsingToolbar.title = searchTerm.city
        is SearchTerm.Zip-> collapsingToolbar.title = searchTerm.zip
        is SearchTerm.GPS-> collapsingToolbar.title = this.getString(R.string.gps)
      }
      viewModel.search(searchTerm)
    }

    weatherView.retryAction = {
      viewModel.go()
    }
  }

  override fun onResume() {
    super.onResume()
    binder.bindProperty(viewModel.getCurrentWeather(),{
      when(it){
        is WeatherViewModel.Weather.LoadingWeather -> {

          weatherView.showLoading()
        }
        is WeatherViewModel.Weather.NoWeather -> {
          weatherView.showNoWeather()
        }
        is WeatherViewModel.Weather.WeatherDetails -> {
          collapsingToolbar.title = it.locationName
          weatherView.showDetails(it)
        }
        is WeatherViewModel.Weather.ErrorLoadingWeather -> {
          if(it.error is LocationPermissionsDeniedException){
            ActivityCompat.requestPermissions(this,LocationDataLayer.getPermissions(),REQUEST_LOCATION_PERMISSIONS)
          }else{

            weatherView.showError()
          }
        }


      }
    },{
      Log.d("Weather","""WTF?! something went wrong with getting the weather,
      should be handled in view model though!?""")
    })

    binder.bindProperty(viewModel.getPreviousSearchTerms(),{
      searchView.update(it)
    },{
      Log.d("Weather","WTF?! somthing went wrong with getting search terms")
    })
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray){
    val permissionsGranted = !grantResults.map {it == PackageManager.PERMISSION_GRANTED}.contains(false)
    if(requestCode == REQUEST_LOCATION_PERMISSIONS && permissionsGranted){
      viewModel.go()
    }else{
      viewModel.permissionsDenied()
      weatherView.showError()
    }
  }

  override fun onPause() {
    super.onPause()
    binder.clear()
  }

}