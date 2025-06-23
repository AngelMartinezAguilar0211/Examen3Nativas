package com.example.examen3nativas

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val REQUEST_PERMISSIONS = 200
    @RequiresApi(Build.VERSION_CODES.S)
    private val btPermissions = arrayOf(
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_ADVERTISE,
        Manifest.permission.BLUETOOTH_SCAN
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !hasPermissions(btPermissions)) {
            ActivityCompat.requestPermissions(this, btPermissions, REQUEST_PERMISSIONS)
        } else {
            startBluetoothService()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startBluetoothService() {
        val intent = Intent(this, BluetoothServerService::class.java)
        startForegroundService(intent)
        findViewById<TextView>(R.id.statusText).text = "Servidor activo"
    }

    private fun hasPermissions(perms: Array<String>) =
        perms.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startBluetoothService()
            } else {
                Toast.makeText(this, "Permisos requeridos denegados", Toast.LENGTH_LONG).show()
            }
        }
    }
}
