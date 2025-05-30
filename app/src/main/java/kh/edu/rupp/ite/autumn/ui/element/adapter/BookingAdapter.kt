package kh.edu.rupp.ite.autumn.ui.element.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kh.edu.rupp.ite.autumn.data.model.TableDataUser
import kh.edu.rupp.ite.autumn.databinding.ItemBookingBinding

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
        fun bind(item: TableDataUser) {
            // Display date
            binding.tableID.text = item.date

            // Join tables list into a comma-separated string
            val tablesText = item.tables.joinToString(", ")
            binding.bookingDetails.text = tablesText
        }
    }
}
