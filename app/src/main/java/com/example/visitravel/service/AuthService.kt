package com.example.visitravel.service

import com.example.visitravel.config.AppConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthService {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun register(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun resetPassword(email: String, callback: (Boolean, String) -> Unit) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Password reset email has been sent.")
                } else {
                    callback(false, "There was an error sending the password reset email.")
                }
            }
    }

    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUid()
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun changePassword(newPassword: String, password: String, callback: (Boolean, String?) -> Unit) {
        val user: FirebaseUser? = auth.currentUser

        user?.let {
            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(true, null)
                    } else {
                        val errorMessage = task.exception?.message ?: "An error occurred during the password change"
                        callback(false, errorMessage)
                    }
                }
        } ?: run {
            callback(false, "User not found")
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun autoLogin() :Boolean {
        val currentUser = getCurrentUser()
        if (currentUser != null) {
            saveUid()
            return true
        } else {
            return false
        }
    }

    fun checkEmailAvailability(email: String, callback: (Boolean, String?) -> Unit) {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods ?: emptyList<String>()
                    if (signInMethods.isEmpty()) {
                        // Email is available, not registered yet
                        callback(true, null)
                    } else {
                        // Email is already registered
                        callback(false, "This email is already in use")
                    }
                } else {
                    // An error occurred while checking email availability
                    callback(false, "Email validity could not be checked")
                }
            }
    }

    private fun saveUid(){
        val user = getCurrentUser()
        val uid = user?.uid
        AppConfig.uid = uid
    }
}