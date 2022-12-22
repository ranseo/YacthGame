package com.ranseo.yatchgame.data.model

import android.os.Build
import androidx.annotation.RequiresApi

data class BoardInfo(
    val realBoards: MutableMap<String, List<Board>>,
    val fakeBoards: MutableMap<String, List<Board>>,
    val boardRecords : MutableMap<String, List<BoardRecord>>
) {

    constructor(realBoards:List<Board>, fakeBoards:List<Board>, boardRecords:List<BoardRecord>) : this (
        realBoards = mutableMapOf("realBoards" to realBoards),
        fakeBoards = mutableMapOf("fakeBoards" to fakeBoards),
        boardRecords = mutableMapOf("boardRecords" to boardRecords)
    )


    @RequiresApi(Build.VERSION_CODES.N)
    fun returnRealBoard(idx:Int) : Board? {
        val boards = this.realBoards.getOrDefault("realBoards", listOf())
        return if(boards.isEmpty()) null else boards[idx]
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun returnFakeBoard(idx:Int) : Board? {
        val boards = this.fakeBoards.getOrDefault("fakeBoards", listOf())
        return if(boards.isEmpty()) null else boards[idx]
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun returnBoardRecord(idx:Int) : BoardRecord? {
        val boardRecords = this.boardRecords.getOrDefault("boardRecords", listOf())
        return if(boardRecords.isEmpty()) null else boardRecords[idx]
    }
}