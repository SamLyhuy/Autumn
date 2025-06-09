package kh.edu.rupp.ite.autumn.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.FoodData
import kh.edu.rupp.ite.autumn.data.model.PostFoodRequest
import kh.edu.rupp.ite.autumn.data.model.UiMessage
import kh.edu.rupp.ite.autumn.ui.element.adapter.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FoodViewModel : ViewModel() {

    companion object {
        private const val TAG = "FoodViewModel"
    }

    // LiveData for fetching a list of food items
    private val _foodData = MutableLiveData<ApiState<List<FoodData>>>()
    val foodData get() = _foodData

    // LiveData for fetching a list of drinks
    private val _drinkData = MutableLiveData<ApiState<List<FoodData>>>()
    val drinkData get() = _drinkData

    // LiveData for posting a new food item
    private val _postFood = MutableLiveData<ApiState<FoodData>>()
    val postFood get() = _postFood

    // LiveData for “server‐response” messages
    private val _uiMessage = MutableLiveData<Event<UiMessage>>()
    val uiMessage: LiveData<Event<UiMessage>> = _uiMessage


    /**
     * Fetches either “food” or “drink” items from the API.
     *
     * @param type "food" or "drink"
     */
    fun loadingFoodData(type: String) {
        Log.d(TAG, "loadingFoodData() called with type: $type")
        // Immediately show a loading state
        _foodData.postValue(ApiState.loading())

        viewModelScope.launch {
            try {
                Log.d(TAG, "Calling API: loadfood($type)")
                val response = ApiClient.get().apiService.loadfood(type)

                if (response.isSuccessFetchFood()) {
                    val items = response.data ?: emptyList()
                    Log.d(TAG, "API Success: fetched ${items.size} items for type '$type'")

                    // Post to the proper LiveData
                    if (type == "food") {
                        _foodData.postValue(ApiState.success(items))
                    } else {
                        _drinkData.postValue(ApiState.success(items))
                    }
                } else {
                    val errorMsg = response.message
                    Log.e(TAG, "API Error fetching '$type': $errorMsg")
                    _foodData.postValue(ApiState.error(errorMsg))
                }
            } catch (ex: Exception) {
                val message = ex.message ?: "Unknown exception"
                Log.e(TAG, "Exception in loadingFoodData: $message")
                _foodData.postValue(ApiState.error(message))
            }
        }
    }


    /**
     * Sends a new food item to the server.
     *
     * @param token            Bearer token string (e.g., "abc123")
     * @param postFoodRequest  Data object containing all fields needed to create a food
     */
    fun postFood(token: String, postFoodRequest: PostFoodRequest) {
        Log.d(TAG, "postFood() called with request: $postFoodRequest")
        // Immediately show a loading state
        _postFood.postValue(ApiState.loading())

        viewModelScope.launch {
            try {
                Log.d(TAG, "Calling API: postFood(...)")
                val response = ApiClient.get()
                    .apiService
                    .postFood("Bearer $token", postFoodRequest)

                if (response.isSuccessCreateFood()) {
                    val createdItem = response.data!!
                    Log.d(TAG, "API Success: created food item with ID=${createdItem.name}")
                    _postFood.postValue(ApiState.success(createdItem))
                    _uiMessage.postValue(Event(UiMessage("Food Created successfully!", true)))
                } else {
                    val message = response.message
                    Log.e(TAG, "API Error posting food: $message")
                    _postFood.postValue(ApiState.error(message))
                    _uiMessage.postValue(Event(UiMessage("Failed: $message", false)))
                }
            } catch (ex: Exception) {
                val message = ex.message ?: "Unknown exception"
                Log.e(TAG, "Exception in postFood: $message")
                _postFood.postValue(ApiState.error(message))
                _uiMessage.postValue(Event(UiMessage("Failed: $message", false)))
            }
        }
    }
}
