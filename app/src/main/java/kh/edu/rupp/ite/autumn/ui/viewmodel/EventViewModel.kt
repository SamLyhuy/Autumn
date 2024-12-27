// EventViewModel.kt
package kh.edu.rupp.ite.autumn.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.EventData
import kh.edu.rupp.ite.autumn.data.model.PostEventRequest
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {

    private val _eventData = MutableLiveData<ApiState<EventData>>()
    val eventData get() = _eventData

    fun postEvent(token: String, postEventRequest: PostEventRequest) {
        _eventData.postValue(ApiState.loading())

        viewModelScope.launch {
            try {
                val response = ApiClient.get().apiService.postEvent("Bearer $token", postEventRequest)

                if (response.isSuccess()) {
                    _eventData.postValue(ApiState.success(response.data!!))
                } else {
                    _eventData.postValue(ApiState.error(response.message))
                }
            } catch (e: Exception) {
                _eventData.postValue(ApiState.error(e.message))
            }
        }
    }
}
