package com.ranseo.yatchgame

import android.util.Log

enum class LogTag {
    I,
    D,
    E,
    W
}
fun log(tag:String, msg:String, logTag: LogTag) {
    when(logTag) {
        LogTag.I -> Log.i(tag, msg)
        LogTag.D -> Log.d(tag, msg)
        LogTag.E -> Log.e(tag, msg)
        LogTag.W -> Log.w(tag, msg)
        else -> Log.i(tag, msg)
    }
}