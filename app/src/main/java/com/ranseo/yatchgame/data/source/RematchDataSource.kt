package com.ranseo.yatchgame.data.source

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.data.model.*
import com.ranseo.yatchgame.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class RematchDataSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    private val TAG = "RematchDataSource"

    suspend fun getRematch(uid: String): Flow<Result<Rematch>> = withContext(Dispatchers.IO) {
        callbackFlow {
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val hashMap = snapshot.value as HashMap<*, *>
                        val playerMap = hashMap["requestPlayer"] as MutableMap<*, *>
                        val player = Player(playerMap["requestPlayer"] as HashMap<*, *>)

                        val rematch = RematchRemote(hashMap, player).asUIState()

                        this@callbackFlow.trySendBlocking(Result.success(rematch))
                    } catch (error: Exception) {
                        this@callbackFlow.trySendBlocking(Result.failure(error))
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    this@callbackFlow.trySendBlocking(Result.failure(error.toException()))
                }
            }

            val ref = firebaseDatabase.reference.child("rematch").child(uid)
            ref.addValueEventListener(valueEventListener)

            awaitClose {
                ref.removeEventListener(valueEventListener)
            }
        }
    }

    suspend fun writeRematch(rematch: Rematch): Flow<Result<String>> = withContext(Dispatchers.IO) {
        callbackFlow {
            val ref = firebaseDatabase.reference.child("rematch").child(rematch.rematchId)
            val key = ref.key ?: return@callbackFlow

            ref.setValue(rematch.asRemoteModel(key)).addOnCompleteListener {
                if (it.isSuccessful) {
                    trySendBlocking(Result.success(key))
                } else {
                    trySendBlocking(
                        Result.failure(
                            it.exception?.cause ?: Throwable("writeRematch failure")
                        )
                    )
                }
            }

            awaitClose {
            }
        }
    }

    suspend fun removeRematch(uid:String) = withContext(Dispatchers.IO){
        val ref = firebaseDatabase.reference.child("rematch").child(uid)
        ref.removeValue()
    }
}