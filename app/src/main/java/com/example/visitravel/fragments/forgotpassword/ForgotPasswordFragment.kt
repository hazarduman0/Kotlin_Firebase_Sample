package com.example.visitravel.fragments.forgotpassword

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.visitravel.activity.login.LoginViewModel
import com.example.visitravel.databinding.FragmentForgotPasswordBinding
import com.example.visitravel.service.AuthService
import com.example.visitravel.utils.Helper
import com.example.visitravel.utils.LoginFragmentType

class ForgotPasswordFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel

    private var _binding: FragmentForgotPasswordBinding? = null

    private val binding get() = _binding!!

    val helper = Helper()
    var authService = AuthService()

    companion object {
        fun newInstance() = ForgotPasswordFragment()
    }

    private lateinit var viewModel: ForgotPasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.forgotObject = this

        emailFocusListener()

        //return inflater.inflate(R.layout.fragment_forgot_password, container, false)
        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginViewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]
    }

    fun onResetPasswordButtonClick(email: String){
        if (email.isNotEmpty()) {
            authService.resetPassword(email) { success, message ->
                if (success) {
                    Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(),"Email field cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    fun onBackButtonClick() {
        loginViewModel.changeFrameVisibility(false)
        loginViewModel.rootToPage(LoginFragmentType.LOGIN)
    }

    private fun emailFocusListener() {
        binding.forgotEmailEditText.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.forgotEmail.helperText =
                    helper.validEmail(binding.forgotEmailEditText.text.toString())
            }
        }
    }
}