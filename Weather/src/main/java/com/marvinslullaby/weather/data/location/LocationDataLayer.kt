package com.marvinslullaby.weather.data.location

import android.location.Location
import rx.Observable


interface LocationDataLayer {
  fun getLocation(): Observable<Location?>
}