package com.graphit.taskmanagerprojetpack.loginsignup.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.graphit.taskmanagerprojetpack.loginsignup.repo.FirebaseAuthRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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