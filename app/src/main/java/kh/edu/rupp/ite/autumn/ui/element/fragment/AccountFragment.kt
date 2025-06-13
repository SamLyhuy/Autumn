package kh.edu.rupp.ite.autumn.ui.element.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kh.edu.rupp.ite.autumn.R
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.Profile
import kh.edu.rupp.ite.autumn.databinding.ActivityAccountBinding
import kh.edu.rupp.ite.autumn.global.AppPref
import kh.edu.rupp.ite.autumn.ui.element.activity.LogInActivity
import kh.edu.rupp.ite.visitme.global.AppEncryptedPref
import kotlinx.coroutines.launch


class AccountFragment: Fragment() {

    private lateinit var binding: ActivityAccountBinding


    private val activityLogInResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val token = AppEncryptedPref.get().getToken(requireContext())
            Log.d("AccountFragment", "Stored token: $token")
            if (token != null) {
                fetchUserInfo()
            }
        }
    }




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpUi()
        setListener()
    }

    private fun fetchUserInfo() {

        val token = AppEncryptedPref.get().getToken(requireContext())
        if (token == null) {
            Log.e("AccountFragment", "Token is null. Cannot fetch user info.")
            showLogInButton()
            return
        }
        // Get User Info
        lifecycleScope.launch {
            try {
                // pass token to viewModel to get User Info
                val response = ApiClient.get().apiService.getUserInfo("Bearer $token")
                Log.d("AccountFragment", "User found")

                val userProfile = response.data?.data
                Log.d("AccountFragment", "userProfile: $userProfile")

                if (userProfile != null) {
                    // Store the fetched profile data in SharedPreferences
                    AppPref.get().storeProfile(requireContext(), userProfile)
                    showProfile(userProfile)
                } else {
                    Log.e("AccountFragment", "No user profile data found.")
                    showLogInButton()
                }
            } catch (e: Exception) {
                Log.e("AccountFragment", "Exception: ${e.message}")
                showLogInButton()
            }

        }

    }


    private fun setUpUi(){
        //val profile = AppPref.get().getProfile(requireContext())
        val token = AppEncryptedPref.get().getToken((requireContext()))

        Log.d("AccountFragment", "Profile: $token")

        if (token == null) {
            showLogInButton()
        } else {
            fetchUserInfo()
        }

    }

    private fun setListener(){
        binding.btnLogIn.setOnClickListener { onLogInButtonClick() }
        binding.btnLogOut.setOnClickListener { onLogOutButtonClick()}
    }

    private fun onLogInButtonClick(){
        val intent = Intent(requireContext(), LogInActivity::class.java)
        activityLogInResult.launch(intent)
    }


    private fun onLogOutButtonClick() {
        // Clear the token
        AppEncryptedPref.get().clearToken(requireContext())

        val token = AppEncryptedPref.get().getToken(requireContext())
        Log.d("AccountFragment", "Token after logout: $token")  // This should log null or an empty value


        // Clear other user data (if necessary)
        requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit().clear().apply()

        // Navigate to login screen
        val intent = Intent(requireContext(), LogInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        requireContext().startActivity(intent)
    }

    private fun showProfile(profile: Profile) {
        binding.lytAccount.isVisible = true
        binding.lytLogIn.isVisible = false

        binding.profileName.text = profile.name
        binding.profileEmail.text = profile.email
        binding.profilePhone.text = profile.phonenumber

    }


    private fun showLogInButton(){
        binding.lytAccount.isVisible = false
        binding.lytLogIn.isVisible = true
        Log.d("AccountFragment", "Displayed login button due to missing profile.")
    }

}