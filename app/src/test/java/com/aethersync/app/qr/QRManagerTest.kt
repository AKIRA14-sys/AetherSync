package com.aethersync.app.qr

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class QRManagerTest {

    @Test
    fun parseQR_validUrl_returnsConnectionInfo() {
        val qrText = "aethersync://192.168.1.50:8888/session_123"
        val connectionInfo = QRManager.parseQR(qrText)

        assertNotNull(connectionInfo)
        assertEquals("192.168.1.50", connectionInfo?.ip)
        assertEquals(8888, connectionInfo?.port)
        assertEquals("session_123", connectionInfo?.sessionId)
    }

    @Test
    fun parseQR_invalidScheme_returnsNull() {
        val qrText = "http://192.168.1.50:8888/session_123"
        val connectionInfo = QRManager.parseQR(qrText)

        assertNull(connectionInfo)
    }

    @Test
    fun parseQR_missingPort_returnsNull() {
        val qrText = "aethersync://192.168.1.50/session_123"
        val connectionInfo = QRManager.parseQR(qrText)

        assertNull(connectionInfo)
    }

    @Test
    fun parseQR_invalidPort_returnsNull() {
        val qrText = "aethersync://192.168.1.50:invalid/session_123"
        val connectionInfo = QRManager.parseQR(qrText)

        assertNull(connectionInfo)
    }

    @Test
    fun parseQR_missingSessionId_returnsNull() {
        val qrText = "aethersync://192.168.1.50:8888/"
        val connectionInfo = QRManager.parseQR(qrText)

        assertNull(connectionInfo)
    }
}
