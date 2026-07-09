package com.example.myapplication.data.remote

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return if (request.url.encodedPath == "/videos") {
            val mockResponseBody = """[
                {
                    "id": "1",
                    "author": "@astro_facts",
                    "description": "Did you know black holes emit Hawking radiation? The universe is wild! #space #physics",
                    "likes": 48200,
                    "comments": 1203,
                    "shares": 892,
                    "music": "Cosmic Vibes — Science Beats",
                    "backgroundColorHex": 4278238766
                },
                {
                    "id": "2",
                    "author": "@quantum_lab",
                    "description": "Schrodinger's cat explained in 60 seconds. Mind = blown. #quantum #physics",
                    "likes": 92100,
                    "comments": 4521,
                    "shares": 3100,
                    "music": "Wave Function — Quantum Sounds",
                    "backgroundColorHex": 4278245950
                },
                {
                    "id": "3",
                    "author": "@bio_wonders",
                    "description": "CRISPR gene editing could cure genetic diseases forever. The future of medicine is here! #biology",
                    "likes": 73400,
                    "comments": 2890,
                    "shares": 5601,
                    "music": "DNA Sequence — Bio Beats",
                    "backgroundColorHex": 4278251744
                },
                {
                    "id": "4",
                    "author": "@climate_science",
                    "description": "Ocean currents regulate our entire climate. Here's how they work and why they matter. #earth",
                    "likes": 31500,
                    "comments": 987,
                    "shares": 2341,
                    "music": "Ocean Drift — Nature Sounds",
                    "backgroundColorHex": 4279225138
                },
                {
                    "id": "5",
                    "author": "@neuro_brain",
                    "description": "Your brain processes images in just 13 milliseconds. Faster than you can blink. #neuroscience",
                    "likes": 115000,
                    "comments": 6200,
                    "shares": 8900,
                    "music": "Neural Pulse — Brain Beats",
                    "backgroundColorHex": 4276998161
                },
                {
                    "id": "6",
                    "author": "@chem_lab",
                    "description": "Why does mixing bleach and ammonia create a deadly gas? The chemistry explained safely. #chemistry",
                    "likes": 204000,
                    "comments": 9800,
                    "shares": 14200,
                    "music": "Molecule Mix — Lab Sounds",
                    "backgroundColorHex": 4277265761
                }
            ]""".trimIndent()

            Response.Builder()
                .code(200)
                .message("OK")
                .request(request)
                .protocol(okhttp3.Protocol.HTTP_1_1)
                .body(mockResponseBody.toResponseBody("application/json".toMediaType()))
                .build()
        } else {
            chain.proceed(request)
        }
    }
}
