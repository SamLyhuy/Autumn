package kh.edu.rupp.ite.autumn.ui.element.fragment

import android.app.Activity
import android.content.Intent
import android.os.Binder
import android.os.Bundle
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


class AccountFragment: Fragment() {

   private lateinit var binding: ActivityAccountBinding

   private val activityLogInResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
       if(it.resultCode == Activity.RESULT_OK) {

           val profile = AppPref.get().getProfile(requireContext())
           showProfile(profile!!)

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
        val profile = AppPref.get().getProfile(requireContext())

        if (profile == null) {
            showLogInButton()
        } else {
            showProfile(profile)
        }

//        if(AppPref.get().isLoggedIn(requireContext())) {
//            showAccount()
//        }else {
//            showLogInButton()
//        }
    }

    private fun setListener(){
        binding.btnLogIn.setOnClickListener { onLogInButtonClick() }
    }

    private fun onLogInButtonClick(){
        val intent = Intent(requireContext(), LogInActivity::class.java)
        activityLogInResult.launch(intent)

    }


    private fun showProfile(profile: Profile) {
        binding.lytAccount.isVisible = true
        binding.lytLogIn.isVisible = false

        binding.profileName.text = profile.fullname()
        //Picasso.get().load(profile.coverImage).into(binding.profileImage)
    }

    private fun showLogInButton(){
        binding.lytLogIn.isVisible = false
        binding.lytAccount.isVisible = true

    }

}