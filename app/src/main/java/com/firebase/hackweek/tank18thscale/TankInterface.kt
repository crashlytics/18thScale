package com.firebase.hackweek.tank18thscale

/**
 * Communicates commands to the tank
 */
interface TankInterface {
    fun moveForward()
    fun moveBackward()
    fun turnLeft()
    fun turnRight()
    fun tiltUp()
    fun tiltDown()
    fun turnOnLights()
    fun turnOffLights()
    fun blinkLights()
    fun turnRed()
    fun turnGreen()
}
