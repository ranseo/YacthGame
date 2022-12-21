package com.ranseo.yatchgame.data.source

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.data.model.EmojiInfo
import com.ranseo.yatchgame.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.lang.NullPointerException
import javax.inject.Inject

class EmojiInfoDataSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    private val TAG = "EmojiInfoDataSource"
    private var emojiInfoValueEventListener: ValueEventListener? = null


    suspend fun getEmojiInfo(gameId: String, playerId: String): Flow<Result<EmojiInfo>> =
        withContext(Dispatchers.IO) {
            callbackFlow {
                emojiInfoValueEventListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            val emojiInfo = EmojiInfo(snapshot.value as HashMap<*, *>)

                            emojiInfo?.let {
                                this@callbackFlow.trySendBlocking(Result.success(it))
                            }
                            log(TAG, "onDataChange : ${emojiInfo}", LogTag.I)
                        } catch (error: NullPointerException) {
                            log(TAG, "onDataChange error : ${error.message} ", LogTag.D)
                        } catch (error: Exception) {
                            log(TAG, "onDataChange error : ${error.message} ", LogTag.D)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        close(error.toException())
                        log(TAG, "onCancelled : error = $error", LogTag.D)
                    }

                }
                val ref = firebaseDatabase.reference.child("emoji").child(gameId).child(playerId)

                emojiInfoValueEventListener?.let { ref.addValueEventListener(it) }

                awaitClose {
                    emojiInfoValueEventListener?.let { ref.removeEventListener(it) }
                }
            }
        }

    suspend fun setEmojiInfo(gameId: String, playerId: String, emojiInfo: EmojiInfo) =
        withContext(Dispatchers.IO) {
            val ref = firebaseDatabase.reference.child("emoji").child(gameId).child(playerId)
            ref.setValue(emojiInfo)
        }
}