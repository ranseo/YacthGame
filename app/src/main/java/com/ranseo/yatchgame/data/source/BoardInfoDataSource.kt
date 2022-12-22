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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class BoardInfoDataSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    private val TAG = "BoardInfoDataSource"
    private var boardInfoValueEventListener: ValueEventListener? = null

    suspend fun getBoardInfo(gameId: String) = withContext(Dispatchers.IO) {
        callbackFlow<Result<BoardInfo>> {
            boardInfoValueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val hashMap = snapshot.value as HashMap<*, *>

                        val realBoardsHashMap = hashMap["realBoards"] as HashMap<*, *>
                        val fakeBoardsHashMap = hashMap["fakeBoards"] as HashMap<*, *>
                        val boardRecordsHashMap = hashMap["boardRecords"] as HashMap<*, *>

                        val realBoardsList = realBoardsHashMap["realBoards"] as List<*>
                        val fakeBoardsList = fakeBoardsHashMap["fakeBoards"] as List<*>
                        val boardRecordsList = boardRecordsHashMap["boardRecords"] as List<*>

                        val realFirstBoard = Board((realBoardsList[0] as HashMap<*, *>))
                        val realSecondBoard = Board((realBoardsList[1] as HashMap<*, *>))

                        val fakeFirstBoard = Board((fakeBoardsList[0] as HashMap<*, *>))
                        val fakeSecondBoard = Board((fakeBoardsList[1] as HashMap<*, *>))

                        val firstBoardRecord = BoardRecord(boardRecordsList[0] as HashMap<*, *>)
                        val secondBoardRecord = BoardRecord(boardRecordsList[1] as HashMap<*, *>)

                        val newBoardInfo = BoardInfo(
                            listOf(realFirstBoard, realSecondBoard),
                            listOf(fakeFirstBoard, fakeSecondBoard),
                            listOf(firstBoardRecord, secondBoardRecord)
                        )

                        this@callbackFlow.trySendBlocking(Result.success(newBoardInfo))
                        log(TAG, "getBoardInfo OnDataChange Success ${newBoardInfo}: ", LogTag.I)
                    } catch (error: Exception) {
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

            awaitClose{
                boardInfoValueEventListener?.let{
                    ref.removeEventListener(it)
                }
            }
        }
    }

    suspend fun writeBoardInfo(gameId: String, boardInfo: BoardInfo) = withContext(Dispatchers.IO) {
        val ref = firebaseDatabase.reference.child("boardInfo").child(gameId)

        ref.setValue(boardInfo).addOnCompleteListener {
            if (it.isSuccessful) {
                log(TAG, "writeBoardInfo setValue Success : ${boardInfo}", LogTag.I)
            } else {
                log(TAG, "writeBoardInfo setValue Failure ${it.exception?.message}", LogTag.D)
            }
        }
    }
}