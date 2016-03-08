package com.marvinslullaby.weather.data.search

import rx.Observable

class SearchTermsDataLayer(val cache: SearchTermsCache) {

  fun getSavedSearchTerms(): Observable<List<SearchTerm>> {
    return cache.getStoredSearchTerms().map {
      val list = mutableListOf<SearchTerm>(SearchTerm.GPS())
      list.addAll(it)
      list
    }
  }

  fun addSearchTerm(value:String){
    cache.addSearchTerm(value)
  }


}