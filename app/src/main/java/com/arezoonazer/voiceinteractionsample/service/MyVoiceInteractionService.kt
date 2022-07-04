package com.arezoonazer.voiceinteractionsample.service

import android.content.Intent
import android.os.IBinder
import android.service.voice.VoiceInteractionService
import android.util.Log
import com.arezoonazer.voiceinteractionsample.util.PorcupineHandler
import com.arezoonazer.voiceinteractionsample.util.startSpeechRecognizerActivity


class MyVoiceInteractionService : VoiceInteractionService() {

    private val porcupineHandler: PorcupineHandler by lazy(LazyThreadSafetyMode.NONE) {
        PorcupineHandler
    }

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onReady() {
        super.onReady()
        porcupineHandler.createPorcupine(applicationContext) { keywordIndex: Int ->
            Log.d(TAG, "onReady: $keywordIndex")
            startSpeechRecognizerActivity()
        }
    }

    override fun onShutdown() {
        Log.d(TAG, "onShutdown: ")
        porcupineHandler.release()
        super.onShutdown()
    }
}

private const val TAG = "MyVoiceInteractionServi"