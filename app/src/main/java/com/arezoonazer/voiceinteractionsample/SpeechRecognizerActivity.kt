package com.arezoonazer.voiceinteractionsample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.arezoonazer.voiceinteractionsample.util.isSetAsDefaultAssistant
import java.util.Locale


class SpeechRecognizerActivity : AppCompatActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startSpeechRecognitionIfPossible()
            } else {
                Log.d(TAG, "permission denied ")
            }
        }

    private val voiceRecognitionResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val results = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val spokenText = results?.get(0)
                findViewById<TextView>(R.id.recognizedText).text = spokenText
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        setContentView(R.layout.activity_speech_recognizer)
        checkIfRecordAudioPermissionIsGranted()

        findViewById<View>(R.id.recognizerButton).setOnClickListener {
            checkIfRecordAudioPermissionIsGranted()
        }
    }

    private fun checkIfRecordAudioPermissionIsGranted() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) -> {
                startSpeechRecognitionIfPossible()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startSpeechRecognitionIfPossible() {
        if (isSetAsDefaultAssistant()) {
            displaySpeechRecognizer()
        } else {
            openVoiceInputSetting()
        }
    }

    private fun openVoiceInputSetting() {
        startActivity(Intent(Settings.ACTION_VOICE_INPUT_SETTINGS))
    }

    private fun displaySpeechRecognizer() {
        val language = Locale.getDefault().language

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language)
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language)
            putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.how_can_i_help_you)
            )
        }

        try {
            voiceRecognitionResult.launch(intent)
        } catch (ignored: Exception) {
            Log.d(TAG, "displaySpeechRecognizer: $ignored")
            getString(R.string.not_compatible)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}