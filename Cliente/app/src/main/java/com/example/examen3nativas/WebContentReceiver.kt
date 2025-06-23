package com.example.examen3nativas

import android.webkit.WebView

object WebContentReceiver {
    fun displayContent(webView: WebView, html: String, incognito: Boolean) {
        webView.clearHistory()
        if (!incognito) {
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        } else {
            webView.loadDataWithBaseURL(null, "<html><body><h3>Modo incógnito activo</h3></body></html>", "text/html", "UTF-8", null)
        }
    }
}
