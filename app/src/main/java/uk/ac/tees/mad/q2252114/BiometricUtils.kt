package uk.ac.tees.mad.q2252114

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

class BiometricUtils(private val context: Context) {

    private val biometricManager = BiometricManager.from(context)

    fun isFaceAuthAvailable(): Boolean {
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun isFingerprintAuthAvailable(): Boolean {
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun showBiometricPrompt(
        callback: BiometricPrompt.AuthenticationCallback,
        promptInfo: BiometricPrompt.PromptInfo?
    ) {
        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(
            context as androidx.fragment.app.FragmentActivity,
            executor,
            callback
        )
        if (promptInfo != null) {
            biometricPrompt.authenticate(promptInfo)
        }
    }


    fun createBiometricPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Place your face or finger on the sensor")
            .setDescription("This app uses biometric authentication to secure your data.")
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()
    }


    fun hasBiometricPermission(): Boolean {
        return true
    // You may need to implement this based on your app's permission handling logic
    }
}

