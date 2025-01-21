package kh.edu.rupp.ite.autumn.ui.element.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.autumn.data.model.EventInfo
import kh.edu.rupp.ite.autumn.data.model.FoodData
import kh.edu.rupp.ite.autumn.databinding.ItemEventBinding
import kh.edu.rupp.ite.autumn.databinding.ItemFoodBinding

class FoodAdapter(): RecyclerView.Adapter<FoodViewHolder>() {

    private var data = emptyList<FoodData>()

    fun setData(data: List<FoodData>) {
        this.data = data
        notifyDataSetChanged()  // Notify the adapter that the data has changed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)

    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = data[position]
        holder.bind(food)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}


class FoodViewHolder(private val binding: ItemFoodBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(foodData: FoodData) {
        Log.d("FoodViewHolder", "Binding food: ${foodData.name}, price: ${foodData.price}")
        binding.productName.text = foodData.name
        Picasso.get().load(foodData.thumbnail).into(binding.productImage)
        binding.productPrice.text = "$${foodData.price}"
    }


}
