package com.mindguard.ai.ml

import android.content.Context
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import java.io.File
import java.io.FileOutputStream

object ModelManager {
    private var modelRunner: OnnxModelRunner? = null
    private val ortEnvironment = OrtEnvironment.getEnvironment()

    @Synchronized
    fun getModelRunner(context: Context, modelFileName: String = "model_v1.onnx"): OnnxModelRunner {
        if (modelRunner == null) {
            val modelBytes = loadModelFileFromAssets(context, modelFileName)
            val session = ortEnvironment.createSession(modelBytes, OrtSession.SessionOptions())
            modelRunner = OnnxModelRunner(ortEnvironment, session)
        }
        return modelRunner!!
    }

    private fun loadModelFileFromAssets(context: Context, fileName: String): ByteArray {
        return context.assets.open(fileName).use { it.readBytes() }
    }
}
