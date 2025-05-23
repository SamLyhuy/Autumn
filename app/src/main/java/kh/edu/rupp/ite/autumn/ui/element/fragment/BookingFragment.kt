package kh.edu.rupp.ite.autumn.ui.element.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kh.edu.rupp.ite.autumn.R
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.EventData
import kh.edu.rupp.ite.autumn.data.model.State
import kh.edu.rupp.ite.autumn.databinding.ActivityBookingBinding
import kh.edu.rupp.ite.autumn.ui.element.adapter.TableAdapter
import kh.edu.rupp.ite.autumn.ui.viewmodel.TableViewModel


class BookingFragment: Fragment() {

    private val viewModel by viewModels<TableViewModel>()

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
    }

    private fun setupUi(){

    }

    private fun setupListener(){
        binding.addBookingButton.setOnClickListener{
            navigateToTableBookingFragment()
        }
    }

    private fun setupObserver(){
//        viewModel.loadingTableData().observe(viewLifecycleOwner) { state ->
//            handleState(state)
//        }
    }

    // Navigate to TableBookingFragment
    private fun navigateToTableBookingFragment() {
        val tableBookingFragment = TableBookingFragment()

        parentFragmentManager.beginTransaction()
            .replace(R.id.booking, tableBookingFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun handleState(state: ApiState<List<EventData>>) {
        when(state.state) {
            State.success -> { showBooking(state.data!!)}
            State.error -> {}
            else -> {}
        }
    }

    private fun showBooking(categories: List<EventData>) {

        // Set up the RecyclerView with a horizontal layout
        val itemBookingLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val itemBookingAdapter = TableAdapter()
        itemBookingAdapter.setData(categories) // Pass Category list to the adapter

        // Bind the RecyclerView to the adapter and layout manager
        binding.currentBooking.apply {
            adapter = itemBookingAdapter
            layoutManager = itemBookingLayoutManager
        }
    }
}