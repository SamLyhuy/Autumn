package kh.edu.rupp.ite.autumn.ui.element.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import kh.edu.rupp.ite.autumn.R
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.BookingInfo
import kh.edu.rupp.ite.autumn.data.model.State

import kh.edu.rupp.ite.autumn.data.model.TableData
import kh.edu.rupp.ite.autumn.databinding.ActivityTableSetectionBinding
import kh.edu.rupp.ite.autumn.ui.viewmodel.BookingViewModel
import kh.edu.rupp.ite.autumn.ui.viewmodel.TableViewModel
import kh.edu.rupp.ite.visitme.global.AppEncryptedPref
import java.util.Calendar

class TableBookingFragment : BaseFragment() {

    private val tableViewModel by viewModels<TableViewModel>()
    private val bookingViewModel by viewModels<BookingViewModel>()
    private lateinit var binding: ActivityTableSetectionBinding
    private var isInitialDataLoaded = false

    private val selectedTables = mutableSetOf<String>() // To track selected table IDs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TableBookingFragment", "onCreateView called")
        binding = ActivityTableSetectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
        setupListener()
        setupObserver()
    }

    private fun setupUi() {
        // Get the current date
        val today = Calendar.getInstance()
        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH)
        val dayOfMonth = today.get(Calendar.DAY_OF_MONTH)

        // Format the date as YYYY-MM-DD
        val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
        Log.d("TableBookingFragment", "Current Date: $formattedDate")

        // Display the current date in the TextView
        binding.tvDate.text = formattedDate

        // Fetch table data for the current date
        fetchTableData(formattedDate)
    }



    private fun onClickDate() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Format year, month, and day with zero-padding
                val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                binding.tvDate.text = formattedDate
                Log.d("TableBookingFragment", "Date selected: $formattedDate")

                // Fetch data after date selection
                fetchTableData(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }



    private fun onClickSubmitBooking() {
        Log.d("TableBookingFragment", "Submit button clicked")
        val date = binding.tvDate.text.toString()

        // Validate if at least one table is selected
        if (selectedTables.isEmpty()) {
            showAlert("TableBookingFragment", "Please select at least one table.")
            return
        }

        // Validate if date is selected
        if (date.isEmpty()) {
            showAlert("TableBookingFragment", "Please select a date.")
            return
        }

        // Retrieve the authorization token
        val token = AppEncryptedPref.get().getToken(requireContext())
        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Authorization token is missing", Toast.LENGTH_SHORT).show()
            return
        }

        val bookingInfo = listOf(BookingInfo(tables = selectedTables.toList()))

        // Validate non-empty booking info
        if (bookingInfo.isEmpty()) {
            showAlert("TableBookingFragment", "Invalid table selection.")
            return
        }

        // Create TableData object
        val tableData = TableData(
            date = date,
            booking_info = bookingInfo
        )

        Log.d("TableBookingFragment", "Final data to be sent: $tableData")

        // Send the booking request via ViewModel
        bookingViewModel.booking(token, tableData)


    }


    private fun setupListener() {
        binding.tvDate.setOnClickListener {
            onClickDate()
        }

        binding.btnSubmitBooking.setOnClickListener {
            Log.d("TableBookingFragment", "Submit button pressed")
            onClickSubmitBooking()
        }

        binding.btnBack.setOnClickListener {
            navigateBack()
        }

    }

    private fun navigateBack() {
        // Use the appropriate navigation method for your project
        parentFragmentManager.popBackStack() // Navigates back to the previous fragment
    }



    private fun fetchTableData(date: String) {
        if (date.isEmpty()) {
            Log.w("TableBookingFragment", "Date not provided")
            return
        }
        Log.d("TableBookingFragment", "Fetching data for date: $date")
        tableViewModel.loadingTableData(date)
    }

    private fun setupObserver() {
        tableViewModel.tableData.observe(viewLifecycleOwner) { state ->
            handleState(state)
            if (!isInitialDataLoaded && state.state == State.success && state.data != null) {
                isInitialDataLoaded = true

                val today = android.icu.util.Calendar.getInstance()
                val year = today.get(android.icu.util.Calendar.YEAR)
                val month = today.get(android.icu.util.Calendar.MONTH)
                val dayOfMonth = today.get(android.icu.util.Calendar.DAY_OF_MONTH)


                val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                Log.d("TableBookingFragment", "Current Date : $formattedDate")
                fetchTableData(formattedDate)
            }
        }
        bookingViewModel.bookingData.observe(viewLifecycleOwner) { state ->
            handleStateA(state)}
    }
    private fun handleStateA(state: ApiState<TableData>) {
        when (state.state) {
            State.loading -> {
                showLoading()
                Log.d("TableBookingFragment", "State: Loading")
            }
            State.success -> {
                hideLoading()
// Fetch table data again after successful booking
                val selectedDate = binding.tvDate.text.toString()
                fetchTableData(selectedDate)
                Log.d("TableBookingFragment", "State: Success, Data: ${state.data}")
            }
            State.error -> {
                hideLoading()
                Log.e("TableBookingFragment", "State: Error, Message: ${state.message}")
                showAlert("TableBookingFragment", state.message ?: "Unexpected Error")
            }
            else -> {
                Log.w("TableBookingFragment", "Unhandled state: ${state.state}")
            }
        }
    }

    private fun handleState(state: ApiState<List<TableData>>) {
        when (state.state) {
            State.loading -> {
                showLoading()
                Log.d("TableBookingFragment", "State: Loading")
            }
            State.success -> {
                hideLoading()
                showTable(state.data ?: emptyList())
                Log.d("TableBookingFragment", "State: Success, Data: ${state.data}")
            }
            State.error -> {
                hideLoading()
                showTable(state.data ?: emptyList())
                Log.e("TableBookingFragment", "State: Error, Message: ${state.message}")
                showAlert("TableBookingFragment", state.message ?: "Unexpected Error")
            }
            else -> {
                showTable(state.data ?: emptyList())
                Log.w("TableBookingFragment", "Unhandled state: ${state.state}")
            }
        }
    }


    private fun showTable(tableDataList: List<TableData>) {
        Log.d("TableBookingFragment", "Displaying table data: $tableDataList")

        // Step 1: Define the master list of all table IDs
        val allTableIds = listOf(
            "table_A1", "table_A2", "table_A3", "table_A4", "table_A5", "table_A6",
            "table_B1", "table_B2", "table_B3", "table_B4", "table_B5", "table_B6",
            "table_B7", "table_B8", "table_B9", "table_B10",
            "table_C1", "table_C2",
            "table_D1", "table_D2"
        )

        // Extract booked tables from API response
        val bookedTables = mutableSetOf<String>()
        tableDataList.forEach { tableInfo ->
            tableInfo.booking_info.forEach { booking ->
                bookedTables.addAll(booking.tables)
            }
        }

        Log.d("TableBookingFragment", "Booked tables: $bookedTables")

        // Calculate available tables
        val availableTables = allTableIds.toSet() - bookedTables
        Log.d("TableBookingFragment", "Available tables: $availableTables")

        // Iterate through all tables and update their UI
        allTableIds.forEach { tableId ->
            val tableViewId = resources.getIdentifier(tableId, "id", requireContext().packageName)
            val tableView = view?.findViewById<View>(tableViewId)

            if (tableView != null) {
                if (bookedTables.contains(tableId)) {
                    // Table is booked, set the unavailable background
                    when {
                        tableId.contains("A") -> tableView.setBackgroundResource(R.drawable.four_seat_unavailable)
                        tableId.contains("B") -> tableView.setBackgroundResource(R.drawable.two_seat_unavailable)
                        tableId.contains("C") -> tableView.setBackgroundResource(R.drawable.six_seat_unavailable)
                        tableId.contains("D") -> tableView.setBackgroundResource(R.drawable.eight_seat_unavailable)
                    }
                } else {
                    // Table is available, set the appropriate available background based on type
                    when {
                        tableId.contains("A") -> tableView.setBackgroundResource(R.drawable.four_seat_available)
                        tableId.contains("B") -> tableView.setBackgroundResource(R.drawable.two_seat_available)
                        tableId.contains("C") -> tableView.setBackgroundResource(R.drawable.six_seat_available)
                        tableId.contains("D") -> tableView.setBackgroundResource(R.drawable.eight_seat_available)
                    }

                    // Set OnClickListener for toggle selection
                    tableView.setOnClickListener {
                        if (selectedTables.contains(tableId)) {
                            // Table is already selected, deselect it
                            selectedTables.remove(tableId)
                            Log.d("TableBookingFragment", "Table $tableId deselected")

                            // Set the table back to its available background
                            when {
                                tableId.contains("A") -> tableView.setBackgroundResource(R.drawable.four_seat_available)
                                tableId.contains("B") -> tableView.setBackgroundResource(R.drawable.two_seat_available)
                                tableId.contains("C") -> tableView.setBackgroundResource(R.drawable.six_seat_available)
                                tableId.contains("D") -> tableView.setBackgroundResource(R.drawable.eight_seat_available)
                            }
                        } else {
                            // Table is not selected, select it
                            selectedTables.add(tableId)
                            Log.d("TableBookingFragment", "Table $tableId selected")

                            // Change background to indicate selection
                            when {
                                tableId.contains("A") -> tableView.setBackgroundResource(R.drawable.four_seat_selected)
                                tableId.contains("B") -> tableView.setBackgroundResource(R.drawable.two_seat_selected)
                                tableId.contains("C") -> tableView.setBackgroundResource(R.drawable.six_seat_selected)
                                tableId.contains("D") -> tableView.setBackgroundResource(R.drawable.eight_seat_selected)
                            }
                        }
                    }
                }
            }
        }
    }


}
