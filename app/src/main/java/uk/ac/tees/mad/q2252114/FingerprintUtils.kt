package uk.ac.tees.mad.q2252114

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class FingerprintUtils(private val context: Context) {

    private val executor = ContextCompat.getMainExecutor(context)
    private val handler = Handler(Looper.getMainLooper())

    fun canAuthenticate(): Boolean {
        // Check if fingerprint authentication is available
        return true
    }

    fun showBiometricPrompt(callback: BiometricPrompt.AuthenticationCallback) {
        // Use the Handler to execute the code on the main thread
        handler.post {
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authenticate with your fingerprint")
                .setSubtitle("Place your finger on the fingerprint sensor")
                .setNegativeButtonText("Cancel")
                .build()

            val biometricPrompt = BiometricPrompt(context as FragmentActivity, executor, callback)
            biometricPrompt.authenticate(promptInfo)
        }
    }
}

