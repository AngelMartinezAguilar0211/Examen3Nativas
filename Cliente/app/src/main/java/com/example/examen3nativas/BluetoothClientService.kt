package com.example.examen3nativas

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import java.io.*
import java.util.*

class BluetoothClientService(private val context: Context) {

    private val uuid = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66")

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun requestWebPage(url: String, lowData: Boolean, deviceAddress: String, callback: (String) -> Unit) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        val device: BluetoothDevice = adapter.bondedDevices.firstOrNull {
            it.address == deviceAddress
        } ?: run {
            callback("Dispositivo no emparejado.")
            return
        }

        Thread {
            try {
                // Crear canal de notificaciones
                NotificationHelper.createChannel(context)

                val socket = device.createRfcommSocketToServiceRecord(uuid)
                socket.connect()

                NotificationHelper.showNotification(
                    context,
                    "Conectado al servidor",
                    "Conexión Bluetooth establecida con éxito"
                )

                val writer = PrintWriter(BufferedWriter(OutputStreamWriter(socket.outputStream)), true)
                val reader = DataInputStream(socket.inputStream)

                val request = "$url|$lowData"
                writer.println(request)
                writer.flush()

                val length = reader.readInt()
                val compressedData = ByteArray(length)
                reader.readFully(compressedData)

                val html = ClientUtils.decompressGzip(compressedData)
                callback(html)

                socket.close()
            } catch (e: Exception) {
                NotificationHelper.createChannel(context)

                NotificationHelper.showNotification(
                    context,
                    "Error de conexión",
                    "No se pudo establecer conexión: ${e.message}"
                )

                Log.e("BluetoothClient", "Error: ${e.message}")
                callback("Error de conexión: ${e.message}")
            }
        }.start()

    }
}
