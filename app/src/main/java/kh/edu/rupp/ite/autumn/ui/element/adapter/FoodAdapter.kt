// app/src/main/java/kh/edu/rupp/ite/autumn/ui/element/adapter/FoodAdapter.kt
package kh.edu.rupp.ite.autumn.ui.element.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.autumn.data.model.FoodData
import kh.edu.rupp.ite.autumn.databinding.DialogFoodDetailBinding
import kh.edu.rupp.ite.autumn.databinding.ItemFoodBinding

class FoodAdapter : RecyclerView.Adapter<FoodViewHolder>() {

    private var data = emptyList<FoodData>()

    /** Call this from your Fragment/Activity to update the list */
    fun setData(data: List<FoodData>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size
}

/** Mirrors your TableAdapter → BookingViewHolder pattern */
class FoodViewHolder(
    private val binding: ItemFoodBinding
) : RecyclerView.ViewHolder(binding.root) {

    // keep a reference to the currently–bound item
    private lateinit var foodItem: FoodData

    init {
        // wire up the click on the entire row
        binding.root.setOnClickListener {
            showDetailDialog()
        }
    }

    /** Binds a single FoodData to the row UI */
    fun bind(food: FoodData) {
        foodItem = food
        binding.productName.text  = food.name
        binding.productPrice.text = "$${"%.2f".format(food.price)}"

        Picasso.get()
            .load(food.thumbnail)
            .into(binding.productImage)
    }

    /** Inflates & shows the same dialog_food_detail.xml you already created */
    private fun showDetailDialog() {
        val ctx = binding.root.context
        val dlgBind = DialogFoodDetailBinding
            .inflate(LayoutInflater.from(ctx))

        // populate the dialog’s views
        Picasso.get()
            .load(foodItem.thumbnail)
            .into(dlgBind.ivFoodDetailImage)

        dlgBind.tvFoodDetailName.text  = foodItem.name
        dlgBind.tvFoodDetailType.text  = "Type: ${foodItem.type}"
        dlgBind.tvFoodDetailPrice.text = "Price: $${"%.2f".format(foodItem.price)}"

        // build & show
        val dialog = AlertDialog.Builder(ctx)
            .setView(dlgBind.root)
            .create()

        dlgBind.btnFoodDetailClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}
