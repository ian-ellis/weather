package com.marvinslullaby.weather.data.weather

import retrofit2.http.GET
import retrofit2.http.QueryMap
import java.util.HashMap
import rx.Observable


interface WeatherService {

  companion object{
    val ENDPOINT = "http://api.openweathermap.org";
  }

  @GET("/data/2.5/weather")
  fun getWeatherInformation(@QueryMap()query: HashMap<String, String>):Observable<WeatherInformation>

}