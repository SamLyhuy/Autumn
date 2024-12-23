package kh.edu.rupp.ite.autumn.ui.element.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.Category
import kh.edu.rupp.ite.autumn.data.model.LogInResponse
import kh.edu.rupp.ite.autumn.data.model.LogInState
import kh.edu.rupp.ite.autumn.data.model.State
import kh.edu.rupp.ite.autumn.data.model.StateLogIn
import kh.edu.rupp.ite.autumn.databinding.ActivityLogInBinding
import kh.edu.rupp.ite.autumn.global.AppPref
import kh.edu.rupp.ite.autumn.ui.viewmodel.LogInViewModel
import kh.edu.rupp.ite.visitme.global.AppEncryptedPref

class LogInActivity: BaseActivity() {

    private lateinit var binding: ActivityLogInBinding

    private val viewModel by viewModels<LogInViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUi()
        setUpListener()
        setObserver()
    }


    private fun setupUi() {
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setUpListener() {
        binding.btnLogIn.setOnClickListener { onLogInButtonClick() }
    }

    private fun setObserver() {
        viewModel.logInData.observe(this) {
            handleState(it)
        }

    }

    private fun onLogInButtonClick() {

        val email = binding.edtUsername.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        Log.d("LogInActivity", "Email entered: $email")
        Log.d("LogInActivity", "Password entered: $password")

        if( email.isEmpty() || password.isEmpty()) {
            showAlert("Invalid Input", "Please entry Username and Password")
            return
        }

        viewModel.login(email, password)

    }

    private fun handleState(state: LogInState) {
        when (state.state) {
            StateLogIn.loading -> {
                Log.d("LogInActivity", "Login started...")
                showLoading()
            }
            StateLogIn.success -> {
                Log.d("LogInActivity", "Login successful")
                Log.d("LogInActivity", "Login Token: ${state.token}")

                // Store token securely
                AppEncryptedPref.get().storeToken(this, state.token ?: "")


                setResult(RESULT_OK)
                finish()
            }
            StateLogIn.error -> {
                Log.e("LogInActivity", "Login failed: ${state.message}")
                hideLoading()
                showAlert("Error", state.message ?: "Unexpected Error!")
            }
            else -> {
                Log.d("LogInActivity", "Unhandled state: ${state.state}")
            }
        }
    }

}

//AppPref.get().setLoggedIn(this, true)
//AppPref.get().storeProfile(this, state.data!!.profile)