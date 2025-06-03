package kh.edu.rupp.ite.autumn.ui.element.fragment

import EventDetailFragment
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
import java.time.format.DateTimeFormatter

// Fragment that handles the Home screen UI and data interactions
class HomeFragment : BaseFragment() {

    // ViewModels for loading home and food data
    private val homeViewModel by viewModels<HomeViewModel>()
    private val foodViewModel by viewModels<FoodViewModel>()

    // Binding object for accessing views
    private lateinit var binding: ActivityHomeBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    // Track in-flight loads
    private var isHomeLoading = false
    private var isFoodLoading = false
    private var isDrinkLoading = false

    // Ensure we only do the first‐ever load once
    private var initialDataLoaded = false

    // Inflate the layout for the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("HomeFragment", "onCreateView called")
        binding = ActivityHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Only re-check the user role here—do NOT reload the lists on every resume
    override fun onResume() {
        super.onResume()
        checkUserRole()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("HomeFragment", "onViewCreated called")

        setupUi()
        setupListener()
        setupObserver()

        // First-time load: events, foods, drinks
        if (!initialDataLoaded) {
            initialDataLoaded = true
            checkUserRole()
            homeViewModel.loadingHomeData()
            foodViewModel.loadingFoodData("food")
            foodViewModel.loadingFoodData("drink")
        }
    }

    // Set up the initial UI elements
    private fun setupUi() {
        swipeRefreshLayout = binding.swipeRefreshLayout
    }

    // Set up listeners for UI interactions
    private fun setupListener() {
        swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
        binding.btnOpenChat.setOnClickListener {
            openChat()
        }
        binding.btnCreateNewFood.setOnClickListener {
            navigateToFoodFormFragment()
        }
        binding.btnCreateNewDrink.setOnClickListener {
            navigateToFoodFormFragment()
        }
    }

