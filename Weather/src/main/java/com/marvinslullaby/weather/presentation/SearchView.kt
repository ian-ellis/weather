package com.marvinslullaby.weather.presentation

import android.content.Context
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RelativeLayout
import butterknife.bindView
import com.marvinslullaby.weather.R
import android.view.View.OnFocusChangeListener
import android.widget.LinearLayout
import com.marvinslullaby.weather.data.search.SearchTerm


class SearchView : LinearLayout {

  var submitHandler: ((String) -> Unit)? = null
  var searchingHandler: ((Boolean) -> Unit)? = null

  var selectHandler:((SearchTerm)->Unit)? = null
    set(value){
      adapter.selectedAction = {
        editText.setText("")
        this.requestFocus()
        value?.invoke(it)
      }
    }
  var deleteHandler:((SearchTerm)->Unit)? = null
    set(value){
      adapter.deleteAction = value
    }

  private val editText: FocusEditText by bindView(R.id.edt_search)
  private val searchOptions: RecyclerView by bindView(R.id.recycler_search_options)

  private val adapter = SearchOptionsAdapter()

  constructor (context: Context) : super(context, null) {}
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

  init {
    this.isFocusable = true
    this.isFocusableInTouchMode = true
    this.requestFocus()
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    inflater.inflate(R.layout.layout_search_view, this)

  }

  override fun onFinishInflate() {
    super.onFinishInflate()
    this.requestFocus()
    editText.setOnEditorActionListener { textView, actionId, keyEvent ->
      if (actionId == EditorInfo.IME_ACTION_DONE) {
        submitHandler?.invoke(textView.text.toString())
        textView.text = ""
        this.requestFocus()
        true
      } else {
        false
      }
    }

    editText.onFocusChangeListener = View.OnFocusChangeListener { p0, hasFocus ->

      searchingHandler?.invoke(hasFocus)

      searchOptions.visibility = if (hasFocus) {
        View.VISIBLE
      } else {
        View.GONE
      }
      this.invalidate()
      this.requestLayout()
    }

    editText.keyboardHideCallback = {
      this.requestFocus()
    }


    searchOptions.adapter = adapter
    searchOptions.layoutManager = LinearLayoutManager(context)
  }

  fun update(data:List<SearchTerm>){
    adapter.update(data)
  }


}