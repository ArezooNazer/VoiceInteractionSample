package com.arezoonazer.voiceinteractionsample.service

import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.service.voice.VoiceInteractionService
import android.service.voice.VoiceInteractionSession
import android.util.Log
import com.arezoonazer.voiceinteractionsample.util.PorcupineHandler
import com.arezoonazer.voiceinteractionsample.util.startSpeechRecognizerActivity

/**
 * Top-level service, which is providing support for hotwording.The current VoiceInteractionService that has been selected by the user is kept always
 * running by the system, to allow it to do things like listen for hotwords in the background to instigate voice interactions.
 * Because this service is always running, it should be kept as lightweight as possible. Heavy-weight operations (including showing UI) should be
 * implemented in the associated [MyVoiceInteractionSessionService] when an actual voice interaction is taking place, and that service should
 * run in a separate process from this one.
 */

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showSession(null, VoiceInteractionSession.SHOW_WITH_ASSIST)
            } else {
                startSpeechRecognizerActivity()
            }
        }
    }

    override fun onShutdown() {
        Log.d(TAG, "onShutdown: ")
        porcupineHandler.release()
        super.onShutdown()
    }
}

private const val TAG = "MyVoiceInteractionServi"