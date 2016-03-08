package com.marvinslullaby.weather.data.search

import android.content.Context
import android.content.SharedPreferences
import com.marvinslullaby.weather.utils.isNumeric
import rx.Observable
import rx.subjects.BehaviorSubject


class SearchTermsCache(context: Context) {

  protected val KEY = "searchterms"



  protected val prefs: SharedPreferences = context.getSharedPreferences("SearchTerms", Context.MODE_PRIVATE)
  protected val memoryCache:BehaviorSubject<List<String>> = BehaviorSubject.create()

  init{
    memoryCache.onNext(getCurrentSet().toList())
  }

  fun getStoredSearchTerms(): Observable<List<SearchTerm>> {

    return memoryCache.map{
      it.map {
        SearchTerm.map(it)
      }
    }
  }

  fun addSearchTerm(searchTerm: SearchTerm){
    addSearchTerm(SearchTerm.map(searchTerm))
  }

  fun addSearchTerm(searchTerm: String){
    val newTerms = getCurrentSet()
    newTerms.add(searchTerm)
    prefs.edit().putStringSet(
      KEY,
      newTerms
    ).commit()
    memoryCache.onNext(newTerms.toList())
  }

  fun removeSearchTerm(searchTerm: SearchTerm){
    val newTerms = getCurrentSet()
    newTerms.remove(SearchTerm.map(searchTerm))
    prefs.edit().putStringSet(
      KEY,
      newTerms
    ).commit()
    memoryCache.onNext(newTerms.toList())
  }

  protected fun getCurrentSet():MutableSet<String>{
    return prefs.getStringSet(KEY,mutableSetOf())
  }



}