package com.example.examen3nativas

import android.bluetooth.BluetoothSocket
import android.util.LruCache
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class HttpProxyHandler {

    private val client = OkHttpClient()
    private val cache = LruCache<String, String>(20)  // simple LRU cache

    fun handleClient(socket: BluetoothSocket) {
        val input = BufferedReader(InputStreamReader(socket.inputStream))
        val output = DataOutputStream(socket.outputStream)

        try {
            val requestLine = input.readLine()
            val parts = requestLine.split("|")
            val url = URLDecoder.decode(parts[0], "UTF-8")
            val lowData = parts.getOrNull(1)?.toBoolean() ?: false

            val response = cache.get(url) ?: fetchAndCache(url, lowData)
            val compressed = compressData(response)

            output.writeInt(compressed.size)
            output.write(compressed)
            output.flush()
        } catch (e: Exception) {
            val error = compressData("ERROR: ${e.message}")
            output.writeInt(error.size)
            output.write(error)
            output.flush()
        } finally {
            input.close()
            output.close()
        }
    }


    private fun fetchAndCache(url: String, lowData: Boolean): String {
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: "Sin contenido"

        val processedBody = if (lowData) {
            body.replace(Regex("<img[^>]*>"), "[imagen omitida]")
        } else body

        cache.put(url, processedBody)
        return processedBody
    }


    private fun compressData(data: String): ByteArray {
        val bos = ByteArrayOutputStream()
        val gzip = java.util.zip.GZIPOutputStream(bos)
        gzip.write(data.toByteArray(Charsets.UTF_8))
        gzip.close()
        return bos.toByteArray()
    }


}
