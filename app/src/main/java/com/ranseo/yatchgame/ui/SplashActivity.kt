package com.ranseo.yatchgame.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ranseo.yatchgame.BuildConfig
import com.ranseo.yatchgame.databinding.ActivitySplashBinding
import com.ranseo.yatchgame.ui.lobby.LobbyActivity
import com.ranseo.yatchgame.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySplashBinding

    @Inject lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.diceAnimation.playAnimation()

        if(BuildConfig.DEBUG) {
            Firebase.auth.useEmulator("10.0.2.2", 9099)
            Firebase.database.useEmulator("10.0.2.2", 9000)
        }

    }


    override fun onStart() {
        super.onStart()
        GlobalScope.launch {
            delay(3000)
            auth.currentUser?.let { startLobbyAct() } ?: startLoginAct()
            finish()
        }
    }

    private fun startLoginAct() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
    private fun startLobbyAct() {
        startActivity(Intent(this, LobbyActivity::class.java))
    }
}
