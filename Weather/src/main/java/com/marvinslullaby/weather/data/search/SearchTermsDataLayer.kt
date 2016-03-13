package com.marvinslullaby.weather.data.search

import android.content.Context
import rx.Observable

open class SearchTermsDataLayer(val cache: SearchTermsCache) {

  companion object {
    fun newInstance(context: Context):SearchTermsDataLayer{
      return SearchTermsDataLayer(
        SearchTermsCache(SearchTermSqlHelper(context))
      )
    }
  }



  open fun getSavedSearchTerms(): Observable<List<SearchTerm>> {
    return cache.getAll()

  }

  fun add(searchTerm:String){
    cache.add(SearchTerm.map(searchTerm))
  }

  fun add(searchTerm:SearchTerm){
    cache.add(searchTerm)
  }


  fun delete(searchTerm:SearchTerm){
    cache.delete(searchTerm)
  }




}