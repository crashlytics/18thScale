package com.firebase.hackweek.tank18thscale.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler

class BluetoothService(private val bluetoothAdapter: BluetoothAdapter) : BluetoothConnectThread.ConnectListener, BluetoothConnectedThread.ConnectionMonitor {

    private lateinit var device: BluetoothDevice
    var isConnected = false

    private val handler = Handler()
    private var connectThread: BluetoothConnectThread? = null
    private var connectedThread: BluetoothConnectedThread? = null

    private var onConnectedListener: OnConnectedListener? = null

    override fun onConnected(socket: BluetoothSocket) {
        connected(socket)
    }

    override fun onConnectionFailure() {
        onConnectedListener?.onConnectionFailure()
        isConnected = false
    }

    override fun onConnectionLost() {
        isConnected = false
        reconnect(onConnectedListener!!)
    }

    @Synchronized fun connect(device: BluetoothDevice, onConnectedListener: OnConnectedListener) {
        this.device = device
        this.onConnectedListener = onConnectedListener
        clearThreads()

        connectThread = BluetoothConnectThread(bluetoothAdapter, device, this)
        connectThread?.start()
    }

    @Synchronized fun reconnect(onConnectedListener: OnConnectedListener) {
        var retryTimes = 30
        val task = object : Runnable {
            override fun run() {
                if (!isConnected && retryTimes > 0) {
                    connect(device, onConnectedListener)
                    retryTimes--
                }
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(task)
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
