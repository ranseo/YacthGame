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
import com.ranseo.yatchgame.data.model.RollDice
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
        gamePlayViewModel.initRollDiceKeep.observe(viewLifecycleOwner, initRollDiceKeepObserver())
        gamePlayViewModel.rollDice.observe(viewLifecycleOwner, rollDiceObserver())
        setRollDiceImageViewClickListener()

        return binding.root
    }

    /**
     *
     * */
    private fun rollDiceObserver() =
        Observer<RollDice> {
            it?.let { rollDice ->
                gamePlayViewModel.myTurn.value?.let { myTurn ->
                    if(!myTurn) {
                        gamePlayViewModel.checkOpponentDiceState(rollDice)
                        setRollDiceSelected(rollDice)
                        log(TAG,"rollDiceObserver() : Not My Turn  ${rollDice}", LogTag.I)
                    } else {
                        log(TAG,"rollDiceObserver() : my Turn  ${rollDice}", LogTag.I)
                    }
                }
            }
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

    private fun setRollDiceUnSelected() {
        binding.ivRollFirst.isSelected = false
        binding.ivRollSecond.isSelected = false
        binding.ivRollThird.isSelected = false
        binding.ivRollFourth.isSelected = false
        binding.ivRollFifth.isSelected = false
    }

    private fun setRollDiceSelected(keeps:RollDice) {
        binding.ivRollFirst.isSelected = keeps.firstFix
        binding.ivRollSecond.isSelected = keeps.secondFix
        binding.ivRollThird.isSelected = keeps.thirdFix
        binding.ivRollFourth.isSelected = keeps.fourthFix
        binding.ivRollFifth.isSelected = keeps.fifthFix
    }

    /**
     * fragment_game_play.xml의 ImageView (iv_roll_dice_first..sixth)에 대해서 해당 iv를 클릭했을 때
     * Selected 되도록 만드는 ClickListener
     */
    private fun rollDiceSelected(idx: Int) = { imageview: View ->
        if (gamePlayViewModel.chance <GamePlayViewModel.CHANCE) {
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

    /**
     * 'myTurn'의 Observer
     * 내 턴이 오면 (myTurn=true) 게임 플레이와 관련한 변수들의 값(chance, diceList, keepList)을 초기화한다.
     * */
    private fun myTurnObserver() =
        Observer<Boolean> {
            it?.let { myTurn ->
                if (myTurn) {
                    if (gamePlayViewModel.chance == GamePlayViewModel.INIT_CHANCE) {
                        gamePlayViewModel.reloadBeforeRollDice()
                        log(TAG, "myTurnObserver : 현재 나의 턴 입니다.", LogTag.I)
                        Toast.makeText(requireContext(), "내 턴 입니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    log(TAG, "myTurnObserver : 현재 나의 턴이 아닙니다.", LogTag.I)
                }
            }
        }


    /**
     * 'initRollDiceKeep'의 Observer.
     * 주사위 턴이 모두 끝나고 'finish'버튼을 눌렀을 때
     * 현재 keep되어 있는 (=selected 가 true로 설정된) fragment_game_play.xml의 주사위들의 상태를 해제함.
     * */
    private fun initRollDiceKeepObserver() =
        Observer<Any?>{
            it?.let {
                setRollDiceUnSelected()
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        gamePlayViewModel.removeListener()
    }


}