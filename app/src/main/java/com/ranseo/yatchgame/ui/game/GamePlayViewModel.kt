package com.ranseo.yatchgame.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranseo.yatchgame.data.model.GameInfo
import com.ranseo.yatchgame.data.repo.GamePlayRepositery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GamePlayViewModel @Inject constructor(private val gamePlayRepositery: GamePlayRepositery) : ViewModel() {
    private val _gameId = MutableLiveData<String>()
    val gameId : LiveData<String>
        get() = _gameId

    private val _gameInfo = MutableLiveData<GameInfo>()
    val gameInfo : LiveData<GameInfo>
        get() = _gameInfo

    init {
        refreshGameId()
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

    fun refreshGameInfo(gameInfoId:String) {
        viewModelScope.launch {
            gamePlayRepositery.getGameInfo(gameInfoId) { gameInfo ->
                _gameInfo.postValue(gameInfo)
                updateGameInfo(gameInfo)
            }
        }
    }

    private fun updateGameInfo(gameInfo: GameInfo) {
        viewModelScope.launch {
            gamePlayRepositery.updateGameInfo(gameInfo)
        }
    }

}