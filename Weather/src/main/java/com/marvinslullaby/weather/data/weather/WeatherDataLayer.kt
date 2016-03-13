package com.marvinslullaby.weather.data.weather

import android.content.Context
import com.marvinslullaby.weather.R
import com.marvinslullaby.weather.data.location.LocationDataLayer
import com.marvinslullaby.weather.data.location.LocationDataLayerImplementation
import com.marvinslullaby.weather.data.location.LocationNotFoundException
import com.marvinslullaby.weather.network.ServiceFactory
import rx.Observable
import rx.schedulers.Schedulers

open class WeatherDataLayer(val context:Context, val locationDataLayer: LocationDataLayer,val service:WeatherService) {

  companion object {
    @JvmStatic val APP_ID: String = "95d190a434083879a6398aafd54d9e73"

    fun newInstance(context:Context):WeatherDataLayer{
      val service = ServiceFactory.createService(WeatherService::class.java,WeatherService.ENDPOINT)
      val locationDataLayer = LocationDataLayerImplementation(context)
      return WeatherDataLayer(context,locationDataLayer,service)
    }
  }

  open fun getWeatherForCity(city: String): Observable<WeatherInformation> {
    return service.getWeatherInformation(hashMapOf(
      Pair("q", "$city"),
      Pair("appId", APP_ID)
    )).subscribeOn(Schedulers.io())
  }

  open fun getWeatherForZip(zip: String): Observable<WeatherInformation> {
    val countryCode = getCountryCodeForZip()
    return service.getWeatherInformation(hashMapOf(
      Pair("zip", "$zip,$countryCode"),
      Pair("appId", APP_ID)
    )).subscribeOn(Schedulers.io())
  }

  open fun getWeatherForGps(): Observable<WeatherInformation> {
    return locationDataLayer.getLocation().flatMap { location->
      if(location == null){
        throw LocationNotFoundException
      }

      service.getWeatherInformation(hashMapOf(
        Pair("lat", location.latitude.toString()),
        Pair("lon", location.longitude.toString()),
        Pair("appId", APP_ID)
      )).subscribeOn(Schedulers.io())
    }
  }

  protected fun getCountryCodeForZip(): String {
    return context.resources.getString(R.string.default_country_code)
  }
}