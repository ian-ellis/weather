package com.marvinslullaby.weather.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by ttuo on 02/08/14.
 * Converted to Kotlin
 */
class RxBinderUtil(target: Any) {

  private val BINDER_TAG: String = javaClass.canonicalName
  private var targetTag: String by Delegates.notNull()
  private val compositeSubscription = CompositeSubscription()

  init {
    targetTag = target.javaClass.canonicalName
  }

  fun clear() {
    compositeSubscription.clear()
  }

  fun <U:Any> bindProperty(
    observable: Observable<U>,
    setter: ((value: U) -> Unit)?,
    error: ((value: Throwable) -> Unit)? = null,
    complete: (() -> Unit)? = null
  ) {
    compositeSubscription.add(subscribeSetter(targetTag, observable, setter, error, complete))
  }



  private fun <U> subscribeSetter(
    tag: String, observable: Observable<U>,
    setter: ((U) -> Unit)?,
    error: ((value: Throwable) -> Unit)? = null,
    complete: (() -> Unit)? = null
  ): Subscription {
    return observable.observeOn(AndroidSchedulers.mainThread())
      .subscribe(SetterSubscriber(tag, setter, error, complete))
  }

  private inner class SetterSubscriber<U> : Subscriber<U> {

    private var tag: String by Delegates.notNull()
    private var setter: ((value: U) -> Unit)? = null

    private var error: ((value: Throwable) -> Unit)? = null
    private var complete: (() -> Unit)? = null

    constructor(
      tag: String,
      setter: ((value: U) -> Unit)?,
      error: ((value: Throwable) -> Unit)?,
      complete: (() -> Unit)?
    ) {
      this.tag = tag
      this.setter = setter
      this.error = error
      this.complete = complete
    }

    override fun onCompleted() {
      Log.v(BINDER_TAG, tag + "." + "onCompleted")

      val c = complete ?: return
      c.let { c() }
    }

    override fun onError(e: Throwable) {
      Log.e(BINDER_TAG, tag + "." + "onError", e)

      val er = error ?: return
      er.let { er(e) }
    }

    override fun onNext(u: U) {
      val s = setter ?: return
      s.let { s(u) }
    }

  }


}


