package kh.edu.rupp.ite.autumn.ui.element.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kh.edu.rupp.ite.autumn.data.model.TableDataUser
import kh.edu.rupp.ite.autumn.databinding.ItemBookingBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Adapter to display a list of user booking entries (date + tables) in a RecyclerView.
 */
class BookingAdapter : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    private var data = emptyList<TableDataUser>()

    /**
     * Update the adapter's data and refresh the view.
     */
    fun setData(data: List<TableDataUser>) {
        this.data = data
        notifyDataSetChanged()
        Log.d("BookingAdapter", "Data updated, size: ${data.size}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookingViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class BookingViewHolder(
        private val binding: ItemBookingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Bind a single booking entry to the layout.
         */
        companion object {
            // one‐time formatter instances:
            @RequiresApi(Build.VERSION_CODES.O)
            private val inputFormatter  = DateTimeFormatter.ISO_LOCAL_DATE
            @RequiresApi(Build.VERSION_CODES.O)
            private val outputFormatter = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: TableDataUser) {
            // 1) parse the raw "YYYY-MM-DD"
            val date = LocalDate.parse(item.date, inputFormatter)

            // 2) format to "6 Jun" (day number + short month name)
            binding.tableID.text = date.format(outputFormatter)

            // Join tables list into a comma-separated string
            val tablesText = item.tables.joinToString(", ")
            binding.bookingDetails.text = tablesText
        }
    }
}
