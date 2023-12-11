package uk.ac.tees.mad.q2252114.loginsignup


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
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

class SignUpActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginAndRegistrationDemoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SignUpContent()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpContent(auth: FirebaseAuth = remember { FirebaseAuth.getInstance() }) {
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
                                    val intent = Intent(mContext, MainActivity::class.java)
                                    mContext.startActivity(intent)
                                    println("Signup Success!")
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Signup Success!")
                                    }
                                } else {
                                    println("Signup Failed: ${task.exception?.message}")
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Signup Failed: ${task.exception?.message}")
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
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                )
                {
                    // sign up button
                    Button(
                        modifier = Modifier
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(c1, c2, c3, c4, c5_, c6, c7, c8, c9)
                                ),
                                shape = ButtonDefaults.shape
                            )
                            .border(0.25.dp, Color.DarkGray, ButtonDefaults.shape),
                        onClick = { signUpUser(usr, psw) },
                        shape = ButtonDefaults.shape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.DarkGray,
                        )
                    ) {
                        Text(
                            text = "Sign Up",
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(250.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpPreview() {
    LoginAndRegistrationDemoAppTheme {
        SignUpContent()
    }
}
