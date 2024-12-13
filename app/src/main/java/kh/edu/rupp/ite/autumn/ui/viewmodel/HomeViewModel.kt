package kh.edu.rupp.ite.autumn.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.Comment
import kh.edu.rupp.ite.autumn.data.model.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel: ViewModel() {

    private val _homeData = MutableLiveData<ApiState<Comment>>()
    val homeData get() = _homeData

    fun loadingHomeData() {
        var apiState = ApiState.loading<Comment>()
        _homeData.postValue(apiState)

        viewModelScope.launch {
            try {

                val response = ApiClient.get().apiService.loadTest()
                if (response.isSuccess()) {
                    Log.d("API Response", response.toString())
                    apiState = ApiState.success(response.comments)
                }else {
                    //apiState = ApiState.error(response.message)
                }

            }catch (ex: Exception) {
                apiState = ApiState.error(ex.message)
            }

            withContext(Dispatchers.Main) {
                _homeData.postValue(apiState)
            }

        }
    }
}