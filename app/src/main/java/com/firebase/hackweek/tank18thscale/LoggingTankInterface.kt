package com.firebase.hackweek.tank18thscale

import android.util.Log

private const val TAG = "Tank18thScale"

class LoggingTankInterface : TankInterface {
    override fun moveForward() {
        log("Move forward")
    }

    override fun moveBackward() {
        log("Move backward")
    }

    override fun turnLeft() {
        log("Turn left")
    }

    override fun turnRight() {
        log("Turn right")
    }

    override fun tiltUp() {
        log("Tilt up")
    }

    override fun tiltDown() {
        log("Tilt down")
    }

    override fun turnOnLights() {
        log("Turn on lights")
    }

    override fun turnOffLights() {
        log("Turn off lights")
    }

    override fun blinkLights() {
        log("Blink lights")
    }

    private fun log(msg : String) {
        Log.i(TAG, msg)
    }
}
