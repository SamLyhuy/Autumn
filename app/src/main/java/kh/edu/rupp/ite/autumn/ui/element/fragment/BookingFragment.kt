package kh.edu.rupp.ite.autumn.ui.element.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kh.edu.rupp.ite.autumn.R
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.Profile
import kh.edu.rupp.ite.autumn.data.model.State
import kh.edu.rupp.ite.autumn.databinding.ActivityBookingBinding
import kh.edu.rupp.ite.autumn.global.AppPref
import kh.edu.rupp.ite.autumn.ui.element.adapter.BookingAdapter
import kh.edu.rupp.ite.visitme.global.AppEncryptedPref
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BookingFragment : Fragment() {

    private lateinit var binding: ActivityBookingBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var currentAdapter: BookingAdapter
    private lateinit var historyAdapter: BookingAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ActivityBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
        setupListener()

        // initial load
        swipeRefreshLayout.isRefreshing = true
        fetchUserProfile()
    }

    private fun setupUi() {
        swipeRefreshLayout = binding.swipeRefreshLayout

        currentAdapter = BookingAdapter()
        binding.currentBooking.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = currentAdapter
        }

        historyAdapter = BookingAdapter()
        binding.historyBooking.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupListener() {
        swipeRefreshLayout.setOnRefreshListener {
            // pull-to-refresh → re-fetch
            fetchUserProfile()
        }

        binding.addBookingButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.booking, TableBookingFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.tabCurrentBooking.setOnClickListener { selectCurrentTab() }
        binding.tabHistoryBooking.setOnClickListener { selectHistoryTab() }
    }

    private fun selectCurrentTab() {
        binding.currentBooking.isVisible = true
        binding.historyBooking.isVisible = false
        binding.tabCurrentBooking.setBackgroundResource(R.drawable.bg_oragne)
        binding.tabHistoryBooking.setBackgroundResource(R.drawable.tab_unselected_background_booking)
    }

    private fun selectHistoryTab() {
        binding.currentBooking.isVisible = false
        binding.historyBooking.isVisible = true
        binding.tabCurrentBooking.setBackgroundResource(R.drawable.tab_unselected_background_booking)
        binding.tabHistoryBooking.setBackgroundResource(R.drawable.bg_oragne)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchUserProfile() {
        // spinner already started by caller
        val token = AppEncryptedPref.get().getToken(requireContext())
//        if (token.isNullOrEmpty()) {
//            Log.e("BookingFragment", "Token missing, hiding bookings")
//            binding.currentBooking.isVisible = false
//            binding.historyBooking.isVisible = false
//            swipeRefreshLayout.isRefreshing = false
//            return
//        }

        // optional: showLoading()

        lifecycleScope.launch {
            try {
                val response = ApiClient.get()
                    .apiService
                    .getUserInfo("Bearer $token")
                val profile = response.data?.data

                if (profile != null) {
                    AppPref.get().storeProfile(requireContext(), profile)
                    showBooking(profile)
                    //showAdminView(profile)
                } else {
                    Log.e("BookingFragment", "Empty profile in response")
                }
            } catch (e: Exception) {
                Log.e("BookingFragment", "Error fetching profile: ${e.message}")
            } finally {
                // always stop both loading indicators
                // optional: hideLoading()
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun showAdminView(profile: Profile) {
        if ( profile.role == "Admin") {
            binding.swipeRefreshLayout.isVisible       = false
            binding.currentHistoryBar.isVisible        = false

            // hide “New” button as well
            binding.addBookingButton.isVisible         = false

            // show only the admin action
            binding.checkBookingButton.isVisible       = true
        } else {
            binding.checkBookingButton.isVisible       = false
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showBooking(profile: Profile) {
        val today = LocalDate.now()
        val (upcoming, past) = profile.booking_history.partition {
            LocalDate.parse(it.date, dateFormatter) >= today
        }

        currentAdapter.setData(upcoming)
        historyAdapter.setData(past)

        if (upcoming.isEmpty() && past.isNotEmpty()) {
            selectHistoryTab()
        } else {
            selectCurrentTab()
        }
    }
}
