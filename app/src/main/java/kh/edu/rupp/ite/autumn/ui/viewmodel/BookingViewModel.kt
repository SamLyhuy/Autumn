package kh.edu.rupp.ite.autumn.ui.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.EventData
import kh.edu.rupp.ite.autumn.data.model.PostEventRequest
import kh.edu.rupp.ite.autumn.data.model.TableData
import kh.edu.rupp.ite.autumn.data.model.UiMessage
import kh.edu.rupp.ite.autumn.ui.element.adapter.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookingViewModel: ViewModel() {

    private val _bookingData = MutableLiveData<ApiState<TableData>>()
    val bookingData get() = _bookingData

    // LiveData for “server‐response” messages
    private val _uiMessage = MutableLiveData<Event<UiMessage>>()
    val uiMessage: LiveData<Event<UiMessage>> = _uiMessage

    fun postBooking(token: String, tableData: TableData) {
        Log.d("BookingViewModel", "Booking method called")
        //var apiState = ApiState.loading<TableData>()
        _bookingData.postValue(ApiState.loading())

        viewModelScope.launch {
            try {
                Log.d("BookingViewModel", "Calling API with data: $tableData")
                Log.d("BookingViewModel", "Calling API with token: $token")
                val response = ApiClient.get().apiService.booking("Bearer $token", tableData)

                if (response.isSuccessBooking()) {
                    Log.d("BookingViewModel", "Booking successful: ${response.data}")
                    // 1) API succeeded → notify observers
                    _bookingData.postValue(ApiState.success(response.data!!))
                    // 2) Also post a “success” UiMessage for the dialog
                    _uiMessage.postValue(Event(UiMessage("Your Booking is successfully!", true)))
                } else {
                    val message = response.message ?: "Unknown error"

                    _bookingData.postValue(ApiState.error(message))

                    _uiMessage.postValue(Event(UiMessage("Failed: $message", false)))

                    Log.e("BookingViewModel", "Booking failed: ${response.message}")
                }

            } catch (ex: Exception) {
                val message = ex.message ?: "Network error"
                _bookingData.postValue(ApiState.error(message))
                Log.e("BookingViewModel", "Exception occurred: ${ex.message}")
            }

        }
    }




}