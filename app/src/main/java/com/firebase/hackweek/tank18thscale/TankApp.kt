package com.firebase.hackweek.tank18thscale

import android.app.Application

class TankApp : Application() {

    var tankInterface: TankInterface = LoggingTankInterface()
}