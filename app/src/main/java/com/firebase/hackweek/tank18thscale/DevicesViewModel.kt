package com.firebase.hackweek.tank18thscale

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.firebase.hackweek.tank18thscale.model.DeviceInfo
import com.firebase.hackweek.tank18thscale.data.BluetoothDeviceList

class DevicesViewModel(private val devicesList : BluetoothDeviceList) : ViewModel() {

    val devices : LiveData<List<DeviceInfo>> = devicesList.liveData

}
