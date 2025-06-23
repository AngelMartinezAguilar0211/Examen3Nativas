package com.example.examen3nativas

import android.content.Context
import android.util.Log

object HistoryManager {
    fun save(context: Context, url: String) {
        try {
            val file = context.getFileStreamPath("history.txt")
            context.openFileOutput("history.txt", Context.MODE_APPEND).use {
                it.write((url + "\n").toByteArray())
            }
        } catch (e: Exception) {
            Log.e("History", "Error al guardar historial: ${e.message}")
        }
    }
}
