package com.ranseo.yatchgame.data.source

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.data.model.Board
import com.ranseo.yatchgame.data.model.BoardInfo
import com.ranseo.yatchgame.data.model.BoardRecord
import com.ranseo.yatchgame.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class BoardInfoDataSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    private val TAG = "BoardInfoDataSource"
    private var boardInfoValueEventListener : ValueEventListener? = null

    suspend fun getBoardInfo(gameId:String, callback:(boardInfo:BoardInfo)->Unit) = withContext(Dispatchers.IO) {
        boardInfoValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val hashMap = snapshot.value as HashMap<*,*>

                    val boardsHashMap = hashMap["boards"] as HashMap<*,*>
                    val boardRecordsHashMap = hashMap["boardRecords"] as HashMap<*,*>

                    val boardsList = boardsHashMap["boards"] as List<*>
                    val boardRecordsList = boardRecordsHashMap["boardRecords"] as List<*>

                    val firstBoard = Board((boardsList[0] as HashMap<*,*>))
                    val secondBoard = Board((boardsList[1] as HashMap<*,*>))

                    val firstBoardRecord = BoardRecord(boardRecordsList[0] as HashMap<*, *>)
                    val secondBoardRecord = BoardRecord(boardRecordsList[1] as HashMap<*, *>)

                    val newBoardInfo = BoardInfo(listOf(firstBoard, secondBoard), listOf(firstBoardRecord, secondBoardRecord))

                    callback(newBoardInfo)

                    log(TAG, "getBoardInfo OnDataChange Success ${newBoardInfo}: ", LogTag.I)
                }catch (error:Exception) {
                    log(TAG, "getBoardInfo OnDataChange Failure ${error.message}", LogTag.D)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                log(TAG, "getBoardInfo onCancelled ${error.message}", LogTag.D)

            }
        }

        val ref = firebaseDatabase.reference.child("boardInfo").child(gameId)
        boardInfoValueEventListener?.let {
            ref.addValueEventListener(it)
        }
    }

    suspend fun removeBoardInfoValueEventListener(gameId: String) = withContext(Dispatchers.IO) {
        val ref = firebaseDatabase.reference.child("boardInfo").child(gameId)
        boardInfoValueEventListener?.let {
            ref.removeEventListener(it)
        }
    }

    suspend fun writeBoardInfo(gameId: String, boardInfo: BoardInfo) = withContext(Dispatchers.IO) {
        val ref = firebaseDatabase.reference.child("boardInfo").child(gameId)

        ref.setValue(boardInfo).addOnCompleteListener {
            if(it.isSuccessful) {
                log(TAG,"writeBoardInfo setValue Success : ${boardInfo}", LogTag.I)
            } else {
                log(TAG,"writeBoardInfo setValue Failure ${it.exception?.message}", LogTag.D)
            }
        }
    }
}