package com.ranseo.yatchgame.ui.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.databinding.ActivityGameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}