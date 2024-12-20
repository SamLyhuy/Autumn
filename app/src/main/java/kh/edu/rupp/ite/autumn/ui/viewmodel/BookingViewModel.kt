package kh.edu.rupp.ite.autumn.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookingViewModel: ViewModel() {

    private val _bookingData = MutableLiveData<ApiState<List<Category>>>()
    val bookingData get() = _bookingData

    fun loadBooking() {
        var apiState = ApiState.loading<List<Category>>()
        _bookingData.postValue(apiState)

        viewModelScope.launch {
            try {
                val response = ApiClient.get().apiService.loadBooking()

                if(response.isSuccess()) {
                    ApiState.success(response.data)
                } else {
                    ApiState.error(response.message)
                }
            } catch (ex: Exception) {
                ApiState.error(ex.message)
            }

            withContext(Dispatchers.Main) {
                _bookingData.postValue(apiState)
            }
        }
    }



}