package kh.edu.rupp.ite.autumn.global

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import kh.edu.rupp.ite.autumn.data.api.client.ApiClient
import kh.edu.rupp.ite.autumn.data.model.Profile

class AppPref private constructor(){


    fun storeProfile(context: Context, profile: Profile){
        val profileJson = Gson().toJson(profile)
        getPref(context).edit().putString(KEY_PROFILE, profileJson).apply()
    }

    fun getProfile(context: Context): Profile? {
        val profileJson = getPref(context).getString(KEY_PROFILE, null)

        if (profileJson == null) {
            return null
        } else {
            return Gson().fromJson(profileJson, Profile::class.java)
        }
    }


    private fun getPref(context: Context): SharedPreferences {

        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    companion object {

        private const val PREF_NAME = "Autumn"
        private const val KEY_PROFILE = "profile"

        private var instance: AppPref? = null
        fun get(): AppPref {
            if (instance == null) {
                instance = AppPref()
            }

            return instance!!
        }




    }
}