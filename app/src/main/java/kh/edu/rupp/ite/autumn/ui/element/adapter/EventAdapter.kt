package kh.edu.rupp.ite.autumn.ui.element.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.autumn.data.model.EventData
import kh.edu.rupp.ite.autumn.databinding.ItemEventBinding
import kh.edu.rupp.ite.autumn.databinding.ItemFoodBinding
import kh.edu.rupp.ite.autumn.databinding.ItemSpecialsTodayBinding

class EventAdapter(
    private val onClick: (EventData) -> Unit
) : Adapter<EventViewHolder>() {

    // Dataset for the adapter
    private var data = emptyList<EventData>()

    // Update the dataset and refresh the RecyclerView
    fun setData(data: List<EventData>){
        this.data = data
        notifyDataSetChanged()
        Log.d("EventAdapter", "Data updated, size: ${data.size}")
    }

    // Inflate the layout for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSpecialsTodayBinding.inflate(layoutInflater, parent, false)
        return EventViewHolder(binding, onClick)
    }

    // Return the size of the dataset
    override fun getItemCount(): Int {
        return data.size
    }

    // Bind data to the ViewHolder for the given position
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val eventData = data[position]
        holder.bind(eventData)
        Log.d("EventAdapter", "Binding data at position: $position, Category: ${eventData.name}")
    }
}

// ViewHolder to bind category data to the UI elements
class EventViewHolder(
    private val binding: ItemSpecialsTodayBinding,
    private val onClick: (EventData) -> Unit
): ViewHolder(binding.root) {

    // Bind a single category to the UI
    fun bind(eventData: EventData) {
        Log.d("EventAdapter", "Category bound: ${eventData.name}")
        binding.textTest.text = eventData.name
        //Picasso.get().load(eventData.thumbail).into(binding.eventImg)
//        binding.root.setOnClickListener { onClick(eventData) }
        binding.root.setOnClickListener { onClick(eventData) }
        Log.d("EventAdapter", "Clicked action done")

    }
}



