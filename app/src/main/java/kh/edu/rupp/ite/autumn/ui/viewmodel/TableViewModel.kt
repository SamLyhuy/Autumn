package kh.edu.rupp.ite.autumn.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.TableData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TableViewModel: ViewModel() {

    private val _tableData = MutableLiveData<ApiState<List<TableData>>>()

    val tableData get() = _tableData

    fun loadingTableData(date: String) {
        var apiState = ApiState.loading<List<TableData>>()
        _tableData.postValue(apiState)
        Log.d("TableViewModel", "Loading table data...")

        viewModelScope.launch {
            try {
                val response = ApiClient.get().apiService.loadTable(date)
                Log.d("TableViewModel", "API Response: ${response}")

                if (response.isSuccessFetchTable()) {
                    apiState =ApiState.success(response.data ?: emptyList())
                    Log.d("TableViewModel", "Data fetched successfully: ${response.data}")
                } else {
                    apiState = ApiState.error(response.message)
                    Log.e("TableViewModel", "Error fetching data: ${response.message}")
                }
            }catch (ex: Exception) {
                apiState = ApiState.error(ex.message)
                Log.e("TableViewModel", "Exception occurred: ${ex.message}")
            }
            withContext(Dispatchers.Main) {
                _tableData.postValue(apiState)
            }
        }

    }
}