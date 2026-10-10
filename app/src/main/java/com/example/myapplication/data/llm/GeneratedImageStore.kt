package com.example.myapplication.data.llm

import android.content.Context
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

interface GeneratedImageStore {
    /** Persists [png] and returns a content URI string for it. */
    suspend fun save(png: ByteArray): String
}

class FileProviderGeneratedImageStore(context: Context) : GeneratedImageStore {
    private val appContext = context.applicationContext

    override suspend fun save(png: ByteArray): String = withContext(Dispatchers.IO) {
        val dir = File(appContext.cacheDir, "generated").apply { mkdirs() }
        val file = File(dir, "${UUID.randomUUID()}.png")
        file.writeBytes(png)
        FileProvider.getUriForFile(appContext, "${appContext.packageName}.fileprovider", file).toString()
    }
}
