package com.firebase.hackweek.tank18thscale.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log

class BluetoothService(private val bluetoothAdapter: BluetoothAdapter) : BluetoothConnectThread.ConnectListener {

    var connectThread: BluetoothConnectThread? = null
    var connectedThread: BluetoothConnectedThread? = null

    override fun onConnected(socket: BluetoothSocket) {
        connected(socket)
    }

    @Synchronized fun connect(device: BluetoothDevice) {
        clearThreads()

        connectThread = BluetoothConnectThread(bluetoothAdapter, device, this)
        connectThread?.start()
    }

    @Synchronized fun clearThreads() {
        connectThread?.cancel()
        connectThread = null

        connectedThread?.cancel()
        connectedThread = null
    }

    fun writeToConnectedDevice(bytes: ByteArray) {
        Log.i("Tank18thScale", "Writing: $bytes")
        var t: BluetoothConnectedThread?
        synchronized(this) {
            t = connectedThread
        }
        t?.write(bytes)
    }

    private fun connected(socket: BluetoothSocket) {
        connectThread = null // connectThread has finished its work
        synchronized(this) {
            clearThreads()

            connectedThread = BluetoothConnectedThread(socket)
            connectedThread?.start()
        }
    }
}