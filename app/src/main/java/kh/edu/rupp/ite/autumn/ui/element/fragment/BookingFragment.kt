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
import kh.edu.rupp.ite.autumn.R
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.Profile
import kh.edu.rupp.ite.autumn.databinding.ActivityBookingBinding
import kh.edu.rupp.ite.autumn.global.AppPref
import kh.edu.rupp.ite.autumn.ui.element.adapter.BookingAdapter
import kh.edu.rupp.ite.autumn.ui.element.fragment.TableBookingFragment
import kh.edu.rupp.ite.visitme.global.AppEncryptedPref
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Fragment for displaying current and history bookings based on user profile data.
 */
class BookingFragment : Fragment() {



    private lateinit var binding: ActivityBookingBinding
    private lateinit var currentAdapter: BookingAdapter
    private lateinit var historyAdapter: BookingAdapter
    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setupListener()
        fetchUserProfile()
    }

    /**
     * Initialize RecyclerViews and their adapters.
     */
    private fun setupUi() {
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

    /**
     * Set up click listener for the Add Booking button.
     */
    private fun setupListener() {
        binding.addBookingButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.booking, TableBookingFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    /**
     * Fetch user profile using stored token and partition bookings.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchUserProfile() {
        val token = AppEncryptedPref.get().getToken(requireContext())
        if (token == null) {
            Log.e("BookingFragment", "Token is null. Hiding bookings.")
            binding.currentBooking.isVisible = false
            binding.historyBooking.isVisible = false
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.get()
                    .apiService
                    .getUserInfo("Bearer $token")
                val profile = response.data?.data
                if (profile != null) {
                    // Cache profile if needed
                    AppPref.get().storeProfile(requireContext(), profile)
                    showBooking(profile)
                } else {
                    Log.e("BookingFragment", "No profile data returned.")
                }
            } catch (e: Exception) {
                Log.e("BookingFragment", "Error fetching profile: ${e.message}")
            }
        }
    }

    /**
     * Partition booking_info into upcoming and past, then update adapters.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showBooking(profile: Profile) {
        val today = LocalDate.now()
        val (upcoming, past) = profile.booking_history.partition { entry ->
            LocalDate.parse(entry.date, dateFormatter) >= today
        }

        currentAdapter.setData(upcoming)
        historyAdapter.setData(past)
        // Ensure RecyclerViews are visible
        binding.currentBooking.isVisible = upcoming.isNotEmpty()
        binding.historyBooking.isVisible = past.isNotEmpty()
    }
}
