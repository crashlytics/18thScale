package com.firebase.hackweek.tank18thscale

class Blinker(private val threshold: Float = 0.5f, private val ti: TankInterface) {
    fun blink(happiness: Float) {
        if (happiness > this.threshold) {
            ti.turnGreen()
        }
        else {
            ti.turnRed()
        }
        return
    }
}