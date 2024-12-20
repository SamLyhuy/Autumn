package kh.edu.rupp.ite.autumn.ui.element.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import kh.edu.rupp.ite.autumn.data.model.ApiState
import kh.edu.rupp.ite.autumn.data.model.Category
import kh.edu.rupp.ite.autumn.data.model.LogInResponse
import kh.edu.rupp.ite.autumn.data.model.State
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

        val username = binding.edtUsername.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if( username.isEmpty() || password.isEmpty()) {
            showAlert("Invalid Input", "Please entry Username and Password")
            return
        }

        viewModel.login(username, password)

    }

    private fun handleState(state: ApiState<LogInResponse>) {
        when (state.state) {
            State.loading -> showLoading()
            State.success -> {
                //AppPref.get().setLoggedIn(this, true)
                AppPref.get().storeProfile(this, state.data!!.profile)
                // store token
                AppEncryptedPref.get().storeToken(this, state.data.token)
                setResult(RESULT_OK)
                finish()
            }
            State.error -> {
                hideLoading()
                showAlert("Error", state.message ?: "Unexpected Error!")
            }
            else -> {}
        }
    }

}