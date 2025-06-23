package com.example.examen3nativas

import android.webkit.WebView

object NavigationController {
    fun goBack(webView: WebView) {
        if (webView.canGoBack()) webView.goBack()
    }

    fun goForward(webView: WebView) {
        if (webView.canGoForward()) webView.goForward()
    }

    fun reload(webView: WebView) {
        webView.reload()
    }
}
