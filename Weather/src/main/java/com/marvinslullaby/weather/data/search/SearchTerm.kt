package com.marvinslullaby.weather.data.search

import com.marvinslullaby.weather.utils.isNumeric
import java.io.Serializable


sealed class SearchTerm : Serializable {
  companion object {

    protected val GPS_VALUE = "__GPS__"

    fun map(value: String): SearchTerm {
      return if (value.isNumeric()) {
        Zip(value)
      } else if (value == GPS_VALUE) {
        GPS()
      } else {
        City(value)
      }
    }

    fun map(searchTerm: SearchTerm): String {
      return when (searchTerm) {
        is Zip -> searchTerm.zip
        is City -> searchTerm.city
        is GPS -> GPS_VALUE
      }
    }
  }

  class City(val city:String): SearchTerm()
  class Zip(val zip:String): SearchTerm()
  class GPS(): SearchTerm()



}