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
import java.lang.Error
import java.lang.NullPointerException
import javax.inject.Inject

@HiltViewModel
class GamePlayViewModel @Inject constructor(
    private val gamePlayRepositery: GamePlayRepositery,
    private val yachtGame: YachtGame,
    application: Application
) : AndroidViewModel(application) {

    private lateinit var player: Player

    private val nameTag = listOf(
        R.drawable.name_tag_cat,
        R.drawable.name_tag_bear,
        R.drawable.name_tag_deer,
        R.drawable.name_tag_frog,
        R.drawable.name_tag_rabbit
    )
    private val nameTagReversed = listOf(
        R.drawable.name_tag_cat_reverse,
        R.drawable.name_tag_bear_reverse,
        R.drawable.name_tag_deer_reverse,
        R.drawable.name_tag_frog_reverse,
        R.drawable.name_tag_rabbit_reverse
    )
    private val _nameTagImages = MutableLiveData<List<Drawable>>()
    val nameTagImages: LiveData<List<Drawable>>
        get() = _nameTagImages

    private val _gameId = MutableLiveData<String>()
    val gameId: LiveData<String>
        get() = _gameId

    private val _gameInfo = MutableLiveData<GameInfo>()
    val gameInfo: LiveData<GameInfo>
        get() = _gameInfo

    private val _rollDice = MutableLiveData<RollDice>()
    val rollDice: LiveData<RollDice>
        get() = _rollDice

    private val _boardInfo = MutableLiveData<BoardInfo>()
    val boardInfo: LiveData<BoardInfo>
        get() = _boardInfo

    private val _myTurn = MediatorLiveData<Boolean>()
    val myTurn: LiveData<Boolean>
        get() = _myTurn

    private val _turnCount = MutableLiveData<Int>(1)
    val turnCount: LiveData<Int>
        get() = _turnCount
    val turnCountStr = Transformations.map(turnCount) {
        if(it<13) "$it/12" else "종료"
    }

    private val _initRollDiceKeep = MutableLiveData<Event<Any?>>()
    val initRollDiceKeep: LiveData<Event<Any?>>
        get() = _initRollDiceKeep

    var rollDiceFirstFlag: Boolean = true

    private fun setMyTurn(
        player: Player,
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

    private val _chanceStr = MutableLiveData<String>()
    val chanceStr: LiveData<String>
        get() = _chanceStr

    private fun getChanceStr() {
        _chanceStr.value = if (chance == INIT_CHANCE) "-/3" else "${3 - chance}/3"
    }


    val firstPlayer = Transformations.map(gameInfo) {
        it.first
    }
    val secondPlayer = Transformations.map(gameInfo) {
        it.second
    }
    val firstRealBoard = Transformations.map(gameInfo) {
        it.boards[0]
    }
    val secondRealBoard = Transformations.map(gameInfo) {
        it.boards[1]
    }

    @RequiresApi(Build.VERSION_CODES.N)
    val firstFakeBoard = Transformations.map(boardInfo) {
        it.returnBoard(0)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    val secondFakeBoard = Transformations.map(boardInfo) {
        it.returnBoard(1)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    val firstBoardRecord = Transformations.map(boardInfo) {
        it.returnBoardRecord(0)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    val secondBoardRecord = Transformations.map(boardInfo) {
        it.returnBoardRecord(1)
    }

    //
    private val _rollDiceImages = MutableLiveData<List<Drawable>>()
    val rollDiceImages: LiveData<List<Drawable>>
        get() = _rollDiceImages

    init {
        refreshNameTag()

        refreshPlayer()
        refreshGameId()

        setRollDiceImage(START_LIST)


    }


    /**
     * Profile - NameTag drawable 이미지를 무작위로 설정
     * */
    private fun refreshNameTag() {
        try {
            val first = nameTag.shuffled().first()
            val second = nameTagReversed.shuffled().first()

            val list = listOf(
                getApplication<Application>().getDrawable(first)!!,
                getApplication<Application>().getDrawable(second)!!
            )
            _nameTagImages.value = list

        } catch (error: NullPointerException) {
            log(TAG,"refreshNameTag : error ${error.message}", LogTag.D)
        }
    }

    /**
     * 사용자의 player data를 초기화하고,
     * player data가 초기화되면 해당 player값을 이용하여 myTurn (MediatorLiveData<>)의 Source 설정.
     * */
    private fun refreshPlayer() {
        viewModelScope.launch {
            player = gamePlayRepositery.refreshHostPlayer()
            with(_myTurn) {
                addSource(rollDice) {
                    setMyTurn(player, firstPlayer, rollDice)
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
     * boardInfo refresh
     * boardInfo는 first, second 플레이어들의 board상황과 boardRecord상황을 기록할 수 있다.
     * */
    fun refreshBoardInfo(gameId: String) {
        viewModelScope.launch {
            val boardInfo =
                BoardInfo(listOf(Board(), Board()), listOf(BoardRecord(), BoardRecord()))

            writeBoardInfo(gameId, boardInfo)

            gamePlayRepositery.getBoardInfo(gameId) { boardInfo ->
                _boardInfo.postValue(boardInfo)
            }


        }
    }

    /**
     *
     * */
    fun refreshTurnCount() {
        _turnCount.value = 0
        log(TAG,"turnCount.value : ${turnCount.value}", LogTag.I)
    }

    /**
     * viewModel 이 생성될 때, HostPlayer가 Firebase 에 rollDice data wrtie
     * */
    fun writeRollDiceAtFirst(gameId: String) {
        viewModelScope.launch {
            val rollDice = RollDice(gameId, START_LIST, INIT_KEEP_LIST.clone(), true)
            gamePlayRepositery.writeRollDice(rollDice)
        }
    }

    /**
     * RollDice Data를 Firebase Database에 write
     * */
    fun writeRollDice(dices: Array<Int>, keeps: Array<Boolean>, turn: Boolean) {
        viewModelScope.launch {
            rollDice.value?.let {
                val new = RollDice(it.gameId, dices, keeps, turn)
                gamePlayRepositery.writeRollDice(new)
            }
        }
    }

    /**
     * BoardInfo Data를 Firebase Database에 write
     * viewModel이 처음 생성될 떄 초기 BoardInfo를 한번 write
     * 이 후, score를 board에 기록할 때 마다 write
     * */
    private fun writeBoardInfo(gameId: String, boardInfo: BoardInfo) {
        viewModelScope.launch {
            gamePlayRepositery.writeBoardInfo(gameId, boardInfo)
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
        getChanceStr()
    }

    /**
     * fragment_game_play.xml 파일의 btn_roll을 눌렀을 때
     * 'YachtGame'의 rollDice 시작.
     * */
    @RequiresApi(Build.VERSION_CODES.N)
    fun rollDices() {
        if (chance <= 0) return
        yachtGame.rollDice(diceList, keepList)
        _chance--
        log(TAG, "rollDice() : ${diceList.toList()}", LogTag.I)

        getChanceStr()

        setRollDiceImage(diceList)

        showScore(diceList)

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
     * diceList의 주사위들의 정보에 따라 플레이어가 얻을 수 있는 score를 계산하고,
     * 해당 score값을 새 board 인스턴스에 (boardInfo에 쓰일) 매개변수로 전달한다.
     * */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun showScore(dices: Array<Int>) {
        try {

            val score: Board = yachtGame.getScore(dices)
            val boards =
                if (firstPlayer.value == player) listOf(score, Board()) else listOf(Board(), score)

            val boardInfo =
                BoardInfo(boards, listOf(firstBoardRecord.value!!, secondBoardRecord.value!!))

            viewModelScope.launch {
                gamePlayRepositery.writeBoardInfo(gameId.value!!, boardInfo)
            }

            log(TAG, "showScore Success : ${boardInfo}", LogTag.I)
        } catch (error: Error) {
            log(TAG, "showScore Failure : ${error.message}", LogTag.D)
        } catch (error: NullPointerException) {
            log(TAG, "showScore Failure ${error.message}", LogTag.D)
        }
    }

    /**
     * 보드판의 점수를 사용자가 클릭하면 해당 점수가 확정된다. 이 때 boardTag 매개변수가 전달된다.
     * boardTag매개변수 값과 "first(또는 second)FakeBoard" 의 value를 이용하여  (fakeBoard는 항상 사용자의 가장 최근 굴린 주사위로 얻을 수 있는 점수들이 board객체로 업데이트)
     * "first(혹은second)RealBoard"값을 구성한다. realBoard는 점수가 확정되면 사용자에게 보여질 점수를 담은 board 인스턴스이다.
     * realBoard를 만든 뒤 새로운 gameInfo 인스터스에 전달하여 Firebase Database에 wrtie하는 함수.
     * */
    @RequiresApi(Build.VERSION_CODES.N)
    fun getScore(boardTag: BoardTag) {
        try {
            val score: Board =
                if (firstPlayer.value == player) firstFakeBoard.value!! else secondFakeBoard.value!!
            val newBoard: Board = if (firstPlayer.value == player) firstRealBoard.value!!.plusScore(
                score,
                boardTag
            ) else secondRealBoard.value!!.plusScore(score, boardTag)

            val newBoards: List<Board> = if (firstPlayer.value == player) listOf(
                newBoard,
                secondRealBoard.value!!
            ) else listOf(firstRealBoard.value!!, newBoard)
            val newGameInfo = GameInfo(gameInfo.value!!, newBoards)

            viewModelScope.launch {
                gamePlayRepositery.writeGameInfo(newGameInfo)
            }
            log(TAG, "getScore() score : $newBoard", LogTag.I)
        } catch (error: Exception) {
            log(TAG, "getScore() error : ${error.message}", LogTag.D)
        }
    }

    /**
     * 임시
     * */
    fun finishTurn() {
        writeRollDice(diceList, INIT_KEEP_LIST.clone(), player != firstPlayer.value)
        _chance = INIT_CHANCE
        _initRollDiceKeep.value = Event(Unit)
        if (!isFirstPlayer()) implementTurnCount()
        getChanceStr()
    }

    /**
     * 현재 내 턴이 아닐 때, 상대방이 주사위를 굴려서 주사위 눈금을 얻었는 지 확인할 수 있게 하기 위하여
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
     * Board를 클릭할 때, 클릭한 곳의 수를 확정하고 사용자에게 display 위해
     * BoardRecord 객체의 각 프로퍼티값을 BoardTag에 따라 true로 변환
     * 해당 값이 변경되면 이와 binding 된 fakeBoard와 realBoard가 사라지고 나타난다.
     * */
    @RequiresApi(Build.VERSION_CODES.N)
    fun confirmBoardRecord(boardTag: BoardTag) {
        try {
            val boardRecord =
                if (firstPlayer.value == player) firstBoardRecord.value else secondBoardRecord.value

            boardRecord?.let {
                when (boardTag) {
                    BoardTag.ONES -> it.isOnes = true
                    BoardTag.TWOS -> it.isTwos = true
                    BoardTag.THREES -> it.isThrees = true
                    BoardTag.FOURS -> it.isFours = true
                    BoardTag.FIVES -> it.isFives = true
                    BoardTag.SIXES -> it.isSixes = true
                    BoardTag.CHOICE -> it.isChoice = true
                    BoardTag.FOURCARD -> it.isFourCard = true
                    BoardTag.FULLHOUSE -> it.isFullHouse = true
                    BoardTag.SMALLSTRAIGHT -> it.isSmallStraight = true
                    BoardTag.LARGESTRAIGHT -> it.isLargeStraight = true
                    BoardTag.YACHT -> it.isYacht = true
                }

                val boardRecords = if (firstPlayer.value == player) listOf(
                    boardRecord,
                    secondBoardRecord.value!!
                ) else listOf(firstBoardRecord.value!!, boardRecord)
                val boardInfo = BoardInfo(listOf(Board(), Board()), boardRecords)

                viewModelScope.launch {
                    gamePlayRepositery.writeBoardInfo(gameId.value!!, boardInfo)
                }
            }

            log(TAG, "confirmBoardRecord Success : ${boardInfo}", LogTag.I)
        } catch (error: Exception) {
            log(TAG, "confirmBoardRecord Failure: ${error.message}", LogTag.D)
        }
    }

    /**
     * 내 턴이 올때마다 'turnCount' 를 1씩 증가
     * */
    fun implementTurnCount() {
        _turnCount.value = _turnCount.value?.plus(1) ?: 0
    }

    /**
     *
     * */
    fun isFirstPlayer() = firstPlayer.value == player


    /**
     * 게임이 끝나고, 승패여부=Resulut 를 알려주는 문자열 및 custom dialog
     * */
    fun getGameResult() : String {
        val first = firstRealBoard.value!!
        val second = secondRealBoard.value!!
        var isDraw = false

        val winner = if(first.total>second.total){
            firstPlayer.value!!.name
        } else if(first.total < second.total) {
            secondPlayer.value!!.name
        } else {
            isDraw= true
            "무승부"
        }

        return if(!isDraw) {
            getApplication<Application?>().getString(R.string.game_result_win, winner)
        } else {
            getApplication<Application?>().getString(R.string.game_result_draw)
        }
    }

    companion object {
        private const val TAG = "GamePlayViewModel"
        const val INIT_CHANCE = 500
        const val CHANCE = 3
        private val START_LIST = arrayOf(1, 2, 3, 4, 5)
        private val INIT_DICE_LIST = Array<Int>(5) { 0 }
        private val INIT_KEEP_LIST = Array<Boolean>(6) { false }
        private val INIT_RECORDS = Array<Boolean>(12) { false }
    }

}