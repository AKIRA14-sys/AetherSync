package com.aethersync.app.model

import java.io.File

data class TransferSession(
    val sessionId: String,
    val fileName: String,
    val fileSize: Long,
    val fileType: String,
    var progress: Float = 0f,
    var speed: Double = 0.0,
    var remainingTime: Long = 0L,
    var status: SessionStatus = SessionStatus.PENDING
)

enum class SessionStatus {
    PENDING,
    CONNECTING,
    TRANSFERRING,
    COMPLETED,
    FAILED,
    CANCELLED
}
