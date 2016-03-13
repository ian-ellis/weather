package com.marvinslullaby.weather.data.search

import rx.observers.TestSubscriber
import spock.lang.Specification
import rx.Observable

class SearchTermDataLayerTest extends Specification{

  SearchTermsDataLayer dataLayer
  SearchTermsCache cache
  TestSubscriber subscriber
  def setup(){
    cache = Mock(SearchTermsCache)
    dataLayer = new SearchTermsDataLayer(cache)
    subscriber = new TestSubscriber()
  }

  def 'getAll() returns all searchTerms from cache'(){
    given: 'A cache with the following search terms'
    def cachedValues = [
      new SearchTerm.GPS(),
      new SearchTerm.City("Sydney"),
      new SearchTerm.Zip("3000")
    ]
    cache.getAll() >> Observable.just(cachedValues)

    when:'we ask for all search terms'
    dataLayer.getSavedSearchTerms().subscribe(subscriber)

    then:'we receive the search terms from the cache'
    subscriber.onNextEvents.size() == 1
    subscriber.onNextEvents[0] == cachedValues
  }

  def 'getAll() adds SearchTerm.GPS if it is not present in the database'(){
    given: 'A cache with out a GPS search'
    def cachedValues = [
      new SearchTerm.City("Sydney"),
      new SearchTerm.Zip("3000")
    ]
    cache.getAll() >> Observable.just(cachedValues)

    when:'we ask for all search terms'
    dataLayer.getSavedSearchTerms().subscribe(subscriber)

    then:'we receive the search terms with GPS added last'
    subscriber.onNextEvents.size() == 1
    ((List)subscriber.onNextEvents[0]).size() == 3
    ((List)subscriber.onNextEvents[0]).last() instanceof SearchTerm.GPS

  }

  def 'getAll() does not add SearchTerm.GPS if it is already present in the database'(){
    given: 'A cache with out a GPS search'
    def cachedValues = [
      new SearchTerm.City("Sydney"),
      new SearchTerm.GPS(),
      new SearchTerm.Zip("3000")
    ]
    cache.getAll() >> Observable.just(cachedValues)

    when:'we ask for all search terms'
    dataLayer.getSavedSearchTerms().subscribe(subscriber)

    then:'we receive the search terms with GPS in its original position'
    subscriber.onNextEvents.size() == 1
    ((List)subscriber.onNextEvents[0]).size() == 3
    ((List)subscriber.onNextEvents[0])[1] instanceof SearchTerm.GPS
  }

  def 'add() converts string to SearchTerm and adds data to cache'(){
    given:
    def searchTerm = "Sydney"
    when:
    dataLayer.add(searchTerm)
    then:
    1 * cache.add(new SearchTerm.City(searchTerm))
  }

  def 'delete() deletes data from cache'(){
    given:
    def searchTerm = "Sydney"
    when:
    dataLayer.delete(searchTerm)
    then:
    1 * cache.delete(new SearchTerm.City(searchTerm))

  }
}