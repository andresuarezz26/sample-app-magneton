package com.example.myapplication.domain.llm

interface LocalImageGenerator {
    /** Generates an image for [prompt] and returns it as PNG bytes. */
    suspend fun generate(prompt: String): Result<ByteArray>
}
