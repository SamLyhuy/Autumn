package kh.edu.rupp.ite.autumn.ui.element.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kh.edu.rupp.ite.autumn.data.model.Category
import kh.edu.rupp.ite.autumn.databinding.ItemFoodBinding

class EventAdapter: Adapter<EventViewHolder>() {

    // Dataset for the adapter
    private var data = emptyList<Category>()

    // Update the dataset and refresh the RecyclerView
    fun setData(data: List<Category>){
        this.data = data
        notifyDataSetChanged()
        Log.d("EventAdapter", "Data updated, size: ${data.size}")
    }

    // Inflate the layout for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemFoodBinding.inflate(layoutInflater, parent, false)
        return EventViewHolder(binding)
    }

    // Return the size of the dataset
    override fun getItemCount(): Int {
        return data.size
    }

    // Bind data to the ViewHolder for the given position
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val category = data[position]
        holder.bind(category)
        Log.d("EventAdapter", "Binding data at position: $position, Category: ${category.name}")
    }
}

// ViewHolder to bind category data to the UI elements
class EventViewHolder(private val binding: ItemFoodBinding): ViewHolder(binding.root) {

    // Bind a single category to the UI
    fun bind(category: Category) {
        binding.drinkName.text = category.name
        Log.d("EventViewHolder", "Category bound: ${category.name}")
    }
}


//package kh.edu.rupp.ite.autumn.ui.element.adapter
//
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView.Adapter
//import androidx.recyclerview.widget.RecyclerView.ViewHolder
//import kh.edu.rupp.ite.autumn.data.model.Category
//import kh.edu.rupp.ite.autumn.databinding.ItemFoodBinding
//
//class EventAdapter: Adapter<EventViewHolder>() {
//
//    // Dataset for the adapter
//    private var data = emptyList<Category>()
//
//    // Update the dataset and refresh the RecyclerView
//    fun setData(data: List<Category>){
//        Log.d("EventAdapter", "setData() - Start, Data size: ${data.size}")
//        this.data = data
//        notifyDataSetChanged()
//        Log.d("EventAdapter", "setData() - End")
//    }
//
//    // Inflate the layout for each item
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
//        Log.d("EventAdapter", "onCreateViewHolder() - Start")
//        val layoutInflater = LayoutInflater.from(parent.context)
//        val binding = ItemFoodBinding.inflate(layoutInflater, parent, false)
//        Log.d("EventAdapter", "onCreateViewHolder() - End")
//        return EventViewHolder(binding)
//    }
//
//    // Return the size of the dataset
//    override fun getItemCount(): Int {
//        Log.d("EventAdapter", "getItemCount() - Data size: ${data.size}")
//        return data.size
//    }
//
//    // Bind data to the ViewHolder for the given position
//    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
//        Log.d("EventAdapter", "onBindViewHolder() - Start, Position: $position")
//        val category = data[position]
//        holder.bind(category)
//        Log.d("EventAdapter", "onBindViewHolder() - End, Category: ${category.name}")
//    }
//}
//
//// ViewHolder to bind category data to the UI elements
//class EventViewHolder(private val binding: ItemFoodBinding): ViewHolder(binding.root) {
//
//    // Bind a single category to the UI
//    fun bind(category: Category) {
//        Log.d("EventViewHolder", "bind() - Start, Category: ${category.name}")
//        binding.drinkName.text = category.name
//        Log.d("EventViewHolder", "bind() - End")
//    }
//}
