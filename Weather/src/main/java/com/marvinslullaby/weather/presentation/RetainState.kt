package com.marvinslullaby.weather.presentation




import android.app.Activity
import android.app.Fragment
import android.os.Bundle

class DataFragment<T: Any> : Fragment() {
  var  data: T? = null

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    retainInstance = true
  }
}

fun <T: Any> Activity.retainState(setter:(T)->Unit, factory:()->T, fragmentId:String = "dataFragment") {

  val fm = fragmentManager

  var dataFragment: DataFragment<T>? = fm.findFragmentByTag(fragmentId) as DataFragment<T>?

  if (dataFragment == null) {
    dataFragment = DataFragment()
    fm.beginTransaction().add(dataFragment, fragmentId).commit()
    dataFragment.data = factory.invoke()
  }else if(dataFragment.data == null){
    dataFragment.data = factory.invoke()
  }

  setter(dataFragment.data!!)
}

