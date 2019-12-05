// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.firebase.hackweek.tank18thscale


import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

import com.google.android.gms.common.annotation.KeepName
import com.google.firebase.ml.common.FirebaseMLException
import com.firebase.hackweek.tank18thscale.common.CameraSource
//import kotlinx.android.synthetic.main.activity_live_preview.facingSwitch
import kotlinx.android.synthetic.main.activity_live_preview.fireFaceOverlay
import kotlinx.android.synthetic.main.activity_live_preview.firePreview
import java.io.IOException


/** Demo app showing the various features of ML Kit for Firebase. This class is used to
 * set up continuous frame processing on frames from a camera source.  */
@KeepName
class LivePreviewActivity : AppCompatActivity(), OnRequestPermissionsResultCallback {

    private var cameraSource: CameraSource? = null
    private var selectedModel = FACE_DETECTION
    private lateinit var panner: Panner
    private lateinit var tilter: Tilter

    private val requiredPermissions: Array<String?>
        get() {
            return try {
                val info = this.packageManager
                    .getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS)
                val ps = info.requestedPermissions
                if (ps != null && ps.isNotEmpty()) {
                    ps
                } else {
                    arrayOfNulls(0)
                }
            } catch (e: Exception) {
                arrayOfNulls(0)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        setContentView(R.layout.activity_live_preview)

        if (firePreview == null) {
            Log.d(TAG, "Preview is null")
        }

        if (fireFaceOverlay == null) {
            Log.d(TAG, "graphicOverlay is null")
        }


//        val facingSwitch = facingSwitch
//        facingSwitch.setOnCheckedChangeListener(this)
//        // Hide the toggle button if there is only 1 camera
//        if (Camera.getNumberOfCameras() == 1) {
//            facingSwitch.visibility = View.GONE
//        }

        if (allPermissionsGranted()) {
            createCameraSource(selectedModel)
        } else {
            getRuntimePermissions()
        }
    }


//    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
//        Log.d(TAG, "Set facing")
//
//        cameraSource?.let {
//            if (isChecked) {
//                it.setFacing(CameraSource.CAMERA_FACING_FRONT)
//            } else {
//                it.setFacing(CameraSource.CAMERA_FACING_BACK)
//            }
//        }
//        firePreview?.stop()
//        startCameraSource()
//    }

    private var bluetoothConnected = false

    private fun createCameraSource(model: String) {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = CameraSource(this, fireFaceOverlay)
        }

        val ti = (application as TankApp).tankInterface
        panner = Panner(150f, ti)
        tilter = Tilter(150f, ti)

        var faceMovementWatcher = object : FaceDetectionProcessor.FaceMovementWatcher {
            override fun onFaceMove(panError: Float, tiltError: Float) {
                if (bluetoothConnected) {
                    panner.pan(panError)
                    tilter.tilt(tiltError)
                }
            }
        }

        cameraSource?.setFacing(CameraSource.CAMERA_FACING_FRONT)

        try {
            when (model) {
                FACE_DETECTION -> {
                    Log.i(TAG, "Using Face Detector Processor")
                    cameraSource?.setMachineLearningFrameProcessor(FaceDetectionProcessor(resources, faceMovementWatcher))
                }

                else -> Log.e(TAG, "Unknown model: $model")
            }
        } catch (e: FirebaseMLException) {
            Log.e(TAG, "can not create camera source: $model")
        }
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private fun startCameraSource() {
        cameraSource?.let {
            try {
                if (firePreview == null) {
                    Log.d(TAG, "resume: Preview is null")
                }
                if (fireFaceOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null")
                }
                firePreview?.start(cameraSource, fireFaceOverlay)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                cameraSource?.release()
                cameraSource = null
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        startCameraSource()
        // TODO Should do this on a callback when it's *actually* connected
        Handler().postDelayed({ bluetoothConnected = true }, 5000)
    }

    /** Stops the camera.  */
    override fun onPause() {
        super.onPause()
        firePreview?.stop()
    }

    public override fun onDestroy() {
        super.onDestroy()
        cameraSource?.release()
    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in requiredPermissions) {
            if (!isPermissionGranted(this, permission!!)) {
                return false
            }
        }
        return true
    }

    private fun getRuntimePermissions() {
        val allNeededPermissions = arrayListOf<String>()
        for (permission in requiredPermissions) {
            if (!isPermissionGranted(this, permission!!)) {
                allNeededPermissions.add(permission)
            }
        }

        if (allNeededPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this, allNeededPermissions.toTypedArray(), PERMISSION_REQUESTS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "Permission granted!")
        if (allPermissionsGranted()) {
            createCameraSource(selectedModel)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val FACE_DETECTION = "Face Detection"
        private const val TAG = "LivePreviewActivity"
        private const val PERMISSION_REQUESTS = 1

        private fun isPermissionGranted(context: Context, permission: String): Boolean {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(TAG, "Permission granted: $permission")
                return true
            }
            Log.i(TAG, "Permission NOT granted: $permission")
            return false
        }
    }
}
