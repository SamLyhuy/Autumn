package kh.edu.rupp.ite.autumn.ui.element.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.util.JsonToken
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.autumn.data.model.Profile
import kh.edu.rupp.ite.autumn.databinding.ActivityAccountBinding
import kh.edu.rupp.ite.autumn.databinding.ActivityHomeBinding
import kh.edu.rupp.ite.autumn.databinding.ItemFoodBinding
import kh.edu.rupp.ite.autumn.global.AppPref
import kh.edu.rupp.ite.autumn.ui.element.activity.LogInActivity
import kh.edu.rupp.ite.visitme.global.AppEncryptedPref


class AccountFragment: Fragment() {

   private lateinit var binding: ActivityAccountBinding

//   private val activityLogInResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//       if(it.resultCode == Activity.RESULT_OK) {
//
//           val profile = AppPref.get().getProfile(requireContext())
//           showProfile(profile!!)
//
//
//       }
//   }

    private val activityLogInResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val token = AppEncryptedPref.get().getToken(requireContext())
            Log.d("AuthInterceptor", "Token after login: $token")  // Token should now be stored properly
            if (token != null) {
                showProfileTest()
            }
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpUi()
        setListener()


    }

    private fun setUpUi(){
        //val profile = AppPref.get().getProfile(requireContext())
        val token = AppEncryptedPref.get().getToken((requireContext()))

        Log.d("LogInActivity", "Profile: $token")

        if (token == null) {
            showLogInButton()
        } else {
            showProfileTest()
        }

//        if(AppPref.get().isLoggedIn(requireContext())) {
//            showAccount()
//        }else {
//            showLogInButton()
//        }
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
        Log.d("Logout", "Token after logout: $token")  // This should log null or an empty value


        // Clear other user data (if necessary)
        requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit().clear().apply()

        // Navigate to login screen
        val intent = Intent(requireContext(), LogInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        requireContext().startActivity(intent)
    }




    private fun showProfile(profile: Profile) {
        binding.lytAccount.isVisible = false
        binding.lytLogIn.isVisible = true

        //binding.profileName.text = profile.fullname()
        //Picasso.get().load(profile.coverImage).into(binding.profileImage)
    }

    private fun showProfileTest() {
        binding.lytAccount.isVisible = true
        binding.lytLogIn.isVisible = false


        //binding.profileName.text = profile.fullname()
        //Picasso.get().load(profile.coverImage).into(binding.profileImage)
    }


    private fun showLogInButton(){
        binding.lytAccount.isVisible = false
        binding.lytLogIn.isVisible = true


    }

}