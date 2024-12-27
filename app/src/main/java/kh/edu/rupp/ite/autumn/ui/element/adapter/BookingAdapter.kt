package kh.edu.rupp.ite.autumn.ui.element.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kh.edu.rupp.ite.autumn.data.model.EventData
import kh.edu.rupp.ite.autumn.databinding.ItemBookingBinding

class BookingAdapter: Adapter<BookingViewHolder>(){

    private var data = emptyList<EventData>()

    fun setData(data: List<EventData>){
        this.data = data
        notifyDataSetChanged()
        Log.d("BookingAdapter", "Data updated, size: ${data.size}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemBookingBinding.inflate(layoutInflater, parent, false)
        return BookingViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = data[position]
        holder.bind(booking)
        //Log.d("BookingAdapter", "Binding data at position: $position, Category: ${booking.name}")

    }


}

class BookingViewHolder(private val binding: ItemBookingBinding): ViewHolder(binding.root) {

    // Bind a single category to the UI
    fun bind(eventData: EventData) {
//        binding.statusText.text = eventData.name
//        Log.d("EventViewHolder", "Category bound: ${eventData.name}")
    }

}


