package com.ranseo.yatchgame.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.databinding.ActivitySplashBinding
import com.ranseo.yatchgame.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
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
