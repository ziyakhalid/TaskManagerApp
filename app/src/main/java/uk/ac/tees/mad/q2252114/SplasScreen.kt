package uk.ac.tees.mad.q2252114

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import uk.ac.tees.mad.q2252114.loginsignup.MainActivity
import java.util.*

class SplasScreen : AppCompatActivity() {

    private lateinit var biometricUtils: BiometricUtils
    private val mainHandler = Handler(Looper.getMainLooper())

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        biometricUtils = BiometricUtils(this)

        checkAndRequestPermissions()

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                mainHandler.post {
                    when {
                        biometricUtils.isFaceAuthAvailable() -> {
                            val promptInfo = biometricUtils.createBiometricPromptInfo()
                            biometricUtils.showBiometricPrompt(
                                createBiometricCallback(),
                                promptInfo
                            )
                        }
                        biometricUtils.isFingerprintAuthAvailable() -> {
                            val callback = createBiometricCallback()
                            biometricUtils.showBiometricPrompt(callback, null)
                        }
                        else -> {
                            moveToNextActivity()
                        }
                    }
                }
            }
        }, 3000)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkAndRequestPermissions() {
        val requiredPermissions = arrayOf(
            android.Manifest.permission.USE_FINGERPRINT,
            android.Manifest.permission.USE_BIOMETRIC,
            android.Manifest.permission.CAMERA
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val notGrantedPermissions = requiredPermissions.filter {
                checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
            }

            if (notGrantedPermissions.isNotEmpty()) {
                // Display a dialog explaining the necessity of permissions
                showPermissionExplanationDialog(notGrantedPermissions.toTypedArray())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showPermissionExplanationDialog(permissions: Array<String>) {
        AlertDialog.Builder(this)
            .setTitle("Permissions Required")
            .setMessage("This app requires certain permissions to function properly. Please grant the necessary permissions.")
            .setPositiveButton("Grant") { _, _ ->
                requestPermissions(permissions, PERMISSION_REQUEST_CODE)
            }
            .setNegativeButton("Exit App") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun createBiometricCallback(): BiometricPrompt.AuthenticationCallback {
        return object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                moveToNextActivity()
            }
        }
    }

    private fun moveToNextActivity() {
        startActivity(Intent(this@SplasScreen, MainActivity::class.java))
        finish()
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }
}
