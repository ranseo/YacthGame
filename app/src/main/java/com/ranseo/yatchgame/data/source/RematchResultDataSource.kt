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
import javax.inject.Inject

class RematchResultDataSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    private val TAG = "RematchReceiverDataSource"

    suspend fun getRematchResult(uid:String) : Flow<Result<RematchResult>> = withContext(Dispatchers.IO) {
        callbackFlow {
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val rematchResult = RematchResult(snapshot.value as HashMap<*,*>)


                    this@callbackFlow.trySendBlocking(Result.success(rematchResult))

                }
                override fun onCancelled(error: DatabaseError) {
                    this@callbackFlow.trySendBlocking(Result.failure(error.toException()))
                }
            }

            val ref = firebaseDatabase.reference.child("rematchResult").child(uid)
            ref.addValueEventListener(valueEventListener)

            awaitClose{
                ref.removeEventListener(valueEventListener)
            }
        }
    }

    suspend fun writeRematchResult(rematchResult: RematchResult) = withContext(Dispatchers.IO){
        val ref = firebaseDatabase.reference.child("rematchResult").child(rematchResult.rematchId)
        ref.setValue(rematchResult).addOnCompleteListener {
            if(it.isSuccessful){
                log(TAG,"writeRematch Success : ${rematchResult}", LogTag.I)
            } else {
                log(TAG,"writeRematch Failure : ${it.exception}", LogTag.D)
            }
        }
    }

    //suspend fun removeRematchResult
}