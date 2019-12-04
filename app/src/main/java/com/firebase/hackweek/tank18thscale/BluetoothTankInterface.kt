package com.firebase.hackweek.tank18thscale

import com.firebase.hackweek.tank18thscale.service.BluetoothService

class BluetoothTankInterface(private val bluetoothService: BluetoothService) : TankInterface {
    override fun stop() {
        // TODO
    }

    override fun moveForward() {
        sendCommand("W")
    }

    override fun moveBackward() {
        sendCommand("S")
    }

    override fun turnLeft() {
        sendCommand("A")
    }

    override fun turnRight() {
        sendCommand("D")
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
        bluetoothService.writeToConnectedDevice(command.toByteArray())
    }
}