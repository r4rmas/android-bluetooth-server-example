package com.example.bluetoothserver

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.bluetoothserver.data.Message
import java.io.IOException
import java.util.*

@SuppressLint("MissingPermission")
class AcceptThread(private val bluetoothAdapter: BluetoothAdapter, private val handler: Handler) : Thread() {
    private val TAG = "Pimienta"
    private val bServerS: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
        bluetoothAdapter
            .listenUsingInsecureRfcommWithServiceRecord(
                "Bluetooth",
                UUID.fromString("f1473161-213d-4607-bd29-519b31206e63")
            )
    }

    override fun run() {
        // Keep listening until exception occurs or a socket is returned.
        var shouldLoop = true
        while (shouldLoop) {
            val socket: BluetoothSocket? = try {
                Log.w(TAG, "Aceptando conexiones...")
                bServerS?.accept()
            } catch (e: IOException) {
                Log.e(TAG, "Socket's accept() method failed", e)
                shouldLoop = false
                null
            }
            socket?.also {
                try {
//                manageMyConnectedSocket(it)
//                    val bluetoothService = BluetoothService(handler, it)
//                    bluetoothService.start()
//                    bServerS?.close()
                    bServerS?.close()
                    Log.w(TAG, "Conectado!")
                    val bluetoothService = BluetoothService(handler, it)
                    bluetoothService.start()

                    shouldLoop = false
                } catch (e: IOException) {
                    Log.e(TAG, "Could not close the connect socket", e)
                }
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    fun cancel() {
        try {
            bServerS?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the connect socket", e)
        }
    }
}