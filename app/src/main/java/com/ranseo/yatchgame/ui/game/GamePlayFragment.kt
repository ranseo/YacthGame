package com.ranseo.yatchgame.ui.game

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.databinding.FragmentGamePlayBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GamePlayFragment : Fragment() {
    private lateinit var binding:FragmentGamePlayBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_play, container, false)
        return binding.root
    }
}