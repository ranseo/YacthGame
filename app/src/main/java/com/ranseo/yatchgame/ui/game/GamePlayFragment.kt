package com.ranseo.yatchgame.ui.game

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ranseo.yatchgame.Event
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.*
import com.ranseo.yatchgame.databinding.FragmentGamePlayBinding
import com.ranseo.yatchgame.log
import com.ranseo.yatchgame.ui.dialog.AlertDialog
import com.ranseo.yatchgame.ui.dialog.GameRematchDialog
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

    private lateinit var rollDiceFirstAnimation: AnimationDrawable
    private lateinit var rollDiceSecondAnimation: AnimationDrawable
    private lateinit var rollDiceThirdAnimation: AnimationDrawable
    private lateinit var rollDiceFourthAnimation: AnimationDrawable
    private lateinit var rollDiceFifthAnimation: AnimationDrawable

    private lateinit var gameResultDialog : GameResultDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log(TAG,"onCreate" , LogTag.I)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun handleOnBackPressed() {
                showAlertDialog()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        yachtSound.initSound(requireContext())

    }

    override fun onStart() {
        super.onStart()
        log(TAG,"onStart" , LogTag.I)
        startBgm()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        log(TAG,"onCreateView" , LogTag.I)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_play, container, false)

        with(binding) {
            viewModel = gamePlayViewModel
            lifecycleOwner = viewLifecycleOwner
            onClickListener = OnBoardClickListener { boardTag ->
                //나의 턴일때만 가능.
                yachtSound.play(YachtSound.BOARD_CONFIRM_SOUND)
                //보드판의 점수를 사용자가 클릭하면 해당 점수가 확정되고, 상대턴으로 넘어가는 과정
                gamePlayViewModel.confirmScore(boardTag)
                //3.finishTurn() : 현재 턴을 넘기고 주사위를 초기화.
                gamePlayViewModel.finishTurn()
            }
            btnVolume.setOnClickListener {
                controlBgm(it)
            }
            binding.ivRollFirstAnim.setBackgroundResource(R.drawable.animation_roll_dice_first)
            rollDiceFirstAnimation = binding.ivRollFirstAnim.background as AnimationDrawable
            rollDiceFirstAnimation.stop()

            binding.ivRollSecondAnim.setBackgroundResource(R.drawable.animation_roll_dice_second)
            rollDiceSecondAnimation = binding.ivRollSecondAnim.background as AnimationDrawable
            rollDiceSecondAnimation.stop()

            binding.ivRollThirdAnim.setBackgroundResource(R.drawable.animation_roll_dice_third)
            rollDiceThirdAnimation = binding.ivRollThirdAnim.background as AnimationDrawable
            rollDiceThirdAnimation.stop()

            binding.ivRollFourthAnim.setBackgroundResource(R.drawable.animation_roll_dice_fourth)
            rollDiceFourthAnimation = binding.ivRollFourthAnim.background as AnimationDrawable
            rollDiceFourthAnimation.stop()

            binding.ivRollFifthAnim.setBackgroundResource(R.drawable.animation_roll_dice_fifth)
            rollDiceFifthAnimation = binding.ivRollFifthAnim.background as AnimationDrawable
            rollDiceFifthAnimation.stop()

        }



        with(gamePlayViewModel) {
            gameInfo.observe(viewLifecycleOwner, gameInfoObserver())
            myTurn.observe(viewLifecycleOwner, myTurnObserver())
            firstPlayer.observe(viewLifecycleOwner, firstPlayerObserver())
            initRollDiceKeep.observe(viewLifecycleOwner, initRollDiceKeepObserver())
            rollDice.observe(viewLifecycleOwner, rollDiceObserver())
            firstBoardRecord.observe(viewLifecycleOwner, firstBoardRecordObserver())
            secondBoardRecord.observe(viewLifecycleOwner, secondBoardRecordObserver())
            turnCount.observe(viewLifecycleOwner, turnCountObserver())
            clickProfile.observe(viewLifecycleOwner, clickProfileObserver())
            opponentEmoji.observe(viewLifecycleOwner, opponentEmojiObserver())
            myEmoji.observe(viewLifecycleOwner, myEmojiObserver())
            yachtSound.observe(viewLifecycleOwner, yachtSoundObserver())
            diceAnim.observe(viewLifecycleOwner, diceAnimObserver())
            rematch.observe(viewLifecycleOwner, rematchObserver())
            makeWaitRoom.observe(viewLifecycleOwner, makeWaitRoomObserver())

            player.observe(viewLifecycleOwner, playerObserver())
        }

        setRollDiceImageViewClickListener()
        setBtnRollDiceClickListener()

        return binding.root
    }

    /**
     * tmp
     * */
    private fun playerObserver() =
        Observer<Player> {
            log(TAG,"playerObserver : ${it}", LogTag.I)
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
        if (gamePlayViewModel.chance < GamePlayViewModel.CHANCE) {
            yachtSound.play(YachtSound.KEEP_SOUND)
            gamePlayViewModel.keepDice(idx)
            imageview.isSelected = !imageview.isSelected

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun gameInfoObserver() =
        Observer<GameInfo> {
            it?.let { gameInfo ->
                log(TAG, "gameInfoObserver : ${gameInfo}", LogTag.I)
                if (gameInfo.result.isNotEmpty() && gameInfo.gameFinishTime.isNotEmpty()) {
                    gamePlayViewModel.updateGameInfo(gameInfo)
                    if (gamePlayViewModel.earlyFinishGame) startLobbyActivity()
                    else {
                        if (EARLY_FLAG.containsMatchIn(gameInfo.result)) {
                            val gameResult = gamePlayViewModel.getGameResult(true)
                            gameResult[gameResult.size-1] = gameInfo.result
                            showGameResultDialog(gameResult, false)
                        } else {
                            val gameResult = gamePlayViewModel.getGameResult(false)
                            showGameResultDialog(gameResult, true)
                        }
                    }
                }
            }
        }


    private fun firstPlayerObserver() =
        Observer<Player> {
            it?.let {
                if (gamePlayViewModel.rollDiceFirstFlag) {
                    gamePlayViewModel.gameId.value?.let { gameId ->
                        gamePlayViewModel.writeRollDiceAtFirst(gameId)
                        gamePlayViewModel.rollDiceFirstFlag = false

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
                gamePlayViewModel.initKeepList()
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
                //First Player의 경우 -> true!=false
                //Secopnd Player의 경우 -> false != true
                if (gamePlayViewModel.isFirstPlayer.value != rollDice.turn) {
                    val prev = gamePlayViewModel.prevRollDice
//                    if (prev != null && rollDice.checkDiceChange(prev)) {
//                        log(TAG, "rollDiceObserver() : KEEP_SOUND", LogTag.I)
//                        yachtSound.play(YachtSound.KEEP_SOUND)
//                        gamePlayViewModel.setKeepList(rollDice)
//                    } else {
//                        log(TAG, "rollDiceObserver() : ROLL_DICE_SOUND", LogTag.I)
//                        yachtSound.play(YachtSound.ROLL_DICE_SOUND)
//                        gamePlayViewModel.checkOpponentDiceState(rollDice)
//                    }

                    if(prev != null && rollDice.count == prev) {
                        log(TAG, "rollDiceObserver() : KEEP_SOUND", LogTag.I)
                        yachtSound.play(YachtSound.KEEP_SOUND)
                        gamePlayViewModel.setKeepList(rollDice)
                    } else {
                        log(TAG, "rollDiceObserver() : ROLL_DICE_SOUND", LogTag.I)
                        yachtSound.play(YachtSound.ROLL_DICE_SOUND)
                        gamePlayViewModel.checkOpponentDiceState(rollDice)
                    }




                    log(TAG, "rollDiceObserver() : Not My Turn  ${rollDice}", LogTag.I)
                    gamePlayViewModel.prevRollDice = rollDice.count
                } else {
                    gamePlayViewModel.setKeepList(rollDice)
                    log(TAG, "rollDiceObserver() : my Turn  ${rollDice}", LogTag.I)
                }

            }
        }

    /**
     *
     * */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun turnCountObserver() =
        Observer<Int> {
            it?.let { turnCount ->
                log(TAG, "turnCountObserver() turnCount : $turnCount", LogTag.I)
                if (turnCount > 12) {
                    val gameResult = gamePlayViewModel.getGameResult(false)
                    //마지막 턴일 때, 게임은 second Player가 끝낼 수 있다.
                    if (gamePlayViewModel.isFirstPlayer.value != true) gamePlayViewModel.finishGame(
                        gameResult
                    )
                }
            }
        }

    /**
     *
     * */
    private fun clickProfileObserver() =
        Observer<Event<Boolean>> {
            it.getContentIfNotHandled()?.let { player ->
                val view =
                    if (player) binding.layoutFirstProfile.root else binding.layoutSecondProfile.root
                showEmojiPopup(view)
            }
        }

    /**
     *
     * */
    private fun opponentEmojiObserver() =
        Observer<Int> {
            it?.let {
                if (gamePlayViewModel.isFirstPlayer.value == true) binding.layoutSecondProfile.emoji = it
                 else binding.layoutFirstProfile.emoji = it
            }
        }

    /**
     *
     * */
    private fun myEmojiObserver() =
        Observer<Int> {
            it?.let {
                if (gamePlayViewModel.isFirstPlayer.value != true) binding.layoutSecondProfile.emoji =
                    it else binding.layoutFirstProfile.emoji = it
            }
        }


    /**
     * yachtSound~
     * */
    private fun yachtSoundObserver() =
        Observer<Event<Any?>> {
            it.getContentIfNotHandled()?.let {
                yachtSound.play(YachtSound.YACHT_SOUND)
            }
        }

    /**
     *
     * */
    private fun diceAnimObserver() =
        Observer<List<Boolean>> {
            it?.let { diceAnims ->
                for ((idx, diceAnim) in diceAnims.withIndex()) {
                    val animation = when (idx) {
                        0 -> {
                            rollDiceFirstAnimation
                        }
                        1 -> {
                            rollDiceSecondAnimation
                        }
                        2 -> {
                            rollDiceThirdAnimation
                        }
                        3 -> {
                            rollDiceFourthAnimation
                        }
                        4 -> {
                            rollDiceFifthAnimation
                        }
                        else -> {
                            rollDiceFifthAnimation
                        }
                    }
                    if (diceAnim) {
                        animation.start()
                    } else {
                        animation.stop()
                    }
                }
            }
        }

    /**
     *
     * */
    private fun rematchObserver() =
        Observer<Rematch> {
            it?.let { rematch ->
                log(TAG,"rematchObserver() : ${rematch}", LogTag.I)
                showGameRematchDialog(rematch)
            }
        }

    /**
     *
     * */
    private fun makeWaitRoomObserver() =
        Observer<Event<String>> {
            it.getContentIfNotHandled()?.let {  newGameKey ->
                val newKeyForHost =
                    requireContext().getString(R.string.make_wait_room) + requireContext().getString(
                        R.string.border_string_for_parsing
                    ) + newGameKey
                findNavController().navigate(
                    GamePlayFragmentDirections.actionPlayToWaiting(newKeyForHost)
                )
            }
        }

    /**
     * 게임이 끝났을 때, 띄우는 Dialog
     * */
    private fun showGameResultDialog(gameResult: List<String>, isRematch: Boolean) {
        gameResultDialog = GameResultDialog(
            requireContext(),
            gameResult[0],
            gameResult[1],
            gameResult[2],
            gameResult[3],
            gameResult[4],
            isRematch
        )

        gameResultDialog.setOnClickListener(
            object : GameResultDialog.OnGameResultDialogClickListener {
                override fun onExitBtn() {
                    startLobbyActivity()
                }

                override fun onRematchBtn() {
                    gamePlayViewModel.requestRematch()
                }
            }
        )

        gameResultDialog.showDialog()


    }

    /**
     * 재대결 신청 dialog (= GameRematchDialog)를 띄운다.
     * */
    private fun showGameRematchDialog(gameRematch: Rematch) {
        gameResultDialog.dismiss()

        val dialog = GameRematchDialog(requireContext(), gameRematch.message)
        dialog.setOnClickListener(
            object  : GameRematchDialog.OnGameRematchDialogClickListener {
                override fun onAccept() {
                    gamePlayViewModel.removeRematch()
                    //accept 시, Host Player가 만든 waiting Room 으로 이동.
                    findNavController().navigate(
                        GamePlayFragmentDirections.actionPlayToWaiting(gameRematch.newGameKey)
                    )
                }

                override fun onDecline() {
                    gamePlayViewModel.removeRematch()
                    //거절 시, lobby로 이동.
                    startLobbyActivity()
                }
            }
        )

        dialog.showDialog()
    }

    /**
     * 게임에서 나갈 때 확인용 dialog
     * */
    private fun showAlertDialog() {
        val dialog = AlertDialog(
            getString(R.string.game_go_out_title),
            getString(R.string.game_go_out_description),
            getString(R.string.dialog_positive),
            getString(R.string.dialog_negative)).apply {
                setOnClickListener(object : AlertDialog.OnAlertDialogListener {
                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onPositiveBtn() {
                        runAway()
                    }

                    override fun onNegativeBtn() {
                    }
                })
        }

        dialog.show(parentFragmentManager, TAG)

    }

    /**
     * 게임에서 나갈 때, 액션
     * */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun runAway(){
        if((gamePlayViewModel.turnCount.value ?: 0) < 13 && gamePlayViewModel.isFinished.value!=true) {
            val gameResult = gamePlayViewModel.getGameResult(true)
            gamePlayViewModel.finishGame(gameResult)
            gamePlayViewModel.earlyFinishGame = true
        } else {
            startLobbyActivity()
        }
    }

    /**
     * dialog의 onExitBtn()을 누를 때 로비로 돌아감
     * */
    private fun startLobbyActivity() {
        val intent = Intent(requireContext(), LobbyActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    /**
     * PopupEmoji Fragment 를 display
     * */
    private fun showEmojiPopup(view: View) {
        val emoji = EmojiPopup(requireContext(), object : EmojiPopup.OnEmojiPopupClickListener {
            override fun onEmojiClick(emoji: Int) {
                gamePlayViewModel.writeOpponentEmoji(emoji)
                gamePlayViewModel.setMyEmoji(emoji)
            }
        })

        emoji.showPopupWindow(binding.layoutGamePlay, view)
    }

    private fun startBgm() {
        yachtSound.play(YachtSound.BGM_CHRISMAS)

    }

    private fun controlBgm(view: View) {
        view.isSelected = !view.isSelected
        if (view.isSelected) {
            muteBgm()
        } else {
            startBgm()
        }
    }


    private fun muteBgm() {
        yachtSound.muteBgm(YachtSound.BGM_CHRISMAS)
    }

    override fun onStop() {
        yachtSound.muteBgm(YachtSound.BGM_CHRISMAS)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        yachtSound.release()
    }

    companion object {
        private val EARLY_FLAG = Regex("님이 게임에서 나가셨습니다.")
    }
}