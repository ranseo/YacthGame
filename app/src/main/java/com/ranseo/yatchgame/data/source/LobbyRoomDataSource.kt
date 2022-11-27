package com.ranseo.yatchgame.data.source

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.data.model.LobbyRoom
import com.ranseo.yatchgame.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LobbyRoomDataSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {

    private val TAG = "LobbyRoomDataSource"

    private var lobbyRoomValueEventListener: ValueEventListener? = null

    suspend fun writeLobbyRoom(lobbyRoom: LobbyRoom, callback: (roomKey:String) -> Unit) = withContext(Dispatchers.IO) {
        val ref = firebaseDatabase.reference.child("lobby").push()
        val key = ref.key ?: return@withContext

        ref.setValue(lobbyRoom.setRoomKey(key)).addOnCompleteListener {
            if (it.isSuccessful) {
                log(TAG, "lobby에 쓰기 성공", LogTag.I)
                callback(key)
            } else {
                log(TAG, "lobby에 쓰기 실패 : ${it.exception?.message}", LogTag.D)
            }
        }
    }

    suspend fun getLobbyRooms(callback: (list: List<LobbyRoom>) -> Unit) = withContext(Dispatchers.IO){
        lobbyRoomValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lobbyRooms = snapshot.children.map { list ->
                    val hashMap = list.value as HashMap<*, *>
                    val lobbyRoom = LobbyRoom(hashMap)

                    lobbyRoom
                }

                callback(lobbyRooms)
                log(TAG, "onDataChange : $lobbyRooms", LogTag.I)
            }

            override fun onCancelled(error: DatabaseError) {
                log(TAG, "onCancelled : ${error.message}", LogTag.D)
            }
        }
        val ref = firebaseDatabase.reference.child("lobby")
        ref.addValueEventListener(lobbyRoomValueEventListener!!)
    }


    suspend fun removeLobbyRoom(roomKey:String) = withContext(Dispatchers.IO){
        val ref = firebaseDatabase.reference.child("lobby").child(roomKey)
        ref.removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                log(TAG, "removeLobbyRoom Success", LogTag.I)
            } else {
                log(TAG, "removeLobbyRoom Failure", LogTag.D)
            }
        }
    }

    suspend fun removeLobbyRoomsValueEventListener() = withContext(Dispatchers.IO){
        val ref = firebaseDatabase.reference.child("lobby")
        lobbyRoomValueEventListener?.let{
            ref.removeEventListener(it)
        }
    }



}