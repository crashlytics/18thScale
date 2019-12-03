package com.firebase.hackweek.tank18thscale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.hackweek.tank18thscale.bluetooth.model.DeviceInfo

class BluetoothDeviceList {
    private val devices = mutableSetOf<DeviceInfo>()

    private val devicesLiveData = MutableLiveData<List<DeviceInfo>>()

    val liveData : LiveData<List<DeviceInfo>>
        get() = devicesLiveData

    fun addDevice(device : DeviceInfo) {
        devices.add(device)
        devicesLiveData.postValue(devices.toList().sortedBy { it.name })
    }

    // TODO: Remove devices too
}