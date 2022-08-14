package com.arezoonazer.voiceinteractionsample.util

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.provider.Settings
import com.arezoonazer.voiceinteractionsample.SpeechRecognizerActivity

fun Context.startSpeechRecognizerActivity() {
//    if (isRunning()) return

    startActivity(getSpeechRecognizerActivityIntent())
}

fun Context.getSpeechRecognizerActivityIntent(): Intent {
    return Intent(this, SpeechRecognizerActivity::class.java).apply {
        flags = FLAG_ACTIVITY_NEW_TASK
    }
}


fun Context.isSetAsDefaultAssistant(): Boolean {
    val setting = Settings.Secure.getString(contentResolver, "assistant")
    return if (setting != null) {
        ComponentName.unflattenFromString(setting)?.packageName == this.packageName
    } else false
}

private fun Context.isRunning(): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val tasks = activityManager.getRunningTasks(Int.MAX_VALUE)
    for (task in tasks) {
        if (packageName.equals(task.baseActivity!!.packageName, ignoreCase = true)) return true
    }
    return false
}