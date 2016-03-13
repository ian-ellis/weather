package com.marvinslullaby.weather.data.search

import rx.Observable
import rx.subjects.BehaviorSubject
import rx.subscriptions.CompositeSubscription


open class SearchTermsCache(val db: SearchTermSqlHelper) {

  protected val KEY = "searchterms"

  protected val all: BehaviorSubject<List<SearchTerm>> = BehaviorSubject.create()
  protected val added: BehaviorSubject<SearchTerm> = BehaviorSubject.create()
  protected val deleted: BehaviorSubject<SearchTerm> = BehaviorSubject.create()


  protected val subscriptions = CompositeSubscription()

  init {
    subscriptions.add(
      db.getAll().subscribe {
        all.onNext(it)
      }
    )

    subscriptions.add(
      added.flatMap { added ->
        all.take(1).map { currentAll ->
          val newList: MutableList<SearchTerm> = mutableListOf()
          newList.add(added)
          newList.addAll(currentAll)
          newList.distinct()
        }
      }.subscribe{
        all.onNext(it)
      }
    )
    subscriptions.add(
      deleted.flatMap { deleted ->
        all.take(1).map { currentAll ->
          val newList: MutableList<SearchTerm> = mutableListOf()
          newList.addAll(all.value)
          newList.remove(deleted)
          newList.distinct()
        }
      }.subscribe {
        all.onNext(it)
      }
    )
  }

  open fun getAll(): Observable<List<SearchTerm>> = all.asObservable()

  open fun add(searchTerm: SearchTerm) {
    db.add(searchTerm).subscribe {
      added.onNext(it)
    }
  }

  open fun delete(searchTerm: SearchTerm) {
    db.delete(searchTerm).subscribe {
      deleted.onNext(it)
    }
  }

}