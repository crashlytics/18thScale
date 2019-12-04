package com.firebase.hackweek.tank18thscale

import android.util.Log
import com.firebase.hackweek.tank18thscale.service.BluetoothService

class BluetoothTankInterface(private val bluetoothService: BluetoothService) : TankInterface {
    override fun stop() {
        sendCommand("x")
    }

    override fun moveForward() {
        sendCommand("w")
    }

    override fun moveBackward() {
        sendCommand("s")
    }

    override fun turnLeft() {
        sendCommand("a")
    }

    override fun turnRight() {
        sendCommand("d")
    }

    override fun turnOnLights() {
        // TODO
    }

    override fun turnOffLights() {
        // TODO
    }

    override fun blinkLights() {
        // TODO
    }

    private fun sendCommand(command: String) {
        Log.i("Tank18thScale", "Sending command = ${command}")
        bluetoothService.writeToConnectedDevice(command.toByteArray())
    }
}
