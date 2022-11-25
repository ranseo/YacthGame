package com.ranseo.yatchgame.data.source

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.data.model.WaitingRoom
import com.ranseo.yatchgame.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WaitingRoomDataSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    private val TAG = "WaitingRoomDataSource"
    private var waitingRoomValueEventListener : ValueEventListener? = null


    suspend fun writeWaitingRoom(waitingRoom: WaitingRoom) = withContext(Dispatchers.IO){
        log(TAG, "writeWaitingRoom : ${waitingRoom}", LogTag.I)
        val ref = firebaseDatabase.reference.child("waiting").child(waitingRoom.roomId)
        ref.setValue(waitingRoom).addOnCompleteListener {
            if(it.isSuccessful) {
                log(TAG, "writeWaitingRoom Success : ${waitingRoom}", LogTag.I)
            } else {
                log(TAG, "writeWaitingRoom Failure : ${it.exception?.message}", LogTag.D)
            }
        }
    }

    suspend fun updateWaitingRoom(waitingRoom: WaitingRoom) {
        val ref = firebaseDatabase.reference.child("waiting").child(waitingRoom.roomId)
        ref.setValue(waitingRoom).addOnCompleteListener {
            if (it.isSuccessful) {
                log(TAG, "updateWaitingRoom Success : ${waitingRoom}", LogTag.I)
            } else {
                log(TAG, "updateWaitingRoom Failure : ${it.exception?.message}", LogTag.D)
            }
        }
    }

    suspend fun removeWaitingRoom(roomId:String) = withContext(Dispatchers.IO) {
        val ref = firebaseDatabase.reference.child("waiting").child(roomId)
        ref.removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                log(TAG, "removeWaitingRoom Success", LogTag.I)
            } else {
                log(TAG, "removeWaitingRoom Failure", LogTag.D)
            }
        }
    }

    suspend fun getWaitingRoom(roomId:String, callback: (waitingRoom: WaitingRoom)->Unit) = withContext(Dispatchers.IO) {
        waitingRoomValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value == null) return
                val hashMap =snapshot.value as HashMap<*,*>

                val roomId = hashMap["roomId"] as String
                val host = hashMap["host"] as MutableMap<String, HashMap<*,*>>
                val guest = hashMap["guest"] as MutableMap<String, HashMap<*,*>>

                val hostPlayerHashMap = host["host"] as HashMap<*,*>
                val guestPlayerHashMap = guest["guest"] as HashMap<*,*>

                val hostPlayer = Player(
                    playerId = hostPlayerHashMap["playerId"] as String,
                    name = hostPlayerHashMap["name"] as String
                )

                val guestPlayer = Player(
                    playerId = guestPlayerHashMap["playerId"] as String,
                    name = guestPlayerHashMap["name"] as String
                )

//                val waitingRoom = WaitingRoom(
//                    roomId= hashMap["roomId"] as String,
//                    host = hashMap["host"] as MutableMap<String, Player>,
//                    guest = hashMap["guest"] as MutableMap<String, Player>
//                )

                val waitingRoom = WaitingRoom(
                    roomId = roomId,
                    host = mutableMapOf("host" to hostPlayer),
                    guest = mutableMapOf("guest" to guestPlayer)
                )

                callback(waitingRoom)
                log(TAG, "onDataChange : $waitingRoom, ${waitingRoom.roomId}", LogTag.I)
            }

            override fun onCancelled(error: DatabaseError) {
                log(TAG, "onDataCancelled error ${error.message}", LogTag.D)
            }
        }
        val ref = firebaseDatabase.reference.child("waiting").child(roomId)
        ref.addValueEventListener(waitingRoomValueEventListener!!)
    }

    suspend fun removeWaitingRoomValueEventListener(roomId:String) = withContext(Dispatchers.IO) {
        val ref = firebaseDatabase.reference.child("waiting").child(roomId)
        waitingRoomValueEventListener?.let{
            ref.removeEventListener(it)
        }
    }
}