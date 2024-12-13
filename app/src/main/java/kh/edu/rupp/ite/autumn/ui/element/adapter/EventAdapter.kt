package kh.edu.rupp.ite.autumn.ui.element.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.autumn.data.model.Comment
import kh.edu.rupp.ite.autumn.data.model.Test
import kh.edu.rupp.ite.autumn.databinding.ItemFoodBinding


class EventAdapter: Adapter<EventViewHolder>() {

    private var data = emptyList<Comment>()

    fun setData(data: List<Comment>){
        this.data = data
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemFoodBinding.inflate(layoutInflater, parent, false)
        return EventViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val comment = data[position]
        holder.bind(comment)
        }

    }



class EventViewHolder(private val binding: ItemFoodBinding): ViewHolder(binding.root) {

    fun bind(comment: Comment) {
        //Picasso.get().load(comment.id).into(binding.drinkName)
        binding.drinkName.text = comment.text
    }



}