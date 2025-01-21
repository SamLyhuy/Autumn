package kh.edu.rupp.ite.autumn.ui.element.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.autumn.R
import kh.edu.rupp.ite.autumn.data.model.EnrichedEventInfo
import kh.edu.rupp.ite.autumn.data.model.EventData
import kh.edu.rupp.ite.autumn.data.model.EventInfo
import kh.edu.rupp.ite.autumn.databinding.ItemEventBinding
import kh.edu.rupp.ite.autumn.databinding.ItemSpecialsTodayBinding

class SelectedEventAdapter(
    private val onClick: (EventInfo) -> Unit
): Adapter<EventSelectedViewHolder>() {

    private var data = emptyList<EventInfo>()

    fun setData(data: List<EventInfo>) {
        this.data = data
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventSelectedViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemEventBinding.inflate(layoutInflater, parent, false)
        return EventSelectedViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: EventSelectedViewHolder, position: Int) {
        val eventInfo = data[position]
        holder.bind(eventInfo)
    }

    override fun getItemCount(): Int {
        return data.size
    }


}

class EventSelectedViewHolder(
    private val binding: ItemEventBinding,
    private val onClick: (EventInfo) -> Unit

): ViewHolder(binding.root){

    fun bind(eventInfo: EventInfo) {

        binding.eventTitle.text = eventInfo.name
        Picasso.get().load(eventInfo.thumbnail).into(binding.eventImage)
        binding.eventTime.text = eventInfo.time
        binding.eventDescription.text = eventInfo.description
        binding.root.setOnClickListener{ onClick(eventInfo)}
        Log.d("SelectedEventAdapter", "Clicked action done")

    }
}