    private fun navigateToFoodFormFragment() {
        val foodFormFragment = FoodFormFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.home, foodFormFragment)
            .addToBackStack(null)
            .commit()
    }

    // Set up observers to watch for data changes from ViewModel
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupObserver() {
        // 1) Observe homeData (events)
        homeViewModel.homeData.observe(viewLifecycleOwner) { state ->
            when (state.state) {
                State.loading -> {
                    isHomeLoading = true
                    Log.d("HomeFragment", "homeData → loading; showLoading()")
                    showLoading()
                }
                State.success -> {
                    isHomeLoading = false
                    Log.d("HomeFragment", "homeData → success; isHomeLoading=false")
                    if (!isFoodLoading && !isDrinkLoading && !isHomeLoading) {
                        Log.d("HomeFragment", "All loads finished → hideLoading() & stop swipe")
                        hideLoading()
                        swipeRefreshLayout.isRefreshing = false
                    }
                    state.data?.let { list ->
                        specialEvent(list)
                        todayEvent(list)
                    }
                }
                State.error -> {
                    isHomeLoading = false
                    Log.e("HomeFragment", "homeData → error (${state.message}); isHomeLoading=false")
                    if (!isFoodLoading && !isDrinkLoading && !isHomeLoading) {
                        Log.d("HomeFragment", "All loads finished (with error) → hideLoading() & stop swipe")
                        hideLoading()
                        swipeRefreshLayout.isRefreshing = false
                    }
                    showAlert("Error", state.message ?: "Unexpected Error")
                }
                else -> {
                    Log.w("HomeFragment", "homeData → unhandled state ${state.state}")
                }
            }
        }

        // 2) Observe foodData
        foodViewModel.foodData.observe(viewLifecycleOwner) { state ->
            when (state.state) {
                State.loading -> {
                    isFoodLoading = true
                    Log.d("HomeFragment", "foodData → loading; showLoading()")
                    showLoading()
                }
                State.success -> {
                    isFoodLoading = false
                    Log.d("HomeFragment", "foodData → success; isFoodLoading=false")
                    if (!isHomeLoading && !isDrinkLoading && !isFoodLoading) {
                        Log.d("HomeFragment", "All loads finished → hideLoading() & stop swipe")
                        hideLoading()
                        swipeRefreshLayout.isRefreshing = false
                    }
                    populateFoodList(state.data ?: emptyList())
                }
                State.error -> {
                    isFoodLoading = false
                    Log.e("HomeFragment", "foodData → error (${state.message}); isFoodLoading=false")
                    if (!isHomeLoading && !isDrinkLoading && !isFoodLoading) {
                        Log.d("HomeFragment", "All loads finished (with error) → hideLoading() & stop swipe")
                        hideLoading()
                        swipeRefreshLayout.isRefreshing = false
                    }
                    showAlert("Error", state.message ?: "Unexpected error")
                }
                State.none -> {
                    Log.d("HomeFragment", "foodData → none")
                }
            }
        }

        // 3) Observe drinkData
        foodViewModel.drinkData.observe(viewLifecycleOwner) { state ->
            when (state.state) {
                State.loading -> {
                    isDrinkLoading = true
                    Log.d("HomeFragment", "drinkData → loading; showLoading()")
                    showLoading()
                }
                State.success -> {
                    isDrinkLoading = false
                    Log.d("HomeFragment", "drinkData → success; isDrinkLoading=false")
                    if (!isHomeLoading && !isFoodLoading && !isDrinkLoading) {
                        Log.d("HomeFragment", "All loads finished → hideLoading() & stop swipe")
                        hideLoading()
                        swipeRefreshLayout.isRefreshing = false
                    }
                    populateDrinkList(state.data ?: emptyList())
                }
                State.error -> {
                    isDrinkLoading = false
                    Log.e("HomeFragment", "drinkData → error (${state.message}); isDrinkLoading=false")
                    if (!isHomeLoading && !isFoodLoading && !isDrinkLoading) {
                        Log.d("HomeFragment", "All loads finished (with error) → hideLoading() & stop swipe")
                        hideLoading()
                        swipeRefreshLayout.isRefreshing = false
                    }
                    showAlert("Error", state.message ?: "Unexpected error")
                }
                State.none -> {
                    Log.d("HomeFragment", "drinkData → none")
                }
            }
        }
    }

    // Check user role and show admin/user UI elements
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

    private fun showUserView() {
        binding.btnCreateNewFood.isVisible = false
        binding.btnCreateNewDrink.isVisible = false
    }

    private fun showAdminView() {
        binding.btnCreateNewFood.isVisible = true
        binding.btnCreateNewDrink.isVisible = true
    }

    // Populate the list of food items in the RecyclerView
    private fun populateFoodList(foodData: List<FoodData>) {
        val foodListLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val foodAdapter = FoodAdapter()
        foodAdapter.setData(foodData)
        binding.foodListRecycler.apply {
            adapter = foodAdapter
            layoutManager = foodListLayoutManager
        }
    }

    // Populate the list of drink items in the RecyclerView
    private fun populateDrinkList(drinkData: List<FoodData>) {
        val drinkListLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val drinkAdapter = FoodAdapter()
        drinkAdapter.setData(drinkData)
        binding.drinkListRecycler.apply {
            adapter = drinkAdapter
            layoutManager = drinkListLayoutManager
        }
    }

    // Show today's events in the RecyclerView
    @RequiresApi(Build.VERSION_CODES.O)
    private fun todayEvent(eventData: List<EventData>) {
        val today = LocalDate.now().toString() // yyyy-MM-dd
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

        if (enrichedTodayEvents.isEmpty()) {
            binding.noTodayEventsTextView.apply {
                text = "No events for today"
                visibility = View.VISIBLE
            }
            binding.todaySpecialEvent.visibility = View.GONE
        } else {
            binding.noTodayEventsTextView.visibility = View.GONE
            val itemTodayEventLayoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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

    // Show upcoming special events
    @RequiresApi(Build.VERSION_CODES.O)
    private fun specialEvent(eventData: List<EventData>) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val today = LocalDate.now()
        Log.d("HomeFragment", "Today's date: $today")

        val enrichedSpecialEvents = eventData.flatMap { event ->
            try {
                val eventDate = LocalDate.parse(event.date, formatter)
                if (eventDate.isAfter(today)) {
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

            val layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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

    // Pull-to-refresh: re-fetch events, foods, and drinks
    private fun refreshData() {
        homeViewModel.loadingHomeData()
        foodViewModel.loadingFoodData("food")
        foodViewModel.loadingFoodData("drink")
    }
}
