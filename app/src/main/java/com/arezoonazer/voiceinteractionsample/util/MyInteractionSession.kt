package com.arezoonazer.voiceinteractionsample.util

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.service.voice.VoiceInteractionSession
import android.util.Log
import androidx.annotation.RequiresApi

class MyInteractionSession(context: Context) : VoiceInteractionSession(context) {

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")
    }

    override fun onShow(args: Bundle?, showFlags: Int) {
        super.onShow(args, showFlags)
        Log.d(TAG, "onShow() called with: args = $args, showFlags = $showFlags")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onHandleAssist(state: AssistState) {
        super.onHandleAssist(state)
        Log.d(TAG, "onHandleAssist() called with")
        context?.startSpeechRecognizerActivity()
    }

    override fun onHide() {
        super.onHide()
        Log.d(TAG, "onHide: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d(TAG, "onBackPressed: ")
    }
}

private const val TAG = "MyInteractionSession"