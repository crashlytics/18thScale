package com.firebase.hackweek.tank18thscale

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.Rect
import com.google.android.gms.vision.CameraSource
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import com.firebase.hackweek.tank18thscale.common.GraphicOverlay

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic(
    overlay: GraphicOverlay,
    private val firebaseVisionFace: FirebaseVisionFace?,
    private val facing: Int,
    private val overlayBitmap: Bitmap?,
    private val bbColor: Int)
    : GraphicOverlay.Graphic(overlay) {

    /**
     * Draws the face annotations for position on the supplied canvas.
     */

    private val centerPaint = Paint().apply {
        color = Color.RED
    }

    private val facePositionPaint = Paint().apply {
        color = Color.WHITE
    }

    private val idPaint = Paint().apply {
        color = Color.WHITE
        textSize = ID_TEXT_SIZE
    }

    fun getPanError() : Float {
        if (firebaseVisionFace == null) {
            return 0f
        }
        val faceCenterX = translateX(firebaseVisionFace.boundingBox.centerX().toFloat())
        return overlayCenterX - faceCenterX
    }

    fun getTiltError() : Float {
        if (firebaseVisionFace == null) {
            return 0f
        }
        val faceCenterY = translateY(firebaseVisionFace.boundingBox.centerY().toFloat())
        return overlayCenterY - faceCenterY
    }

    fun getHappiness() : Float {
        return firebaseVisionFace?.smilingProbability ?: 0f
    }

    override fun draw(canvas: Canvas) {
        val face = firebaseVisionFace ?: return

        // Draws a circle at the position of the detected face, with the face's track id below.
        // An offset is used on the Y axis in order to draw the circle, face id and happiness level in the top area
        // of the face's bounding box
        val x = translateX(face.boundingBox.centerX().toFloat())
        val y = translateY(face.boundingBox.centerY().toFloat())
        // draw camera center
        canvas.drawCircle(overlayCenterX, overlayCenterY, 30.0f, centerPaint)
        // draw box center
        canvas.drawCircle(x, y, 30.0f, centerPaint)
        canvas.drawCircle(x, y - 4 * ID_Y_OFFSET, FACE_POSITION_RADIUS, facePositionPaint)

        canvas.drawText("id: " + face.trackingId, x + ID_X_OFFSET, y - 3 * ID_Y_OFFSET, idPaint)

        canvas.drawText(
            "happiness: ${String.format("%.2f", face.smilingProbability)}",
            x + ID_X_OFFSET * 3,
            y - 2 * ID_Y_OFFSET,
            idPaint)

        canvas.drawText(
            "pan error: ${String.format("%.2f", getPanError())}",
            x - ID_X_OFFSET,
            y,
            idPaint)

        canvas.drawText(
            "tilt error: ${String.format("%.2f", getTiltError())}",
            x + ID_X_OFFSET * 6,
            y,
            idPaint)



        // Draws a bounding box around the face.
        val xOffset = scaleX(face.boundingBox.width() / 2.0f)
        val yOffset = scaleY(face.boundingBox.height() / 2.0f)
        val left = x - xOffset
        val top = y - yOffset
        val right = x + xOffset
        val bottom = y + yOffset
        canvas.drawRect(left, top, right, bottom, Paint().apply {
            color = bbColor
            style = Style.STROKE
            strokeWidth = BOX_STROKE_WIDTH
        })

        // draw landmarks
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.MOUTH_BOTTOM)
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.LEFT_CHEEK)
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.LEFT_EAR)
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.MOUTH_LEFT)
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.LEFT_EYE)
        drawBitmapOverLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.NOSE_BASE)
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.RIGHT_CHEEK)
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.RIGHT_EAR)
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.RIGHT_EYE)
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.MOUTH_RIGHT)
    }


    private fun drawLandmarkPosition(canvas: Canvas, face: FirebaseVisionFace, landmarkID: Int) {
        val landmark = face.getLandmark(landmarkID)
        landmark?.let {
            val point = it.position
            canvas.drawCircle(
                translateX(point.x),
                translateY(point.y),
                10f, idPaint
            )
        }
    }

    private fun drawBitmapOverLandmarkPosition(
        canvas: Canvas,
        face: FirebaseVisionFace,
        landmarkID: Int
    ) {
        val landmark = face.getLandmark(landmarkID) ?: return

        val point = landmark.position

        overlayBitmap?.let {
            val imageEdgeSizeBasedOnFaceSize = face.boundingBox.width() / 4.0f

            val left = (translateX(point.x) - imageEdgeSizeBasedOnFaceSize).toInt()
            val top = (translateY(point.y) - imageEdgeSizeBasedOnFaceSize).toInt()
            val right = (translateX(point.x) + imageEdgeSizeBasedOnFaceSize).toInt()
            val bottom = (translateY(point.y) + imageEdgeSizeBasedOnFaceSize).toInt()

            canvas.drawBitmap(
                it, null,
                Rect(left, top, right, bottom), null
            )
        }
    }

    companion object {
        private const val FACE_POSITION_RADIUS = 4.0f
        private const val ID_TEXT_SIZE = 30.0f
        private const val ID_Y_OFFSET = 50.0f
        private const val ID_X_OFFSET = -50.0f
        private const val BOX_STROKE_WIDTH = 5.0f
    }
}
