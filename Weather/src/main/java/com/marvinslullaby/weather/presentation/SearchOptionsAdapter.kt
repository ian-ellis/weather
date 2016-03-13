package com.marvinslullaby.weather.presentation

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.marvinslullaby.weather.R
import com.marvinslullaby.weather.data.search.SearchTerm


class SearchOptionsAdapter : RecyclerView.Adapter<SearchOptionsAdapter.SearchViewHolder>(){

  var deleteAction:((SearchTerm)->Unit)? = null
  private var data:List<SearchTerm> = listOf()

  fun update(data:List<SearchTerm>){
    this.data = data
    notifyDataSetChanged()
  }
  override fun onBindViewHolder(viewHolder: SearchViewHolder, position: Int) {
    viewHolder.bind(data[position])
  }

  override fun getItemCount(): Int {
    return data.size
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
    val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as android.view.LayoutInflater
    val v = inflater.inflate(R.layout.recycler_search_term,parent,true)
    return SearchViewHolder(v)

  }

  inner class SearchViewHolder(val view: View):RecyclerView.ViewHolder(view) {

    val icon:ImageView
    val txt: TextView
    val deleteBtn:Button

    init {
      icon = view.findViewById(R.id.img_icon) as ImageView
      txt = view.findViewById(R.id.txt_search_term) as TextView
      deleteBtn = view.findViewById(R.id.btn_delete) as Button
    }

    fun bind(searchTerm:SearchTerm){

      when(searchTerm){
        is SearchTerm.GPS->{
          icon.visibility = View.VISIBLE
          icon.setImageDrawable(view.context.getDrawable(R.drawable.ic_my_location_24dp))
          txt.text = view.context.getString(R.string.gps)
          deleteBtn.visibility = View.GONE
        }
        is SearchTerm.City ->{
          icon.visibility = View.GONE
          txt.text = searchTerm.city
          deleteBtn.visibility = View.VISIBLE
        }
        is SearchTerm.Zip ->{
          icon.visibility = View.GONE
          txt.text = searchTerm.zip
          deleteBtn.visibility = View.VISIBLE
        }
      }

      deleteBtn.setOnClickListener {
        deleteAction?.invoke(searchTerm)
      }

    }
  }
}