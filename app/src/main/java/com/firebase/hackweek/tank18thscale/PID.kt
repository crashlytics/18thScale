package com.firebase.hackweek.tank18thscale

import java.time.*

class PID(private val kP : Float = 1f, private val kI: Float = 0f, private val kD : Float = 0f) {

    private var currentTime = LocalDateTime.now()
    private var previousTime = currentTime
    private var previousError : Float = 0f
    private var cP : Float = 0f
    private var cI : Float = 0f
    private var cD : Float = 0f

    fun update(error : Float, sleep : Long = 100) : Float {
       // Thread.sleep(sleep)
        currentTime = LocalDateTime.now()
        val deltaTime = Duration.between(currentTime, previousTime).toMillis()

        val deltaError = error - previousError

        cP = error
        cI += error * deltaTime

        if (deltaTime > 0 ) {
            cD = deltaError  / deltaTime
        } else {
            cD = 0f
        }

        previousTime = currentTime
        previousError = error

        return (kP * cP) + (kI * cI) + (kD * cD)
    }
}