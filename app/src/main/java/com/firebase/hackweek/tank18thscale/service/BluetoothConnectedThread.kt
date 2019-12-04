package com.firebase.hackweek.tank18thscale.service

import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class BluetoothConnectedThread(private val connectedSocket: BluetoothSocket) : Thread() {

    private val inputStream: InputStream
    private val outputStream: OutputStream

    init {
        name = "Bluetooth ConnectedThread"

        var inStream: InputStream? = null
        var outStream: OutputStream? = null

        try {
            inStream = connectedSocket.inputStream
            outStream = connectedSocket.outputStream
        } catch (e: IOException) {
            Log.e("Tank18thScale", "Getting streams failed " + e.message)
        }

        inputStream = inStream!!
        outputStream = outStream!!
        // TODO: keep connection state

    }

    override fun run() {
        val buffer = ByteArray(1024)
        var bytes: Int

        // TODO: Keep connection state and do this while connected
        while(true) {
            try {
                bytes = inputStream.read(buffer)
            } catch (e: IOException) {
                Log.e("Tank18thScale", "Failed read: ${e.message}")
                break
            }
        }
    }

    fun write(bytes: ByteArray) {
        try {
            outputStream.write(bytes)
        } catch (e: IOException) {
            Log.e("Tank18thScale", "Failed write: ${e.message}")
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
