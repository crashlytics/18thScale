package com.firebase.hackweek.tank18thscale.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.firebase.hackweek.tank18thscale.BluetoothDeviceList
import com.firebase.hackweek.tank18thscale.bluetooth.model.DeviceInfo

class BluetoothDiscoveryReceiver(private val deviceList : BluetoothDeviceList) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("Tank18thScale", "Onreceive in recevier calls")
        when(intent?.action) {
            BluetoothDevice.ACTION_FOUND -> {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                val device: BluetoothDevice =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                val deviceInfo = DeviceInfo(device.name, device.address)
                Log.i("Tank18thScale", "Found a new device $deviceInfo")
                if (device.bondState != BluetoothDevice.BOND_BONDED) {
                    Log.i("Tank18thScale", "Device is new!")
                    deviceList.addDevice(deviceInfo)
                }
            }
        }

    }
}