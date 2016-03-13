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
    return cache.getAll().map {
      val list = mutableListOf<SearchTerm>()
      list.addAll(it)
      if(!list.contains(SearchTerm.GPS())){
        list.add(SearchTerm.GPS())
      }
      list.distinct()
    }
  }

  fun add(searchTerm:String){
    cache.add(SearchTerm.map(searchTerm))
  }

  fun delete(searchTerm:String){
    cache.delete(SearchTerm.map(searchTerm))
  }




}