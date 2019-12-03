package com.firebase.hackweek.tank18thscale

/**
 * Communicates commands to the tank
 */
interface TankInterface {
    fun stop()
    fun moveForward()
    fun moveBackward()
    fun turnLeft()
    fun turnRight()
    fun turnOnLights()
    fun turnOffLights()
    fun blinkLights()
}
