package com.marvinslullaby.weather.data.search

import com.marvinslullaby.weather.utils.isNumeric
import java.io.Serializable

sealed class SearchTerm : Serializable {
  companion object {
    val GPS_VALUE = "__GPS__"
    @JvmStatic fun map(value: String): SearchTerm {
      return if (value.isNumeric()) {
        SearchTerm.Zip(value)
      } else if (value == SearchTerm.GPS_VALUE) {
        SearchTerm.GPS()
      } else {
        SearchTerm.City(value)
      }
    }

    @JvmStatic fun map(searchTerm: SearchTerm): String {
      return when (searchTerm) {
        is SearchTerm.Zip -> searchTerm.zip
        is SearchTerm.City -> searchTerm.city
        is SearchTerm.GPS -> SearchTerm.GPS_VALUE
      }
    }
  }

  class City(val city:String): SearchTerm(){
    override fun equals(other: Any?): Boolean {
      return if(other is City) {other.city == city} else {false}
    }
    override fun hashCode() = city.hashCode()
    override fun toString() = city
  }
  class Zip(val zip:String): SearchTerm(){
    override fun equals(other: Any?): Boolean {
      return if(other is Zip) {other.zip == zip} else {false}
    }
    override fun hashCode() = zip.hashCode()
    override fun toString() = zip
  }
  class GPS(): SearchTerm(){
    override fun equals(other: Any?) = other is GPS
    override fun hashCode() = GPS_VALUE.hashCode()
    override fun toString() = GPS_VALUE
  }



}