package com.firebase.hackweek.tank18thscale.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.firebase.hackweek.tank18thscale.bluetooth.model.DeviceInfo

class BluetoothDiscoveryReceiver : BroadcastReceiver() {

    private val liveData : MutableLiveData<DeviceInfo> = MutableLiveData()

    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action) {
            BluetoothDevice.ACTION_FOUND -> {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                val device: BluetoothDevice =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                liveData.value = DeviceInfo(device.name, device.address)
            }
        }

    }
}