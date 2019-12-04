package com.firebase.hackweek.tank18thscale.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException

// FIXME: This is probably dynamically retrievable device.uuids[0]
// private const val DEVICE_UUID = "00001101-0000-1000-8000-00805f9b34fb"

class BluetoothConnectThread(private val bluetoothAdapter: BluetoothAdapter, device: BluetoothDevice, private val connectListener: ConnectListener) : Thread() {
    private val connectSocket: BluetoothSocket

    init {
        name = "Bluetooth ConnectThread"
        var socket: BluetoothSocket? = null
        try {
            socket = createSocket(device)
        } catch (e: Exception) {
            Log.e("Tank18thScale", "Socket connection failed " + e.message)
        }
        // FIXME: This will blow up if an exception was thrown.
        connectSocket = socket!!
    }

    override fun run() {
        bluetoothAdapter.cancelDiscovery()

        try {
            connectSocket.connect()
        } catch (e: IOException) {
            Log.e("Tank18thScale", "Socket connect error " + e.message)
            try {
                connectSocket.close()
            } catch (closeError: IOException) {
                // TODO: log the failure
            }
            return
        }

        connectListener.onConnected(connectSocket)
    }

    fun cancel() {
        try {
            connectSocket.close()
        } catch (closeError: IOException) {
            // TODO: log the failure
        }
    }

    @Throws(Exception::class)
    private fun createSocket(device: BluetoothDevice) : BluetoothSocket {
//        return device.createRfcommSocketToServiceRecord(UUID.fromString(DEVICE_UUID))

        // Use the internal connect method to bypass the service record lookup
        val m = device.javaClass.getMethod(
            "createRfcommSocket",
            Int::class.javaPrimitiveType
        )
        return m.invoke(device, 1) as BluetoothSocket
    }

    interface ConnectListener {
        fun onConnected(socket: BluetoothSocket)
    }
}