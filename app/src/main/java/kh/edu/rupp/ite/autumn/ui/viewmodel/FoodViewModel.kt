package kh.edu.rupp.ite.autumn.ui.viewmodel


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.FoodData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FoodViewModel: ViewModel() {

    private val _foodData = MutableLiveData<ApiState<List<FoodData>>>()

    val foodData get() = _foodData
    private val _drinkData = MutableLiveData<ApiState<List<FoodData>>>()
    val drinkData: MutableLiveData<ApiState<List<FoodData>>> = _drinkData


    fun loadingFoodData(type: String) {
        var apiState = ApiState.loading<List<FoodData>>()
        _foodData.postValue(apiState)

        viewModelScope.launch {
            try {
                val response = ApiClient.get().apiService.loadfood(type)

                if (response.isSuccessFetchFood()) {
//                    apiState =ApiState.success(response.data ?: emptyList())
//                    Log.d("FoodViewModel", "Data fetched successfully: ${response.data}")

                    if (type == "food") {
                        _foodData.value = ApiState.success(response.data ?: emptyList())
                    } else if (type == "drink") {
                        _drinkData.value = ApiState.success(response.data ?: emptyList())
                    }
                } else {
                    apiState = ApiState.error(response.message)
                    Log.e("FoodViewModel", "Error fetching data: ${response.message}")
                }
            }catch (ex: Exception) {
                apiState = ApiState.error(ex.message)
                Log.e("FoodViewModel", "Exception occurred: ${ex.message}")
            }
            withContext(Dispatchers.Main) {
                _foodData.postValue(apiState)
            }
        }
    }


}