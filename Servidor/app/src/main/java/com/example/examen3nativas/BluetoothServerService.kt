package com.example.examen3nativas

import android.Manifest
import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import java.io.IOException
import java.util.*

class BluetoothServerService : Service() {

    private val UUID_SERVER: UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66")
    private val NAME = "BluetoothWebServer"
    private var running = false
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        Thread { listenForConnections() }.start()
    }

    private fun startForegroundService() {
        val channelId = "bt_server_channel"
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Servidor Bluetooth", NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Servidor activo")
            .setContentText("Esperando conexiones Bluetooth...")
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .build()

        startForeground(1, notification)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun listenForConnections() {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        val serverSocket: BluetoothServerSocket? = try {
            adapter.listenUsingRfcommWithServiceRecord(NAME, UUID_SERVER)
        } catch (e: IOException) {
            null
        }

        running = true
        serverSocket?.use {
            while (running) {
                val socket: BluetoothSocket? = try {
                    it.accept()
                } catch (e: IOException) {
                    null
                }

                socket?.also { client ->
                    val handler = HttpProxyHandler()
                    handler.handleClient(client)
                    client.close()
                }
            }
        }
    }

    override fun onDestroy() {
        running = false
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
