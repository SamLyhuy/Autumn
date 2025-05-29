package kh.edu.rupp.ite.autumn.ui.element.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.autumn.data.model.EnrichedEventInfo
import kh.edu.rupp.ite.autumn.databinding.ItemEventBinding
import kh.edu.rupp.ite.autumn.databinding.ItemFoodBinding
import kh.edu.rupp.ite.autumn.databinding.ItemSpecialsTodayBinding

class EventAdapter(
    private val onClick: (EnrichedEventInfo) -> Unit
) : Adapter<EventViewHolder>() {

    // Dataset for the adapter
    private var data = emptyList<EnrichedEventInfo>()

    // Update the dataset and refresh the RecyclerView
    fun setData(data: List<EnrichedEventInfo>){
        this.data = data
        notifyDataSetChanged()
        Log.d("EventAdapter", "Data updated, size: ${data.size}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSpecialsTodayBinding.inflate(layoutInflater, parent, false)
        return EventViewHolder(binding, onClick)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val eventData = data[position]
        holder.bind(eventData)
        Log.d("EventAdapter", "Binding data at position: $position, Category: ${eventData.date}")
    }
}

class EventViewHolder(
    private val binding: ItemSpecialsTodayBinding,
    private val onClick: (EnrichedEventInfo) -> Unit
): ViewHolder(binding.root) {

    // Bind a single category to the UI
    fun bind(eventData: EnrichedEventInfo) {

        Log.d("EventAdapter", "Category bound: ${eventData.date}")
        binding.textTest.text = eventData.date
        Picasso.get().load(eventData.eventInfo.thumbnail).into(binding.eventImg)
        binding.root.setOnClickListener { onClick(eventData) }
        Log.d("EventAdapter", "Clicked action done")
    }
}



