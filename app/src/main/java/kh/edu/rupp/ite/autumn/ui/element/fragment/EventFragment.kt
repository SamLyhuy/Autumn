package kh.edu.rupp.ite.autumn.ui.element.fragment

import EventDetailFragment
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Binder
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


class EventFragment: BaseFragment() {

    private val viewModel by viewModels<HomeViewModel>()

    private lateinit var binding: ActivityEventBinding

    private lateinit var calendarViewUser: CalendarView
    private lateinit var SelectedDateUser: TextView

    private var formattedDate: String? = null

    private var isInitialDataLoaded = false

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("EventFragment", "onCreateView called")
        binding = ActivityEventBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        refreshUserRole() // Refresh role and data when the fragment resumes
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("EventFragment", "onViewCreated called")
        checkUserRole()
        setupUi()
        setupListener()
        setupObserver()





        calendarViewUser = view.findViewById(R.id.calendarViewUser)
        SelectedDateUser = view.findViewById(R.id.SelectedDateUser)


        calendarViewUser.setOnDateChangeListener{ _, year, month, dayOfMonth ->
            updateSelectedDate(year, month, dayOfMonth)
        }


        binding.btnCreateNewEvent.setOnClickListener {
            navigateToEventFormFragment()
        }


    }

    private fun navigateToEventFormFragment() {
        val eventFormFragment = EventFormFragment()

        parentFragmentManager.beginTransaction()
            .replace(R.id.event, eventFormFragment)  // Replace container with EventFormFragment
            .addToBackStack(null)  // Add to back stack so user can navigate back
            .commit()
    }

    private fun refreshUserRole() {
        checkUserRole() // Re-check the user's role
        viewModel.loadingHomeData() // Trigger a reload of home data
    }



    private fun showUserView(){
        binding.btnCreateNewEvent.isVisible = false


    }

    private fun showAdminView(){
        binding.btnCreateNewEvent.isVisible = true

    }

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
                    Log.d("EventFragment", "User view displayed. Role: ${userProfile?.role}")
                }
            } catch (e: Exception) {
                Log.e("EventFragment", "Exception: ${e.message}")
                showUserView()
            }
        }
    }

    private fun updateSelectedDate(year: Int, month: Int, dayOfMonth: Int) {

        val selectedDate = Calendar.getInstance().apply {
            set(year, month, dayOfMonth)
        }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        formattedDate = dateFormat.format(selectedDate.time)
        Log.e("EventFragment", "Exception: ${dateFormat}")

        SelectedDateUser.text = "Date: $formattedDate"

        // Filter EventData objects by the selected date and extract EventInfo

        viewModel.homeData.value?.let { state  ->
            if(state.state == State.success && state.data != null) {
                val filteredEventInfo = state.data
                    .filter { it.date == formattedDate }
                    .flatMap { it.event_info }

                Log.d("EventFragment", "Filtered EventInfo for date $formattedDate: $filteredEventInfo")

                showHomeData(filteredEventInfo)

            }
        }

    }

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


    private fun handleState(state: ApiState<List<EventData>>) {

        when( state.state){
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

                

                //showHomeData(state.data!!)


//                state.data?.let { eventData ->
//                    val selectedDateText = SelectedDateUser.text.toString()
//                    if (selectedDateText.startsWith("Date: ")) {
//                        val selectedDate = selectedDateText.removePrefix("Date: ").trim()
//                        val filteredEventInfo = eventData
//                            .filter { it.date == selectedDate } // Filter by selected date
//                            .flatMap { it.event_info } // Extract EventInfo
//
//                        Log.d("EventFragment", "Filtered EventInfo for date $selectedDate: $filteredEventInfo")
//                        showHomeData(filteredEventInfo)

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

    private fun showHomeData(eventInfo: List<EventInfo>) {

        Log.d("EventFragment", "Start passing all data to adapter, Data: ${eventInfo}")

        //val eventInfoList = eventData.flatMap { it.event_info }

        //Log.d("EventFragment", "Start passing only event_info to adapter, Data: ${eventInfoList}")







        //val itemEventAdapter = SelectedEventAdapter()

        val itemEventAdapter =SelectedEventAdapter { eventInfo ->
            openEventDetailFragment (eventInfo)
        }



        val itemEventLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        itemEventAdapter.setData(eventInfo)
        binding.rvEventList.apply {
            adapter = itemEventAdapter
            layoutManager = itemEventLayoutManager
        }


    }

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

    private fun setupListener() {
        swipeRefreshLayout.setOnRefreshListener {
            refreshUserRole()
            refreshData()  // Refresh data on swipe
        }
    }

    private fun setupUi() {
        // Initialize SwipeRefreshLayout properly
        swipeRefreshLayout = binding.swipeRefreshLayout  // This references the SwipeRefreshLayout
    }

    private fun refreshData() {
        viewModel.loadingHomeData() // Trigger data reload
    }
}