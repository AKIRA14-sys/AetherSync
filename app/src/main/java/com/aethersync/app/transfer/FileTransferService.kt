package com.aethersync.app.transfer

import android.content.Context
import android.net.Uri
import android.util.Log
import com.aethersync.app.model.TransferSession
import com.aethersync.app.model.SessionStatus
import kotlinx.coroutines.*
import java.io.*
import java.net.Socket

class FileTransferService(private val context: Context) {
    private val TAG = "AetherSync_Transfer"
    private val BUFFER_SIZE = 8192 // 8KB buffer for low memory impact

    suspend fun sendFile(session: TransferSession, socket: Socket, fileUri: Uri) = withContext(Dispatchers.IO) {
        try {
            session.status = SessionStatus.TRANSFERRING
            val outputStream = BufferedOutputStream(socket.getOutputStream())
            val inputStream = context.contentResolver.openInputStream(fileUri) ?: throw IOException("Cannot open file")

            val buffer = ByteArray(BUFFER_SIZE)
            var bytesSent = 0L
            val totalSize = session.fileSize
            val startTime = System.currentTimeMillis()

            outputStream.use { output ->
                inputStream.use { input ->
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        bytesSent += bytesRead

                        // Update progress
                        session.progress = if (totalSize > 0) bytesSent.toFloat() / totalSize else 0f

                        // Calculate speed (MB/s)
                        val currentTime = System.currentTimeMillis()
                        val duration = (currentTime - startTime) / 1000.0
                        if (duration > 0 && bytesSent > 0) {
                            val bytesPerSec = bytesSent / duration
                            session.speed = bytesPerSec / (1024.0 * 1024.0)
                            session.remainingTime = if (bytesPerSec > 0) ((totalSize - bytesSent) / bytesPerSec).toLong() else 0L
                        }
                    }
                    output.flush()
                }
            }
            session.status = SessionStatus.COMPLETED
        } catch (e: Exception) {
            Log.e(TAG, "Send Error: ${e.message}")
            session.status = SessionStatus.FAILED
        } finally {
            socket.close()
        }
    }

    suspend fun receiveFile(session: TransferSession, socket: Socket, destinationFile: File) = withContext(Dispatchers.IO) {
        try {
            session.status = SessionStatus.TRANSFERRING
            val inputStream = BufferedInputStream(socket.getInputStream())
            val outputStream = BufferedOutputStream(FileOutputStream(destinationFile))

            val buffer = ByteArray(BUFFER_SIZE)
            var bytesReceived = 0L
            val totalSize = session.fileSize
            val startTime = System.currentTimeMillis()

            outputStream.use { output ->
                inputStream.use { input ->
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        bytesReceived += bytesRead

                        session.progress = if (totalSize > 0) bytesReceived.toFloat() / totalSize else 0f

                        val currentTime = System.currentTimeMillis()
                        val duration = (currentTime - startTime) / 1000.0
                        if (duration > 0 && bytesReceived > 0) {
                            val bytesPerSec = bytesReceived / duration
                            session.speed = bytesPerSec / (1024.0 * 1024.0)
                            session.remainingTime = if (bytesPerSec > 0) ((totalSize - bytesReceived) / bytesPerSec).toLong() else 0L
                        }
                    }
                    output.flush()
                }
            }
            session.status = SessionStatus.COMPLETED
        } catch (e: Exception) {
            Log.e(TAG, "Receive Error: ${e.message}")
            session.status = SessionStatus.FAILED
        } finally {
            socket.close()
        }
    }
}
