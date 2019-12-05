package com.firebase.hackweek.tank18thscale.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket

class BluetoothService(private val bluetoothAdapter: BluetoothAdapter) : BluetoothConnectThread.ConnectListener, BluetoothConnectedThread.ConnectionMonitor {

    var isConnected = false

    private var connectThread: BluetoothConnectThread? = null
    private var connectedThread: BluetoothConnectedThread? = null

    private var onConnectedListener: OnConnectedListener? = null

    override fun onConnected(socket: BluetoothSocket) {
        connected(socket)
    }

    override fun onConnectionFailure() {
        onConnectedListener?.onConnectionFailure()
        onConnectedListener = null
        isConnected = false
    }

    override fun onConnectionLost() {
        onConnectedListener = null
        isConnected = false
    }

    @Synchronized fun connect(device: BluetoothDevice, onConnectedListener: OnConnectedListener) {
        this.onConnectedListener = onConnectedListener
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

    fun write(bytes: ByteArray) {
        var t: BluetoothConnectedThread?
        synchronized(this) {
            if (!isConnected) return
            t = connectedThread
        }
        t?.write(bytes)
    }

    private fun connected(socket: BluetoothSocket) {
        connectThread = null // connectThread has finished its work
        synchronized(this) {
            clearThreads()

            connectedThread = BluetoothConnectedThread(socket, this)
            connectedThread?.start()
            isConnected = true
            onConnectedListener?.onConnected()
        }
    }

    interface OnConnectedListener {
        fun onConnected()
        fun onConnectionFailure()
    }
}
