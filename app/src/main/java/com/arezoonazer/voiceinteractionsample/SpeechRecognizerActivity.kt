package com.arezoonazer.voiceinteractionsample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.arezoonazer.voiceinteractionsample.databinding.ActivitySpeechRecognizerBinding
import com.arezoonazer.voiceinteractionsample.util.isSetAsDefaultAssistant
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.Locale


class SpeechRecognizerActivity : AppCompatActivity() {

    private val binding: ActivitySpeechRecognizerBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivitySpeechRecognizerBinding.inflate(layoutInflater)
    }

    private val bottomSheetBehavior by lazy(LazyThreadSafetyMode.NONE) {
        iniBottomSheet()
    }

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
                results?.get(0)?.let { spokenText ->
                    onVoiceRecognized(spokenText)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        setContentView(binding.root)
        checkIfRecordAudioPermissionIsGranted()

        binding.recognizerButton.setOnClickListener {
            checkIfRecordAudioPermissionIsGranted()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
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
        when {
            isSetAsDefaultAssistant().not() -> {
                openVoiceInputSetting()
            }
            else -> {
                displaySpeechRecognizer()
            }
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

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setEdgeToEdgeMode()
        } else {
            setFullScreen()
        }
    }

    private fun setEdgeToEdgeMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, binding.root)?.let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    @Suppress("DEPRECATION")
    private fun setFullScreen() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }

    private fun iniBottomSheet(): BottomSheetBehavior<*> {
        return BottomSheetBehavior.from(binding.container).apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        finish()
                        //Cancels animation on finish()
                        overridePendingTransition(0, 0)
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }
    }

    private fun onVoiceRecognized(spokenText: String) {
        binding.recognizedText.text = spokenText
        if (spokenText.contains("dismiss")) {
            hideBottomSheet()
        }
    }

    private fun hideBottomSheet() {
        Log.d(TAG, "hideBottomSheet: ")
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}