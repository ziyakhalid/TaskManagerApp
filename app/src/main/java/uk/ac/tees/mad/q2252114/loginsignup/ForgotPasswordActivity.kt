package uk.ac.tees.mad.q2252114.loginsignup

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

import kotlinx.coroutines.launch
import uk.ac.tees.mad.q2252114.R
import uk.ac.tees.mad.q2252114.loginsignup.ui.theme.LoginAndRegistrationDemoAppTheme
import uk.ac.tees.mad.q2252114.loginsignup.ui.theme.c1
import uk.ac.tees.mad.q2252114.loginsignup.ui.theme.c2
import uk.ac.tees.mad.q2252114.loginsignup.ui.theme.c3
import uk.ac.tees.mad.q2252114.loginsignup.ui.theme.c4
import uk.ac.tees.mad.q2252114.loginsignup.ui.theme.c5_
import uk.ac.tees.mad.q2252114.loginsignup.ui.theme.c6
import uk.ac.tees.mad.q2252114.loginsignup.ui.theme.c7
import uk.ac.tees.mad.q2252114.loginsignup.ui.theme.c8
import uk.ac.tees.mad.q2252114.loginsignup.ui.theme.c9

private lateinit var auth: FirebaseAuth

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContent {
            LoginAndRegistrationDemoAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ResetPasswordPreview()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPassword() {
    // header block background with image (half)
    val bgLogo = painterResource(id = R.drawable.vivid_blurred_colorful_background)
    val headerLogo = painterResource(id = R.drawable.header_logo)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    val activity = LocalContext.current

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerContent ->
        Column(
            modifier = Modifier
                .padding(innerContent)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .height(265.dp)
                ) {
                    Image(
                        painter = bgLogo,
                        contentDescription = "Header Background Image",
                        alignment = Alignment.TopCenter,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Image(
                        painter = headerLogo,
                        contentDescription = "Header Logo Brand Text",
                        contentScale = ContentScale.Crop,
                    )
                }
                Spacer(modifier = Modifier.height(46.dp))
                Text(
                    text = "Forgot Password Screen",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Light,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Enter your email address we'll send you an email to reset your password",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp),
                )
                Spacer(modifier = Modifier.height(42.dp))
                EmailTextField(email = email, onEmailChange = { email = it })
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(c1, c2, c3, c4, c5_, c6, c7, c8, c9)
                            ),
                            shape = ButtonDefaults.shape
                        )
                        .border(0.25.dp, Color.DarkGray, ButtonDefaults.shape),
                    onClick = {
                        if (email.isBlank()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Enter an email address to continue")
                            }
                        } else {
                            auth.sendPasswordResetEmail(email)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        println("Email Sent")
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Email Sent")

                                            val intent = Intent(activity, MainActivity::class.java)
                                            activity.startActivity(intent)
                                        }

                                    } else {
                                        println("Failed to send email")
                                        scope.launch {
                                            snackbarHostState.showSnackbar("${task.exception?.message}")
                                        }
                                    }
                                }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.DarkGray
                    )
                ) {
                    Text(
                        text = "Submit",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(300.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailTextField(email: String, onEmailChange: (String) -> Unit) {
    OutlinedTextField(
        value = email,
        onValueChange = { onEmailChange(it) },
        label = { Text(text = "Email") },
        placeholder = { Text(text = "email@example.com") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Email,
                contentDescription = "Email Icon"
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier
            .width(350.dp)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResetPasswordPreview() {
    LoginAndRegistrationDemoAppTheme {
        ResetPassword()
    }
}