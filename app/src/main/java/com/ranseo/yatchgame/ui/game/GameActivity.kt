package com.ranseo.yatchgame.ui.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.databinding.ActivityGameBinding
import com.ranseo.yatchgame.util.YachtSound
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onBackPressed() {
        if(onBackPressedDispatcher.hasEnabledCallbacks()) {
            onBackPressedDispatcher.onBackPressed()
            return
        }
        super.onBackPressed()

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}