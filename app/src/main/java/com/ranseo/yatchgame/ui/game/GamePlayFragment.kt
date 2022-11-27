package com.ranseo.yatchgame.ui.game

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.GameInfo
import com.ranseo.yatchgame.databinding.FragmentGamePlayBinding
import com.ranseo.yatchgame.log
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GamePlayFragment : Fragment() {
    private val TAG = "GamePlayFragment"
    private lateinit var binding: FragmentGamePlayBinding

    private val gamePlayViewModel: GamePlayViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_play, container, false)

        binding.viewModel = gamePlayViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        gamePlayViewModel.gameId.observe(viewLifecycleOwner, gameIdObserver())
        gamePlayViewModel.gameInfo.observe(viewLifecycleOwner, gameInfoObserver())
        return binding.root
    }

    private fun gameInfoObserver() =
        Observer<GameInfo> {
            it?.let{ gameInfo ->
                log(TAG, "gameInfoObserver : ${gameInfo}", LogTag.I)
            }
        }


    private fun gameIdObserver() =
        Observer<String> {
            it?.let { gameId ->
                log(TAG, "gameInfoObserver : ${gameId}", LogTag.I)
                gamePlayViewModel.refreshGameInfo(gameId)
            }
        }
}