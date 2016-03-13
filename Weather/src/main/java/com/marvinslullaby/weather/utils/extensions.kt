package com.marvinslullaby.weather.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun hideKeyboard(context: Context, view: View?) {
  val im = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
  if(view != null) {
    im.hideSoftInputFromWindow(view.windowToken, 0)
  }
}

