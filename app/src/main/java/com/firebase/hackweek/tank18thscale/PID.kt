package com.firebase.hackweek.tank18thscale

import java.time.*

class PID(private val kP : Float = 1f, private val kI: Float = 0f, private val kD : Float = 0f) {

    private var currentTime = LocalDateTime.now()
    private var previousTime = currentTime
    private var previousError : Double = 0.0
    private var cP : Double = 0.0
    private var cI : Double = 0.0
    private var cD : Double = 0.0

    fun update(error : Double, sleep : Long = 100) : Double {
        //Thread.sleep(sleep)
        currentTime = LocalDateTime.now()
        val deltaTime = Duration.between(currentTime, previousTime).toMillis()

        val deltaError = error - previousError

        cP = error
        cI += error * deltaTime

        if (deltaTime > 0 ) {
            cD = deltaError  / deltaTime
        } else {
            cD = 0.0
        }

        previousTime = currentTime
        previousError = error

        return (kP * cP) + (kI * cI) + (kD * cD)
    }


}