package com.arezoonazer.voiceinteractionsample.util

import ai.picovoice.porcupine.Porcupine
import ai.picovoice.porcupine.PorcupineException
import ai.picovoice.porcupine.PorcupineManager
import ai.picovoice.porcupine.PorcupineManagerCallback
import android.content.Context
import android.util.Log


object PorcupineHandler {

    private var porcupineManager: PorcupineManager? = null

    fun createPorcupine(
        context: Context,
        callback: PorcupineManagerCallback
    ) {
        try {
            porcupineManager = PorcupineManager.Builder()
                .setAccessKey(ACCESS_KEY)
                .setKeywords(
                    arrayOf(
                        Porcupine.BuiltInKeyword.ALEXA,
                        Porcupine.BuiltInKeyword.HEY_GOOGLE
                    )
                )
                .setSensitivities(floatArrayOf(0.7f, 0.7f))
                .build(context, callback)

            porcupineManager?.start()
        } catch (e: PorcupineException) {
            Log.e(TAG, e.toString())
        }
    }

    fun release() {
        try {
            porcupineManager?.apply {
                stop()
                delete()
            }
        } catch (e: PorcupineException) {
            Log.e(TAG, e.toString())
        }
    }

    private const val ACCESS_KEY = "q0HpY+y/4T2wUIwy5sGaibuN/Kz31/I4PDPvQVUxVjc5LWjaeFbpMQ=="
    private const val TAG = "PorcupineBuilder"
}

