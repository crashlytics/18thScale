package com.firebase.hackweek.tank18thscale

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity



const val REQUEST_ENABLE_BT = 1

class MainActivity : AppCompatActivity() {

    private var bluetoothAdapter : BluetoothAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            // Bluetooth is not enabled.
            throw RuntimeException("Bluetooth not enabled!")
        }
    }

    override fun onStart() {
        super.onStart()
        if (bluetoothAdapter?.isEnabled == true) {
        } else {
            requestBluetoothPermission()
        }
    }

    private fun requestBluetoothPermission() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ENABLE_BT ->
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so now we can connect to the tank

                } else {
                    // User did not enable Bluetooth or an error occurred
                    // TODO
                }
        }
    }
}
