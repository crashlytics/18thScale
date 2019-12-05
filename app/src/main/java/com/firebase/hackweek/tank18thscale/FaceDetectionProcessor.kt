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
private const val TAG = "Tank18thScale"

/** Face Detector Demo.  */
class FaceDetectionProcessor(res: Resources, private val faceMovementWatcher: FaceMovementWatcher) : VisionProcessorBase<List<FirebaseVisionFace>>() {
    private val detector: FirebaseVisionFaceDetector
    private val overlayBitmap: Bitmap
    private var previousErrorSendTime = 0L

    init {
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .enableTracking()
            .build()

        detector = FirebaseVision.getInstance().getVisionFaceDetector(options)
        overlayBitmap = BitmapFactory.decodeResource(res, R.drawable.clown_nose)
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
        val currentTime = System.currentTimeMillis()
        if (currentTime - previousErrorSendTime > 100) {
            previousErrorSendTime = currentTime
            calculateErrorAndSend(
                FaceGraphic(graphicOverlay, selectedFace, frameMetadata.cameraFacing, null, Color.WHITE))
        }
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
                graphicOverlay, face, cameraFacing, null, if (face == selectedFace) Color.GREEN else Color.WHITE)
            graphicOverlay.add(faceGraphic)
        }
    }

    fun calculateErrorAndSend(inputFace : FaceGraphic){
        if(inputFace != null) {
            faceMovementWatcher.onFaceMove(inputFace!!.getPanError(), inputFace!!.getTiltError(), inputFace!!.getHappiness())
        }
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Face detection failed $e")
    }

    companion object {
        private const val TAG = "FaceDetectionProcessor"
    }

    interface FaceMovementWatcher {
        fun onFaceMove(panError: Float, tiltError: Float, smileProbability : Float)
    }
}