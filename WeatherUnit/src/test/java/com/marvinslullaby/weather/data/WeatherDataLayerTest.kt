package com.marvinslullaby.weather.data

import org.jetbrains.spek.api.Spek
import rx.observers.TestSubscriber
import kotlin.test.assertEquals

class WeatherDataLayerTest : Spek(){
  init{
    given("a WeatherDataLayer"){
      val dataLayer = WeatherDataLayer()
      val subscriber = TestSubscriber<WeatherInfomation>()
      on("getting weather for Sydney, AU") {
        dataLayer.getWeatherForCity("Sydney","AU").subscribe(subscriber)
        it("should emit weather for Sydney Australia"){
          assertEquals(2,1)
        }

      }
    }
  }
}

