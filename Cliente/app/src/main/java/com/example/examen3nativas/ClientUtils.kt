package com.example.examen3nativas

import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream

object ClientUtils {
    fun decompressGzip(data: ByteArray): String {
        val inputStream = GZIPInputStream(ByteArrayInputStream(data))
        return InputStreamReader(inputStream).readText()
    }
}
