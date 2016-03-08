package com.marvinslullaby.weather.data

class WeatherInfomation() {
  lateinit var coords: WeatherCoords
  lateinit var weather: List<WeatherData>
  lateinit var base: String
  lateinit var main:WeatherMain
  lateinit var wind:WeatherWind
  lateinit var clouds:WeatherClouds
  var dt:Int = 0
  lateinit var sys:WeatherSystem
  var id:Int = 0
  lateinit var name:String
  var cod:Int = 0
}

class WeatherCoords() {
  var lat: Double = 0.0
  var lon: Double = 0.0
}

class WeatherData() {
  var id: Int = 0
  lateinit var main: String
  lateinit var description: String
  lateinit var icon: String
}

class WeatherMain() {
  var temp: Double = 0.0
  var pressure: Double = 0.0
  var humidity: Int = 0

  var minTemp:Double = 0.0
  var maxTemp:Double = 0.0
  var sealevel:Double = 0.0
  var groundLevel:Double = 0.0
}

class WeatherWind() {
  var speed:Double = 0.0
  var deg:Double = 0.0
}

class WeatherClouds() {
  var all:Int = 0
}

class WeatherSystem() {
  var message:Double = 0.0
  lateinit var country:String
  var sunrise:Int = 0
  var sunset:Int = 0
}

