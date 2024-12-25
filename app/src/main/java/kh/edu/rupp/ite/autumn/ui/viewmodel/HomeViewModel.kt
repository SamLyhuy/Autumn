package kh.edu.rupp.ite.autumn.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.EventData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel: ViewModel() {

    // LiveData to hold the API state, including loading, success, and error states
    private val _homeData = MutableLiveData<ApiState<List<EventData>>>()
    val homeData get() = _homeData

    // Function to load home data from the API
    fun loadingHomeData() {
        // Set the state to loading and notify observers
        var apiState = ApiState.loading<List<EventData>>()
        _homeData.postValue(apiState)
        Log.d("HomeViewModel", "Loading home data...")

        // Launch a coroutine to perform the API call
        viewModelScope.launch {
            try {
                // Perform the API call using the ApiClient
                val response = ApiClient.get().apiService.loadEvent()
                Log.d("HomeViewModel", "API Response: ${response}")

                // Check if the API response is successful
                if (response.isSuccess()) {
                    // Update the state with the successful data
                    apiState = ApiState.success(response.data ?: emptyList())
                    Log.d("HomeViewModel", "Data fetched successfully: ${response.data}")
                } else {
                    // Update the state with an error message from the API
                    apiState = ApiState.error(response.message)
                    Log.e("HomeViewModel", "Error fetching data: ${response.message}")
                }
            } catch (ex: Exception) {
                // Handle any exceptions during the API call
                apiState = ApiState.error(ex.message)
                Log.e("HomeViewModel", "Exception occurred: ${ex.message}")
            }

            // Post the updated state to the LiveData on the main thread
            withContext(Dispatchers.Main) {
                _homeData.postValue(apiState)
            }
        }
    }
}
