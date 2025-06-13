package kh.edu.rupp.ite.autumn.ui.element.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.autumn.data.model.EnrichedEventInfo
import kh.edu.rupp.ite.autumn.databinding.ItemSpecialsTodayBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * A RecyclerView adapter that can operate in infinite-loop mode (for upcoming events)
 * or finite mode (for today’s events) based on the `infinite` flag.
 */
class EventAdapter(
    private val dataList: List<EnrichedEventInfo>,
    private val infinite: Boolean = true,
    private val onClick: (EnrichedEventInfo) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun getItemCount(): Int = when {
        dataList.isEmpty() -> 0
        infinite -> Int.MAX_VALUE
        else -> dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemSpecialsTodayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding, onClick)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        // choose real index depending on infinite vs finite
        val index = if (infinite) position % dataList.size else position
        val info = dataList[index]
        holder.bind(info)
        Log.d(
            "EventAdapter",
            "Binding position=$position (realIndex=$index), date=${info.date}"
        )
    }

    inner class EventViewHolder(
        private val binding: ItemSpecialsTodayBinding,
        private val onClick: (EnrichedEventInfo) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(eventInfo: EnrichedEventInfo) {
            // parse & format date
            val inputFmt = DateTimeFormatter.ISO_LOCAL_DATE
            val outputFmt = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())
            val date = LocalDate.parse(eventInfo.date, inputFmt)
            binding.textTest.text = date.format(outputFmt)

            // load image
            Picasso.get()
                .load(eventInfo.eventInfo.thumbnail)
                .into(binding.eventImg)

            // click action
            binding.root.setOnClickListener { onClick(eventInfo) }
        }
    }
}
