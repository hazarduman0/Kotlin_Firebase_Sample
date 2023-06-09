package com.example.visitravel.fragments.register

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.visitravel.activity.login.LoginViewModel
import com.example.visitravel.databinding.FragmentRegisterBinding
import com.example.visitravel.utils.Helper

class RegisterFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel

    private var _binding: FragmentRegisterBinding? = null

    private val binding get() = _binding!!

    val helper = Helper()

    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater,container,false)
        val root: View = binding.root
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        binding.registerObject = this

        emailFocusListener()
        passwordFocusListener()

        //return inflater.inflate(R.layout.fragment_register, container, false)
        return root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginViewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]
    }

    fun onBackButtonClick(){
        viewModel.navigateToLogin(loginViewModel)
    }

    private fun emailFocusListener(){
        binding.signUpEmailEditText.setOnFocusChangeListener { _, focused ->
            if (!focused){
                binding.signUpEmail.helperText = helper.validEmail(binding.signUpEmailEditText.text.toString())
            }
        }
    }

    private fun passwordFocusListener()
    {
        binding.signUpPasswordEditText.setOnFocusChangeListener { _, focused ->
            if(!focused)
            {
                binding.signUpPassword.helperText = helper.validPassword(binding.signUpPasswordEditText.text.toString())
            }
        }
    }

    fun registerButtonClick(email: String, password: String){

        if (helper.validEmail(email) != null || helper.validPassword(password) != null) return

        viewModel.checkEmailCanRegister(email, password, requireContext(), loginViewModel)
    }

}