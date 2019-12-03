package com.firebase.hackweek.tank18thscale

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.hackweek.tank18thscale.bluetooth.BluetoothDiscoveryReceiver
import com.firebase.hackweek.tank18thscale.bluetooth.model.DeviceInfo

private const val REQUEST_ENABLE_BT = 1

class DeviceListActivity : AppCompatActivity() {

    private val bluetoothDeviceList = BluetoothDeviceList()

    private var bluetoothAdapter : BluetoothAdapter? = null

    private lateinit var viewModel: DevicesViewModel
    private lateinit var discoveryReceiver: BluetoothDiscoveryReceiver
    private lateinit var listAdapter: DeviceListAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)
        recyclerView = findViewById(R.id.devices_list)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        listAdapter = DeviceListAdapter()

        val viewModelFactory = DevicesViewModelFactory(bluetoothDeviceList)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DevicesViewModel::class.java)
        viewModel.devices.observe(this, Observer { devices ->
            listAdapter.setDevices(devices)
        })

        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        discoveryReceiver = BluetoothDiscoveryReceiver(bluetoothDeviceList)
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(discoveryReceiver, filter)
    }

    override fun onStart() {
        super.onStart()
        if (bluetoothAdapter?.isEnabled == false) {
            requestBluetoothPermission()
        } else {
            findBluetoothDevices()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothAdapter?.cancelDiscovery()
        // I think we might need to do this in the viewmodel maybe?
        unregisterReceiver(discoveryReceiver)
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
}
