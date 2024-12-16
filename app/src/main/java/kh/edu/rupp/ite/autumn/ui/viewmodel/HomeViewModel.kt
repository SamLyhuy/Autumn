//package kh.edu.rupp.ite.autumn.ui.viewmodel
//
//import android.util.Log
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
//import kh.edu.rupp.ite.autumn.data.model.ApiState
//import kh.edu.rupp.ite.autumn.data.model.Comment
//import kh.edu.rupp.ite.autumn.data.model.Test
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class HomeViewModel: ViewModel() {
//
//    private val _homeData = MutableLiveData<ApiState<List<Comment>>>()
//    val homeData get() = _homeData
//
//    fun loadingHomeData() {
//        var apiState = ApiState.loading<List<Comment>>()
//        _homeData.postValue(apiState)
//
//        viewModelScope.launch {
//            try {
//
//                val response = ApiClient.get().apiService.loadTest()
//
////                if (response.isSuccess()) {
////                   apiState = ApiState.success(response.data)
////                }else {
////                    apiState = ApiState.error(response.message)
////                }
//
//                if (response.statusCode == 200) {
//                   apiState = ApiState.success(response.data)
//                }else {
//                    apiState = ApiState.error(response.message)
//                }
//
//            }catch (ex: Exception) {
//                apiState = ApiState.error(ex.message)
//            }
//
//            withContext(Dispatchers.Main) {
//                _homeData.postValue(apiState)
//            }
//
//        }
//    }
//}

package kh.edu.rupp.ite.autumn.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.Comment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    private val _homeData = MutableLiveData<ApiState<List<Comment>>>()
    val homeData get() = _homeData

    fun loadingHomeData() {
        var apiState = ApiState.loading<List<Comment>>()
        _homeData.postValue(apiState)

        viewModelScope.launch {
            try {
                Log.d("ApiCall", "Making API call to fetch data")
                val response = ApiClient.get().apiService.loadTest()
                Log.d("ApiResponse", "Response: $response")

                if (response.statusCode == 200) {
                    Log.d("ApiResponse", "Data fetched: ${response.data}")
                    val data = response.data ?: emptyList()
                    apiState = ApiState.success(data)
                } else {
                    Log.e("ApiError", "Error Message: ${response.message}")
                    apiState = ApiState.error(response.message ?: "Unknown error")
                }

            } catch (ex: Exception) {
                Log.e("Exception", "Error fetching data: ${ex.message}")
                apiState = ApiState.error(ex.message ?: "Unknown error")
            }

            withContext(Dispatchers.Main) {
                _homeData.postValue(apiState)
            }
        }
    }

}
