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
import kh.edu.rupp.ite.autumn.data.model.FoodData
import kh.edu.rupp.ite.autumn.data.model.State
import kh.edu.rupp.ite.autumn.databinding.ActivityHomeBinding
import kh.edu.rupp.ite.autumn.ui.element.adapter.EventAdapter
import kh.edu.rupp.ite.autumn.ui.element.adapter.FoodAdapter
import kh.edu.rupp.ite.autumn.ui.viewmodel.FoodViewModel
import kh.edu.rupp.ite.autumn.ui.viewmodel.HomeViewModel
import kh.edu.rupp.ite.visitme.global.AppEncryptedPref
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
import java.time.format.DateTimeFormatter

// Fragment that handles the Home screen UI and data interactions
class HomeFragment: BaseFragment() {

    // ViewModels for loading home and food data
    private val homeViewModel by viewModels<HomeViewModel>()
    private val foodViewModel by viewModels<FoodViewModel>()

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
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("HomeFragment", "onViewCreated called")

        // Set up the UI, listeners, and observers
        setupUi()
        setupListener()
        setupObserver()

        // Check user role and load data
        checkUserRole()
        homeViewModel.loadingHomeData()
        foodViewModel.loadingFoodData("food")
        foodViewModel.loadingFoodData("drink")
    }

    // Set up the initial UI elements (to be defined later)
    private fun setupUi() {
        // UI setup can be added here if needed
    }

    // Set up listeners for any UI interactions (to be defined later)
    private fun setupListener() {
        binding.btnOpenChat.setOnClickListener {
            openChat()
        }
    }

    // Set up observers to watch for data changes from ViewModel
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupObserver() {
        homeViewModel.homeData.observe(viewLifecycleOwner) { state ->
            handleStateEvent(state)
        }

        foodViewModel.foodData.observe(viewLifecycleOwner) { state ->
            handleStateFood(state)
        }

        foodViewModel.drinkData.observe(viewLifecycleOwner) { state ->
            handleStateDrink(state)
        }
    }

    // Check user role and determine whether to show admin or user view
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

    // Handle state changes for food data (loading, success, error)
    private fun handleStateFood(state: ApiState<List<FoodData>>) {
        when (state.state) {
            State.loading -> showLoading()
            State.success -> {
                hideLoading()
                populateFoodList(state.data ?: emptyList())
            }
            State.error -> {
                hideLoading()
                showAlert("Error", state.message ?: "Unexpected error")
            }
            State.none -> TODO()
        }
    }

    // Handle state changes for drink data (loading, success, error)
    private fun handleStateDrink(state: ApiState<List<FoodData>>) {
        when (state.state) {
            State.loading -> showLoading()
            State.success -> {
                hideLoading()
                populateDrinkList(state.data ?: emptyList())
            }
            State.error -> {
                hideLoading()
                showAlert("Error", state.message ?: "Unexpected error")
            }
            State.none -> TODO()
        }
    }

    // Populate the list of food items in the RecyclerView
    private fun populateFoodList(foodData: List<FoodData>) {
        val foodListLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val foodAdapter = FoodAdapter()
        foodAdapter.setData(foodData)
        binding.foodListRecycler.apply {
            adapter = foodAdapter
            layoutManager = foodListLayoutManager
        }
    }

    // Populate the list of drink items in the RecyclerView
    private fun populateDrinkList(drinkData: List<FoodData>) {
        val drinkListLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val drinkAdapter = FoodAdapter()
        drinkAdapter.setData(drinkData)
        binding.drinkListRecycler.apply {
            adapter = drinkAdapter
            layoutManager = drinkListLayoutManager
        }
    }

    // Handle different states of the event data (loading, success, error)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleStateEvent(state: ApiState<List<EventData>>) {
        when (state.state) {
            State.loading -> {
                showLoading()
                Log.d("HomeFragment", "State: Loading")
            }
            State.success -> {
                hideLoading()
                Log.d("HomeFragment", "State: Success, Data: ${state.data}")
                specialEvent(state.data!!)
                todayEvent(state.data)
            }
            State.error -> {
                hideLoading()
                Log.e("HomeFragment", "State: Error, Message: ${state.message}")
                showAlert("Error", state.message ?: "Unexpected Error")
            }
            else -> {
                Log.w("HomeFragment", "Unhandled state: ${state.state}")
            }
        }
    }

    // Show today's events in the RecyclerView
    @RequiresApi(Build.VERSION_CODES.O)
    private fun todayEvent(eventData: List<EventData>) {
        val today = LocalDate.now().toString() // Today's date in yyyy-MM-dd format
        Log.d("HomeFragment", "Today's date: $today")

        // Map to get only Event Today
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

        // Handle if No Event Today
        if (enrichedTodayEvents.isEmpty()) {
            binding.noTodayEventsTextView.apply {
                text = "No events for today"
                visibility = View.VISIBLE
            }
            binding.todaySpecialEvent.visibility = View.GONE
        } else {
            binding.noTodayEventsTextView.visibility = View.GONE
            val itemTodayEventLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            val itemTodayEventAdapter = EventAdapter { enrichedEventInfo ->
                openEventDetailFragment(enrichedEventInfo)
            }

            itemTodayEventAdapter.setData(enrichedTodayEvents)

            binding.todaySpecialEvent.apply {
                adapter = itemTodayEventAdapter
                layoutManager = itemTodayEventLayoutManager
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun specialEvent(eventData: List<EventData>) {
        // Formatter matching your server’s "yyyy-MM-dd" strings
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val today = LocalDate.now()
        Log.d("HomeFragment", "Today's date: $today")

        // Map to get only upcoming special events
        val enrichedSpecialEvents = eventData.flatMap { event ->
            try {
                val eventDate = LocalDate.parse(event.date, formatter)
                if (eventDate.isAfter(today)) {
                    // Now filter for isSpecial inside the event_info list
                    event.event_info
                        .filter { it.isSpecial }
                        .map { info ->
                            EnrichedEventInfo(info, event.date)
                        }
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error parsing date: ${event.date}", e)
                emptyList()
            }
        }

        Log.d("HomeFragment", "Upcoming special events: $enrichedSpecialEvents")

        if (enrichedSpecialEvents.isEmpty()) {
            binding.noUpComingEventsTextView.apply {
                text = "No upcoming special events"
                visibility = View.VISIBLE
            }
            binding.upComingEvents.visibility = View.GONE
        } else {
            binding.noUpComingEventsTextView.visibility = View.GONE
            binding.upComingEvents.visibility = View.VISIBLE

            val sorted = enrichedSpecialEvents.sortedBy {
                LocalDate.parse(it.date, formatter)
            }

            val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            val adapter = EventAdapter { enrichedEventInfo ->
                openEventDetailFragment(enrichedEventInfo)
            }.apply {
                setData(sorted)
            }

            binding.upComingEvents.apply {
                this.adapter = adapter
                this.layoutManager = layoutManager
            }
        }
    }


    // Open the detailed view of an event when clicked
    private fun openEventDetailFragment(enrichedEventInfo: EnrichedEventInfo) {
        val bundle = Bundle().apply {
            putParcelable("event_info", enrichedEventInfo.eventInfo)
            putString("event_date", enrichedEventInfo.date)
        }

        val fragment = EventDetailFragment().apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.home, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun openChat() {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.home, ChatBotFragment())
            .addToBackStack(null)
            .commit()
    }

    // Show or hide views for user or admin based on role
    private fun showUserView() {
        binding.btnCreateNewFood.isVisible = false
        binding.btnCreateNewDrink.isVisible = false
    }

    private fun showAdminView() {
        binding.btnCreateNewFood.isVisible = true
        binding.btnCreateNewDrink.isVisible = true
    }
}
