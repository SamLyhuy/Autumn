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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kh.edu.rupp.ite.autumn.R
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.EventData
import kh.edu.rupp.ite.autumn.data.model.EventInfo
import kh.edu.rupp.ite.autumn.data.model.State
import kh.edu.rupp.ite.autumn.databinding.ActivityEventBinding
import kh.edu.rupp.ite.autumn.databinding.ActivityHomeBinding
import kh.edu.rupp.ite.autumn.databinding.ItemFoodBinding
import kh.edu.rupp.ite.autumn.ui.element.adapter.EventAdapter
import kh.edu.rupp.ite.autumn.ui.element.adapter.SelectedEventAdapter
import kh.edu.rupp.ite.autumn.ui.viewmodel.HomeViewModel
import kh.edu.rupp.ite.visitme.global.AppEncryptedPref
import kotlinx.coroutines.launch
import java.util.Locale

class EventFragment : BaseFragment() {

    // ViewModel and binding initialization
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var binding: ActivityEventBinding

    // UI elements
    private lateinit var calendarViewUser: CalendarView
    private lateinit var selectedDateUser: TextView
    private var formattedDate: String? = null
    private var isInitialDataLoaded = false
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("EventFragment", "onCreateView called")
        binding = ActivityEventBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    // Refreshes user role and data when the fragment resumes.
    override fun onResume() {
        super.onResume()
        refreshUserRole() // Refresh role and data when the fragment resumes
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkUserRole()
        setupUi()
        setupListener()
        setupObserver()

        // Calendar setup for date selection
        calendarViewUser = view.findViewById(R.id.calendarViewUser)
        selectedDateUser = view.findViewById(R.id.SelectedDateUser)
        calendarViewUser.setOnDateChangeListener { _, year, month, dayOfMonth ->
            updateSelectedDate(year, month, dayOfMonth)
        }
    }

    // Checks the user's role and adjusts the UI accordingly.
    private fun checkUserRole() {

        // Get token
        val token = AppEncryptedPref.get().getToken(requireContext())
        Log.d("EventFragment", "Token is checking: $token")

        if (token == null) {
            Log.e("EventFragment", "Token is null.")
            showUserView()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.get().apiService.getUserInfo("Bearer $token")
                Log.d("EventFragment", "API Response: $response")
                val userProfile = response.data?.data
                Log.d("EventFragment", "User Profile: $userProfile")

                if (userProfile?.role.equals("admin", ignoreCase = true)) {
                    showAdminView()
                    Log.d("EventFragment", "Admin view displayed.")
                } else {
                    showUserView()
                    Log.d("EventFragment", "User view displayed. Role: ${userProfile?.role}")
                }
            } catch (e: Exception) {
                Log.e("EventFragment", "Exception: ${e.message}")
                showUserView()
            }
        }
    }

    // Initializes the user interface elements, including SwipeRefreshLayout.
    private fun setupUi() {
        swipeRefreshLayout = binding.swipeRefreshLayout // Initialize SwipeRefreshLayout properly
    }

    // Sets up listeners for UI elements, including swipe-to-refresh and event creation button.
    private fun setupListener() {
        swipeRefreshLayout.setOnRefreshListener {
            refreshUserRole()
            refreshData()  // Refresh data on swipe
        }
        binding.btnCreateNewEvent.setOnClickListener {
            navigateToEventFormFragment()
        }
    }

    // Sets up the observer for the home data live data to handle UI updates.
    private fun setupObserver() {
        viewModel.homeData.observe(viewLifecycleOwner) { state ->
            handleState(state)

            // Check if it's the first load and update with the current date
            if (!isInitialDataLoaded && state.state == State.success && state.data != null) {
                isInitialDataLoaded = true

                val today = Calendar.getInstance()
                val year = today.get(Calendar.YEAR)
                val month = today.get(Calendar.MONTH)
                val dayOfMonth = today.get(Calendar.DAY_OF_MONTH)

                updateSelectedDate(year, month, dayOfMonth)
            }
        }
    }


    // Refreshes user role and triggers a data reload.
    private fun refreshUserRole() {
        checkUserRole() // Re-check the user's role
        viewModel.loadingHomeData() // Trigger a reload of home data
    }

    // Updates the displayed date when the user selects a new date on the calendar.
    private fun updateSelectedDate(year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate = Calendar.getInstance().apply {
            set(year, month, dayOfMonth)
        }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        formattedDate = dateFormat.format(selectedDate.time)
        Log.e("EventFragment", "Exception: ${dateFormat}")

        selectedDateUser.text = "$formattedDate"

        // Filter EventData objects by the selected date and extract EventInfo
        viewModel.homeData.value?.let { state ->
            if (state.state == State.success && state.data != null) {
                val filteredEventInfo = state.data
                    .filter { it.date == formattedDate }
                    .flatMap { it.event_info }

                Log.d("EventFragment", "Filtered EventInfo for date $formattedDate: $filteredEventInfo")
                showHomeData(filteredEventInfo)
            }
        }
    }

    private fun handleState(state: ApiState<List<EventData>>) {
        when (state.state) {
            State.loading -> {
                // Show a loading indicator
                showLoading()
                Log.d("EventFragment", "State: Loading")
            }
            State.success -> {
                // Hide the loading indicator and show the data
                hideLoading()
                Log.d("EventFragment", "State: Success, Data: ${state.data}")
                swipeRefreshLayout.isRefreshing = false
            }
            State.error -> {
                // Hide the loading indicator and show an error alert
                hideLoading()
                Log.e("EventFragment", "State: Error, Message: ${state.message}")
                showAlert("EventFragment", state.message ?: "Unexpected Error")
            }
            else -> {
                Log.w("EventFragment", "Unhandled state: ${state.state}")
            }
        }
    }

    // Displays home data in the UI by passing it to the adapter for the recycler view.
    private fun showHomeData(eventInfo: List<EventInfo>) {
        Log.d("EventFragment", "Start passing all data to adapter, Data: ${eventInfo}")

        val itemEventAdapter = SelectedEventAdapter { eventInfo ->
            openEventDetailFragment(eventInfo)
        }

        val itemEventLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        itemEventAdapter.setData(eventInfo)
        binding.rvEventList.apply {
            adapter = itemEventAdapter
            layoutManager = itemEventLayoutManager
        }
    }

    // Opens the EventDetailFragment to display detailed information about the selected event.
    private fun openEventDetailFragment(eventInfo: EventInfo) {
        val bundle = Bundle().apply {
            putParcelable("event_info", eventInfo)
            putString("event_date", formattedDate)
        }

        val fragment = EventDetailFragment().apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.event, fragment)
            .addToBackStack(null)
            .commit()
    }


    // Reload of home data.
    private fun refreshData() {
        viewModel.loadingHomeData() // Trigger data reload
    }

    // Navigates to the event form fragment to create a new event.
    private fun navigateToEventFormFragment() {
        val eventFormFragment = EventFormFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.event, eventFormFragment)  // Replace container with EventFormFragment
            .addToBackStack(null)  // Add to back stack so user can navigate back
            .commit()
    }

    // showing admin-specific controls.
    private fun showAdminView() {
        binding.btnCreateNewEvent.isVisible = true
    }

    // showing user-specific controls.
    private fun showUserView() {
        binding.btnCreateNewEvent.isVisible = false
    }
}
