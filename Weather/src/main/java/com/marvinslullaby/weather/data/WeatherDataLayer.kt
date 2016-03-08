package com.marvinslullaby.weather.data

import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable
import rx.schedulers.Schedulers

class WeatherDataLayer {

  val APPID:String = "95d190a434083879a6398aafd54d9e73"

  protected val service by lazy {

    val retrofit :Retrofit = Retrofit.Builder()
      .baseUrl(WeatherService.WEATHER_SERVICE_ENDPOINT)
      .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .build()

    retrofit.create(WeatherService::class.java);
  }


  fun getWeatherForCity(city:String, countryCode:String):Observable<WeatherInfomation> {
    return service.getWeatherForCity(hashMapOf(
      Pair("q", "$city,$countryCode"),
      Pair("appId", APPID)
    )).subscribeOn(Schedulers.io())
  }
}