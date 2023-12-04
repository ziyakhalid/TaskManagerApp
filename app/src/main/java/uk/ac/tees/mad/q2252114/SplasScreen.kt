package uk.ac.tees.mad.q2252114

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import java.util.*

class SplasScreen : AppCompatActivity() {

    private lateinit var fingerprintUtils: FingerprintUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        fingerprintUtils = FingerprintUtils(this)

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {

                if (fingerprintUtils.canAuthenticate()) {
                    val callback = object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            startActivity(Intent(this@SplasScreen, MainActivity::class.java))
                            finish()
                        }
                    }

                    fingerprintUtils.showBiometricPrompt(callback)
                } else {
                    Toast.makeText(this@SplasScreen, "invalid fingerprint", Toast.LENGTH_SHORT).show()

                }
            }
        }, 3000)
    }
}
