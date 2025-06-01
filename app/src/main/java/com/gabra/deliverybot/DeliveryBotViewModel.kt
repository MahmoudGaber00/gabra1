package com.gabra.deliverybot

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DeliveryBotViewModel(application: Application) : AndroidViewModel(application) {
    private val _serviceState = MutableStateFlow(false)
    val serviceState: StateFlow<Boolean> = _serviceState

    private val _testingMode = MutableStateFlow(false)
    val testingMode: StateFlow<Boolean> = _testingMode

    private val _log = MutableStateFlow("")
    val log: StateFlow<String> = _log

    fun setServiceState(enabled: Boolean) {
        viewModelScope.launch {
            _serviceState.emit(enabled)
        }
    }

    fun setTestingMode(enabled: Boolean) {
        viewModelScope.launch {
            _testingMode.emit(enabled)
        }
    }

    fun updateLog(message: String) {
        viewModelScope.launch {
            val currentLog = _log.value
            val newLog = if (currentLog.isEmpty()) {
                message
            } else {
                "$currentLog\n$message"
            }
            _log.emit(newLog)
        }
    }
}
