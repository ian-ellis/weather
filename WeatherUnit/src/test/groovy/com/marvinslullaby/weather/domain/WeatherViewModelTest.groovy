package com.marvinslullaby.weather.domain
import android.content.Context
import android.content.res.Resources
import com.marvinslullaby.weather.R
import com.marvinslullaby.weather.data.search.SearchTerm
import com.marvinslullaby.weather.data.search.SearchTermsDataLayer
import com.marvinslullaby.weather.data.weather.*
import rx.Observable
import rx.observers.TestSubscriber
import rx.subjects.BehaviorSubject
import spock.lang.Specification
import spock.lang.Unroll

class WeatherViewModelTest extends Specification {

  WeatherViewModel viewModel
  WeatherDataLayer weatherDataLayer
  SearchTermsDataLayer searchTermsDataLayer
  TestSubscriber subscriber
  BehaviorSubject<SearchTerm> allSearchTerms
  Context context

  def setup() {
    context = Mock (Context){
      it.getResources() >> Mock(Resources){
        it.getString(R.string.temp_celcius, _) >> {args->
          "${args[1][0]} &#8451;"
        }
        it.getString(R.string.humidity, _) >> {args ->
          "${args[1][0]} &#37;"
        }
      }
    }
    subscriber = new TestSubscriber()

    allSearchTerms = BehaviorSubject.create()
    weatherDataLayer = Mock WeatherDataLayer
    searchTermsDataLayer = Mock SearchTermsDataLayer
    searchTermsDataLayer.getSavedSearchTerms() >> allSearchTerms.asObservable()
    viewModel = new WeatherViewModel(context, weatherDataLayer, searchTermsDataLayer)
  }

  def 'getPreviousSearchTerms() - returns search from dataLayer with GPS added or moved to start'() {
    given:
    def firstTerms = [new SearchTerm.City("Sydney"), new SearchTerm.Zip("4000")]
    def secondTerms = [new SearchTerm.City("Brisbane"),new SearchTerm.GPS(), new SearchTerm.City("Sydney"), new SearchTerm.Zip("4000")]

    //mock out weather data layer to never return, not of concern for this test
    mockNonCompletingWeather()

    when:
    viewModel.go()
    viewModel.getPreviousSearchTerms().subscribe(subscriber)
    allSearchTerms.onNext(firstTerms)

    then:
    subscriber.onNextEvents.size() == 1
    subscriber.onNextEvents[0] == [new SearchTerm.GPS(), new SearchTerm.City("Sydney"), new SearchTerm.Zip("4000")]

    when:
    allSearchTerms.onNext(secondTerms)

    then:
    subscriber.onNextEvents.size() == 2
    subscriber.onNextEvents[1] == [new SearchTerm.GPS(), new SearchTerm.City("Brisbane"), new SearchTerm.City("Sydney"), new SearchTerm.Zip("4000")]

  }

  @Unroll('getSearchTerms() - #terms should be ordered as #expectedOrder')
  def 'getSearchTerms() - are ordered with GPS first'() {

    given:
    //mock out weather data layer to never return, not of concern for this test
    mockNonCompletingWeather()

    when:
    viewModel.go()
    allSearchTerms.onNext(terms)
    viewModel.getPreviousSearchTerms().subscribe(subscriber)

    then:
    subscriber.onNextEvents.size() == 1
    def result = ((List)subscriber.onNextEvents[0])
    result.size() == expectedOrder.size()
    expectedOrder[0] == result[0]
    expectedOrder[1] == result[1]
    expectedOrder[2] == result[2]

    where:
    terms                                                                               | expectedOrder
    [new SearchTerm.City("Sydney"), new SearchTerm.Zip("4000"), new SearchTerm.GPS()]   | [new SearchTerm.GPS(), new SearchTerm.City("Sydney"), new SearchTerm.Zip("4000")]
    [new SearchTerm.City("Sydney"), new SearchTerm.GPS(), new SearchTerm.Zip("4000")]   | [new SearchTerm.GPS(), new SearchTerm.City("Sydney"), new SearchTerm.Zip("4000")]
    [new SearchTerm.Zip("4000"), new SearchTerm.GPS(), new SearchTerm.City("Sydney")]   | [new SearchTerm.GPS(), new SearchTerm.Zip("4000"), new SearchTerm.City("Sydney")]
    [new SearchTerm.GPS(), new SearchTerm.City("Sydney"), new SearchTerm.Zip("4000")]   | [new SearchTerm.GPS(), new SearchTerm.City("Sydney"), new SearchTerm.Zip("4000")]
    [new SearchTerm.GPS(), new SearchTerm.Zip("4000"), new SearchTerm.City("Sydney")]   | [new SearchTerm.GPS(), new SearchTerm.Zip("4000"), new SearchTerm.City("Sydney")]

  }

  def 'getCurrentWeather() - returns LoadingWeather when new search terms received'() {
    given:
    def firstTerms = [new SearchTerm.GPS(), new SearchTerm.City("Sydney"), new SearchTerm.Zip("4000")]
    allSearchTerms.onNext(firstTerms)
    //mock out weather data layer to never return, not of concern for this test
    mockNonCompletingWeather()
    when:
    viewModel.go()
    viewModel.getCurrentWeather().subscribe(subscriber)
    then:
    subscriber.onNextEvents.size() == 1
    subscriber.onNextEvents[0] instanceof WeatherViewModel.Weather.LoadingWeather
  }

