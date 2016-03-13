package com.marvinslullaby.weather.network

import com.marvinslullaby.weather.data.weather.WeatherService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.reflect.KClass


object ServiceFactory {

  fun <T>createService(clazz:Class<T>, endpoint: String):T {
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(endpoint)
      .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .build()
    return retrofit.create(clazz)
  }
}