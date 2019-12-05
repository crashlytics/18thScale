package com.firebase.hackweek.tank18thscale

class Tilter(private val threshold: Float = 0f, private val ti : TankInterface) {
    fun tilt(angle: Float) {
        if (angle > 0 && angle > this.threshold) {
            ti.tiltUp()
        }
        if (angle < 0 && angle < (-1 * this.threshold)) {
            ti.tiltDown()
        }
    }
}