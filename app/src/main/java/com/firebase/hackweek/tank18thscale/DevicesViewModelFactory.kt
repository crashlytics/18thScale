package com.firebase.hackweek.tank18thscale

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firebase.hackweek.tank18thscale.data.BluetoothDeviceList

class DevicesViewModelFactory(private val devicesList : BluetoothDeviceList) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DevicesViewModel::class.java)) {
            return DevicesViewModel(devicesList) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}