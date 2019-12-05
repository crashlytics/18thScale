package com.firebase.hackweek.tank18thscale

import android.util.Log
import com.firebase.hackweek.tank18thscale.service.BluetoothService

class BluetoothTankInterface(private val bluetoothService: BluetoothService) : TankInterface {
    override fun moveForward() {
        sendCommand("w")
    }

    override fun moveBackward() {
        sendCommand("s")
    }

    override fun turnLeft() {
        sendCommand("h")
    }

    override fun turnRight() {
        sendCommand("k")
    }

    override fun tiltUp() {
        sendCommand("u")
    }

    override fun tiltDown(){
        sendCommand("j")
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

    override fun turnGreen() {
        sendCommand("g")
    }

    override fun turnRed() {
        sendCommand("r")
    }

    private fun sendCommand(command: String) {
        Log.i("Tank18thScale", "Sending command = ${command}")
        bluetoothService.write(command.toByteArray())
    }
}
