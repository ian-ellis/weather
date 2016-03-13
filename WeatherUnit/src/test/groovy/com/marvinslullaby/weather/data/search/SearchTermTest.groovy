package com.marvinslullaby.weather.data.search

import spock.lang.Specification
import spock.lang.Unroll

class SearchTermTest extends Specification {

  @Unroll
  def "map() maps #value string to correct search term type"() {
    when:
    def result = SearchTerm.map(value)
    then:
    result == expected
    where:
    value                | expected
    "Sydney"             | new SearchTerm.City("Sydney")
    "asdasd"             | new SearchTerm.City("asdasd")
    "45a"                | new SearchTerm.City("45a")
    "45"                 | new SearchTerm.Zip("45")
    "45678"              | new SearchTerm.Zip("45678")
    SearchTerm.GPS_VALUE | new SearchTerm.GPS()
  }
}