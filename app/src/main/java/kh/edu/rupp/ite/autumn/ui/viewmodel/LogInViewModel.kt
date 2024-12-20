package kh.edu.rupp.ite.autumn.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.LogInResponse
import kh.edu.rupp.ite.autumn.global.AppPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LogInViewModel: ViewModel() {

   private val _logInData = MutableLiveData<ApiState<LogInResponse>>()
   val logInData get() = _logInData

   fun login(username: String, password: String) {
      var apiState = ApiState.loading<LogInResponse>()
      _logInData.postValue(apiState)

      viewModelScope.launch {
         try {
            val response = ApiClient.get().apiService.login(username, password)

            if(response.isSuccess()) {


               ApiState.success(response.data)
            } else {
               ApiState.error(response.message)
            }
         } catch (ex: Exception) {
            ApiState.error(ex.message)
         }

         withContext(Dispatchers.Main) {
            _logInData.postValue(apiState)
         }
      }
   }

}

