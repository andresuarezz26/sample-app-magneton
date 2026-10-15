package com.example.myapplication.data.llm

import com.example.myapplication.domain.llm.LocalImageGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Stub backend: checks that a model has been provisioned into [modelDir] (e.g. via adb push)
 * and reports a clear error otherwise. The inference runtime call is not wired yet.
 */
class OnDeviceImageGenerator(private val modelDir: File) : LocalImageGenerator {

    override suspend fun generate(prompt: String): Result<ByteArray> = withContext(Dispatchers.Default) {
        val hasModel = modelDir.isDirectory && !modelDir.list().isNullOrEmpty()
        if (!hasModel) {
            Result.failure(IllegalStateException("Model not installed. Copy a model into ${modelDir.path}"))
        } else {
            Result.failure(IllegalStateException("No on-device inference runtime is integrated yet"))
        }
    }
}
