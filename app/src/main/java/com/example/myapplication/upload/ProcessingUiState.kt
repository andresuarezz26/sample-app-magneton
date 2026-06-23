package com.example.myapplication.upload

import com.example.myapplication.data.model.PaperStatus

data class ProcessingUiState(
    val status: PaperStatus = PaperStatus.QUEUED
)
