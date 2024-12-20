package kh.edu.rupp.ite.visitme.global

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class AppEncryptedPref private constructor(){

    fun storeToken(context: Context, token: String) {
        getPref(context).edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(context: Context): String? {
        return getPref(context).getString(KEY_TOKEN, null)
    }

    private fun getPref(context: Context): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            PREF_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    companion object {
        private const val PREF_NAME = "Autumn"
        private const val KEY_TOKEN = "token"
        private var instance: AppEncryptedPref? = null

        fun get(): AppEncryptedPref {
            if(instance == null) {
                instance = AppEncryptedPref()
            }

            return instance!!
        }

    }

}