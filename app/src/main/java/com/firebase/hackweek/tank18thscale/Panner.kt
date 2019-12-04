package com.firebase.hackweek.tank18thscale

class Panner(private val threshold: Float = 0f, private val ti : TankInterface) {
    fun pan(angle: Float) {
        if (angle > 0 && angle > this.threshold) {
            ti.turnLeft()
        }
        if (angle < 0 && angle < (-1 * this.threshold)) {
            ti.turnRight()
        }
    }
}