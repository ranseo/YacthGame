package com.ranseo.yatchgame.data.source

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.data.model.RollDice
import com.ranseo.yatchgame.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RollDiceDataSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    private val TAG = "RollDiceDataSource"
    private var rollDiceValueEventListener: ValueEventListener? = null

    suspend fun getRollDice(gameId: String, callback: (rollDice: RollDice) -> Unit) =
        withContext(Dispatchers.IO) {
            rollDiceValueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val hashMap = snapshot.value as HashMap<*, *>
                        val new = RollDice(hashMap)
                        callback(new)
                        log(TAG, "getRollDice onDataChange Success : $new", LogTag.I)
                    } catch (error: Exception) {
                        log(TAG, "getRollDice onDataChange Failure : ${error.message}", LogTag.D)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    log(TAG, "getRollDice Failure : ${error.message}", LogTag.D)
                }
            }

            val ref = firebaseDatabase.reference.child("rollDice").child(gameId)
            ref.addValueEventListener(rollDiceValueEventListener!!)
        }

    suspend fun removeValueEventListener(gameId: String) {
        rollDiceValueEventListener?.let {
            val ref = firebaseDatabase.reference.child("rollDice").child(gameId)
            ref.removeEventListener(it)
        }
    }

    suspend fun writeRollDice(rollDice: RollDice) = withContext(Dispatchers.IO) {
        val ref = firebaseDatabase.reference.child("rollDice").child(rollDice.gameId)
        ref.setValue(rollDice).addOnCompleteListener {
            if (it.isSuccessful) {
                log(TAG, "writeRollDice Success: ${rollDice}", LogTag.I)
            } else {
                log(TAG, "writeRollDice Failure", LogTag.D)
            }
        }
    }
}