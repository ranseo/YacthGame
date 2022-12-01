package com.ranseo.yatchgame.ui.game

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.ranseo.yatchgame.Event
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.*
import com.ranseo.yatchgame.data.repo.GamePlayRepositery
import com.ranseo.yatchgame.log
import com.ranseo.yatchgame.util.YachtGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GamePlayViewModel @Inject constructor(
    private val gamePlayRepositery: GamePlayRepositery,
    private val yachtGame: YachtGame,
    application: Application
) : AndroidViewModel(application) {

    private lateinit var player: Player

    private val _gameId = MutableLiveData<String>()
    val gameId: LiveData<String>
        get() = _gameId

    private val _gameInfo = MutableLiveData<GameInfo>()
    val gameInfo: LiveData<GameInfo>
        get() = _gameInfo

    private val _rollDice = MutableLiveData<RollDice>()
    val rollDice: LiveData<RollDice>
        get() = _rollDice

    private val _myTurn = MediatorLiveData<Boolean>()
    val myTurn: LiveData<Boolean>
        get() = _myTurn

    private val _initRollDiceKeep = MutableLiveData<Event<Any?>>()
    val initRollDiceKeep : LiveData<Event<Any?>>
        get() = _initRollDiceKeep

    private val _boardRecord = MutableLiveData<BoardRecord>()
    val boardRecord : LiveData<BoardRecord>
        get() = _boardRecord
    val records = INIT_RECORDS.clone()


    private fun setMyTurn(
        player:Player,
        firstPlayer: LiveData<Player>,
        rollDice: LiveData<RollDice>
    ) {
        if (player != null && rollDice.value != null) {
            _myTurn.value = if (player == firstPlayer.value && rollDice.value!!.turn) true
            else player == secondPlayer.value && !rollDice.value!!.turn
        }
    }


    private var diceList = INIT_DICE_LIST.clone()
    private var keepList = INIT_KEEP_LIST.clone()
    private var _chance: Int = INIT_CHANCE
    val chance: Int
        get() = _chance


    val firstPlayer = Transformations.map(gameInfo) {
        it.first
    }
    val secondPlayer = Transformations.map(gameInfo) {
        it.second
    }
    val firstBoard = Transformations.map(gameInfo) {
        it.boards[0]
    }
    val secondBoard = Transformations.map(gameInfo) {
        it.boards[1]
    }

    //
    private val _rollDiceImages = MutableLiveData<List<Drawable>>()
    val rollDiceImages: LiveData<List<Drawable>>
        get() = _rollDiceImages

    init {
        refreshPlayer()
        refreshGameId()
        refreshBoardRecord() //임시

        setRollDiceImage(START_LIST)
    }


    /**
     * 임시 - BoardRecord 설정.
     * */
    private fun refreshBoardRecord() {
        _boardRecord.value = BoardRecord()
    }


    /**
     * 사용자의 player data를 초기화하고,
     * player data가 초기화되면 해당 player값을 이용하여 myTurn (MediatorLiveData<>)의 Source 설정.
     * */
    private fun refreshPlayer() {
        viewModelScope.launch {
            player = gamePlayRepositery.refreshHostPlayer()
            with(_myTurn) {
                addSource(firstPlayer) {
                    setMyTurn(player, firstPlayer, rollDice)
                }
                addSource(rollDice) {
                    setMyTurn(player, firstPlayer,rollDice)
                }
            }
        }
    }

    /**
     * 'gameId'는 WaitingFragment에서 Host와 Guest 플레이어가 모두 접속했을 때
     * 초기의 GameInfo 객체를 만들어 ROOM 과 FIREBASE 데이터베이스에 삽입한다.
     * 초기 GameInfo 객체에는 gameId, startTime, listOf(Board,Board) 의 데이터만 담겨있다.
     *
     * 'GamePlayViewModel'에서는 사용자가 이전에 삽입한 GameInfo의 정보를 얻기 위해 Room으로부터
     * 가장 최근의 GameInfo 데이터를 받아오며, 여기서 추출한 'gameId' 값을 가지고 Firebase Database에도
     * 접근할 수 있다.
     * */
    private fun refreshGameId() {
        viewModelScope.launch {
            _gameId.postValue(gamePlayRepositery.getGameId())
        }
    }

    /**
     * gameInfo data를 Firebase Database로부터 읽어온 뒤,
     * 이 과정은 Firebase Database의 gameinfo data가  갱신될 때마다 실행된다.
     * */
    fun refreshGameInfo(gameInfoId: String) {
        viewModelScope.launch {
            gamePlayRepositery.getGameInfo(gameInfoId) { gameInfo ->
                _gameInfo.postValue(gameInfo)
                //updateGameInfo(gameInfo)
            }
        }
    }

    /**
     * "RollDice" data 를 Firebase Database 로부터 읽어온다.
     * */
    fun refreshRollDice(gameId: String) {
        viewModelScope.launch {
            gamePlayRepositery.getRollDice(gameId) {
                _rollDice.postValue(it)
            }
        }
    }

    /**
     * viewModel 이 생성될 때, HostPlayer가 Firebase 에 rollDice data wrtie
     * */
    fun writeRollDiceAtFirst(gameId:String) {
        viewModelScope.launch {
            val rollDice = RollDice(gameId, START_LIST ,INIT_KEEP_LIST.clone(), true )
            gamePlayRepositery.writeRollDice(rollDice)
        }
    }

    /**
     * RollDice Data를 Firebase Database에 write
     * */
    fun writeRollDice(dices: Array<Int>, keeps: Array<Boolean>, turn: Boolean) {
        viewModelScope.launch{
            rollDice.value?.let{
                val new = RollDice(it.gameId,dices, keeps,turn)
                gamePlayRepositery.writeRollDice(new)
            }
        }
    }

    /**
     * room DATABASE에 gameInfo 데이터 업데이트.
     * */
    private fun updateGameInfo(gameInfo: GameInfo) {
        viewModelScope.launch {
            gamePlayRepositery.updateGameInfo(gameInfo)
        }
    }

    /**
     * 선택한 주사위를 KEEP 하는 함수
     * 매개변수로 받은 idx 값을 이용하여 'keepList'배열의 값을 true or false로 할당.
     * */
    fun keepDice(idx: Int) {
        keepList[idx] = !keepList[idx]
        viewModelScope.launch {
            writeRollDice(diceList.clone(), keepList.clone(), rollDice.value!!.turn)
        }
    }

    /**
     * roll dice 와 관련된 변수 [keepList, chance] 초기화
     * */
    fun reloadBeforeRollDice() {
        diceList = INIT_DICE_LIST.clone()
        keepList = INIT_KEEP_LIST.clone()
        _chance = CHANCE
    }

    /**
     * fragment_game_play.xml 파일의 btn_roll을 눌렀을 때
     * 'YachtGame'의 rollDice 시작.
     * */
    fun rollDices() {
        if(chance<=0) return
        yachtGame.rollDice(diceList, keepList)
        _chance--
        log(TAG, "rollDice() : ${diceList.toList()}", LogTag.I)
        setRollDiceImage(diceList)
        getScore(diceList)
        writeRollDice(diceList.clone(), keepList.clone(), rollDice.value!!.turn)
    }

    /**
     * 'keepList'의 data에 맞춰 주사위 모양 변형하기.
     * */
    @SuppressLint("UseCompatLoadingForDrawables")
    fun setRollDiceImage(diceList: Array<Int>) {
        var list = mutableListOf<Drawable>()
        for (dice in diceList) {
            val drawable = when (dice) {
                1 -> getApplication<Application?>().getDrawable(R.drawable.selector_roll_dice_first)!!
                2 -> getApplication<Application?>().getDrawable(R.drawable.selector_roll_dice_second)!!
                3 -> getApplication<Application?>().getDrawable(R.drawable.selector_roll_dice_third)!!
                4 -> getApplication<Application?>().getDrawable(R.drawable.selector_roll_dice_fourth)!!
                5 -> getApplication<Application?>().getDrawable(R.drawable.selector_roll_dice_fifth)!!
                6 -> getApplication<Application?>().getDrawable(R.drawable.selector_roll_dice_sixth)!!
                else -> getApplication<Application?>().getDrawable(R.drawable.selector_roll_dice_first)!!
            }
            list.add(drawable)

        }
        log(TAG, "setRollDiceImage : ${list}", LogTag.I)
        _rollDiceImages.value = list
    }


    /**
     * diceList에 따라 board 결과를 반한
     * 'YachtGame' 의 getScore() 메서드를 사용
     * */
    private fun getScore(dices: Array<Int>) {
        val score = yachtGame.getScore(dices)
        log(TAG, "score : $score" , LogTag.I)
        val origin = if(firstPlayer.value == player) firstBoard.value else secondBoard.value
        origin?.let {
            val newBoard = score
            //todo: 임시 보드판에만 보이는 fakeBoard : LiveData<Board>를 만들어야 겠다.
        }
    }

    /**
     * 임시
     * */
    fun finishTurn() {
        writeRollDice(diceList, INIT_KEEP_LIST.clone(), player != firstPlayer.value)
        _chance = INIT_CHANCE
        _initRollDiceKeep.value = Event(Unit)
    }

    /**
     * 현재 내 턴이 아닐 때, 상대방이 주사위를 굴랴서 어떤 주사위 눈금을 얻었는 지 확인할 수 있게 하기 위하여
     * rollDice의 변수 값에 따라 현재 주사위 이미지를 바꾸고, keep의 현황을 확인.
     * */
    fun checkOpponentDiceState(dices: RollDice) {
        val tmpDices = arrayOf(dices.first, dices.second, dices.third, dices.fourth, dices.fifth)
        setRollDiceImage(tmpDices)
    }


    /**
     * Firebase Database Reference에 연결된 ValueEventListener 제거
     * */
    fun removeListener() {
        viewModelScope.launch {
            gameId.value?.let {
                gamePlayRepositery.removeListener(it)
            }
        }
    }


    /**
     * BoardRecord 객체의 각 프로퍼티값을 BoardTag에 따라 true로 변환
     * */
    fun confirmBoardRecord(boardTag: BoardTag) {
        when(boardTag) {
            BoardTag.ONES -> records[0] = true
            BoardTag.TWOS -> records[1] = true
            BoardTag.THREES -> records[2] = true
            BoardTag.FOURS -> records[3] = true
            BoardTag.FIVES -> records[4] = true
            BoardTag.SIXES -> records[5] = true
            BoardTag.CHOICE -> records[6] = true
            BoardTag.FOURCARD -> records[7] = true
            BoardTag.FULLHOUSE -> records[8] = true
            BoardTag.SMALLSTRAIGHT -> records[9] = true
            BoardTag.LARGESTRAIGHT -> records[10] = true
            BoardTag.YACHT -> records[11] = true
        }

        val newBoardRecord = BoardRecord(records.clone())
        _boardRecord.value = newBoardRecord


    }

    companion object {
        private const val TAG = "GamePlayViewModel"
        const val INIT_CHANCE = 500
        const val CHANCE = 3
        private val START_LIST = arrayOf(1, 2, 3, 4, 5)
        private val INIT_DICE_LIST = Array<Int>(5) { 0 }
        private val INIT_KEEP_LIST = Array<Boolean>(6) { false }
        private val INIT_RECORDS = Array<Boolean>(12){false}
    }

}