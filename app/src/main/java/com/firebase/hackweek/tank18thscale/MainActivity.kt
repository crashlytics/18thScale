package com.firebase.hackweek.tank18thscale

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.firebase.hackweek.tank18thscale.bluetooth.BluetoothDiscoveryReceiver
import com.firebase.hackweek.tank18thscale.bluetooth.model.DeviceInfo


const val REQUEST_ENABLE_BT = 1

class MainActivity : AppCompatActivity() {

    private val bluetoothDeviceList = BluetoothDeviceList()

    private lateinit var viewModel: DevicesViewModel
    private lateinit var viewModelFactory: DevicesViewModelFactory
    private lateinit var discoveryReceiver: BluetoothDiscoveryReceiver

    private var bluetoothAdapter : BluetoothAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            // Bluetooth is not available.
            throw RuntimeException("Bluetooth not available!")
        }

        viewModelFactory = DevicesViewModelFactory(bluetoothDeviceList)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DevicesViewModel::class.java)

        viewModel.devices.observe(this, Observer {
            it.forEach {
                Log.i("Tank18thScale", "${it.name} : ${it.address}")
            }
        })

        discoveryReceiver = BluetoothDiscoveryReceiver(bluetoothDeviceList)
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(discoveryReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("Tank18thScale", "Discovering now? ${bluetoothAdapter?.isDiscovering}")
        bluetoothAdapter?.cancelDiscovery()
        // I think we might need to do this in the viewmodel maybe?
        unregisterReceiver(discoveryReceiver)
    }

    override fun onStart() {
        super.onStart()
        if (bluetoothAdapter?.isEnabled == false) {
            requestBluetoothPermission()
        } else {
            findBluetoothDevices()
        }
    }

    private fun requestBluetoothPermission() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
    }

    private fun findBluetoothDevices() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceAddress = device.address // MAC address
            bluetoothDeviceList.addDevice(DeviceInfo(deviceName, deviceAddress))
        }
        if(bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter?.cancelDiscovery()
        }
        
        bluetoothAdapter?.startDiscovery()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ENABLE_BT ->
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so now we can connect to the tank
                    findBluetoothDevices()
                } else {
                    // User did not enable Bluetooth or an error occurred
                    // TODO
                }
        }
    }
}
