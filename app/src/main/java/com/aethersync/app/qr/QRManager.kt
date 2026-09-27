package com.aethersync.app.qr

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

object QRManager {
    fun generateConnectionQR(ip: String, port: Int, sessionId: String): Bitmap {
        val content = "aethersync://$ip:$port/$sessionId"
        val width = 512
        val height = 512
        val bitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        return bitmap
    }

    fun parseQR(scannedText: String): ConnectionInfo? {
        if (!scannedText.startsWith("aethersync://")) return null
        return try {
            val path = scannedText.removePrefix("aethersync://")
            val data = path.split("/")
            if (data.size < 2) return null
            val address = data[0].split(":")
            if (address.size < 2) return null
            val ip = address[0]
            val port = address[1].toIntOrNull() ?: return null
            val sessionId = data[1]
            if (ip.isBlank() || sessionId.isBlank()) return null
            ConnectionInfo(
                ip = ip,
                port = port,
                sessionId = sessionId
            )
        } catch (e: Exception) {
            null
        }
    }

    data class ConnectionInfo(val ip: String, val port: Int, val sessionId: String)
}
