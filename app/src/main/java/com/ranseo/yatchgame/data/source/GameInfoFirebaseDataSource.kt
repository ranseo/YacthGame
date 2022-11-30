package com.ranseo.yatchgame.data.source

import androidx.room.Database
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.data.model.*
import com.ranseo.yatchgame.log
import com.ranseo.yatchgame.room.YachtRoomDao
import com.ranseo.yatchgame.util.DateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class GameInfoFirebaseDataSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    private val TAG = "GameInfoFirebaseDataSource"
    private var gameInfoValueEventListener : ValueEventListener? = null

    /**
     * FirebaeDatbase에서 GameInfo ValueEventListener반환
     * */
    suspend fun getGameInfo(gameInfoId:String, callback: (gameInfo: GameInfo) -> Unit) = withContext(Dispatchers.IO) {
        gameInfoValueEventListener = object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val hashMap = snapshot.value as HashMap<*,*>

                    val gameId = hashMap["gameId"] as String
                    val gameScore = hashMap["gameScore"] as String
                    val gameStartTime = hashMap["gameStartTime"] as String
                    val gameFinishTime = hashMap["gameFinishTime"] as String
                    val firstHashMap = hashMap["first"] as MutableMap<*, *>
                    val secondHashMap = hashMap["second"] as MutableMap<*, *>
                    val result = hashMap["result"] as String
                    val boardsHashMap = hashMap["boards"] as MutableMap<*,*>

                    val first = Player(firstHashMap["first"] as HashMap<*, *>)
                    val second = Player(secondHashMap["second"] as HashMap<*,*>)

                    val boardsList = boardsHashMap["boards"] as List<*>
                    val firstBoard = Board((boardsList[0] as HashMap<*,*>))
                    val secondBoard = Board(boardsList[1] as HashMap<*,*>)


                    val newGameInfo = GameInfo(
                        gameId,
                        gameScore,
                        gameStartTime,
                        gameFinishTime,
                        first,
                        second,
                        result,
                        listOf(firstBoard, secondBoard)
                    )

                    callback(newGameInfo)
                    log(TAG,"onDataChange : ${newGameInfo}", LogTag.I)
                } catch (error:Exception) {
                    log(TAG, "onDataChange Read : ${error.message}", LogTag.D)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                log(TAG,"onCancelled : ${error.message}", LogTag.D)
            }
        }

        val ref = firebaseDatabase.reference.child("gameInfo").child(gameInfoId)
        ref.addValueEventListener(gameInfoValueEventListener!!)

    }

    suspend fun removeValueEventListener(gameInfoId: String) = withContext(Dispatchers.IO){
        val ref = firebaseDatabase.reference.child("gameInfo").child(gameInfoId)
        gameInfoValueEventListener?.let{
            ref.removeEventListener(it)
        }
    }


    /**
     * Firebase Database에 GameInfo 데이터 write
     * */
    suspend fun writeGameInfo(gameInfo: GameInfo) = withContext(Dispatchers.IO) {
        val ref = firebaseDatabase.reference.child("gameInfo").child(gameInfo.gameId)
        val new = gameInfo.asFirebaseModel()
        ref.setValue(new).addOnCompleteListener {
            if(it.isSuccessful) {
                log(TAG,"writeGameInfo Success : ${gameInfo}", LogTag.I)
            } else {
                log(TAG,"writeGameInfo Failure", LogTag.D)
            }
        }
    }

    /**
     * Firebase Database에 GameInfo 데이터 write
     * */
    suspend fun writeGameInfoAtFirst(waitingRoom: WaitingRoom, callback:(flag:Boolean, gameInfo:GameInfo)->Unit) = withContext(Dispatchers.IO) {
        val ref = firebaseDatabase.reference.child("gameInfo").child(waitingRoom.roomId)

        val new =GameInfo(waitingRoom, DateTime.getNowDate(System.currentTimeMillis()), listOf(Board(), Board()))
        val firebaseModel = new.asFirebaseModel()
        ref.setValue(firebaseModel).addOnCompleteListener {
            if(it.isSuccessful) {
                log(TAG,"writeGameInfo Success : ${new}", LogTag.I)
                callback(true, new)
            } else {
                log(TAG,"writeGameInfo Failure", LogTag.D)
                callback(false,new)
            }
        }
    }



}