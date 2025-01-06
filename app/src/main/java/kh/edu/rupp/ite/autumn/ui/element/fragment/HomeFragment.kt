package kh.edu.rupp.ite.autumn.ui.element.fragment

import EventDetailFragment
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.annotation.RequiresApi
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
import java.time.LocalDate
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
    @RequiresApi(Build.VERSION_CODES.O)
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


        binding.btnCreateNewEventTest.setOnClickListener {
            navigateToEventFormFragment()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
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
    @RequiresApi(Build.VERSION_CODES.O)
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

                specialEvent(state.data!!)
                todayEvent(state.data!!)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun todayEvent(eventData: List<EventData>) {

        val today = LocalDate.now().toString() // Today's date in yyyy-MM-dd format
        Log.d("HomeFragment", "Today's date: $today")

        val enrichedTodayEvents = eventData.flatMap { event ->

            try {
                if (event.date == today) {
                    event.event_info.map { eventInfo ->
                        EnrichedEventInfo(eventInfo, event.date)
                    }
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error processing event: ${event.date}", e)
                emptyList()
            }
        }

        Log.d("HomeFragment", "Today's enriched events: $enrichedTodayEvents")

        if (enrichedTodayEvents.isEmpty()) {
            binding.noEventsTextView.apply {
                text = "No events for to day"
                visibility = View.VISIBLE
            }
            binding.upComingEvents.visibility = View.GONE
        } else {
            binding.noEventsTextView.visibility = View.GONE
            binding.upComingEvents.visibility = View.VISIBLE

            val itemTodayEventLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            val itemTodayEventAdapter = EventAdapter { enrichedEventInfo ->
                openEventDetailFragment(enrichedEventInfo)
            }

            itemTodayEventAdapter.setData(enrichedTodayEvents)

            binding.specialsToday.apply {
                adapter = itemTodayEventAdapter
                layoutManager = itemTodayEventLayoutManager
            }

        }



    }

    // Display the list of categories using a RecyclerView
    private fun specialEvent(eventData: List<EventData>) {

        // Enrich and filter the special events
        val enrichedSpecialEvents = eventData.flatMap { event ->
            event.event_info.filter { it.isSpecial }.map { eventInfo ->
                EnrichedEventInfo(eventInfo, event.date)
            }
        }

        Log.d("HomeFragment", "Enriched special events: ${enrichedSpecialEvents}")

        // Sort the events by event.date in ascending order
        val sortedSpecialEvents = enrichedSpecialEvents.sortedBy { it.date }
        val itemSpecialEventLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val itemSpecialEventAdapter = EventAdapter { enrichedEventInfo  ->
            openEventDetailFragment(enrichedEventInfo ) // When an item is clicked, open the detail view
        }

        itemSpecialEventAdapter.setData(sortedSpecialEvents) // Pass Category list to the adapter

        // Bind the RecyclerView to the adapter and layout manager
        binding.upComingEvents.apply {
            adapter = itemSpecialEventAdapter
            layoutManager = itemSpecialEventLayoutManager
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



