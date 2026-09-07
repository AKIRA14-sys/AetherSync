package com.aethersync.app.network

import android.content.Context
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pPeer
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

class LocalNetworkManager(private val context: Context) {
    private val manager: WifiP2pManager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    private val channel = manager.initialize()

    fun discoverPeers(callback: (List<WifiP2pDevice>) -> Unit) {
        manager.discoverPeers(channel, object : WifiP2pManager.WifiP2pPeerListListener {
            override fun onPeersAvailable(peers: WifiP2pDeviceList) {
                callback(peers.deviceList)
            }
        })
    }

    fun connectToDevice(device: WifiP2pDevice, callback: (Boolean) -> Unit) {
        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
        }
        manager.connect(channel, config, object : WifiP2pManager.ConnectionFailedListener {
            override fun onConnectionFailed(device: WifiP2pDevice, reason: Int) {
                callback(false)
            }
        })
    }

    fun startServer(port: Int = 8888, onConnection: (Socket) -> Unit) {
        Thread {
            try {
                val serverSocket = ServerSocket(port)
                while (true) {
                    val client = serverSocket.accept()
                    onConnection(client)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun createClientConnection(ip: String, port: Int = 8888): Socket {
        return Socket(InetAddress.getByName(ip), port)
    }
}
