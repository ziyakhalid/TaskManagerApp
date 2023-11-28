package uk.ac.tees.mad.q2252114.loginsignup.repo

import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthRepo {
    fun login(
        firebaseAuth: FirebaseAuth,
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception?) -> Unit
    ) {

        firebaseAuth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(it.exception)
                }
            }
    }

    fun signUp(
        firebaseAuth: FirebaseAuth,
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception?) -> Unit,
    ) {
        firebaseAuth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(it.exception)
                }
            }
    }
}