package com.firebase.hackweek.tank18thscale

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
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
    private var firstFace : FaceGraphic? = null
    private var previousErrorSendTime = 0L

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
        faces: List<FirebaseVisionFace>,
        frameMetadata: FrameMetadata,
        graphicOverlay: GraphicOverlay
    ) {
        val selectedFace = faces.maxBy { it.smilingProbability }

        drawImage(selectedFace, faces, graphicOverlay, frameMetadata, originalCameraImage);
    }

    private fun drawImage(
        selectedFace: FirebaseVisionFace?,
        faces: List<FirebaseVisionFace>,
        graphicOverlay: GraphicOverlay,
        frameMetadata: FrameMetadata,
        originalCameraImage: Bitmap?
    ) {
        graphicOverlay.clear()

        val imageGraphic = CameraImageGraphic(graphicOverlay, originalCameraImage)
        graphicOverlay.add(imageGraphic)

        for (face in faces) {
            val cameraFacing = frameMetadata.cameraFacing
            val faceGraphic = FaceGraphic(
                graphicOverlay, face, cameraFacing, null, if (face == selectedFace) Color.RED else Color.WHITE )
            graphicOverlay.add(faceGraphic)
        }

        // take first face and calculate and correct for its error
        // this is a non-blocking call
        val currentTime = System.currentTimeMillis()
        if (currentTime - previousErrorSendTime > 1000) {
            previousErrorSendTime = currentTime
            calculateErrorAndSend()
        }

        graphicOverlay.postInvalidate()
    }

    fun calculateErrorAndSend(){
        if(firstFace != null) {
//            val panAngle = panProcessor.update(firstFace!!.getPanError())
//            val tiltAngle = tiltProcessor.update(firstFace!!.getTiltError())
            panner.pan(firstFace!!.getPanError())
            tilter.tilt(firstFace!!.getTiltError())
        }
    }


    override fun onFailure(e: Exception) {
        Log.e(TAG, "Face detection failed $e")
    }

    companion object {
        private const val TAG = "FaceDetectionProcessor"
    }
}