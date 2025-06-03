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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kh.edu.rupp.ite.autumn.R
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.EventData
import kh.edu.rupp.ite.autumn.data.model.EventInfo
import kh.edu.rupp.ite.autumn.data.model.State
import kh.edu.rupp.ite.autumn.databinding.ActivityEventBinding
import kh.edu.rupp.ite.autumn.ui.element.adapter.SelectedEventAdapter
import kh.edu.rupp.ite.autumn.ui.viewmodel.HomeViewModel
import kh.edu.rupp.ite.visitme.global.AppEncryptedPref
import kotlinx.coroutines.launch
import java.util.Locale

class EventFragment : BaseFragment() {

    // ViewModel and view-binding
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var binding: ActivityEventBinding

    // UI elements
    private lateinit var calendarViewUser: CalendarView
    private lateinit var selectedDateUser: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    // Tracks first-time load
    private var isInitialDataLoaded = false

    // Holds the last selected date in "yyyy-MM-dd" form
    private var formattedDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("EventFragment", "onCreateView called")
        binding = ActivityEventBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    // Only re-check the user role here (no data reload on mere resume)
    override fun onResume() {
        super.onResume()
        checkUserRole()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
        setupListener()
        setupObserver()

        // Calendar setup for date selection:
        calendarViewUser = view.findViewById(R.id.calendarViewUser)
        selectedDateUser = view.findViewById(R.id.SelectedDateUser)
        calendarViewUser.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Whenever the user taps a date, re-fetch and then filter
            onDateClicked(year, month, dayOfMonth)
        }

        // Initial load: fetch events once
        if (!isInitialDataLoaded) {
            isInitialDataLoaded = true
            checkUserRole()
            viewModel.loadingHomeData()
        }
    }

    private fun setupUi() {
        swipeRefreshLayout = binding.swipeRefreshLayout
    }

    private fun setupListener() {
        swipeRefreshLayout.setOnRefreshListener {
            // Pull-to-refresh: re-fetch the list, then apply whatever date is selected
            refreshData()
        }
        binding.btnCreateNewEvent.setOnClickListener {
            navigateToEventFormFragment()
        }
    }

    private fun setupObserver() {
        viewModel.homeData.observe(viewLifecycleOwner) { state ->
            when (state.state) {
                State.loading -> {
                    showLoading()
                    Log.d("EventFragment", "State: Loading")
                }
                State.success -> {
                    hideLoading()
                    swipeRefreshLayout.isRefreshing = false
                    Log.d("EventFragment", "State: Success, total events = ${state.data?.size ?: 0}")

                    // If a date was already chosen, filter by that date now
                    formattedDate?.let { dateStr ->
                        val filteredEventInfo = state.data!!
                            .filter { it.date == dateStr }
                            .flatMap { it.event_info }
                        showHomeData(filteredEventInfo)
                    }
                    // Otherwise, if this is the very first successful load, auto-select "today"
                    if (formattedDate == null && state.data != null) {
                        val todayCal = Calendar.getInstance()
                        val y = todayCal.get(Calendar.YEAR)
                        val m = todayCal.get(Calendar.MONTH)
                        val d = todayCal.get(Calendar.DAY_OF_MONTH)
                        updateSelectedDate(y, m, d)
                    }
                }
                State.error -> {
                    hideLoading()
                    swipeRefreshLayout.isRefreshing = false
                    Log.e("EventFragment", "State: Error, Message = ${state.message}")
                    showAlert("EventFragment", state.message ?: "Unexpected Error")
                }
                else -> {
                    Log.w("EventFragment", "Unhandled state: ${state.state}")
                }
            }
        }
    }

    // Called whenever the user taps a date in the CalendarView
    private fun onDateClicked(year: Int, month: Int, dayOfMonth: Int) {
        // 1) Compute the yyyy-MM-dd string
        val cal = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        formattedDate = sdf.format(cal.time)
        selectedDateUser.text = formattedDate

        // 2) Re-fetch the full event list, then filtering will happen in the observer
        viewModel.loadingHomeData()
    }

    // Updates the displayed date text (only used on first load)
    private fun updateSelectedDate(year: Int, month: Int, dayOfMonth: Int) {
        val cal = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        formattedDate = sdf.format(cal.time)
        selectedDateUser.text = formattedDate

        // Filter the already-loaded data now:
        viewModel.homeData.value?.let { state ->
            if (state.state == State.success && state.data != null) {
                val filteredEventInfo = state.data
                    .filter { it.date == formattedDate }
                    .flatMap { it.event_info }
                showHomeData(filteredEventInfo)
            }
        }
    }

    // Displays filtered EventInfo in the RecyclerView.
    private fun showHomeData(eventInfo: List<EventInfo>) {
        Log.d("EventFragment", "Passing ${eventInfo.size} items to adapter")

        val itemEventAdapter = SelectedEventAdapter { info ->
            openEventDetailFragment(info)
        }
        itemEventAdapter.setData(eventInfo)

        val itemEventLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.rvEventList.apply {
            adapter = itemEventAdapter
            layoutManager = itemEventLayoutManager
        }
    }

    // Opens EventDetailFragment for the tapped EventInfo
    private fun openEventDetailFragment(eventInfo: EventInfo) {
        val bundle = Bundle().apply {
            putParcelable("event_info", eventInfo)
            putString("event_date", formattedDate)
        }
        val fragment = EventDetailFragment().apply { arguments = bundle }
        parentFragmentManager.beginTransaction()
            .replace(R.id.event, fragment)
            .addToBackStack(null)
            .commit()
    }

    // Pull-to-refresh: re-fetch the list and re-apply the last‐selected date
    private fun refreshData() {
        viewModel.loadingHomeData()
    }

    // On resume, only re-check role (no re-fetch)
    private fun refreshUserRole() {
        checkUserRole()
    }

    // Checks user’s role and toggles the “Create New Event” button
    private fun checkUserRole() {
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
                    Log.d("EventFragment", "User view displayed. Role = ${userProfile?.role}")
                }
            } catch (e: Exception) {
                Log.e("EventFragment", "Exception: ${e.message}")
                showUserView()
            }
        }
    }

    private fun showAdminView() {
        binding.btnCreateNewEvent.isVisible = true
    }

    private fun showUserView() {
        binding.btnCreateNewEvent.isVisible = false
    }

    // Navigate to the form to create a new event
    private fun navigateToEventFormFragment() {
        val eventFormFragment = EventFormFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.event, eventFormFragment)
            .addToBackStack(null)
            .commit()
    }
}
