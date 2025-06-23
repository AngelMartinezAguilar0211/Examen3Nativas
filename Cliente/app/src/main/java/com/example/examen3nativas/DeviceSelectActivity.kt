package com.example.examen3nativas

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class DeviceSelectActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val devices = mutableListOf<BluetoothDevice>()

    private val REQUEST_BT_PERMISSIONS = 200
    @RequiresApi(Build.VERSION_CODES.S)
    private val btPermissions = arrayOf(
        Manifest.permission.BLUETOOTH_CONNECT
    )

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_select)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !hasPermissions(btPermissions)) {
            ActivityCompat.requestPermissions(this, btPermissions, REQUEST_BT_PERMISSIONS)
            return
        }

        loadDevices()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun loadDevices() {
        listView = findViewById(R.id.deviceList)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        listView.adapter = adapter

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val bonded = bluetoothAdapter.bondedDevices

        bonded.forEach { device ->
            devices.add(device)
            adapter.add("${device.name} (${device.address})")
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val selected = devices[position]
            val intent = Intent()
            intent.putExtra("device_address", selected.address)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BT_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadDevices()
            } else {
                Toast.makeText(this, "Permiso Bluetooth denegado", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
