// EventViewModel.kt
package kh.edu.rupp.ite.autumn.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.*
import kh.edu.rupp.ite.autumn.ui.element.adapter.Event
import kh.edu.rupp.ite.autumn.data.model.UiMessage
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {

    // LiveData for API success/error
    private val _eventData = MutableLiveData<ApiState<EventData>>()
    val eventData: LiveData<ApiState<EventData>> = _eventData

    // LiveData for “server‐response” messages
    private val _uiMessage = MutableLiveData<Event<UiMessage>>()
    val uiMessage: LiveData<Event<UiMessage>> = _uiMessage

    fun postEvent(token: String, request: PostEventRequest) {
        Log.d("EventViewModel", "postEvent() called")
        _eventData.postValue(ApiState.loading())

        viewModelScope.launch {
            try {
                val response = ApiClient.get().apiService.postEvent("Bearer $token", request)

                if (response.isSuccessCreateEvent()) {
                    // 1) API succeeded → notify observers
                    _eventData.postValue(ApiState.success(response.data!!))
                    // 2) Also post a “success” UiMessage for the dialog
                    _uiMessage.postValue(Event(UiMessage("Event submitted successfully!", true)))
                } else {
                    val message = response.message ?: "Unknown error"
                    _eventData.postValue(ApiState.error(message))
                    _uiMessage.postValue(Event(UiMessage("Failed: $message", false)))
                }
            } catch (ex: Exception) {
                val message = ex.message ?: "Network error"
                _eventData.postValue(ApiState.error(message))
                _uiMessage.postValue(Event(UiMessage("Failed: $message", false)))
            }
        }
    }
}
