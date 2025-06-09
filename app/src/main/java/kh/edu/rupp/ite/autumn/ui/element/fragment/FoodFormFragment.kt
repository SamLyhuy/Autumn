package kh.edu.rupp.ite.autumn.ui.element.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kh.edu.rupp.ite.autumn.R
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.PostFoodRequest
import kh.edu.rupp.ite.autumn.data.model.State
import kh.edu.rupp.ite.autumn.databinding.ActivityPostFoodBinding
import kh.edu.rupp.ite.autumn.ui.viewmodel.FoodViewModel
import kh.edu.rupp.ite.visitme.global.AppEncryptedPref

class FoodFormFragment : BaseFragment() {

    private lateinit var binding: ActivityPostFoodBinding
    private val viewModel by viewModels<FoodViewModel>()

    // Holds the list of ingredients typed by the user
    private val ingredientsList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityPostFoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Clear any leftover chips if returning to this fragment
        ingredientsList.clear()
        binding.chipGroupIngredients.removeAllViews()

        setUpListener()
        setObserver()
    }

    private fun setUpListener() {
        // Back arrow: pop back stack
        binding.btnBack.setOnClickListener {
            navigateBack()
        }

        // “Save” button: collect all fields and submit
        binding.btnSave.setOnClickListener {
            onSaveFood()
        }

        // Image placeholder: not implemented here
        binding.imageContainer.setOnClickListener {
            Toast.makeText(requireContext(), "Image picker not implemented", Toast.LENGTH_SHORT).show()
        }

        // “Add” next to the ingredient input
        binding.btnAddIngredient.setOnClickListener {
            val raw = binding.etIngredientInput.text.toString().trim()
            if (raw.isNotEmpty() && raw !in ingredientsList) {
                ingredientsList.add(raw)
                addChipToGroup(raw, binding.chipGroupIngredients)
                binding.etIngredientInput.text?.clear()
            } else if (raw.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter an ingredient", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Ingredient already added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateBack() {
        parentFragmentManager.popBackStack()
    }

    private fun addChipToGroup(text: String, chipGroup: ChipGroup) {
        val chip = Chip(requireContext()).apply {
            this.text = text
            isCloseIconVisible = true
            setChipBackgroundColorResource(R.color.light_gray)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            closeIconTint = ContextCompat.getColorStateList(requireContext(), R.color.black)
            setOnCloseIconClickListener {
                chipGroup.removeView(this)
                ingredientsList.remove(text)
            }
        }
        chipGroup.addView(chip)
    }

    private fun onSaveFood() {
        val name          = binding.etName.text.toString().trim()
        val priceText     = binding.etPrice.text.toString().trim()
        val descriptionText = binding.etDescription.text.toString().trim()
        val type          = binding.etType.text.toString().trim()
        val cuisineText   = binding.etCuisine.text.toString().trim()
        val prepTimeText  = binding.etPreparationTime.text.toString().trim()
        val thumbnail     = binding.FoodImage.text.toString().trim()

        // Validate required fields
        if (name.isEmpty() || priceText.isEmpty() || type.isEmpty() || thumbnail.isEmpty()) {
            Toast.makeText(requireContext(),
                "Name, Price, Type, and Thumbnail are required.",
                Toast.LENGTH_SHORT).show()
            return
        }

        // Convert price to Double
        val price = priceText.toDoubleOrNull()
        if (price == null) {
            Toast.makeText(requireContext(), "Invalid price format.", Toast.LENGTH_SHORT).show()
            return
        }

        // Build the request, passing in the list of ingredients if any
        val postRequest = PostFoodRequest(
            name = name,
            price = price,
            type = type,
            thumbnail = thumbnail,
            isdeleted = false,
            description = descriptionText.ifBlank { null },
            ingredients = if (ingredientsList.isNotEmpty()) ingredientsList.toList() else null,
            cuisine = cuisineText.ifBlank { null },
            spiciness = null,
            preparationTime = prepTimeText.ifBlank { null }
        )

        Log.d("FoodFormFragment", "Submitting: $postRequest")

        // Retrieve token
        val token = AppEncryptedPref.get().getToken(requireContext())
        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Authorization token is missing", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.postFood(token, postRequest)
    }

    private fun setObserver() {
        viewModel.postFood.observe(viewLifecycleOwner) { state ->
            when (state.state) {
                State.loading -> {
                    Log.d("FoodFormFragment", "Loading...")
                    showLoading()
                }
                State.success -> {
                    Log.d("FoodFormFragment", "Success: ${state.data}")
                    hideLoading()
                    navigateBack()
                }
                State.error -> {
                    Log.e("FoodFormFragment", "Error: ${state.message}")
                    hideLoading()
                }
                else -> {
                    Log.w("FoodFormFragment", "Unhandled state: ${state.state}")
                }
            }
            observeServerResponse(viewModel.uiMessage)
        }
    }
}
