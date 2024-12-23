package kh.edu.rupp.ite.autumn.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.LogInResponse
import kh.edu.rupp.ite.autumn.data.model.LogInState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LogInViewModel : ViewModel() {

   private val _logInData = MutableLiveData<LogInState>()
   val logInData get() = _logInData

   fun login(email: String, password: String) {
      // Set loading state initially
      _logInData.postValue(LogInState.loading())

      Log.d("LoginRequest", "Email: $email, Password: $password")

      viewModelScope.launch {
         try {
            Log.d("LogInViewModel", "Sending login request to server...")
            // API call to login
            val response = ApiClient.get().apiService.login(email, password)

            if (response.token.isNotEmpty()) {
               Log.d("LogInViewModel", "Server response received successfully.")
               _logInData.postValue(LogInState.success(response))
            } else {
               Log.e("LogInViewModel", "Login failed: No token received")
               _logInData.postValue(LogInState.error("Login failed: No token received"))
            }
         } catch (ex: Exception) {
            Log.e("LoginError", "Error during login", ex)
            _logInData.postValue(LogInState.error("An unexpected error occurred: ${ex.localizedMessage}"))
         }

      }
   }
}
