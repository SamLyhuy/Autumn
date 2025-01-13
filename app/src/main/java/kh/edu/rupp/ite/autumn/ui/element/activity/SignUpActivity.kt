package kh.edu.rupp.ite.autumn.ui.element.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import kh.edu.rupp.ite.autumn.R
import kh.edu.rupp.ite.autumn.data.model.ApiResponse
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.Profile
import kh.edu.rupp.ite.autumn.data.model.RegisterData
import kh.edu.rupp.ite.autumn.data.model.State
import kh.edu.rupp.ite.autumn.databinding.ActivitySignupBinding
import kh.edu.rupp.ite.autumn.ui.element.fragment.AccountFragment
import kh.edu.rupp.ite.autumn.ui.viewmodel.SignUpViewModel
import kh.edu.rupp.ite.visitme.global.AppEncryptedPref

class SignUpActivity: BaseActivity() {

    private lateinit var binding: ActivitySignupBinding

    private val viewModel by viewModels<SignUpViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUi()
        setUpListener()
        setObserver()
    }

    private fun setupUi() {
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    private fun setUpListener() {
        binding.signInButton.setOnClickListener{
            onSignUpButtonClick()
        }
        binding.tabSignUp.setOnClickListener{
            onLogInButtonClick()
        }
    }
    private fun setObserver() {
        viewModel.registerData.observe(this) {
            handleState(it)
        }
    }

    private fun onLogInButtonClick() {
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun onSignUpButtonClick() {

        val name = binding.fullNameInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val phoneNumber = binding.phNumberInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()
        val confirmPassword = binding.confirmPasswordInput.text.toString().trim()

        // Check if all fields are filled
        if (name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Invalid Input", "All fields are required")
            return
        }

        // Check if passwords match
        if (password != confirmPassword) {
            showAlert("Invalid Input", "Passwords do not match")
            return
        }

        // Proceed with signup
        //val registerData = RegisterData(name, email, phoneNumber, password, profile = "")
        val registerData = RegisterData(name, email, phoneNumber, password)
        viewModel.postRegister(registerData)
    }



    private fun handleState(state: ApiState<RegisterData>) {
        when(state.state){
            State.success -> {
                Log.d("SignUpActivity", "${state.message}")
                Log.d("SignUpActivity", "Data: ${state.data}")
                Log.d("SignUpActivity", "Token: ${state.token}")

                AppEncryptedPref.get().storeToken(this, state.token ?:"")

//                setResult(RESULT_OK)
//                finish()

                val intent = Intent()
                intent.putExtra("navigateTo", "AccountFragment")
                setResult(RESULT_OK, intent)

                finish() // Close SignUpActivity

                //hideLoading()
            }
            State.error -> {
                showAlert("SignUpActivity", state.message ?: "An unexpected error occurred")
                Log.e("SignUpActivity", "Error message: ${state.message}")
            }
            else ->
                Log.d("SignUpActivity", "Unhandled state: ${state.state}")
        }

    }





}