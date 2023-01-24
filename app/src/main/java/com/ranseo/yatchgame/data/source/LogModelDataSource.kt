package com.ranseo.yatchgame.data.source

import com.google.firebase.database.FirebaseDatabase
import com.ranseo.yatchgame.data.model.log.LogModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LogModelDataSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {

    suspend fun writeLog(log:LogModel) = withContext(Dispatchers.IO) {
        val ref = firebaseDatabase.reference.child("log").child(log.id).child(log.tag).push()
        ref.setValue(log)
    }
}