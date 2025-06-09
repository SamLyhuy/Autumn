package kh.edu.rupp.ite.autumn.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.TableData
import kh.edu.rupp.ite.autumn.data.model.UiMessage
import kh.edu.rupp.ite.autumn.ui.element.adapter.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TableViewModel: ViewModel() {

    private val _tableData = MutableLiveData<ApiState<List<TableData>>>()
    val tableData get() = _tableData

    // LiveData for “server‐response” messages
    private val _uiMessage = MutableLiveData<Event<UiMessage>>()
    val uiMessage get() = _uiMessage

    fun loadingTableData(date: String) {

        _tableData.postValue(ApiState.loading())
        Log.d("TableViewModel", "Loading table data...")

        viewModelScope.launch {
            try {
                val response = ApiClient.get().apiService.loadTable(date)
                Log.d("TableViewModel", "API Response: ${response}")

                if (response.isSuccessFetchTable()) {
                    // 1) API succeeded → notify observers
                    _tableData.postValue(ApiState.success(response.data!!))
                    // 2) Also post a “success” UiMessage for the dialog
                    _uiMessage.postValue(Event(UiMessage("Event submitted successfully!", true)))
                } else {
                    val message = response.message ?: "Unknown error"
                    _tableData.postValue(ApiState.error(message))
                    _uiMessage.postValue(Event(UiMessage("Failed: $message", false)))
                }
            }catch (ex: Exception) {
                val message = ex.message ?: "Network error"
                _tableData.postValue(ApiState.error(message))
                _uiMessage.postValue(Event(UiMessage("Failed: $message", false)))
            }
        }
    }
}