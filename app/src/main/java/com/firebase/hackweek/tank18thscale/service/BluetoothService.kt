package com.firebase.hackweek.tank18thscale.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

// FIXME: Get the appropriate UUID for the device
private const val DEVICE_UUID = "d7d5df12-c0b3-437b-811d-530958260118"

class BluetoothService(private val bluetoothAdapter: BluetoothAdapter) {

    var connectionState = ConnectionState.NONE
        private set
    var connectThread: ConnectThread? = null
    var connectedThread: ConnectedThread? = null

    @Synchronized fun connect(device: BluetoothDevice) {
        clearThreads()

        connectThread = ConnectThread(bluetoothAdapter, device)
        connectThread?.start()
    }

    @Synchronized fun connected(socket: BluetoothSocket, device: BluetoothDevice) {
        clearThreads()

        connectedThread = ConnectedThread(socket)
        connectedThread?.start()
    }

    @Synchronized fun clearThreads() {
        connectThread?.cancel()
        connectThread = null

        connectedThread?.cancel()
        connectedThread = null
    }

    fun writeToConnectedDevice(bytes: ByteArray) {
        var t: ConnectedThread?
        synchronized(this) {
            if (connectionState != ConnectionState.CONNECTED) {
                return
            }
            t = connectedThread
        }
        t?.write(bytes)
    }

    enum class ConnectionState {
        NONE, CONNECTING, CONNECTED
    }

    class ConnectThread(private val bluetoothAdapter: BluetoothAdapter, device: BluetoothDevice) : Thread() {
        private val connectSocket: BluetoothSocket? // TODO: Make this non-null

        init {
            name = "Bluetooth ConnectThread"
            var socket: BluetoothSocket? = null
            try {
                socket = device.createRfcommSocketToServiceRecord(UUID.fromString(DEVICE_UUID))
            } catch (e: IOException) {
                // TODO: Socket creation failed
            }
            connectSocket = socket
            // TODO: track connection state
//            connectionState = ConnectionState.CONNECTING
        }

        override fun run() {
            bluetoothAdapter.cancelDiscovery()

            try {
                connectSocket?.connect()
            } catch (e: IOException) {
                try {
                    connectSocket?.close()
                } catch (closeError: IOException) {
                    // TODO: log the failure
                }
                return
            }

            // TODO: Drop the reference to this thread
            // TODO: Engage the long-running "connected" thread.
        }

        fun cancel() {
            try {
                connectSocket?.close()
            } catch (closeError: IOException) {
                // TODO: log the failure
            }
        }
    }

    class ConnectedThread(private val connectedSocket: BluetoothSocket) : Thread() {
        private val inputStream: InputStream?
        private val outputStream: OutputStream?

        init {
            name = "Bluetooth ConnectedThread"

            var inStream: InputStream? = null
            var outStream: OutputStream? = null

            try {
                inStream = connectedSocket.inputStream
                outStream = connectedSocket.outputStream
            } catch (e: IOException) {
                // TODO: Log the failure
            }

            inputStream = inStream
            outputStream = outStream
            // TODO: keep connection state

        }

        override fun run() {
            val buffer = ByteArray(1024)
            var bytes: Int

            // TODO: Keep connection state and do this while connected
            while(true) {
                try {
                    bytes = inputStream?.read(buffer) ?: 0
                } catch (e: IOException) {
                    break
                    // TODO: Do better.
                }
            }
        }

        fun write(bytes: ByteArray) {
            try {
                outputStream?.write(bytes)
            } catch (e: IOException) {
                // TODO
            }
        }

        fun cancel() {
            try {
                connectedSocket.close()
            } catch (closeError: IOException) {
                // TODO: log the failure
            }
        }
    }
}