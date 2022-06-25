package com.arezoonazer.voiceinteractionsample.service

import android.os.Bundle
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService
import com.arezoonazer.voiceinteractionsample.MyInteractionSession

class MyVoiceInteractionSessionService : VoiceInteractionSessionService() {
    override fun onNewSession(p0: Bundle?): VoiceInteractionSession {
        return MyInteractionSession(this)
    }
}