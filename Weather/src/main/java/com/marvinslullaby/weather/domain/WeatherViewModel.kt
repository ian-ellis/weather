package com.marvinslullaby.weather.domain

import android.content.Context
import com.marvinslullaby.weather.R
import com.marvinslullaby.weather.data.search.SearchTerm
import com.marvinslullaby.weather.data.search.SearchTermsDataLayer
import com.marvinslullaby.weather.data.weather.WeatherDataLayer
import com.marvinslullaby.weather.data.weather.WeatherInfomation
import com.marvinslullaby.weather.utils.isNumeric
import rx.subjects.BehaviorSubject
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.subscriptions.CompositeSubscription
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class WeatherViewModel(val context: Context,
                       val weaterDataLayer: WeatherDataLayer,
                       val searchTermsDataLayer: SearchTermsDataLayer) {

  private val iconMap: HashMap<String, Int> = hashMapOf(
    Pair("01d", R.drawable.ic_clear),
    Pair("01n", R.drawable.ic_clear),
    Pair("02d", R.drawable.ic_clouds_broken),
    Pair("02n", R.drawable.ic_clouds_broken),
    Pair("03d", R.drawable.ic_clouds),
    Pair("03n", R.drawable.ic_clouds),
    Pair("04d", R.drawable.ic_clouds_heavy),
    Pair("04n", R.drawable.ic_clouds_heavy),
    Pair("09d", R.drawable.ic_rain),
    Pair("09n", R.drawable.ic_rain),
    Pair("10d", R.drawable.ic_rain_heavy),
    Pair("10n", R.drawable.ic_rain_heavy),
    Pair("11d", R.drawable.ic_thunder),
    Pair("11n", R.drawable.ic_thunder),
    Pair("13d", R.drawable.ic_snow),
    Pair("13n", R.drawable.ic_snow),
    Pair("50d", R.drawable.ic_mist),
    Pair("50n", R.drawable.ic_mist)
  )
  //output
  protected var currentWeather: BehaviorSubject<Weather> = BehaviorSubject.create()
  protected var searchTerms: BehaviorSubject<SearchTerm> = BehaviorSubject.create()
  //input
  protected var searchTermsInput: BehaviorSubject<String> = BehaviorSubject.create()
  protected var subscriptions: CompositeSubscription = CompositeSubscription()

  fun go() {

    subscriptions.clear()

    val searchTerms = searchTermsDataLayer.getSavedSearchTerms().subscribe()

    subscriptions.add(
      searchTermsDataLayer.getSavedSearchTerms().map {
        if(it.size <= 1){
          //only GPS - so nothing previously searched
          throw NoSearchTermsException()
        }else{
          it[1]
        }
      }.doOnNext {
        currentWeather.onNext(Weather.LoadingWeather())
      }.switchMap { searchTerm ->

        when(searchTerm){
          is SearchTerm.Zip -> weaterDataLayer.getWeatherForZip(searchTerm.zip, "au")
          is SearchTerm.City -> weaterDataLayer.getWeatherForCity(searchTerm.city)
          is SearchTerm.GPS -> weaterDataLayer.getWeatherForGps()
        }

      }.map {
        mapInfoToDetails(it)
      }.subscribe ({
        currentWeather.onNext(it)
      }, {
        if(it is NoSearchTermsException){
          currentWeather.onNext(Weather.NoWeather())
        }else{
          currentWeather.onNext(Weather.ErrorLoadingWeather())
        }
      })
    )
  }

  fun search(searchValue: String) {
    searchTermsDataLayer.addSearchTerm(searchValue)
  }

  fun getCurrentWeather(): Observable<Weather> = currentWeather.asObservable()
  fun getPreviousSearchTerms(): Observable<SearchTerm> = searchTerms.asObservable()

  protected fun mapInfoToDetails(info: WeatherInfomation): Weather.WeatherDetails {
    val sunriseDate = Date(info.sys.sunrise.toLong())
    val sunsetDate = Date(info.sys.sunset.toLong())
    val dateFormat = SimpleDateFormat("HH:mm")
    return Weather.WeatherDetails(
      locationName = info.name,
      description = info.weather[0].description,
      icon = iconMap[info.weather[0].icon],
      temperatureMax = context.resources.getString(R.string.temp_celcius, info.main.maxTemp),
      temperatureMin = context.resources.getString(R.string.temp_celcius, info.main.minTemp),
      humidity = context.resources.getString(R.string.temp_celcius, info.main.humidity),
      sunrise = dateFormat.format(sunriseDate),
      sunset = dateFormat.format(sunsetDate)

    )
  }

  sealed class Weather {
    class NoWeather() : Weather()
    class LoadingWeather() : Weather()
    class ErrorLoadingWeather() : Weather()
    class WeatherDetails(val locationName: String,
                         val description: String,
                         val icon: Int?,
                         val temperatureMax: String,
                         val temperatureMin: String,
                         val humidity: String,
                         val sunrise: String,
                         val sunset: String) : Weather()
  }


}