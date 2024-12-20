package kh.edu.rupp.ite.autumn.ui.element.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.Category
import kh.edu.rupp.ite.autumn.data.model.State
import kh.edu.rupp.ite.autumn.databinding.ActivityHomeBinding
import kh.edu.rupp.ite.autumn.ui.element.adapter.EventAdapter
import kh.edu.rupp.ite.autumn.ui.viewmodel.HomeViewModel

class HomeFragment: BaseFragment() {

    // Instantiate the ViewModel using the viewModels delegate
    private val viewModel by viewModels<HomeViewModel>()

    // Binding object for accessing views in the layout
    private lateinit var binding: ActivityHomeBinding

    // Inflate the layout for the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("HomeFragment", "onCreateView called")
        binding = ActivityHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    // Initialize UI and observe ViewModel data after the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("HomeFragment", "onViewCreated called")

        // Observe the LiveData from the ViewModel
        viewModel.homeData.observe(viewLifecycleOwner) { state ->
            handleState(state)
        }

        // Load data from the ViewModel
        viewModel.loadingHomeData()
    }

    // Handle different states of the API call (loading, success, error)
    private fun handleState(state: ApiState<List<Category>>) {
        when (state.state) {
            State.loading -> {
                // Show a loading indicator
                showLoading()
                Log.d("HomeFragment", "State: Loading")
            }
            State.success -> {
                // Hide the loading indicator and show the data
                hideLoading()
//                val data = state.data
//                Log.d("HomeFragment", "State: Success, Data: $data")
//                if (data != null) {
//                    showHomeData(data)
//                }
                showHomeData(state.data!!)
            }
            State.error -> {
                // Hide the loading indicator and show an error alert
                hideLoading()
                Log.e("HomeFragment", "State: Error, Message: ${state.message}")
                showAlert("Error", state.message ?: "Unexpected Error")
            }
            else -> {
                Log.w("HomeFragment", "Unhandled state: ${state.state}")
            }
        }
    }

    // Display the list of categories using a RecyclerView
    private fun showHomeData(categories: List<Category>) {
        // Set up the RecyclerView with a horizontal layout
        val itemFoodLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val itemFoodAdapter = EventAdapter()
        itemFoodAdapter.setData(categories) // Pass Category list to the adapter

        // Bind the RecyclerView to the adapter and layout manager
        binding.foodListRecycler.apply {
            adapter = itemFoodAdapter
            layoutManager = itemFoodLayoutManager
        }
    }
}



//package kh.edu.rupp.ite.autumn.ui.element.fragment
//
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.viewModels
//import androidx.recyclerview.widget.LinearLayoutManager
//import kh.edu.rupp.ite.autumn.data.model.ApiState
//import kh.edu.rupp.ite.autumn.data.model.Category
//import kh.edu.rupp.ite.autumn.data.model.State
//import kh.edu.rupp.ite.autumn.databinding.ActivityHomeBinding
//import kh.edu.rupp.ite.autumn.ui.element.adapter.EventAdapter
//import kh.edu.rupp.ite.autumn.ui.viewmodel.HomeViewModel
//
//class HomeFragment: BaseFragment() {
//
//    // Instantiate the ViewModel using the viewModels delegate
//    private val viewModel by viewModels<HomeViewModel>()
//
//    // Binding object for accessing views in the layout
//    private lateinit var binding: ActivityHomeBinding
//
//    // Inflate the layout for the fragment
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        Log.d("HomeFragment", "onCreateView() - Start")
//        binding = ActivityHomeBinding.inflate(layoutInflater, container, false)
//        Log.d("HomeFragment", "onCreateView() - End")
//        return binding.root
//    }
//
//    // Initialize UI and observe ViewModel data after the view is created
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        Log.d("HomeFragment", "onViewCreated() - Start")
//
//        // Observe the LiveData from the ViewModel
//        viewModel.homeData.observe(viewLifecycleOwner) { state ->
//            Log.d("HomeFragment", "onViewCreated() - Observing LiveData state: ${state.state}")
//            handleState(state)
//        }
//
//        // Load data from the ViewModel
//        viewModel.loadingHomeData()
//        Log.d("HomeFragment", "onViewCreated() - End")
//    }
//
//    // Handle different states of the API call (loading, success, error)
//    private fun handleState(state: ApiState<List<Category>>) {
//        Log.d("HomeFragment", "handleState() - Start")
//        when (state.state) {
//            State.loading -> {
//                // Show a loading indicator
//                showLoading()
//                Log.d("HomeFragment", "State: Loading")
//            }
//            State.success -> {
//                // Hide the loading indicator and show the data
//                hideLoading()
//                val data = state.data
//                Log.d("HomeFragment", "State: Success, Data: $data")
//                if (data != null) {
//                    showHomeData(data)
//                }
//            }
//            State.error -> {
//                // Hide the loading indicator and show an error alert
//                hideLoading()
//                Log.e("HomeFragment", "State: Error, Message: ${state.message}")
//                showAlert("Error", state.message ?: "Unexpected Error")
//            }
//            else -> {
//                Log.w("HomeFragment", "Unhandled state: ${state.state}")
//            }
//        }
//        Log.d("HomeFragment", "handleState() - End")
//    }
//
//    // Display the list of categories using a RecyclerView
//    private fun showHomeData(categories: List<Category>) {
//        Log.d("HomeFragment", "showHomeData() - Start")
//        // Set up the RecyclerView with a horizontal layout
//        val itemFoodLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        val itemFoodAdapter = EventAdapter()
//        itemFoodAdapter.setData(categories) // Pass Category list to the adapter
//
//        // Bind the RecyclerView to the adapter and layout manager
//        binding.foodListRecycler.apply {
//            adapter = itemFoodAdapter
//            layoutManager = itemFoodLayoutManager
//        }
//        Log.d("HomeFragment", "showHomeData() - End")
//    }
//}