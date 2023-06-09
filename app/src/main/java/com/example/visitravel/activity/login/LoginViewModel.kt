package com.example.visitravel.activity.login

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.visitravel.service.AuthService
import com.example.visitravel.utils.LoginFragmentType

class LoginViewModel : ViewModel() {
    var isFrameVisible = MutableLiveData<Boolean>(false)
    var isLoginSucces = MutableLiveData<Boolean>(false)
    var loginFragmentType = MutableLiveData<LoginFragmentType>(LoginFragmentType.LOGIN)

    val authService = AuthService()

    fun rootToPage(fragmentType: LoginFragmentType){
        loginFragmentType.value = fragmentType
    }

    fun changeFrameVisibility(isVisible: Boolean){
        isFrameVisible.value = isVisible
    }

    private fun login(email: String, password: String, context: Context){
        authService.login(email, password) { success, errorMessage ->
            if (success) {
                //navigateToLogin(loginViewModel)
                isLoginSucces.value = true
                Toast.makeText(context, "Welcome", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun checkCanLogin(email: String, password: String, context: Context){
        authService.checkEmailAvailability(email) { available, error ->
            if (!available) {
                login(email, password, context)
            } else {
                Toast.makeText(context, "This e-mail address is not registered.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}