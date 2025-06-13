package kh.edu.rupp.ite.autumn.ui.element.fragment

import EventDetailFragment
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
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


    // 1) Handler + Runnable for auto-scroll
    private lateinit var upcomingHandler: Handler
    private val scrollInterval = 4000L
    private val scrollRunnable = object : Runnable {
        override fun run() {
            Log.d("HomeFragment", "run() fired")
            val lm = binding.upComingEvents.layoutManager as LinearLayoutManager
            // Use the fully-visible item to avoid half-visible glitches:
            val current = lm.findFirstCompletelyVisibleItemPosition()
                .takeIf { it != RecyclerView.NO_POSITION }
                ?: lm.findFirstVisibleItemPosition()

            // Just advance by one—no modulo needed:
            val next = current + 1

            binding.upComingEvents.smoothScrollToPosition(next)
            upcomingHandler.postDelayed(this, scrollInterval)
        }

    }

    private val snapHelper = PagerSnapHelper()

    override fun onResume() {
        super.onResume()
        checkUserRole()

        // start auto-scroll once views are laid out
        upcomingHandler.postDelayed(scrollRunnable, scrollInterval)
        Log.d("HomeFragment", "→ onResume")
    }

    override fun onPause() {
        super.onPause()
        // stop to avoid leaks
        Log.d("HomeFragment", "→ onPause – removing callbacks")
        upcomingHandler.removeCallbacks(scrollRunnable)
    }
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


        // 1) One-time: horizontal LayoutManager + snap helper
        binding.upComingEvents.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        snapHelper.attachToRecyclerView(binding.upComingEvents)

        // 2) Prepare your Handler
        upcomingHandler = Handler(Looper.getMainLooper())

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
        val today = LocalDate.now()
        Log.d("HomeFragment", "Today's date: $today")

        // build today’s list by comparing LocalDate objects
        val enrichedTodayEvents = eventData.flatMap { event ->
            runCatching {
                // parse your event.date into LocalDate
                val eventDate = LocalDate.parse(event.date, DateTimeFormatter.ISO_LOCAL_DATE)
                if (eventDate.isEqual(today)) {
                    event.event_info.map { info ->
                        EnrichedEventInfo(info, event.date)
                    }
                } else emptyList()
            }.getOrDefault(emptyList())
        }

        Log.d("HomeFragment", "Found ${enrichedTodayEvents.size} events for today")

        if (enrichedTodayEvents.isEmpty()) {
            binding.noTodayEventsTextView.apply {
                text = "No events for today"
                // set your custom card-style background
                setBackgroundResource(R.drawable.bg_card_no_event_holder)
                // center the text inside the view
                gravity = Gravity.CENTER
                textAlignment = View.TEXT_ALIGNMENT_CENTER

                visibility = View.VISIBLE
            }
            binding.todaySpecialEvent.visibility = View.GONE
            return
        }

        binding.noTodayEventsTextView.visibility = View.GONE
        binding.todaySpecialEvent.visibility = View.VISIBLE

        // set up a simple horizontal list (no looping)
        binding.todaySpecialEvent.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = EventAdapter(enrichedTodayEvents, false) { info ->
                openEventDetailFragment(info)
            }
        }
    }



    // Show upcoming special events

    @RequiresApi(Build.VERSION_CODES.O)
    private fun specialEvent(eventData: List<EventData>) {
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val today = LocalDate.now()

        val enriched = eventData.flatMap { ev ->
            runCatching {
                val d = LocalDate.parse(ev.date, fmt)
                if (d.isAfter(today))
                    ev.event_info.filter { it.isSpecial }
                        .map { EnrichedEventInfo(it, ev.date) }
                else emptyList()
            }.getOrDefault(emptyList())
        }

        if (enriched.isEmpty()) {
            binding.noUpComingEventsTextView.apply {
                text = "No upcoming special events"
                // set your custom card-style background
                setBackgroundResource(R.drawable.bg_card_event_holder)
                // center the text inside the view
                gravity = Gravity.CENTER
                textAlignment = View.TEXT_ALIGNMENT_CENTER

                visibility = View.VISIBLE
            }
            binding.upComingEvents.visibility = View.GONE
            return
        }

        binding.noUpComingEventsTextView.visibility = View.GONE
        binding.upComingEvents.visibility = View.VISIBLE

        // sort your real list
        val sorted = enriched.sortedBy { LocalDate.parse(it.date, fmt) }

        // 3) infinite-loop adapter
        // For upcoming events (infinite):
        val upcomingAdapter = EventAdapter(sorted, true) { info ->
            openEventDetailFragment(info)
        }
        binding.upComingEvents.apply {
            layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false)
            adapter = upcomingAdapter
            // center jump, etc.
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
