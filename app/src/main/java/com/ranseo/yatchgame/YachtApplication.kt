package com.ranseo.yatchgame

import android.app.Application
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.core.utilities.Utilities
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ranseo.yatchgame.util.YachtGame
import com.ranseo.yatchgame.util.YachtSound
import dagger.hilt.android.HiltAndroidApp
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.inject.Inject

@HiltAndroidApp
class YachtApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        FirebaseApp.initializeApp(this)

        getAppKeyHash()

    }

    private fun getAppKeyHash() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                log("YachtApplication", "해시키 : $hashKey", LogTag.I)
            }
        } catch (e: Exception) {
            log("YachtApplication", "해시키를 찾을 수 없습니다 : $e", LogTag.I)
        }
    }
}


