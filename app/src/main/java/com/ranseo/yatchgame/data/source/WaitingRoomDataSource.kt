package com.ranseo.yatchgame.data.source

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.data.model.WaitingRoom
import com.ranseo.yatchgame.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WaitingRoomDataSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    private val TAG = "WaitingRoomDataSource"
    private var waitingRoomValueEventListener : ValueEventListener? = null


    suspend fun writeWaitingRoom(waitingRoom: WaitingRoom) = withContext(Dispatchers.IO){
        val ref = firebaseDatabase.reference.child("waiting").child(waitingRoom.roomId)
        ref.setValue(waitingRoom).addOnCompleteListener {
            if(it.isSuccessful) {
                log(TAG, "writeWaitingRoom Success : ${waitingRoom}", LogTag.I)
            } else {
                log(TAG, "writeWaitingRoom Failure : ${it.exception?.message}", LogTag.D)
            }
        }
    }

    suspend fun updateWaitingRoom(waitingRoom: WaitingRoom) = withContext(Dispatchers.IO) {
        val ref = firebaseDatabase.reference.child("waiting").child(waitingRoom.roomId)
        ref.setValue(waitingRoom).addOnCompleteListener {
            if (it.isSuccessful) {
                log(TAG, "updateWaitingRoom Success : ${waitingRoom}", LogTag.I)
            } else {
                log(TAG, "updateWaitingRoom Failure : ${it.exception?.message}", LogTag.D)
            }
        }
    }

    suspend fun getWaitingRoom(roomId:String, callback: (waitingRoom: WaitingRoom)->Unit) = withContext(Dispatchers.IO) {
        waitingRoomValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val waitingRoom = WaitingRoom((snapshot.value as HashMap<*, *>))
                callback(waitingRoom)
                log(TAG, "onDataChange : $waitingRoom", LogTag.I)
            }

            override fun onCancelled(error: DatabaseError) {
                log(TAG, "onDataCancelled error ${error.message}", LogTag.D)
            }
        }
        val ref = firebaseDatabase.reference.child("waiting").child(roomId)
        ref.addValueEventListener(waitingRoomValueEventListener!!)
    }

    suspend fun removeWaitingRoomValueEventListener(roomId:String) {
        val ref = firebaseDatabase.reference.child("waiting").child(roomId)
        waitingRoomValueEventListener?.let{
            ref.removeEventListener(it)
        }
    }
}