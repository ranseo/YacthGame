package com.ranseo.yatchgame.ui.game

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.GameInfo
import com.ranseo.yatchgame.data.model.Player
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
        gamePlayViewModel.myTurn.observe(viewLifecycleOwner, myTurnObserver())
        gamePlayViewModel.firstPlayer.observe(viewLifecycleOwner, firstPlayerObserver())

        setRollDiceImageViewClickListener()

        return binding.root
    }


    /**
     * fragment_game_play.xml의 ImageView (iv_roll_dice_first..sixth)에 대해서 해당 iv를 클릭했을 때
     * Selected 되도록 만드는 ClickListener를 등록.
     * */
    private fun setRollDiceImageViewClickListener() {
        binding.ivRollFirst.setOnClickListener(rollDiceSelected(0))
        binding.ivRollSecond.setOnClickListener(rollDiceSelected(1))
        binding.ivRollThird.setOnClickListener(rollDiceSelected(2))
        binding.ivRollFourth.setOnClickListener(rollDiceSelected(3))
        binding.ivRollFifth.setOnClickListener(rollDiceSelected(4))
    }

    /**
     * fragment_game_play.xml의 ImageView (iv_roll_dice_first..sixth)에 대해서 해당 iv를 클릭했을 때
     * Selected 되도록 만드는 ClickListener
     */
    private fun rollDiceSelected(idx: Int) = { imageview: View ->
        if (gamePlayViewModel.chance < 3) {
            gamePlayViewModel.keepDice(idx)
            imageview.isSelected = !imageview.isSelected
        }
    }

    private fun gameInfoObserver() =
        Observer<GameInfo> {
            it?.let { gameInfo ->
                log(TAG, "gameInfoObserver : ${gameInfo}", LogTag.I)
            }
        }


    private fun gameIdObserver() =
        Observer<String> {
            it?.let { gameId ->
                log(TAG, "gameIdObserver : ${gameId}", LogTag.I)
                gamePlayViewModel.refreshGameInfo(gameId)
                gamePlayViewModel.refreshRollDice(gameId)
                //gamePlayViewModel.writeRollDiceAtFirst(gameId)
            }
        }

    private fun firstPlayerObserver() =
        Observer<Player> {
            it?.let {
                gamePlayViewModel.gameId.value?.let { gameId ->
                    gamePlayViewModel.writeRollDiceAtFirst(gameId)
                }

            }
        }

    private fun myTurnObserver() =
        Observer<Boolean> {
            it?.let { myTurn ->
                if (myTurn) {
                    if(gamePlayViewModel.chance==0) gamePlayViewModel.reloadBeforeRollDice()
                    log(TAG, "myTurnObserver : 현재 나의 턴 입니다.", LogTag.I)
                    Toast.makeText(requireContext(), "내 턴 입니다.", Toast.LENGTH_SHORT).show()
                } else {
                    log(TAG, "myTurnObserver : 현재 나의 턴이 아닙니다.", LogTag.I)
                }
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        gamePlayViewModel.removeListener()
    }
}