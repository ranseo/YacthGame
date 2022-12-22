package com.ranseo.yatchgame.data.source

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.data.model.TurnCountInfo
import com.ranseo.yatchgame.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TurnCountDataSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    private val TAG = "TurnCountDataSource"
    private var turnCountInfoValueEventListener : ValueEventListener? = null

    suspend fun getTurnCountInfo(gameId:String) = withContext(Dispatchers.IO) {
        callbackFlow<Result<TurnCountInfo>> {
            turnCountInfoValueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val hashMap = snapshot.value as HashMap<*,*>
                        val turnCountInfo = TurnCountInfo(hashMap["turnCount"] as Long)

                        this@callbackFlow.trySendBlocking(Result.success(turnCountInfo))
                        log(TAG, "getTurnCountInfo() Success : ${turnCountInfo}", LogTag.D)

                    }catch (error:Exception) {
                        log(TAG, "getTurnCountInfo() Failure : ${error.message}", LogTag.D)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    log(TAG, "getTurnCountInfo() Failure : ${error.message}", LogTag.D)
                }
            }

            val ref = firebaseDatabase.reference.child("turnCount").child(gameId)
            turnCountInfoValueEventListener?.let {
                ref.addValueEventListener(it)
            }

            awaitClose {
                turnCountInfoValueEventListener?.let {
                    ref.removeEventListener(it)
                }
            }

        }
    }

    suspend fun writeTurnCountInfo(gameId: String, turnCountInfo: TurnCountInfo) = withContext(Dispatchers.IO){
        val ref = firebaseDatabase.reference.child("turnCount").child(gameId)
        ref.setValue(turnCountInfo).addOnCompleteListener {
            if(it.isSuccessful) {
                log(TAG, "writeTurnCountInfo Success", LogTag.I)
            } else {
                log(TAG, "writeTurnCountInfo Failure", LogTag.D)
            }
        }
    }
}