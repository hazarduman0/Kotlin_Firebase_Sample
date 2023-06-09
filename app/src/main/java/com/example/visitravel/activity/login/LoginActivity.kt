package com.example.visitravel.activity.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.visitravel.R
import com.example.visitravel.activity.main.MainActivity
import com.example.visitravel.databinding.ActivityLoginBinding
import com.example.visitravel.fragments.forgotpassword.ForgotPasswordFragment
import com.example.visitravel.fragments.register.RegisterFragment
import com.example.visitravel.service.AuthService
import com.example.visitravel.utils.LoginFragmentType

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    val authService = AuthService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.loginObject = this

        if (authService.autoLogin()) navigateMain()

        viewModel.isFrameVisible.observe(this){ isFrameVisible ->
            with(binding){
                if (isFrameVisible){
                    frameLayout.visibility = View.VISIBLE
                }
                else{
                    frameLayout.visibility = View.GONE
                }
            }
        }

        viewModel.isLoginSucces.observe(this){isLoginSucces ->
            with(binding){
                if (isLoginSucces){
                    navigateMain()
                }
            }
        }

        viewModel.loginFragmentType.observe(this){loginFragmentType ->
            with(binding){

                when (loginFragmentType) {
                    LoginFragmentType.REGISTER -> {
                        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, RegisterFragment()).commit()
                        loginSignInButton.visibility = View.GONE
                    }
                    LoginFragmentType.FORGOTPASSWORD -> {
                        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, ForgotPasswordFragment()).commit()
                        loginSignInButton.visibility = View.GONE
                    }
                    LoginFragmentType.LOGIN -> {
                        closeFragment()
                        loginSignInButton.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun closeFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = fragmentManager.findFragmentById(R.id.frameLayout)

        if (fragment != null) {
            fragmentTransaction.remove(fragment).commit()
        }
    }

    private fun navigateMain(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun forgotClick(){
        viewModel.rootToPage(LoginFragmentType.FORGOTPASSWORD)
        viewModel.changeFrameVisibility(true)
    }

    fun signUpClick(){
        viewModel.rootToPage(LoginFragmentType.REGISTER)
        viewModel.changeFrameVisibility(true)
    }

    fun loginClick(email: String, password: String){
        viewModel.checkCanLogin(email, password, this)
    }
}