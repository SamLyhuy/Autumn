package kh.edu.rupp.ite.autumn.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.RegisterData
import kotlinx.coroutines.launch

class SignUpViewModel: ViewModel() {

    private val _registerData = MutableLiveData<ApiState<RegisterData>>()
    val registerData get() = _registerData

    fun postRegister(registerData: RegisterData) {
        _registerData.postValue(ApiState.loading())

        viewModelScope.launch {
            try {
                val response = ApiClient.get().apiService.register(registerData)

                Log.d("SignUpViewModel",  "response: ${response}")

                if (response.isSuccessSignUp()) {
                    _registerData.postValue(ApiState.success(response.data!!))
                    Log.d("SignUpViewModel", "${response.message}")
                    Log.d("SignUpViewModel", "${response.data}")


                } else {
                    _registerData.postValue(ApiState.error(response.message))
                    Log.e("SignUpViewModel", "${response.message}")
                }
            } catch (e: Exception) {
                _registerData.postValue(ApiState.error(e.message))
            }
        }
    }
}