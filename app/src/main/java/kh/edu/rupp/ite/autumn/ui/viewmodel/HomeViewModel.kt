package kh.edu.rupp.ite.autumn.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.Test
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

    private val _homeData = MutableLiveData<ApiState<Test>>()
    val homeData get() = _homeData

    fun loadingHomeData() {
        val apiState = ApiState.loading<Test>()
        _homeData.postValue(ApiState)

        viewModelScope.launch {
            try {

            }
        }
    }
}