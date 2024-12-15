package com.example.tomatech.ui.helper

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TFLiteHelper(private val context: Context) {
    private var interpreter: Interpreter? = null

    private val labels = listOf(
        "Healthy",
        "Bacterial Spot",
        "Early Blight",
        "Late Blight",
        "Leaf Mold",
        "Septoria Leaf Spot",
        "Spider Mites",
        "Target Spot",
        "Tomato Yellow Leaf Curl Virus",
        "Tomato Mosaic Virus"
    )

    init {
        interpreter = Interpreter(loadModel("model.tflite"))
    }

    @OptIn(UnstableApi::class)
    private fun loadModel(modelPath: String): MappedByteBuffer {
        return try {
            val fileDescriptor = context.assets.openFd(modelPath)
            FileInputStream(fileDescriptor.fileDescriptor).use { fis ->
                fis.channel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.length)
            }
        } catch (e: Exception) {
            Log.e("TFLiteHelper", "Error loading model: ${e.message}")
            throw RuntimeException("Error loading model: ${e.message}")
        }
    }

    fun classifyImage(bitmap: Bitmap): Pair<String, Float> {
        val input = preprocessImage(bitmap)
        val output = Array(1) { FloatArray(labels.size) }

        interpreter?.run(input, output)

        val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
        if (maxIndex == -1) return Pair("Unknown", 0.0f)

        val confidenceScore = output[0][maxIndex]
        val label = labels.getOrNull(maxIndex) ?: "Unknown"

        return Pair(label, confidenceScore)
    }

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true)
        val byteBuffer = ByteBuffer.allocateDirect(4 * 150 * 150 * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        for (y in 0 until 150) {
            for (x in 0 until 150) {
                val pixel = resizedBitmap.getPixel(x, y)
                byteBuffer.putFloat((pixel shr 16 and 0xFF) / 255.0f) // Red channel
                byteBuffer.putFloat((pixel shr 8 and 0xFF) / 255.0f)  // Green channel
                byteBuffer.putFloat((pixel and 0xFF) / 255.0f)       // Blue channel
            }
        }
        return byteBuffer
    }

    fun close() {
        interpreter?.close()
    }
}
