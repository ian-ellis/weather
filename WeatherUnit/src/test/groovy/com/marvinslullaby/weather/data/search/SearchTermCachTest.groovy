package com.marvinslullaby.weather.data.search

import rx.Observable
import rx.observers.TestSubscriber
import spock.lang.Specification

class SearchTermCacheTest extends Specification{

  TestSubscriber subscriber
  def setup(){
    subscriber = new TestSubscriber()
  }

  def 'getAllReturns all values store in the DB'(){

    given: 'a data base populated with the following values'
    def db = Mock SearchTermSqlHelper
    def allSearchTerms = [
      new SearchTerm.City("Sydney"),
      new SearchTerm.Zip("3000"),
      new SearchTerm.GPS()
    ]
    db.getAll() >> Observable.just(allSearchTerms)
    def cache = mockCache(db)

    when:'when we get all'
    cache.getAll().subscribe(subscriber)

    then: 'the values from the data base are returned'
    subscriber.onNextEvents.size() == 1
    ((List)subscriber.onNextEvents[0]) == allSearchTerms
  }

  def 'Adding a search term adds the value to database and updates all stream'(){
    given: 'A database with the following values'
    def db = Mock SearchTermSqlHelper
    def allSearchTerms = [
      new SearchTerm.City("Sydney"),
      new SearchTerm.Zip("3000"),
      new SearchTerm.GPS()
    ]
    db.getAll() >> Observable.just(allSearchTerms)

    def cache = mockCache(db)
    SearchTerm newValue = new SearchTerm.City("Melbourne")
    SearchTerm newValue2 = new SearchTerm.City("Brisbane")

    when: 'we add another value'
    cache.getAll().subscribe(subscriber)
    cache.add(newValue)

    then:'we should have received 2 events with the old ad new value'
    1 * db.add(newValue) >> Observable.just(newValue)
    subscriber.onNextEvents.size() == 2
    ((List)subscriber.onNextEvents.first()).size() == 3
    ((List)subscriber.onNextEvents.last()).size() == 4
    subscriber.onNextEvents[1][0] == newValue

    when:'we add another search term'
    cache.add(newValue2)

    then:'it is in the results'
    1 * db.add(newValue2) >> Observable.just(newValue2)
    subscriber.onNextEvents.size() == 3
    ((List)subscriber.onNextEvents.last()).size() == 5
    subscriber.onNextEvents.last()[0] == newValue2
  }

  def 'Adding a search term already used does not create duplicate in the result'(){
    given:
    def db = Mock SearchTermSqlHelper
    def allSearchTerms = [
      new SearchTerm.City("Sydney"),
      new SearchTerm.Zip("3000"),
      new SearchTerm.GPS()
    ]
    def duplicateValue = new SearchTerm.Zip("3000")
    db.getAll() >> Observable.just(allSearchTerms)

    def cache = mockCache(db)

    when:'we add a value already stored in the database'
    cache.getAll().subscribe(subscriber)
    cache.add(duplicateValue)

    then:
    1 * db.add(duplicateValue) >> Observable.just(duplicateValue)
    subscriber.onNextEvents.size() == 2
    //no new terms -> results are same length
    ((List)subscriber.onNextEvents.first()).size() == 3
    ((List)subscriber.onNextEvents.last()).size() == 3
    //added is now latest in the list
    ((List)subscriber.onNextEvents.last())[0] == allSearchTerms[1]//3000
    ((List)subscriber.onNextEvents.last())[1] == allSearchTerms[0]//Sydney
    ((List)subscriber.onNextEvents.last())[2] == allSearchTerms[2]//GPS

  }

  def 'Removing search terms updates all'(){
    given: 'A database with the following values'
    def db = Mock SearchTermSqlHelper
    def allSearchTerms = [
      new SearchTerm.City("Sydney"),
      new SearchTerm.Zip("3000"),
      new SearchTerm.GPS()
    ]
    db.getAll() >> Observable.just(allSearchTerms)
    def cache = mockCache(db)
    cache.getAll().subscribe(subscriber)
    SearchTerm valueToDelete = new SearchTerm.City("Sydney")

    when: 'we add another value'
    cache.delete(valueToDelete)

    then:'we should have received 2 events with the old ad new value'
    1 * db.delete(valueToDelete) >>Observable.just(valueToDelete)
    subscriber.onNextEvents.size() == 2
    ((List)subscriber.onNextEvents[0]).size() == 3
    ((List)subscriber.onNextEvents[1]).size() == 2
    ((SearchTerm.Zip)subscriber.onNextEvents[1][0]).zip == "3000"
  }

  def mockCache(SearchTermSqlHelper db){
    return new SearchTermsCache(db)
  }
}