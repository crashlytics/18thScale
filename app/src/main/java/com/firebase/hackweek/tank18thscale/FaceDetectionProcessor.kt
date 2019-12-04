package com.firebase.hackweek.tank18thscale

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.firebase.hackweek.tank18thscale.R
import com.firebase.hackweek.tank18thscale.common.CameraImageGraphic
import com.firebase.hackweek.tank18thscale.common.FrameMetadata
import com.firebase.hackweek.tank18thscale.common.GraphicOverlay
import java.io.IOException



/** Face Detector Demo.  */
class FaceDetectionProcessor(res: Resources) : VisionProcessorBase<List<FirebaseVisionFace>>() {

    private val detector: FirebaseVisionFaceDetector

    private val overlayBitmap: Bitmap
    // @GuardedBy("processorLock")
    private val panProcessor: PID
    private val tiltProcessor: PID
    private val panner: Panner
    private val tilter: Tilter

    init {
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .enableTracking()
            .build()

        detector = FirebaseVision.getInstance().getVisionFaceDetector(options)

        overlayBitmap = BitmapFactory.decodeResource(res, R.drawable.clown_nose)

        panProcessor = PID(0.09f, 0.08f, 0.002f)
        tiltProcessor = PID(0.11f, 0.10f, 0.002f)
        panner = Panner(0f, LoggingTankInterface())
        tilter = Tilter(0f, LoggingTankInterface())

    }

//    private class FaceInfo(overlay: GraphicOverlay, private val firebaseVisionFace: FirebaseVisionFace) : GraphicOverlay.Graphic(overlay) {
//        fun getPanError() : Float {
//            val faceCenterX = translateX(firebaseVisionFace.boundingBox.centerX().toFloat())
//            return overlayCenterX - faceCenterX
//        }
//
//        fun getTiltError() : Float {
//            val faceCenterY = translateY(firebaseVisionFace.boundingBox.centerY().toFloat())
//            return overlayCenterY - faceCenterY
//        }
//        override fun draw(canvas: Canvas){}
//    }


    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Face Detector: $e")
        }
    }

    override fun detectInImage(image: FirebaseVisionImage): Task<List<FirebaseVisionFace>> {
        return detector.detectInImage(image)
    }

    override fun onSuccess(
        originalCameraImage: Bitmap?,
        results: List<FirebaseVisionFace>,
        frameMetadata: FrameMetadata,
        graphicOverlay: GraphicOverlay
    ) {
        graphicOverlay.clear()
        val imageGraphic = CameraImageGraphic(graphicOverlay, originalCameraImage)
        graphicOverlay.add(imageGraphic)
        var firstFace : FaceGraphic? = null
        for (i in results.indices) {
            val face = results[i]
            val cameraFacing = frameMetadata.cameraFacing
            val faceGraphic = FaceGraphic(graphicOverlay, face, cameraFacing, null)
            graphicOverlay.add(faceGraphic)
            if (i==0) {
                firstFace = faceGraphic
            }
        }
        // take first face and calculate and correct for its error
        if(firstFace != null) {
            val panAngle = panProcessor.update(firstFace.getPanError())
            val tiltAngle = tiltProcessor.update(firstFace.getTiltError())
        //    println("tiltAngle")
        //    println(tiltAngle)
        //    println("panAngle")
        //    println(panAngle)
            panner.pan(panAngle)
            tilter.tilt(tiltAngle)
        }

        graphicOverlay.postInvalidate()
    }


    override fun onFailure(e: Exception) {
        Log.e(TAG, "Face detection failed $e")
    }

    companion object {
        private const val TAG = "FaceDetectionProcessor"
    }
}