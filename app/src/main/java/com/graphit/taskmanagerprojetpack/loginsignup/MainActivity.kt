package com.graphit.taskmanagerprojetpack.loginsignup

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.graphit.taskmanagerprojetpack.MainActivity
import com.graphit.taskmanagerprojetpack.loginsignup.ui.theme.LoginAndRegistrationDemoAppTheme
import com.graphit.taskmanagerprojetpack.loginsignup.ui.theme.c1
import com.graphit.taskmanagerprojetpack.loginsignup.ui.theme.c2
import com.graphit.taskmanagerprojetpack.loginsignup.ui.theme.c3
import com.graphit.taskmanagerprojetpack.loginsignup.ui.theme.c4
import com.graphit.taskmanagerprojetpack.loginsignup.ui.theme.c5_
import com.graphit.taskmanagerprojetpack.loginsignup.ui.theme.c6
import com.graphit.taskmanagerprojetpack.loginsignup.ui.theme.c7
import com.graphit.taskmanagerprojetpack.loginsignup.ui.theme.c8
import com.graphit.taskmanagerprojetpack.loginsignup.ui.theme.c9
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Obtain the FirebaseAnalytics instance.
        analytics = Firebase.analytics
        auth = FirebaseAuth.getInstance()
        setContent {
            LoginAndRegistrationDemoAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginAndRegistrationPreview()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginAndRegistration(auth: FirebaseAuth = remember { FirebaseAuth.getInstance() }) {

    var usr by remember { mutableStateOf("") }
    var psw by remember { mutableStateOf("") }
    val mContext = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerContent ->
        Column(
            modifier = Modifier
                .padding(innerContent)
                .fillMaxSize()
                .wrapContentSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxSize()
            ) {
                // signup Logic
                fun signUpUser(usr: String, psw: String) {
                    if (usr.isBlank() || psw.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Form cannot be left blank")
                        }
                    } else {
                        auth.createUserWithEmailAndPassword(usr, psw)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // sign up success
                                    println("Signup Success!")
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Signup Success!")
                                    }
                                } else {
                                    // sign up failed
                                    println("Signup Failed: ${task.exception?.message}")
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Signup Failed: ${task.exception?.message}")
                                    }
                                }
                            }
                    }
                }

                // login logic
                fun loginUser(usr: String, psw: String) {
                    if (usr.isBlank() || psw.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Form cannot be left blank")
                        }
                    } else {
                        auth.signInWithEmailAndPassword(usr, psw)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    mContext.startActivity(Intent(mContext, com.graphit.taskmanagerprojetpack.MainActivity::class.java))
                                    println("Login success")
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Login Success!")
                                    }

                                } else {
                                    println("Sign in failed: ${task.exception?.message}")
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Login Failed: ${task.exception?.message}")
                                    }
                                }
                            }
                    }
                }

                GreetingText()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    UsernameAndPasswordTextField(usr, { usr = it }, psw, { psw = it })
                    Spacer(modifier = Modifier.height(16.dp))
                    PasswordReset()
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                )
                {
                    // sign in button
                    Button(
                        modifier = Modifier
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(c1, c2, c3, c4, c5_, c6, c7, c8, c9)
                                ),
                                shape = ButtonDefaults.shape
                            )
                            .border(0.25.dp, Color.DarkGray, ButtonDefaults.shape),
                        onClick = { loginUser(usr, psw) },
                        shape = ButtonDefaults.shape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.DarkGray,
                        )
                    ) {
                        Text(
                            text = "Login",
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(250.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Don't have an account?",
                            style = MaterialTheme.typography.titleSmall
                        )
                        // sign-in text button
                        TextButton(
                            onClick = { signUpUser(usr, psw) },
                        ) {
                            Text(
                                text = "Sign up!",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GreetingText() {
    Text(
        text = "Welcome to Task Manager!",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        fontSize = 42.sp,
        letterSpacing = 2.sp,
        lineHeight = 54.sp,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsernameAndPasswordTextField(
    usr: String,
    onUsernameChange: (String) -> Unit,
    psw: String,
    onPasswordChange: (String) -> Unit
) {
    var pswVisible by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = usr,
            onValueChange = { onUsernameChange(it) },
            label = { Text("Username") },
            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = "Username Icon") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = psw,
            onValueChange = { onPasswordChange(it) },
            label = { Text(text = "Password") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = "Password Icon") },
            singleLine = true,
            visualTransformation = if (pswVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            trailingIcon = {
                val visibilityStatus = if (pswVisible)
                    Icons.Outlined.Visibility
                else
                    Icons.Outlined.VisibilityOff
                // Accessibility
                val description = if (pswVisible) "Hide Password" else "Show password"

                IconButton(onClick = { pswVisible = !pswVisible }) {
                    Icon(imageVector = visibilityStatus, contentDescription = description)
                }
            }
        )
    }
}

@Composable
fun PasswordReset() {
    val context = LocalContext.current
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 46.dp)
    ) {
        TextButton(
            onClick = {
                val intent = Intent(context, ForgotPasswordActivity::class.java)
                context.startActivity(intent)
            }
        ) {
            Text(text = "Forgot password?")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginAndRegistrationPreview() {
    LoginAndRegistrationDemoAppTheme {
        LoginAndRegistration()
    }
}