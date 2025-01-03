package kh.edu.rupp.ite.autumn.ui.element.fragment

import EventDetailFragment
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kh.edu.rupp.ite.autumn.R
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.EnrichedEventInfo
import kh.edu.rupp.ite.autumn.data.model.EventData
import kh.edu.rupp.ite.autumn.data.model.State
import kh.edu.rupp.ite.autumn.databinding.ActivityHomeBinding
import kh.edu.rupp.ite.autumn.ui.element.adapter.EventAdapter
import kh.edu.rupp.ite.autumn.ui.viewmodel.HomeViewModel
import kh.edu.rupp.ite.visitme.global.AppEncryptedPref
import kotlinx.coroutines.launch
import java.util.Locale

class HomeFragment: BaseFragment() {

    // Instantiate the ViewModel using the viewModels delegate
    private val viewModel by viewModels<HomeViewModel>()

    // Binding object for accessing views in the layout
    private lateinit var binding: ActivityHomeBinding

    private lateinit var calendarView: CalendarView
    private lateinit var tvSelectedDate: TextView

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

        checkUserRole()


        calendarView = view.findViewById(R.id.calendarView)
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate)

        // Set listener for when a date is selected
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            updateSelectedDate(year, month, dayOfMonth)
        }


        binding.btnCreateNewEvent.setOnClickListener {
            navigateToEventFormFragment()
        }

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

        Log.d("HomeFragment", "Start passing all data to adapter, Data: ${eventData}")

        //val isSpecialList = eventData.get().event_info.map { it.isSpecial }

        val enrichedSpecialEvents = eventData.flatMap { event ->
            event.event_info.filter { it.isSpecial }.map { eventInfo ->
                EnrichedEventInfo(eventInfo, event.date)
            }
        }

        Log.d("HomeFragment", "Enriched special events: ${enrichedSpecialEvents}")


        val itemEventLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

//      val itemEventAdapter = EventAdapter()
        val itemEventAdapter = EventAdapter { enrichedEventInfo  ->
            openEventDetailFragment(enrichedEventInfo ) // When an item is clicked, open the detail view
        }


        itemEventAdapter.setData(enrichedSpecialEvents) // Pass Category list to the adapter

        // Bind the RecyclerView to the adapter and layout manager
        binding.upComingEvents.apply {
            adapter = itemEventAdapter
            layoutManager = itemEventLayoutManager
        }
    }

    private fun openEventDetailFragment(enrichedEventInfo: EnrichedEventInfo) {

        // Pass the clicked event data to the EventDetailFragment
        val bundle = Bundle().apply {
            putParcelable("event_info", enrichedEventInfo.eventInfo)
            putString("event_date", enrichedEventInfo.date)
        }

        val fragment = EventDetailFragment().apply {
            arguments = bundle
        }

        // Navigate to EventDetailFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.home, fragment) // Replace with your container ID
            .addToBackStack(null)
            .commit()
    }


    private fun navigateToEventFormFragment() {
        val eventFormFragment = EventFormFragment()

        parentFragmentManager.beginTransaction()
            .replace(R.id.home, eventFormFragment)  // Replace container with EventFormFragment
            .addToBackStack(null)  // Add to back stack so user can navigate back
            .commit()
    }


    private fun showUserView(){
        binding.adminView.isVisible = false
        binding.userView.isVisible = true

    }

    private fun showAdminView(){
        binding.adminView.isVisible = true
        binding.userView.isVisible = false

    }

    private fun checkUserRole() {
        val token = AppEncryptedPref.get().getToken(requireContext())
        Log.d("HomeFragment", "Token is checking: $token")

        if (token == null) {
            Log.e("HomeFragment", "Token is null.")
            showUserView()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.get().apiService.getUserInfo("Bearer $token")
                Log.d("HomeFragment", "API Response: $response")
                val userProfile = response.data?.data
                Log.d("HomeFragment", "User Profile: $userProfile")

                if (userProfile?.role.equals("admin", ignoreCase = true)) {
                    showAdminView()
                    Log.d("HomeFragment", "Admin view displayed.")
                } else {
                    showUserView()
                    Log.d("HomeFragment", "User view displayed. Role: ${userProfile?.role}")
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Exception: ${e.message}")
                showUserView()
            }
        }
    }

    private fun updateSelectedDate(year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate = Calendar.getInstance().apply {
            set(year, month, dayOfMonth)
        }

        // Format the date as desired
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate.time)

        // Update the TextView with the formatted date
        tvSelectedDate.text = "Date: $formattedDate"
    }


}



