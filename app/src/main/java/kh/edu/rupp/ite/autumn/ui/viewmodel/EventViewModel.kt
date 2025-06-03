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

    companion object {
        private const val TAG = "EventViewModel"
    }

    // LiveData that emits loading/success/error states for posting an Event
    private val _eventData = MutableLiveData<ApiState<EventData>>()
    val eventData get() = _eventData

    /**
     * Sends a new event to the server.
     *
     * @param token            Bearer token string (e.g., "abc123")
     * @param postEventRequest Data object containing all fields needed to create an event
     */
    fun postEvent(token: String, postEventRequest: PostEventRequest) {
        Log.d(TAG, "postEvent() called with request: $postEventRequest")
        // 1) Immediately emit “loading” state to observers
        _eventData.postValue(ApiState.loading())

        viewModelScope.launch {
            try {
                Log.d(TAG, "Calling API: postEvent(...)")
                val response = ApiClient.get()
                    .apiService
                    .postEvent("Bearer $token", postEventRequest)

                // 2) Check if API responded with success
                if (response.isSuccessCreateEvent()) {
                    val createdEvent = response.data!!
                    Log.d(TAG, "API Success: created event with title='${createdEvent.event_info}'")
                    _eventData.postValue(ApiState.success(createdEvent))
                } else {
                    // 3) API returned an error code/message
                    val errorMsg = response.message
                    Log.e(TAG, "API Error posting event: $errorMsg")
                    _eventData.postValue(ApiState.error(errorMsg))
                }
            } catch (ex: Exception) {
                val message = ex.message ?: "Unknown exception"
                Log.e(TAG, "Exception in postEvent: $message")
                _eventData.postValue(ApiState.error(message))
            }
        }
    }
}
