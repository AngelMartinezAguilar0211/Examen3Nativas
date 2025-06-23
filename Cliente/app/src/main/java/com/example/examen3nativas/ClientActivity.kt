package com.example.examen3nativas

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ClientActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var urlBar: EditText
    private lateinit var controlPanel: LinearLayout
    private var selectedAddress: String? = null
    private var incognitoMode = false
    private var lowDataMode = false

    private lateinit var bluetoothService: BluetoothClientService
    private val REQUEST_BT_PERMISSIONS = 101
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val btPermissions = arrayOf(
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)

        webView = findViewById(R.id.webView)
        urlBar = findViewById(R.id.urlBar)
        controlPanel = findViewById(R.id.controlPanel)

        bluetoothService = BluetoothClientService(this)

        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true

        // Solicitar permisos al inicio
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !hasPermissions(btPermissions)) {
            ActivityCompat.requestPermissions(this, btPermissions, REQUEST_BT_PERMISSIONS)
        }

        findViewById<ImageButton>(R.id.btnGo).setOnClickListener {
            val url = urlBar.text.toString()
            if (selectedAddress == null) {
                Toast.makeText(this, "Selecciona un dispositivo primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            bluetoothService.requestWebPage(url, lowDataMode, selectedAddress!!) { html ->
                runOnUiThread {
                    WebContentReceiver.displayContent(webView, html, incognitoMode)
                }
                if (!incognitoMode) HistoryManager.save(this, url)
            }
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { webView.goBack() }
        findViewById<ImageButton>(R.id.btnForward).setOnClickListener { webView.goForward() }
        findViewById<ImageButton>(R.id.btnReload).setOnClickListener { webView.reload() }

        findViewById<ImageButton>(R.id.btnIncognito).setOnClickListener {
            incognitoMode = !incognitoMode
            Toast.makeText(this, "Modo incógnito: $incognitoMode", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageButton>(R.id.btnLowData).setOnClickListener {
            lowDataMode = !lowDataMode
            Toast.makeText(this, "Ahorro de datos: $lowDataMode", Toast.LENGTH_SHORT).show()
        }

        findViewById<FloatingActionButton>(R.id.fabToggleControls).setOnClickListener {
            controlPanel.visibility = if (controlPanel.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        // Botón de selección de dispositivo
        findViewById<ImageButton>(R.id.btnSelectDevice).setOnClickListener {
            startActivityForResult(Intent(this, DeviceSelectActivity::class.java), 100)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            selectedAddress = data?.getStringExtra("device_address")
            Toast.makeText(this, "Dispositivo seleccionado: $selectedAddress", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}