  def 'getCurrentWeather() - returns NoWeather when no previous search terms are saved'() {
    given: 'search terms have only one value (data layer adds GPS to every list)'
    def firstTerms = []
    allSearchTerms.onNext(firstTerms)
    //mock out weather data layer to never return, not of concern for this test
    mockNonCompletingWeather()
    when:
    viewModel.go()
    viewModel.getCurrentWeather().subscribe(subscriber)

    then:
    subscriber.onNextEvents.size() == 1
    subscriber.onNextEvents[0] instanceof WeatherViewModel.Weather.NoWeather
  }

  def 'getCurrentWeather() - returns ErrorLoadingWeather when there is an error getting weather info'() {
    given:
    def cityName = "Sydney"
    def firstTerms = [new SearchTerm.City(cityName),new SearchTerm.GPS()]
    allSearchTerms.onNext(firstTerms)
    weatherDataLayer.getWeatherForCity(cityName) >> Observable.error(new Error("Oops something went wrong"))
    when:
    viewModel.go()
    viewModel.getCurrentWeather().subscribe(subscriber)

    then:
    subscriber.onNextEvents.size() == 1
    subscriber.onNextEvents[0] instanceof WeatherViewModel.Weather.ErrorLoadingWeather
  }

  def 'getCurrentWeather() - can recover from an error'(){

    given:
    def weather = mockWeatherInfo()
    def cityName = "Sydney"
    def secondCityName = "Melbourne"
    def firstTerms = [new SearchTerm.City(cityName), new SearchTerm.GPS()]
    def secondTerms = [new SearchTerm.City(secondCityName), new SearchTerm.City(cityName), new SearchTerm.GPS()]
    allSearchTerms.onNext(firstTerms)
    weatherDataLayer.getWeatherForCity(cityName) >> Observable.error(new Error("Oops something went wrong"))
    weatherDataLayer.getWeatherForCity(secondCityName) >> Observable.just(weather)

    when:
    viewModel.go()
    viewModel.getCurrentWeather().subscribe(subscriber)

    then:
    subscriber.onNextEvents.size() == 1
    subscriber.onNextEvents[0] instanceof WeatherViewModel.Weather.ErrorLoadingWeather

    when:
    allSearchTerms.onNext(secondTerms)

    then:
    subscriber.onNextEvents.size() == 3
    subscriber.onNextEvents[1] instanceof WeatherViewModel.Weather.LoadingWeather
    subscriber.onNextEvents[2] instanceof WeatherViewModel.Weather.WeatherDetails


  }

  @Unroll('getCurrentWeather() - calls data layers #expectedCall when searchTerms are #searchTerms')
  def 'getCurrentWeather() - return WeatherDetails when there is a last search term'() {
    given:
    def weather = mockWeatherInfo()
    allSearchTerms.onNext(searchTerms)
    int gpsCalls = expectedCall.get('gps')
    int zipCalls = expectedCall.get('zip')
    int cityCalls = expectedCall.get('city')

    when:
    viewModel.go()
    viewModel.getCurrentWeather().subscribe(subscriber)

    then:
    cityCalls *  weatherDataLayer.getWeatherForCity(_) >> Observable.just(weather)
    zipCalls *  weatherDataLayer.getWeatherForZip(_) >> Observable.just(weather)
    gpsCalls *  weatherDataLayer.getWeatherForGps() >> Observable.just(weather)

    subscriber.onNextEvents.size() == 1
    subscriber.onNextEvents[0] instanceof WeatherViewModel.Weather.WeatherDetails

    where:
    searchTerms << [
      [new SearchTerm.GPS(), new SearchTerm.City("Sydney"), new SearchTerm.Zip("3000")],
      [new SearchTerm.Zip("3000"), new SearchTerm.GPS(), new SearchTerm.City("Sydney")],
      [new SearchTerm.City("Sydney"),new SearchTerm.GPS(), new SearchTerm.Zip("3000")]
    ]

    expectedCall << [
      [gps:1,city:0,zip:0],
      [gps:0,city:0,zip:1],
      [gps:0,city:1,zip:0]
    ]
  }




  def 'mapInfoToDetails() - maps weather info to details'(){
    given:
    def info = mockWeatherInfo()
    when:
    WeatherViewModel.Weather.WeatherDetails result = viewModel.mapInfoToDetails(info)
    then:
    result.locationName == info.name
    result.description == info.weather[0].description
    result.icon == R.drawable.ic_clouds_heavy
    result.temperatureMax == "29.0 &#8451;"
    result.temperatureMin == "21.0 &#8451;"
    result.sunrise == "19:48"
    result.sunset == "12:15"
  }

  def mockNonCompletingWeather() {
    weatherDataLayer.getWeatherForZip(_) >> Observable.never()
    weatherDataLayer.getWeatherForCity(_) >> Observable.never()
    weatherDataLayer.getWeatherForGps() >> Observable.never()
  }

  WeatherInformation mockWeatherInfo() {

    def weather = new WeatherInformation()
    weather.sys = new WeatherSystem()
    weather.sys.sunrise = 1457843618771
    weather.sys.sunset = 1457643618771
    weather.coords = new WeatherCoords()
    weather.weather = [new WeatherData()]
    weather.weather[0].description = "broken clouds"
    weather.weather[0].icon = "04d"
    weather.main = new WeatherMain()
    weather.main.maxTemp = 29.0d
    weather.main.minTemp = 21.0d
    weather.main.humidity = 90
    weather.base = "base"
    weather.name = "weather name"
    return weather

  }
}