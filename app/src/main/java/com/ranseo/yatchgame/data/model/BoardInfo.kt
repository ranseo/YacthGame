package com.ranseo.yatchgame.data.model

import android.os.Build
import androidx.annotation.RequiresApi

data class BoardInfo(
    val boards: MutableMap<String, List<Board>>,
    val boardRecords : MutableMap<String, List<BoardRecord>>
) {
    constructor(boards:List<Board>, boardRecords:List<BoardRecord>) : this (
        boards = mutableMapOf("boards" to boards),
        boardRecords = mutableMapOf("boardRecords" to boardRecords)
    )

    @RequiresApi(Build.VERSION_CODES.N)
    fun returnBoard(idx:Int) : Board? {
        val boards = this.boards.getOrDefault("boards", listOf())
        return if(boards.isEmpty()) null else boards[idx]
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun returnBoardRecord(idx:Int) : BoardRecord? {
        val boardRecords = this.boardRecords.getOrDefault("boardRecords", listOf())
        return if(boardRecords.isEmpty()) null else boardRecords[idx]
    }
}