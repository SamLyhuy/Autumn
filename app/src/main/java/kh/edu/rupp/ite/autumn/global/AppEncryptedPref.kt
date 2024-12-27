package kh.edu.rupp.ite.visitme.global

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import kh.edu.rupp.ite.autumn.data.model.LogInResponse
import kh.edu.rupp.ite.autumn.data.model.Profile

class AppEncryptedPref private constructor(){

    fun storeToken(context: Context, token: String) {
        Log.d("AppEncryptedPref", "Storing token: $token")
        getPref(context).edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(context: Context): String? {
        return getPref(context).getString(KEY_TOKEN, null)
    }

    fun clearToken(context: Context) {
        val sharedPreferences = getPref(context)  // Use EncryptedSharedPreferences here
        sharedPreferences.edit().remove(KEY_TOKEN).apply()
        Log.d("AppEncryptedPref", "Token cleared")
    }




//    fun storeToken(context: Context, logInResponse: LogInResponse) {
//        val loginJson = Gson().toJson(logInResponse)
//        getPref(context).edit().putString(KEY_TOKEN, loginJson).apply()
//    }
//
//    fun getToken(context: Context): LogInResponse? {
//        val loginJson = getPref(context).getString(KEY_TOKEN, null)
//        if (loginJson == null) {
//            return null
//        } else {
//            return Gson().fromJson(loginJson, LogInResponse::class.java)
//        }
//    }

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


//package kh.edu.rupp.ite.visitme.global
//
//import android.content.Context
//import android.content.SharedPreferences
//import android.util.Log
//import androidx.security.crypto.EncryptedSharedPreferences
//import androidx.security.crypto.MasterKeys
//import com.google.gson.Gson
//import kh.edu.rupp.ite.autumn.data.model.LogInResponse
//
//class AppEncryptedPref private constructor() {
//
//    fun storeToken(context: Context, token: String, expiresInSeconds: Long) {
//        Log.d("AppEncryptedPref", "Storing token: $token")
//        val expirationTime = System.currentTimeMillis() + (expiresInSeconds * 1000) // Convert seconds to milliseconds
//        getPref(context).edit().apply {
//            putString(KEY_TOKEN, token)
//            putLong(KEY_TOKEN_EXPIRATION, expirationTime)
//            apply()
//        }
//    }
//
//    fun getToken(context: Context): String? {
//        val sharedPreferences = getPref(context)
//        val expirationTime = sharedPreferences.getLong(KEY_TOKEN_EXPIRATION, 0L)
//        return if (System.currentTimeMillis() > expirationTime) {
//            Log.d("AppEncryptedPref", "Token is expired")
//            clearToken(context)
//            null
//        } else {
//            sharedPreferences.getString(KEY_TOKEN, null)
//        }
//    }
//
//    fun clearToken(context: Context) {
//        val sharedPreferences = getPref(context)
//        sharedPreferences.edit().apply {
//            remove(KEY_TOKEN)
//            remove(KEY_TOKEN_EXPIRATION)
//            apply()
//        }
//        Log.d("AppEncryptedPref", "Token cleared")
//    }
//
//    private fun getPref(context: Context): SharedPreferences {
//        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
//        return EncryptedSharedPreferences.create(
//            PREF_NAME,
//            masterKeyAlias,
//            context,
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )
//    }
//
//    companion object {
//        private const val PREF_NAME = "Autumn"
//        private const val KEY_TOKEN = "token"
//        private const val KEY_TOKEN_EXPIRATION = "token_expiration"
//        private var instance: AppEncryptedPref? = null
//
//        fun get(): AppEncryptedPref {
//            if (instance == null) {
//                instance = AppEncryptedPref()
//            }
//            return instance!!
//        }
//    }
//}
