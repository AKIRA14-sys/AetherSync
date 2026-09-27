package com.aethersync.app.model

import org.junit.Assert.assertEquals
import org.junit.Test

class TransferSessionTest {

    @Test
    fun testTransferSessionDefaultValues() {
        val session = TransferSession(
            sessionId = "s1",
            fileName = "test.mp4",
            fileSize = 1048576L,
            fileType = "video/mp4"
        )

        assertEquals("s1", session.sessionId)
        assertEquals("test.mp4", session.fileName)
        assertEquals(1048576L, session.fileSize)
        assertEquals("video/mp4", session.fileType)
        assertEquals(0f, session.progress, 0.001f)
        assertEquals(0.0, session.speed, 0.001)
        assertEquals(0L, session.remainingTime)
        assertEquals(SessionStatus.PENDING, session.status)
    }

    @Test
    fun testTransferSessionStatusUpdates() {
        val session = TransferSession(
            sessionId = "s2",
            fileName = "document.pdf",
            fileSize = 2048L,
            fileType = "application/pdf"
        )

        session.status = SessionStatus.TRANSFERRING
        session.progress = 0.5f
        session.speed = 10.5
        session.remainingTime = 30L

        assertEquals(SessionStatus.TRANSFERRING, session.status)
        assertEquals(0.5f, session.progress, 0.001f)
        assertEquals(10.5, session.speed, 0.001)
        assertEquals(30L, session.remainingTime)
    }
}
