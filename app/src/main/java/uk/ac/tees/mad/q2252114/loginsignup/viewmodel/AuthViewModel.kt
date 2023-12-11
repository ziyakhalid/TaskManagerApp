package uk.ac.tees.mad.q2252114.loginsignup.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.ac.tees.mad.q2252114.loginsignup.repo.FirebaseAuthRepo

class AuthViewModel: ViewModel() {
    private val _userLoginStatus = MutableStateFlow<UserLoginStatus?>(null)
    val userLoginStatus = _userLoginStatus.asStateFlow()

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun performLogin(username: String, password: String) {
        FirebaseAuthRepo.login(
            firebaseAuth, username, password,
            onSuccess = {
                _userLoginStatus.value = UserLoginStatus.Successful
            },
            onFailure = {
                _userLoginStatus.value = UserLoginStatus.Failure(it)
            }
        )
    }
    fun createAccount(username: String, password: String) {
        FirebaseAuthRepo.signUp(
            firebaseAuth, username, password,
            onSuccess = {}, onFailure = {}
        )
    }
}

sealed class UserLoginStatus {
    data object Successful: UserLoginStatus()
    class Failure(val exception: Exception?): UserLoginStatus()
}