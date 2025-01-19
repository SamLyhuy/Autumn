package kh.edu.rupp.ite.autumn.ui.element.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import kh.edu.rupp.ite.autumn.R
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.State
import kh.edu.rupp.ite.autumn.data.model.TableData
import kh.edu.rupp.ite.autumn.databinding.ActivityTableSetectionBinding
import kh.edu.rupp.ite.autumn.ui.viewmodel.TableViewModel
import java.util.Calendar

class TableBookingFragment : BaseFragment() {

    private val viewModel by viewModels<TableViewModel>()
    private lateinit var binding: ActivityTableSetectionBinding

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
        // Placeholder for any future UI setup logic
    }

    private fun setupListener() {
        binding.tvDate.setOnClickListener {
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

    }

    private fun fetchTableData(date: String) {
        if (date.isEmpty()) {
            Log.w("TableBookingFragment", "Date not provided")
            return
        }
        Log.d("TableBookingFragment", "Fetching data for date: $date")
        viewModel.loadingTableData(date)
    }

    private fun setupObserver() {
        viewModel.tableData.observe(viewLifecycleOwner) { state ->
            handleState(state)
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
                Log.e("TableBookingFragment", "State: Error, Message: ${state.message}")
                showAlert("TableBookingFragment", state.message ?: "Unexpected Error")
            }
            else -> {
                Log.w("TableBookingFragment", "Unhandled state: ${state.state}")
            }
        }
    }

    private fun showTable(tableDataList: List<TableData>) {
        Log.d("TableBookingFragment", "Displaying table data: $tableDataList")

        // Iterate through each TableData object and its booking info
        tableDataList.forEach { tableInfo ->
            tableInfo.booking_info.forEach { booking ->
                // For each table, check if it matches any of your views and update the background
                booking.tables.forEach { table ->
                    val tableId = table
                    Log.d("TableBookingFragment", "Displaying table data: $tableId")
                    val tableViewId = resources.getIdentifier(tableId, "id", requireContext().packageName)
                    Log.d("TableBookingFragment", "Displaying tableViewId: $tableViewId")
                    val tableView = view?.findViewById<View>(tableViewId)
                    Log.d("TableBookingFragment", "Displaying tableView: $tableView")

                    if (tableView != null) {
                        // Check if tableId contains letter and update the background based on that
                        when {
                            tableId.contains("A") -> {
                                tableView.setBackgroundResource(R.drawable.four_seat_unavailable) // For A tables
                            }
                            tableId.contains("B") -> {
                                tableView.setBackgroundResource(R.drawable.two_seat_unavailable) // For B tables
                            }
                            tableId.contains("C") -> {
                                tableView.setBackgroundResource(R.drawable.six_seat_unavailable) // For C tables
                            }
                            tableId.contains("D") -> {
                                tableView.setBackgroundResource(R.drawable.eight_seat_unavailable) // For D tables
                            }
                            else -> {
                                // Default background for other cases if necessary
                                tableView.setBackgroundResource(R.drawable.four_seat_unavailable)
                            }
                        }

                        // Set an OnClickListener to change the background when the table is selected
                        tableView.setOnClickListener {
                            when {
                                tableId.contains("A") -> {
                                    tableView.setBackgroundResource(R.drawable.four_seat_selected) // For A tables
                                }
                                tableId.contains("B") -> {
                                    tableView.setBackgroundResource(R.drawable.two_seat_selected) // For B tables
                                }
                                tableId.contains("C") -> {
                                    tableView.setBackgroundResource(R.drawable.six_seat_selected) // For C tables
                                }
                                tableId.contains("D") -> {
                                    tableView.setBackgroundResource(R.drawable.eight_seat_selected) // For D tables
                                }
                            }
                        }
                    }
                }
            }
        }
    }







}
