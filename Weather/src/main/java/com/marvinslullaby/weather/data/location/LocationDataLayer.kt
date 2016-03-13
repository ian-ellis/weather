package com.marvinslullaby.weather.data.location

import android.Manifest
import android.location.Location
import rx.Observable


interface LocationDataLayer {
  companion object {

    fun getPermissions():Array<String> {
      return arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    }
  }
  fun getLocation(): Observable<Location?>
}