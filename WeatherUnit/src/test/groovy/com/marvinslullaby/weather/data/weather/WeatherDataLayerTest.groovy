package com.marvinslullaby.weather.data.weather
import android.content.Context
import android.content.res.Resources
import android.location.Location
import com.marvinslullaby.weather.R
import com.marvinslullaby.weather.data.location.LocationDataLayer
import com.marvinslullaby.weather.data.location.LocationNotFoundException
import rx.Observable
import rx.observers.TestSubscriber
import spock.lang.Specification

import java.util.concurrent.TimeUnit

class WeatherDataLayerTest extends Specification {
    WeatherDataLayer dataLayer
    TestSubscriber subscriber
    LocationDataLayer locationDataLayer
    WeatherService service
    Context context
    String defaultCountryCode = "au"
    def setup(){
      context = Mock(Context){}

      def resources = Mock(Resources)
      resources.getString(R.string.default_country_code) >> "au"
      context.getResources() >> resources

      locationDataLayer = Mock LocationDataLayer
      service = Mock(WeatherService)
      dataLayer = new WeatherDataLayer(context,locationDataLayer,service)
      subscriber = new TestSubscriber()
    }

  def 'getWeatherForCity() - queries service for weather'(){
    given:'a city name'
    def weather = Mock WeatherInformation
    def city = "Sydney"

    when:'we get the weather for that city'
    dataLayer.getWeatherForCity(city).subscribe(subscriber)
    subscriber.awaitTerminalEvent(1,TimeUnit.SECONDS)

    then:'we make a call to the service with the correct parameters'
    1 * service.getWeatherInformation([q:city, appId: WeatherDataLayer.APP_ID]) >> Observable.just(weather)
    subscriber.onNextEvents.size() == 1
    subscriber.onNextEvents[0] == weather

  }

  def 'getWeatherForZip() - queries service for weather'(){
    given: 'a zip code'
    def zip = "2000"
    def weather = Mock WeatherInformation

    when: 'we get weather for that zip code'
    dataLayer.getWeatherForZip(zip).subscribe(subscriber)
    subscriber.awaitTerminalEvent(1,TimeUnit.SECONDS)

    then:'we make a call to the service with the correct parameters'
    1 * service.getWeatherInformation([zip:"$zip,$defaultCountryCode", appId:WeatherDataLayer.APP_ID]) >> Observable.just(weather)
    subscriber.onNextEvents.size() == 1
    subscriber.onNextEvents[0] == weather
  }

  def 'getWeatherForGps() - queries service with correct lat / lng'(){
    given: 'a location'
    def location = Mock(Location){
      it.getLatitude() >> -33.873836
      it.getLongitude() >> 151.240540
    }
    def weather = Mock WeatherInformation

    when: 'we get the weather for GPS location'
    dataLayer.getWeatherForGps().subscribe(subscriber)
    subscriber.awaitTerminalEvent(1,TimeUnit.SECONDS)

    then: 'we should get our location from the location data layer and call the service with the result'
    1 * locationDataLayer.getLocation() >> Observable.just(location)
    1 * service.getWeatherInformation([
      lat:'-33.873836',
      lon:'151.24054',
      appId: WeatherDataLayer.APP_ID]
    ) >> Observable.just(weather)
  }

  def 'getWeatherForGps() throws error if location returned is null'(){

    when: 'we get the weather for GPS location'
    dataLayer.getWeatherForGps().subscribe(subscriber)

    then: 'if the location is null we should receive an error in our subscriber'
    1 * locationDataLayer.getLocation() >> Observable.just(null)
    subscriber.assertError(LocationNotFoundException)
  }

}