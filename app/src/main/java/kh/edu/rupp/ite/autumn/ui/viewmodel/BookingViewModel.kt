package kh.edu.rupp.ite.autumn.ui.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.EventData
import kh.edu.rupp.ite.autumn.data.model.PostEventRequest
import kh.edu.rupp.ite.autumn.data.model.TableData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookingViewModel: ViewModel() {

    private val _bookingData = MutableLiveData<ApiState<TableData>>()
    val bookingData get() = _bookingData

    fun booking(token: String, tableData: TableData) {
        Log.d("BookingViewModel", "Booking method called")
        var apiState = ApiState.loading<TableData>()
        _bookingData.postValue(apiState)

        viewModelScope.launch {
            try {
                Log.d("BookingViewModel", "Calling API with data: $tableData")
                Log.d("BookingViewModel", "Calling API with token: $token")
                val response = ApiClient.get().apiService.booking("Bearer $token", tableData)

                if (response.isSuccessBooking()) {
                    Log.d("BookingViewModel", "Booking successful: ${response.data}")

                    apiState = ApiState.success(response.data!!)  // Ensure you check for nullability
                } else {
                    Log.e("BookingViewModel", "Booking failed: ${response.message}")
                    apiState = ApiState.error(response.message)
                }

            } catch (ex: Exception) {
                Log.e("BookingViewModel", "Exception occurred: ${ex.message}")
                apiState = ApiState.error(ex.message)
            }

            withContext(Dispatchers.Main) {
                _bookingData.postValue(apiState)
            }
        }
    }




}