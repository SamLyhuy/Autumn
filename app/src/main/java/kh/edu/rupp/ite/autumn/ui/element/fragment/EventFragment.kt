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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kh.edu.rupp.ite.autumn.R
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
import java.util.Locale


class EventFragment: BaseFragment() {

    private val viewModel by viewModels<HomeViewModel>()

    private lateinit var binding: ActivityEventBinding

    private lateinit var calendarViewUser: CalendarView
    private lateinit var SelectedDateUser: TextView

    private var formattedDate: String? = null

    private var isInitialDataLoaded = false

    private var currentSelectedDate: Long = 0



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("EventFragment", "onCreateView called")
        binding = ActivityEventBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("EventFragment", "onViewCreated called")

        setupUi()
        setupListener()
        setupObserver()

        viewModel.loadingHomeData()

        calendarViewUser = view.findViewById(R.id.calendarViewUser)
        SelectedDateUser = view.findViewById(R.id.SelectedDateUser)


        calendarViewUser.setOnDateChangeListener{ _, year, month, dayOfMonth ->
            updateSelectedDate(year, month, dayOfMonth)
        }


    }

    private fun updateSelectedDate(year: Int, month: Int, dayOfMonth: Int) {

        val selectedDate = Calendar.getInstance().apply {
            set(year, month, dayOfMonth)
        }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        formattedDate = dateFormat.format(selectedDate.time)

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

    }

    private fun setupUi() {

    }
}