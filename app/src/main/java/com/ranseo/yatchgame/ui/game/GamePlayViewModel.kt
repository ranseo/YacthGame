package com.ranseo.yatchgame.ui.game

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.ranseo.yatchgame.Event
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.*
import com.ranseo.yatchgame.data.repo.GamePlayRepositery
import com.ranseo.yatchgame.domain.usecase.*
import com.ranseo.yatchgame.log
import com.ranseo.yatchgame.util.DateTime
import com.ranseo.yatchgame.util.YachtGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class GamePlayViewModel @Inject constructor(
    private val gamePlayRepositery: GamePlayRepositery,
    private val yachtGame: YachtGame,
    private val writeTurnCountInfoUseCase: WriteTurnCountInfoUseCase,
    private val getFlowTurnCountInfoUseCase: GetFlowTurnCountInfoUseCase,
    private val writeRollDiceUseCase: WriteRollDiceUseCase,
    private val getFlowRollDiceUseCase: GetFlowRollDiceUseCase,
    private val writeEmojiInfoUseCase: WriteEmojiInfoUseCase,
    private val getFlowEmojiInfoUseCase: GetFlowEmojiInfoUseCase,
    private val writeBoardInfoUseCase: WriteBoardInfoUseCase,
    private val getFlowBoardInfoUseCase: GetFlowBoardInfoUseCase,
    private val getPlayerUseCase: GetPlayerUseCase,
    application: Application
) : AndroidViewModel(application) {

//    private lateinit var audioAttributes: AudioAttributes
//    private var soundPool: SoundPool? = null
//    private var upSound: Int = 0

    private val player: LiveData<Player> = getPlayerUseCase()

    private val _nameTagImages = MutableLiveData<List<Int>>()
    val nameTagImages: LiveData<List<Int>>
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
    val turnFlag = Transformations.map(rollDice) {
        it.turn
    }

    var prevRollDice: RollDice? = null

    private val _boardInfo = MutableLiveData<BoardInfo>()
    val boardInfo: LiveData<BoardInfo>
        get() = _boardInfo

    private val _myTurn = MediatorLiveData<Boolean>()
    val myTurn: LiveData<Boolean>
        get() = _myTurn


    /**
     * MediatorLiveData 타입의 MyTurn 프로퍼티값을 초기화하는 함수.
     * */
    private fun setMyTurn(
        player: LiveData<Player>,
        firstPlayer: LiveData<Player>,
        secondPlayer: LiveData<Player>,
        turnFlag: LiveData<Boolean>
    ) {
        if (player.value != null && turnFlag.value != null) {
            _myTurn.value = if (player.value == firstPlayer.value && turnFlag.value!!) {
                true
            } else if (player.value == secondPlayer.value && !turnFlag.value!!) {
                true
            } else {
                initChance()
                getChanceStr()
                false
            }
        }
    }

    private val _turnCount = MutableLiveData<Int>()
    val turnCount: LiveData<Int>
        get() = _turnCount

    val turnCountStr = Transformations.map(turnCount) {
        if (it < 13) "$it/12" else "종료"
    }


    private val _initRollDiceKeep = MutableLiveData<Event<Any?>>()
    val initRollDiceKeep: LiveData<Event<Any?>>
        get() = _initRollDiceKeep

    var rollDiceFirstFlag: Boolean = true

    private var diceList = INIT_DICE_LIST.clone()
    private var keepList = INIT_KEEP_LIST.clone()
    private var _chance: Int = INIT_CHANCE
    val chance: Int
        get() = _chance

    private fun initChance() {
        _chance = INIT_CHANCE
    }

    private val _chanceString = MutableLiveData<String>()
    val chanceString: LiveData<String>
        get() = _chanceString

    private fun getChanceStr() {
        _chanceString.value = if (chance == INIT_CHANCE) "-/3" else "${3 - chance}/3"
    }


    val firstPlayer = Transformations.map(gameInfo) {
        it.first
    }
    val secondPlayer = Transformations.map(gameInfo) {
        it.second
    }

    @RequiresApi(Build.VERSION_CODES.N)
    val firstRealBoard = Transformations.map(boardInfo) {
        it.returnRealBoard(0)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    val secondRealBoard = Transformations.map(boardInfo) {
        it.returnRealBoard(1)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    val firstFakeBoard = Transformations.map(boardInfo) {
        it.returnFakeBoard(0)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    val secondFakeBoard = Transformations.map(boardInfo) {
        it.returnFakeBoard(1)
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
    private val _rollDiceImages = MutableLiveData<List<Int>>()
    val rollDiceImages: LiveData<List<Int>>
        get() = _rollDiceImages


    var earlyFinishGame: Boolean = false

    private val _isFirstPlayer = MediatorLiveData<Boolean>()
    val isFirstPlayer: LiveData<Boolean>
        get() = _isFirstPlayer

    private fun setIsFirstPlayer(
        player: LiveData<Player>,
        firstPlayer: LiveData<Player>
    ) {
        if (player.value != null && firstPlayer.value != null) {
            _isFirstPlayer.value = (player.value == firstPlayer.value)
        }
    }

    private val _clickProfile = MutableLiveData<Event<Boolean>>()
    val clickProfile: LiveData<Event<Boolean>>
        get() = _clickProfile

    fun onClickProfile(player: Boolean) {
        _clickProfile.value = Event(player)
    }

    private val _emojiInfo = MutableLiveData<EmojiInfo?>()
    val emojiInfo: LiveData<EmojiInfo?>
        get() = _emojiInfo

    val opponentEmoji = Transformations.map(emojiInfo) {
        it?.let { it.emoji }
    }

    private var opponentEmojiJob: Job = Job()

    private val _myEmoji = MutableLiveData<Int>()
    val myEmoji: LiveData<Int>
        get() = _myEmoji

    private var myEmojiJob: Job = Job()

    private val _yachtSound = MutableLiveData<Event<Any?>>()
    val yachtSound: LiveData<Event<Any?>>
        get() = _yachtSound

    private val _diceAnim = MutableLiveData<List<Boolean>>()
    val diceAnim: LiveData<List<Boolean>>
        get() = _diceAnim

    init {

        //refreshSoundPool()

        refreshNameTag()

        refreshGameId()
        refreshMyTurn()
        refreshIsFirstPlayer()

        setRollDiceImage(START_LIST)


    }

    /**
     * turnCountInfo refresh
     * */
    fun refreshTurnCountInfo(gameId: String) {
        viewModelScope.launch {
            writeTurnCountInfoUseCase(gameId, TurnCountInfo(1))
            getFlowTurnCountInfoUseCase(gameId).collect {
                if (it.isSuccess) {
                    _turnCount.value = it.getOrDefault(TurnCountInfo(1)).turnCount
                }
            }
        }
    }

    /**
     * Firebase Database, emojiInfo Flow -> Collect -> 'emojiInfo'
     * */
    fun refreshEmojiInfo(gameId: String) {
        viewModelScope.launch {
            try {
                getFlowEmojiInfoUseCase(gameId, player.value!!.playerId)
                    .collect { emojiInfo ->
                        if (emojiInfo.isSuccess) {
                            displayEmojiAtOpponent(emojiInfo)
                        } else {
                            log(TAG, "refreshEmojiInfo : emojiInfo Failure", LogTag.D)
                        }
                    }
            } catch (error: NullPointerException) {
                log(TAG, "refreshEmojiInfo : ${error.message}", LogTag.D)
            }
        }
    }

    private fun CoroutineScope.displayEmojiAtOpponent(emojiInfo: Result<EmojiInfo>) {
        opponentEmojiJob.cancel()
        _emojiInfo.value = emojiInfo.getOrNull()
        log(TAG, "refreshEmojiInfo : emojiInfo Success : $emojiInfo", LogTag.I)
        opponentEmojiJob = launch {
            delay(1500)
            _emojiInfo.value = EmojiInfo(0)
        }
    }


    /**
     * myTurn - MediatorLiveData를
     * */
    private fun refreshMyTurn() {
        with(_myTurn) {
            addSource(turnFlag) {
                setMyTurn(player, firstPlayer, secondPlayer, turnFlag)
            }
            addSource(player) {
                setMyTurn(player, firstPlayer, secondPlayer, turnFlag)
            }
            addSource(firstPlayer) {
                setMyTurn(player, firstPlayer, secondPlayer, turnFlag)
            }
        }
    }

    /**
     * 내가 첫번째 플레이어인지 아닌지 구분
     * */
    private fun refreshIsFirstPlayer() {
        with(_isFirstPlayer) {
            addSource(player) {
                setIsFirstPlayer(player, firstPlayer)
            }
            addSource(firstPlayer) {
                setIsFirstPlayer(player, firstPlayer)
            }
        }
    }

    /**
     * Profile - NameTag drawable 이미지를 무작위로 설정
     * */
    private fun refreshNameTag() {
        try {
            val first = listOf(1, 2, 3, 4, 5).shuffled().first()
            val second = listOf(1, 2, 3, 4, 5).shuffled().first()

            val list = listOf(first, second)

            _nameTagImages.value = list
        } catch (error: NullPointerException) {
            log(TAG, "refreshNameTag : error ${error.message}", LogTag.D)
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

        viewModelScope.launch {
            gamePlayRepositery.getGameStartTime()
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
            }
        }
    }

    /**
     * "RollDice" data 를 Firebase Database 로부터 읽어온다.
     * */
    fun refreshRollDice(gameId: String) {
        viewModelScope.launch {
            getFlowRollDiceUseCase(gameId).collect {
                if (it.isSuccess) {
                    _rollDice.value = it.getOrNull()
                }
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
                BoardInfo(
                    listOf(Board(), Board()),
                    listOf(Board(), Board()),
                    listOf(BoardRecord(), BoardRecord())
                )

            writeBoardInfoUseCase(gameId, boardInfo)

            getFlowBoardInfoUseCase(gameId).collect { result ->
                if (result.isSuccess) {
                    _boardInfo.value = result.getOrDefault(boardInfo)
                }
            }
        }
    }

    /**
     * viewModel 이 생성될 때, HostPlayer가 Firebase 에 rollDice data wrtie
     * */
    fun writeRollDiceAtFirst(gameId: String) {
        viewModelScope.launch {
            val rollDice = RollDice(gameId, START_LIST.clone(), INIT_KEEP_LIST.clone(), true)
            writeRollDiceUseCase(rollDice)
        }
    }

    /**
     * RollDice Data를 Firebase Database에 write
     * */
    private fun writeRollDice(dices: Array<Int>, keeps: Array<Boolean>, turn: Boolean) {
        viewModelScope.launch {
            rollDice.value?.let {
                val new = RollDice(it.gameId, dices, keeps, turn)
                writeRollDiceUseCase(new)
            }
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
     * 야추일 경우, 사운드
     * */
    private fun isYacht(board: Board) {
        if (board.yacht > 0) {
            _yachtSound.value = Event(Unit)
        }
    }

    /**
     * 'keepList'의 data에 맞춰 주사위 모양 변형하기.
     * */
    @SuppressLint("UseCompatLoadingForDrawables")
    fun setRollDiceImage(diceList: Array<Int>) {

        viewModelScope.launch(Dispatchers.Main) {
            launch {
                _diceAnim.value = mutableListOf<Boolean>().apply {
                    addAll(keepList.map { !it })
                }
                delay(400)
            }.join()


            launch {
                _diceAnim.value = INIT_DICE_ANIM

                var list = mutableListOf<Int>().apply {
                    addAll(diceList.toList())
                }

                log(TAG, "setRollDiceImage : ${list}", LogTag.I)
                _rollDiceImages.value = list
            }
        }


    }


    /**
     * diceList의 주사위들의 정보에 따라 플레이어가 얻을 수 있는 score를 계산하고,
     * 해당 score값을 새 board 인스턴스에 (boardInfo에 쓰일) 매개변수로 전달한다.
     * */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun showScore(dices: Array<Int>) {
        try {

            val score: Board = yachtGame.getScore(dices)
            isYacht(score)
            val fakeBoards =
                if (isFirstPlayer.value == true) listOf(score, Board()) else listOf(
                    Board(),
                    score
                )

            val boardInfo =
                BoardInfo(
                    listOf(firstRealBoard.value!!, secondRealBoard.value!!),
                    fakeBoards,
                    listOf(firstBoardRecord.value!!, secondBoardRecord.value!!),
                )

            viewModelScope.launch {
                writeBoardInfoUseCase(gameId.value!!, boardInfo)
            }

            log(TAG, "showScore Success : ${boardInfo}", LogTag.I)
        } catch (error: Error) {
            log(TAG, "showScore Failure : ${error.message}", LogTag.D)
        } catch (error: NullPointerException) {
            log(TAG, "showScore Failure ${error.message}", LogTag.D)
        }
    }


    /**
     * 임시
     * */
    fun finishTurn() {
        writeRollDice(diceList, INIT_KEEP_LIST.clone(), player.value != firstPlayer.value)
        log(TAG, "finishTurn()  : chance = ${chance}", LogTag.I)
        _initRollDiceKeep.value = Event(Unit)
        if (isFirstPlayer.value != true) implementTurnCount() // Second Player인 경우 턴을 넘길 때 turn이 증가.
        //getChanceStr()
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
     *
     * */
    @RequiresApi(Build.VERSION_CODES.N)
    fun confirmScore(boardTag: BoardTag) {
        val newRealBoard = getScore(boardTag)
        val newFakeBoard = listOf(Board(), Board())
        val newBoardRecords = getBoardRecord(boardTag)

        val boardInfo = BoardInfo(newRealBoard, newFakeBoard, newBoardRecords)

        viewModelScope.launch {
            writeBoardInfoUseCase(gameId.value!!, boardInfo)
        }
    }

    /**
     * 보드판의 점수를 사용자가 클릭하면 해당 점수가 확정된다. 이 때 boardTag 매개변수가 전달된다.
     * boardTag매개변수 값과 "first(또는 second)FakeBoard" 의 value를 이용하여  (fakeBoard는 항상 사용자의 가장 최근 굴린 주사위로 얻을 수 있는 점수들이 board객체로 업데이트)
     * "first(혹은second)RealBoard"값을 구성한다. realBoard는 점수가 확정되면 사용자에게 보여질 점수를 담은 board 인스턴스이다.
     * realBoard를 만든 뒤 새로운 gameInfo 인스터스에 전달하여 Firebase Database에 wrtie하는 함수.
     * */
    @RequiresApi(Build.VERSION_CODES.N)
    fun getScore(boardTag: BoardTag): List<Board> {
        try {
            val score: Board =
                if (isFirstPlayer.value == true) firstFakeBoard.value!! else secondFakeBoard.value!!
            val newBoard: Board = if (isFirstPlayer.value == true) firstRealBoard.value!!.plusScore(
                score,
                boardTag
            ) else secondRealBoard.value!!.plusScore(score, boardTag)

            log(TAG, "getScore() score : $newBoard", LogTag.I)

            return if (isFirstPlayer.value == true) listOf(
                newBoard,
                secondRealBoard.value!!
            ) else listOf(firstRealBoard.value!!, newBoard)


        } catch (error: Exception) {
            log(TAG, "getScore() error : ${error.message}", LogTag.D)
            return emptyList()
        }
    }


    /**
     * Board를 클릭할 때, 클릭한 곳의 수를 확정하고 사용자에게 display 위해
     * BoardRecord 객체의 각 프로퍼티값을 BoardTag에 따라 true로 변환
     * 해당 값이 변경되면 이와 binding 된 fakeBoard와 realBoard가 사라지고 나타난다.
     * */
    @RequiresApi(Build.VERSION_CODES.N)
    fun getBoardRecord(boardTag: BoardTag): List<BoardRecord> {
        try {
            val boardRecord =
                if (isFirstPlayer.value == true) firstBoardRecord.value!! else secondBoardRecord.value!!

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

            }
            log(TAG, "confirmBoardRecord Success : ${boardInfo}", LogTag.I)

            return if (isFirstPlayer.value == true) listOf(
                boardRecord,
                secondBoardRecord.value!!
            ) else listOf(firstBoardRecord.value!!, boardRecord)


        } catch (error: Exception) {
            log(TAG, "confirmBoardRecord Failure: ${error.message}", LogTag.D)
            return emptyList()
        }
    }

    /**
     * 내 턴이 올때마다 'turnCount' 를 1씩 증가시키고
     * writeTurnCountInfoUserCase를 이용하여
     * turnCount를 FirebaseDatabase에 write.
     * */
    fun implementTurnCount() {
        viewModelScope.launch {
            writeTurnCountInfoUseCase(gameId.value!!, TurnCountInfo(turnCount.value!! + 1))
        }
    }

    /**
     * 게임이 끝나고, 승패여부=Resulut 를 알려주는 문자열 및 custom dialog
     * 'result : List<String>' idx별로 각 원소 = 1.첫번째 플레이어의 점수, 2.첫번째 플레이어의 이름, 3.두번째 플레이어의 점수, 4.두번째 플레이어의 이름, 5.승리 결과
     *
     * 누군가가 나갔을 때, 해당 플레이어의 패배로 게임을 끝내고 = early : Boolean -> true일 경우
     * 이에 맞게 GameResult:String을 생성
     * */
    @RequiresApi(Build.VERSION_CODES.N)
    fun getGameResult(early: Boolean): List<String> {
        val result = mutableListOf<String>()
        try {
            val first = firstRealBoard.value!!
            val second = secondRealBoard.value!!
            var isDraw = false


            val winner = if (first.total > second.total) {
                firstPlayer.value!!.name
            } else if (first.total < second.total) {
                secondPlayer.value!!.name
            } else {
                isDraw = true
                "무승부"
            }

            result.addAll(
                listOf(
                    first.total.toString(),
                    firstPlayer.value!!.name,
                    second.total.toString(),
                    secondPlayer.value!!.name
                )
            )

            if (early) {
                result.add(
                    getApplication<Application?>().getString(
                        R.string.game_result_early,
                        player.value!!.name,
                        if (isFirstPlayer.value == true) secondPlayer.value!!.name else firstPlayer.value!!.name
                    )
                )
            } else if(!isDraw) {
                result.add(getApplication<Application?>().getString(
                            R.string.game_result_win,
                            winner
                        ))
            } else {
                result.add(getApplication<Application?>().getString(R.string.game_result_draw))
            }

        } catch (error: Exception) {
            log(TAG, "getGameResult() error : ${error.message}", LogTag.D)
        } catch (error: NullPointerException) {
            log(TAG, "getGameResult() error : ${error.message}", LogTag.D)
        } finally {
            return result
        }
    }


    /**
     * 게임이 끝나면 완성된 GameInfo 인스턴스를 만들어서 Firebase 및 Room Database에 write, insert
     * */
    @RequiresApi(Build.VERSION_CODES.N)
    fun finishGame(gameResult: List<String>) {

        try {
            val gameInfo = gameInfo.value!!
            val gameScore = getApplication<Application?>().getString(
                R.string.game_score,
                gameResult[0],
                gameResult[2]
            )
            val finishTime = DateTime.getNowDate(System.currentTimeMillis())
            val result = gameResult[4]
            val finalBoards = listOf(firstRealBoard.value!!, secondRealBoard.value!!)
            val new = GameInfo(gameInfo, gameScore, finishTime, result, finalBoards)

            writeGameInfo(new)
            updateGameInfo(new)
            log(TAG, "finishGame Success : ${new}", LogTag.I)
        } catch (error: Exception) {
            log(TAG, "finishGame error : ${error.message}", LogTag.D)
        } catch (error: NullPointerException) {
            log(TAG, "finishGame error : ${error.message}", LogTag.D)
        }
    }


    /**
     * Firebase Database 에 GameInfo Write.
     * */
    private fun writeGameInfo(gameInfo: GameInfo) {
        viewModelScope.launch {
            gamePlayRepositery.writeGameInfo(gameInfo)
        }
    }

    /**
     * room DATABASE 에 gameInfo 데이터 업데이트.
     * */
    private fun updateGameInfo(gameInfo: GameInfo) {
        viewModelScope.launch {
            gamePlayRepositery.updateGameInfo(gameInfo)
        }
    }

    /**
     * 상대편 uid에 나의 emojiInfo Write
     * */
    fun writeOpponentEmoji(emoji: Int) {
        viewModelScope.launch {
            writeEmojiInfoUseCase(
                gameId.value!!,
                if (isFirstPlayer.value == true) secondPlayer.value!!.playerId else firstPlayer.value!!.playerId,
                EmojiInfo(emoji)
            )
        }
    }

    /**
     * 내 profile layout 에 emoji쓰기
     * */
    fun setMyEmoji(emoji: Int) {
        viewModelScope.launch {
            myEmojiJob.cancel()
            _myEmoji.value = emoji

            myEmojiJob = launch {
                delay(1500)
                _myEmoji.value = 0
            }
        }
    }

    companion object {
        private const val TAG = "GamePlayViewModel"
        const val INIT_CHANCE = 500
        const val CHANCE = 3
        private val START_LIST = arrayOf(1, 2, 3, 4, 5)
        private val INIT_DICE_LIST = Array<Int>(5) { 0 }
        private val INIT_KEEP_LIST = Array<Boolean>(6) { false }
        private val INIT_DICE_ANIM = List(5) { false }
    }

}