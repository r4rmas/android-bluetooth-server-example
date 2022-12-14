package com.example.bluetoothserver

import android.annotation.SuppressLint
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.bluetoothserver.data.Message
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

const val TAG = "Pimienta"

// Defines several constants used when transmitting messages between the
// service and the UI.
const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2
// ... (Add other message types here as needed.)

class BluetoothService(private val handler: Handler, private val connectedSocket: BluetoothSocket) : Thread() {
    private val mmInStream: InputStream = connectedSocket.inputStream
    private val mmOutStream: OutputStream = connectedSocket.outputStream
    private val mmBuffer: ByteArray = ByteArray(26) // mmBuffer store for the stream

    @SuppressLint("MissingPermission")
    override fun run() {
//        var numBytes: Int // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs.
        var i = 1
        while (i < 2) {
            // Read from the InputStream.
//            numBytes = try {
            try {
                Log.w(TAG, "Leyendo...")
                mmInStream.read(mmBuffer)
            } catch (e: IOException) {
                Log.d(TAG, "Input stream was disconnected", e)
//                break
            }

            val message = Message.parseFrom(mmBuffer)
//            val message = Message.newBuilder(Message.parseFrom(mmBuffer)).build()
            Log.i(TAG, message.content)

            // Send the obtained bytes to the UI activity.
//            val readMsg = handler.obtainMessage(
//                MESSAGE_READ, numBytes, -1,
//                mmBuffer
//            )
//            readMsg.sendToTarget()
            i++
        }
    }

    // Call this from the main activity to send data to the remote device.
    fun write(bytes: ByteArray) {
        try {
            mmOutStream.write(bytes)
        } catch (e: IOException) {
            Log.e(TAG, "Error occurred when sending data", e)

            // Send a failure message back to the activity.
            val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
            val bundle = Bundle().apply {
                putString("toast", "Couldn't send data to the other device")
            }
            writeErrorMsg.data = bundle
            handler.sendMessage(writeErrorMsg)
            return
        }

        // Share the sent message with the UI activity.
        val writtenMsg = handler.obtainMessage(
            MESSAGE_WRITE, -1, -1, mmBuffer
        )
        writtenMsg.sendToTarget()
    }

    // Call this method from the main activity to shut down the connection.
    fun cancel() {
        try {
            connectedSocket.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the connect socket", e)
        }
    }
}
