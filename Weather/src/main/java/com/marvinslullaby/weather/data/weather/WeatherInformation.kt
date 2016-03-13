package com.marvinslullaby.weather.data.weather

open class WeatherInformation() {
  open lateinit var coords: WeatherCoords
  open lateinit var weather: List<WeatherData>
  open lateinit var base: String
  open lateinit var main: WeatherMain
  open lateinit var wind: WeatherWind
  open lateinit var clouds: WeatherClouds
  open var dt:Int = 0
  open lateinit var sys: WeatherSystem
  open var id:Int = 0
  open lateinit var name:String
  open var cod:Int = 0
}

open class WeatherCoords() {
  open var lat: Double = 0.0
  open var lon: Double = 0.0
}

open class WeatherData() {
  open var id: Int = 0
  open lateinit var main: String
  open lateinit var description: String
  open lateinit var icon: String
}

open class WeatherMain() {
  open var temp: Double = 0.0
  open var pressure: Double = 0.0
  open var humidity: Int = 0

  open var minTemp:Double = 0.0
  open var maxTemp:Double = 0.0
  open var sealevel:Double = 0.0
  open var groundLevel:Double = 0.0
}

open class WeatherWind() {
  open var speed:Double = 0.0
  open var deg:Double = 0.0
}

open class WeatherClouds() {
  open var all:Int = 0
}

open class WeatherSystem() {
  open var message:Double = 0.0
  open lateinit var country:String
  open var sunrise:Int = 0
  open var sunset:Int = 0
}

