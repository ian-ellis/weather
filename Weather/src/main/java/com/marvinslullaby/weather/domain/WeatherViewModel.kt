package com.marvinslullaby.weather.domain

import android.content.Context
import com.marvinslullaby.weather.R
import com.marvinslullaby.weather.data.search.SearchTerm
import com.marvinslullaby.weather.data.search.SearchTermsDataLayer
import com.marvinslullaby.weather.data.weather.WeatherDataLayer
import com.marvinslullaby.weather.data.weather.WeatherInformation
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
                       val weatherDataLayer: WeatherDataLayer,
                       val searchTermsDataLayer: SearchTermsDataLayer) {

  companion object {
    fun newInstance(context:Context):WeatherViewModel{
        return WeatherViewModel(context,
          WeatherDataLayer.newInstance(context),
          SearchTermsDataLayer.newInstance(context)
        )

    }
  }

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

  protected var currentWeather: BehaviorSubject<Weather> = BehaviorSubject.create()
  protected var subscriptions: CompositeSubscription = CompositeSubscription()

  fun go() {

    subscriptions.clear()
    subscriptions.add(
      getSearchTermsForSearch().doOnNext {
        currentWeather.onNext(Weather.LoadingWeather())
      }.switchMap { searchTerms ->
        getWeatherForSearchTerm(searchTerms[0])
      }.subscribe ({
        currentWeather.onNext(it)
      })
    )
  }

  fun permissionsDenied(){
    currentWeather.onNext(Weather.ErrorLoadingWeather(Error("Permissions Denied")))
  }

  fun getCurrentWeather(): Observable<Weather> = currentWeather.asObservable()

  fun search(searchValue: String) {
    searchTermsDataLayer.add(searchValue)
  }

  fun search(searchValue: SearchTerm) {
    searchTermsDataLayer.add(searchValue)
  }

  fun delete(searchTerm:SearchTerm){
    searchTermsDataLayer.delete(searchTerm)
  }


  fun getPreviousSearchTerms(): Observable<List<SearchTerm>> {
    // re-order to have GPS always at top
    return searchTermsDataLayer.getSavedSearchTerms().map {
      val list = mutableListOf<SearchTerm>()
      list.addAll(it)
      if(!list.contains(SearchTerm.GPS())){
        list.add(SearchTerm.GPS())
      }
      list.distinct()
      list.sortedWith(Comparator { t1, t2 ->
        if (t1 is SearchTerm.GPS) {
          -1
        } else if (t2 is SearchTerm.GPS) {
          1
        } else {
          0
        }
      })
    }
  }

  private fun getWeatherForSearchTerm(searchTerm:SearchTerm):Observable<Weather>{
    return when(searchTerm){
      is SearchTerm.Zip -> weatherDataLayer.getWeatherForZip(searchTerm.zip)
      is SearchTerm.City -> weatherDataLayer.getWeatherForCity(searchTerm.city)
      is SearchTerm.GPS -> weatherDataLayer.getWeatherForGps()
    }.map {
      mapInfoToDetails(it)
    }.onErrorReturn{
      Weather.ErrorLoadingWeather(it)
    }
  }

  private fun getSearchTermsForSearch():Observable<List<SearchTerm>>{
    return searchTermsDataLayer.getSavedSearchTerms().doOnNext{
      if(it.size == 0){
        currentWeather.onNext(Weather.NoWeather())
      }
    }.filter{
      it.size > 0
    }
  }

  private fun mapInfoToDetails(info: WeatherInformation): Weather {
    val sunriseDate = Date(info.sys.sunrise.toLong())
    val sunsetDate = Date(info.sys.sunset.toLong())
    val dateFormat = SimpleDateFormat("HH:mm")
    return Weather.WeatherDetails(
      locationName = info.name,
      description = info.weather[0].description,
      icon = iconMap[info.weather[0].icon],
      temperatureMax = context.resources.getString(R.string.temp_celcius, String.format("%s",info.main.maxTemp)),
      temperatureMin = context.resources.getString(R.string.temp_celcius, String.format("%s",info.main.minTemp)),
      humidity = context.resources.getString(R.string.humidity, String.format("%s",info.main.humidity)),
      sunrise = dateFormat.format(sunriseDate),
      sunset = dateFormat.format(sunsetDate)

    )
  }

  sealed class Weather {
    class NoWeather() : Weather()
    class ErrorLoadingWeather(val error:Throwable) : Weather()
    class LoadingWeather() : Weather()
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