package com.example.visitravel.fragments.register

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.visitravel.activity.login.LoginViewModel
import com.example.visitravel.service.AuthService
import com.example.visitravel.utils.LoginFragmentType

class RegisterViewModel : ViewModel() {

    val authService = AuthService()

    private fun register(email: String, password: String, context: Context, loginViewModel: LoginViewModel){
        authService.register(email, password) { success, errorMessage ->
            if (success) {
                navigateToLogin(loginViewModel)
                Toast.makeText(context, "Register Succes", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

     fun checkEmailCanRegister(email: String, password: String, context: Context, loginViewModel: LoginViewModel){
        authService.checkEmailAvailability(email) { available, error ->
            if (available) {
                register(email, password, context, loginViewModel)
            } else {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun navigateToLogin(loginViewModel: LoginViewModel){
        loginViewModel.changeFrameVisibility(false)
        loginViewModel.rootToPage(LoginFragmentType.LOGIN)
    }
}