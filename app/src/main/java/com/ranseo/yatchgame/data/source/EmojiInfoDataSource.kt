package com.ranseo.yatchgame.data.source

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.data.model.EmojiInfo
import com.ranseo.yatchgame.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EmojiInfoDataSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    private val TAG = "EmojiInfoDataSource"
    private var emojiInfoValueEventListener : ValueEventListener?= null

    suspend fun getEmojiInfo(gameId:String) = withContext(Dispatchers.IO){
        emojiInfoValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hashMap = snapshot.value as HashMap<*,*>

                val emojiInfo = EmojiInfo(hashMap)
                log(TAG, "onDataChange : ", LogTag.I)
            }

            override fun onCancelled(error: DatabaseError) {
                log(TAG, "onCancelled : error = $error", LogTag.D)
            }

        }

        val ref = firebaseDatabase.reference.child("emoji").child(gameId)


    }
}