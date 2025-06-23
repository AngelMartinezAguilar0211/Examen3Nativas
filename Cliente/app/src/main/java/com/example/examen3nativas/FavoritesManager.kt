package com.example.examen3nativas

import android.content.Context

object FavoritesManager {
    fun save(context: Context, url: String) {
        context.openFileOutput("favorites.txt", Context.MODE_APPEND).use {
            it.write((url + "\n").toByteArray())
        }
    }

    fun list(context: Context): List<String> {
        return context.openFileInput("favorites.txt").bufferedReader().readLines()
    }
}
