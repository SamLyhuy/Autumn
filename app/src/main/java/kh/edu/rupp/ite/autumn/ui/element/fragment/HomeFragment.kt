package kh.edu.rupp.ite.autumn.ui.element.fragment

import EventDetailFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kh.edu.rupp.ite.autumn.R
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.EventData
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

        setupUi()
        setupListener()
        setupObserver()

        viewModel.loadingHomeData()

    }

    private fun setupObserver(){
        viewModel.homeData.observe(viewLifecycleOwner) { state ->
            handleState(state)
        }
    }

    private fun setupListener() {
    }

    private fun setupUi() {
    }

    // Handle different states of the API call (loading, success, error)
    private fun handleState(state: ApiState<List<EventData>>) {
        when (state.state) {
            State.loading -> {
                // Show a loading indicator
                showLoading()
                Log.d("HomeFragment", "State: Loading")
            }
            State.success -> {
                // Hide the loading indicator and show the data
                hideLoading()

                Log.d("HomeFragment", "State: Success, Data: ${state.data}")

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
    private fun showHomeData(eventData: List<EventData>) {
        // Set up the RecyclerView with a horizontal layout

        val itemEventLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

//      val itemEventAdapter = EventAdapter()
        val itemEventAdapter = EventAdapter { event ->
            openEventDetail(event) // When an item is clicked, open the detail view
        }

        Log.d("HomeFragment", "State: 1")

        itemEventAdapter.setData(eventData) // Pass Category list to the adapter

        Log.d("HomeFragment", "State: 2")
        // Bind the RecyclerView to the adapter and layout manager
        binding.specialsToday.apply {
            adapter = itemEventAdapter
            layoutManager = itemEventLayoutManager
        }
    }

    private fun openEventDetail(eventData: EventData) {
        Log.d("HomeFragment", "State: 3")
        // Pass the clicked event data to the EventDetailFragment
        val bundle = Bundle().apply {
            putParcelable("event_data", eventData) // Ensure EventData implements Parcelable
        }
        val fragment = EventDetailFragment().apply {
            arguments = bundle
        }
        Log.d("HomeFragment", "State: 4")

        // Navigate to EventDetailFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.main, fragment) // Replace with your container ID
            .addToBackStack(null)
            .commit()
    }

}



