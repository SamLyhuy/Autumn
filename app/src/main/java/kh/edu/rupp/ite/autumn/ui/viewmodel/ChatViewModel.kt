package kh.edu.rupp.ite.autumn.ui.viewmodel

import android.os.Message
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.ChatRequest
import kh.edu.rupp.ite.autumn.data.model.ChatResponse
import kh.edu.rupp.ite.autumn.data.model.FoodData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel : ViewModel() {

//    companion object {
//        private const val TAG = "ChatViewModel"
//    }

    // LiveData for successful AI replies
    private val _chatReply = MutableLiveData<ApiState<List<FoodData>>>()

    val chatReply get() = _chatReply

    // LiveData for errors occurring during the request
//    private val _error = MutableLiveData<String>()
//    val error: LiveData<String> = _error


    fun sendMessage(message: ChatRequest) {
        Log.d("ChatViewModel", "Request to AI: $message")
        var apiState = ApiState.loading<List<FoodData>>()
        _chatReply.postValue(apiState)


        viewModelScope.launch {
            try {
                // Call the ApiService to chat with AI
                val response = ApiClient.get().apiService.chatAI(message)
                if (response.data != null) {
                    apiState = ApiState.success(
                        data    = response.data,
                        message = response.message      // ← now this really goes into .message
                    )
                    Log.d("ChatViewModel", "Message : ${response.message}")
                    Log.d("ChatViewModel", "Data fetched successfully: ${response.data}")
                } else {
                    apiState = ApiState.error(response.message)
                    Log.e("ChatViewModel", "Error fetching data: ${response.message}")
                }

            }  catch (ex: Exception) {
                // Handle any exceptions during the API call
                apiState = ApiState.error(ex.message)
                Log.e("ChatViewModel", "Exception occurred: ${ex.message}")
            }

            withContext(Dispatchers.Main) {
                _chatReply.postValue(apiState)
            }
        }
    }
}
