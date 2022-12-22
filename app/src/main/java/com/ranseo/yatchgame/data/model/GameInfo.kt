package com.ranseo.yatchgame.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "game_info_table")
data class GameInfo(
    @PrimaryKey(autoGenerate = false)
    @field:Json(name="gameId")
    val gameId:String ="",
    @field:Json(name = "gameScore")
    var gameScore : String="",
    @ColumnInfo(name="game_start_time")
    @field:Json(name ="gameStartTime")
    var gameStartTime: String,
    @field:Json(name="gameFinishTime")
    var gameFinishTime:String = "",
    @field:Json(name="first")
    var first:Player,
    @field:Json(name="second")
    var second:Player,
    @field:Json(name = "result")
    var result: String="",
    @field:Json(name="board")
    var boards:List<Board>
) {
    constructor(waitingRoom: WaitingRoom, gameStartTime: String, boards: List<Board>) : this(
        gameId = waitingRoom.roomId,
        gameStartTime = gameStartTime,
        first = waitingRoom.host["host"]!!,
        second = waitingRoom.guest["guest"]!!,
        boards = boards
    )

    constructor(gameInfo:GameInfo, boards:List<Board>) : this(
        gameId = gameInfo.gameId,
        gameStartTime = gameInfo.gameStartTime,
        first = gameInfo.first,
        second = gameInfo.second,
        boards = boards
    )

    constructor(gameInfo:GameInfo, gameScore: String, gameFinishTime: String, result:String, boards:List<Board>) : this(
        gameId = gameInfo.gameId,
        gameScore = gameScore,
        gameStartTime = gameInfo.gameStartTime,
        gameFinishTime = gameFinishTime,
        first = gameInfo.first,
        second = gameInfo.second,
        result = result,
        boards = boards
    )
}

//Firebase Database read/write 용
data class GameInfoFirebaseModel(
    val gameId:String,
    var gameScore : String,
    var gameStartTime: String,
    var gameFinishTime:String = "",
    var first:MutableMap<String,Player>,
    var second:MutableMap<String,Player>,
    var result: String,
    var boards:MutableMap<String,List<Board>>
) {
    constructor(gameInfo: GameInfo) : this(
        gameInfo.gameId,
        gameInfo.gameScore,
        gameInfo.gameStartTime,
        gameInfo.gameFinishTime,
        mutableMapOf("first" to gameInfo.first),
        mutableMapOf("second" to gameInfo.second),
        gameInfo.result,
        mutableMapOf("boards" to gameInfo.boards)
    )


}

fun GameInfo.asFirebaseModel() : GameInfoFirebaseModel {
    return GameInfoFirebaseModel(this)
}

//fun GameInfoFirebaseModel.asDomainModel() : GameInfo {
//    return
//}
