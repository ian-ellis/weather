package com.marvinslullaby.weather.presentation

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText


class FocusEditText : EditText {


  var keyboardHideCallback:(()->Unit)? = null

  constructor (context: Context) : super(context, null) {}
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

  override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
    if(event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP){
      keyboardHideCallback?.invoke()
    }
    return super.onKeyPreIme(keyCode, event)
  }
}