package com.marvinslullaby.weather.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.v4.content.PermissionChecker
import rx.subjects.BehaviorSubject

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import rx.Observable

class LocationDataLayerImplementation(val context: Context):
  LocationDataLayer,
  GoogleApiClient.ConnectionCallbacks,
  GoogleApiClient.OnConnectionFailedListener,
  LocationListener {


  protected val googleApiClient: GoogleApiClient
  protected val locationManager: LocationManager?
  protected val locationBehaviour = BehaviorSubject.create<Location?>()
  protected val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

  init {
    googleApiClient = GoogleApiClient.Builder(context)
      .addApi(LocationServices.API)
      .addConnectionCallbacks(this)
      .addOnConnectionFailedListener(this)
      .build()

    //on unsubscribe, if no other subscribers disconnect from location information
    locationBehaviour.doOnUnsubscribe {
      if (locationBehaviour.hasObservers()) {
        googleApiClient.disconnect()
      }
    }

    locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
  }

  override fun getLocation(): Observable<Location?> {

    if(!hasLocationPermissions()){
      return Observable.error(LocationPermissionsDeniedException)
    }

    if (!googleApiClient.isConnected && !googleApiClient.isConnecting) {
      googleApiClient.connect()
    } else if(googleApiClient.isConnected) {
      requestUpdate()
    }

    return locationBehaviour
  }

  protected fun requestUpdate() {
    val locationRequest = LocationRequest();
    locationRequest.numUpdates = 1
    locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

    val settingsRequest = LocationSettingsRequest.Builder()
      .addLocationRequest(locationRequest)
      .build()

    val settings = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, settingsRequest)
    settings.setResultCallback {
      if(it.status.isSuccess) {
        //Requirements for location updates are satisfied
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this)
      } else {
        //Requirements for location updates are not satisfied
        locationBehaviour.onNext(null)
      }
    }
  }

  protected fun locationAvailable():Boolean{
    return locationManager?.let{
      if(hasLocationPermissions()) {
        it.isProviderEnabled(LocationManager.GPS_PROVIDER) || it.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
      }else{
        false
      }
    } ?: false
  }

  protected fun hasLocationPermissions():Boolean{
    return !permissions.map {
      selfPermissionGranted(it)
    }.contains(false)

  }
  protected fun selfPermissionGranted(permission:String):Boolean {
    //for SDKs lower than Marshmallow
    var result = true;

    var targetSdkVersion = try {
      val info = context.packageManager.getPackageInfo(context.packageName, 0);
      info.applicationInfo.targetSdkVersion;
    } catch (e: PackageManager.NameNotFoundException) {
      null
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && targetSdkVersion != null) {
      if (targetSdkVersion >= Build.VERSION_CODES.M) {
        // targetSdkVersion >= Android M, we can
        // use Context#checkSelfPermission
        result = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
      } else {
        // targetSdkVersion < Android M, we have to use PermissionChecker
        result = PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED
      }
    }
    return result;
  }

  override fun onConnected(p0: Bundle?) {
    requestUpdate()
  }

  override fun onConnectionSuspended(reason: Int) {/*no op*/}

  override fun onLocationChanged(location: Location)  {
    locationBehaviour.onNext(location)
  }

  override fun onConnectionFailed(p0: ConnectionResult) {
    locationBehaviour.onError(Exception("${p0.errorCode} ${p0.errorMessage}"))
  }
}