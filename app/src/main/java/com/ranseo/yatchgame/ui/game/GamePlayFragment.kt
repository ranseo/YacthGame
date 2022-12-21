package com.ranseo.yatchgame.ui.game

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ranseo.yatchgame.Event
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.*
import com.ranseo.yatchgame.databinding.FragmentGamePlayBinding
import com.ranseo.yatchgame.log
import com.ranseo.yatchgame.ui.dialog.GameResultDialog
import com.ranseo.yatchgame.ui.lobby.LobbyActivity
import com.ranseo.yatchgame.ui.popup.EmojiPopup
import com.ranseo.yatchgame.util.YachtSound
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class GamePlayFragment() : Fragment() {
    private val TAG = "GamePlayFragment"
    private lateinit var binding: FragmentGamePlayBinding

    @Inject
    lateinit var yachtSound: YachtSound


    private val gamePlayViewModel: GamePlayViewModel by viewModels()

    private lateinit var rollDiceAnimation: AnimationDrawable


//    private lateinit var audioAttributes: AudioAttributes
//    private lateinit var soundPool :SoundPool
//    private var rollSound : Int = 0
//    private var boardConfirmSound : Int = 0
//    private var keepSound : Int = 0
//    private var myTurnSound: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val gameResult = gamePlayViewModel.getGameResult(true)
                gamePlayViewModel.finishGame(gameResult)
                gamePlayViewModel.earlyFinishGame = true
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        yachtSound.initSound(requireContext())

    }

    override fun onStart() {
        super.onStart()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_play, container, false)

        with(binding) {
            viewModel = gamePlayViewModel
            lifecycleOwner = viewLifecycleOwner
            onClickListener = OnBoardClickListener { boardTag ->
                yachtSound.play(YachtSound.BOARD_CONFIRM_SOUND)
                //보드판의 점수를 사용자가 클릭하면 해당 점수가 확정되고, 상대턴으로 넘어가는 과정
                //1.getScore() : 점수 확정한 뒤, 확정된 점수판을 database에 기록
                gamePlayViewModel.getScore(boardTag)
                //2.confirmBoardRecord() : 점수를 확정하면 확정 점수가 사용자에게 보이도록 boardRecord를 변경하고, database에 기록
                gamePlayViewModel.confirmBoardRecord(boardTag)
                //3.finishTurn() : 현재 턴을 넘기고 주사위를 초기화.
                gamePlayViewModel.finishTurn()

            }

        }

        with(gamePlayViewModel) {
            gameId.observe(viewLifecycleOwner, gameIdObserver())
            gameInfo.observe(viewLifecycleOwner, gameInfoObserver())
            myTurn.observe(viewLifecycleOwner, myTurnObserver())
            firstPlayer.observe(viewLifecycleOwner, firstPlayerObserver())
            initRollDiceKeep.observe(viewLifecycleOwner, initRollDiceKeepObserver())
            rollDice.observe(viewLifecycleOwner, rollDiceObserver())
            firstBoardRecord.observe(viewLifecycleOwner, firstBoardRecordObserver())
            secondBoardRecord.observe(viewLifecycleOwner, secondBoardRecordObserver())
            turnCount.observe(viewLifecycleOwner, turnCountObserver())
            clickProfile.observe(viewLifecycleOwner, clickProfileObserver())

        }

        setRollDiceImageViewClickListener()
        setBtnRollDiceClickListener()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setBtnRollDiceClickListener() {
        binding.btnRoll.setOnClickListener {
            yachtSound.play(YachtSound.ROLL_DICE_SOUND)
//            rollDiceAnimation.start()
            gamePlayViewModel.rollDices()

        }
    }


    /**
     * fragment_game_play.xml의 ImageView (iv_roll_dice_first..sixth)에 대해서 해당 iv를 클릭했을 때
     * Selected 되도록 만드는 ClickListener를 등록.
     * */
    private fun setRollDiceImageViewClickListener() {

//        binding.ivRollFirst.setBackgroundResource(R.drawable.animation_roll_dice)
//        rollDiceAnimation = binding.ivRollFirst.background as AnimationDrawable

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

    private fun setRollDiceSelected(keeps: RollDice) {
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
        if (gamePlayViewModel.chance < GamePlayViewModel.CHANCE) {
            yachtSound.play(YachtSound.KEEP_SOUND)
            gamePlayViewModel.keepDice(idx)
            imageview.isSelected = !imageview.isSelected

        }
    }

    private fun gameInfoObserver() =
        Observer<GameInfo> {
            it?.let { gameInfo ->
                log(TAG, "gameInfoObserver : ${gameInfo}", LogTag.I)
                if (gameInfo.result.isNotEmpty() && gameInfo.gameFinishTime.isNotEmpty()) {
                    if (gamePlayViewModel.earlyFinishGame) startLobbyActivity()
                    else {
                        if (EARLY_FLAG.matches(gameInfo.result)) {
                            val gameResult = gamePlayViewModel.getGameResult(true)
                            showGameResultDialog(gameResult, false)
                        } else {
                            val gameResult = gamePlayViewModel.getGameResult(false)
                            showGameResultDialog(gameResult, true)
                        }
                    }
                }
            }
        }


    private fun gameIdObserver() =
        Observer<String> {
            it?.let { gameId ->
                log(TAG, "gameIdObserver : ${gameId}", LogTag.I)
                gamePlayViewModel.refreshGameInfo(gameId)
                gamePlayViewModel.refreshRollDice(gameId)
                gamePlayViewModel.refreshBoardInfo(gameId)
                gamePlayViewModel.refreshEmojiInfo(gameId)
                //gamePlayViewModel.refreshTurnCount()
                //gamePlayViewModel.writeRollDiceAtFirst(gameId)
            }
        }

    private fun firstPlayerObserver() =
        Observer<Player> {
            it?.let {
                if (gamePlayViewModel.rollDiceFirstFlag) {
                    gamePlayViewModel.gameId.value?.let { gameId ->
                        gamePlayViewModel.writeRollDiceAtFirst(gameId)
                        gamePlayViewModel.rollDiceFirstFlag = false
                        if (gamePlayViewModel.isFirstPlayer.value==true) gamePlayViewModel.refreshTurnCount()

                    }
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
                        yachtSound.play(YachtSound.MY_TURN_SOUND)
                        log(TAG, "myTurnObserver : chance = ${gamePlayViewModel.chance}", LogTag.I)
                        gamePlayViewModel.reloadBeforeRollDice()
                        log(TAG, "myTurnObserver : 현재 나의 턴 입니다.", LogTag.I)
                        if (gamePlayViewModel.isFirstPlayer.value == true) gamePlayViewModel.implementTurnCount() //First Player인 경우
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
        Observer<Any?> {
            it?.let {
                setRollDiceUnSelected()

            }
        }


    private fun firstBoardRecordObserver() =
        Observer<BoardRecord?> {
            it?.let { boardRecord ->
                log(TAG, "firstBoardRecordObserver : ${boardRecord}", LogTag.I)
            }
        }

    private fun secondBoardRecordObserver() =
        Observer<BoardRecord?> {
            it?.let { boardRecord ->
                log(TAG, "secondBoardRecordObserver : ${boardRecord}", LogTag.I)
            }
        }

    /**
     * 내 턴이 아니고 상대턴일 경우 상대가 주사위를 굴렸을 때, 주사위 눈을 현황을 보여주기 위하여
     * rollDice가 갱신될 때 마다 ViewModel의 checkOpponentDiceState() 메서드 및 Fragment의 setRollDiceSelected() 메서드 실행
     * */
    private fun rollDiceObserver() =
        Observer<RollDice> {
            it?.let { rollDice ->

                gamePlayViewModel.myTurn.value?.let { myTurn ->
                    if (!myTurn) {
                        val prev = gamePlayViewModel.prevRollDice
                        if (prev != null && rollDice.checkKeepChange(prev)) yachtSound.play(
                            YachtSound.KEEP_SOUND
                        )
                        else yachtSound.play(YachtSound.ROLL_DICE_SOUND)
                        gamePlayViewModel.checkOpponentDiceState(rollDice)
                        setRollDiceSelected(rollDice)
                        log(TAG, "rollDiceObserver() : Not My Turn  ${rollDice}", LogTag.I)
                        gamePlayViewModel.prevRollDice = rollDice
                    } else {
                        log(TAG, "rollDiceObserver() : my Turn  ${rollDice}", LogTag.I)
                    }
                }
            }
        }

    /**
     *
     * */
    private fun turnCountObserver() =
        Observer<Int> {
            it?.let { turnCount ->
                if (turnCount > 12) {
                    val gameResult = gamePlayViewModel.getGameResult(false)
                    gamePlayViewModel.finishGame(gameResult)
                }
            }
        }

    /**
     *
     * */
    private fun clickProfileObserver() =
        Observer<Event<Boolean>> {
            it.getContentIfNotHandled()?.let{ player->
                val view = if(player)  binding.layoutFirstProfile.root else binding.layoutSecondProfile.root
                showEmojiPopup(view)
            }
        }
    /**
     * 게임이 끝났을 때, 띄우는 Dialog
     * */
    private fun showGameResultDialog(gameResult: List<String>, isRematch: Boolean) {
        val dialog = GameResultDialog(
            requireContext(),
            gameResult[0],
            gameResult[1],
            gameResult[2],
            gameResult[3],
            gameResult[4],
            isRematch
        )

        dialog.setOnClickListener(
            object : GameResultDialog.OnGameResultDialogClickListener {
                override fun onExitBtn() {
                    startLobbyActivity()
                }

                override fun onRematchBtn() {

                }

            }
        )
        dialog.showDialog()
    }

    /**
     * dialog의 onExitBtn()을 누를 때 로비로 돌아감
     * */
    private fun startLobbyActivity() {
        val intent = Intent(requireContext(), LobbyActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

//    private fun playSound(sound:Int, leftVolume:Float, rightVolume:Float, priority:Int, loop:Int, rate:Float) {
//        soundPool.play(
//            sound,
//            leftVolume,
//            rightVolume,
//            priority,
//            loop,
//            rate
//        )
//    }

    /**
     * PopupEmoji Fragment 를 display
     * */
    fun showEmojiPopup(view:View) {
        val emoji = EmojiPopup(requireContext(), object: EmojiPopup.OnEmojiPopupClickListener {
            override fun onEmojiClick(emoji: Int) {
                gamePlayViewModel.writeEmoji(emoji)
            }
        })

        emoji.showPopupWindow(binding.layoutGamePlay, view)
    }

    override fun onDestroy() {
        super.onDestroy()
        gamePlayViewModel.removeListener()
//        gamePlayViewModel.releaseResource()
        yachtSound.release()
    }

    companion object {
        private val EARLY_FLAG = Regex("님이 게임에서 나가셨습니다.")
    }

}