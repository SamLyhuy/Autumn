package kh.edu.rupp.ite.autumn.ui.element.fragment

import android.os.Binder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.Category
import kh.edu.rupp.ite.autumn.data.model.State
import kh.edu.rupp.ite.autumn.databinding.ActivityBookingBinding
import kh.edu.rupp.ite.autumn.databinding.ActivityHomeBinding
import kh.edu.rupp.ite.autumn.databinding.ItemFoodBinding
import kh.edu.rupp.ite.autumn.ui.element.adapter.BookingAdapter
import kh.edu.rupp.ite.autumn.ui.element.adapter.EventAdapter
import kh.edu.rupp.ite.autumn.ui.viewmodel.BookingViewModel


class BookingFragment: Fragment() {

    private val viewModel by viewModels<BookingViewModel>()

    private lateinit var binding: ActivityBookingBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("BookingFragment", "onCreateView called")
        binding = ActivityBookingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
        setupListener()
        setupObserver()
        viewModel.loadBooking()
    }

    private fun setupUi(){

    }

    private fun setupListener(){

    }

    private fun setupObserver(){
        viewModel.bookingData.observe(viewLifecycleOwner) { state ->
            handleState(state)
        }
    }

    private fun handleState(state: ApiState<List<Category>>) {
        when(state.state) {
            State.success -> { showBooking(state.data!!)}
            State.error -> {}
            else -> {}
        }
    }

    private fun showBooking(categories: List<Category>) {
        // Set up the RecyclerView with a horizontal layout
        val itemBookingLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val itemBookingAdapter = BookingAdapter()
        itemBookingAdapter.setData(categories) // Pass Category list to the adapter

        // Bind the RecyclerView to the adapter and layout manager
        binding.currentBooking.apply {
            adapter = itemBookingAdapter
            layoutManager = itemBookingLayoutManager
        }
    }
}