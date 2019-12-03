package com.firebase.hackweek.tank18thscale

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.hackweek.tank18thscale.bluetooth.BluetoothDiscoveryReceiver
import com.firebase.hackweek.tank18thscale.model.DeviceInfo
import com.firebase.hackweek.tank18thscale.data.BluetoothDeviceList

private const val REQUEST_ENABLE_BT = 1

class DeviceListActivity : AppCompatActivity(), DeviceListAdapter.DeviceClickListener {

    private val bluetoothDeviceList = BluetoothDeviceList()

    private var bluetoothAdapter : BluetoothAdapter? = null

    private lateinit var viewModel: DevicesViewModel
    private lateinit var discoveryReceiver: BluetoothDiscoveryReceiver
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)
        recyclerView = findViewById(R.id.devices_list)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // TODO: Bail out if bluetoothAdapter is null

        val listAdapter = DeviceListAdapter(this)

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

    override fun onItemClick(deviceInfo: DeviceInfo) {
        // TODO: Send the appropriate device address back to establish the bluetooth connection
        Toast.makeText(this, "Selected ${deviceInfo.name}", Toast.LENGTH_SHORT).show()
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

        // TODO: Figure out why this isn't working.
        bluetoothAdapter?.startDiscovery()
    }
}
