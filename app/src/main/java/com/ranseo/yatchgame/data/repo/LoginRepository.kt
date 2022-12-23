package com.ranseo.yatchgame.data.repo

import com.ranseo.yatchgame.data.Result
import com.ranseo.yatchgame.data.model.LoggedInUser
import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.data.source.PlayerRemoteDataSource
import com.ranseo.yatchgame.data.source.LoginDataSource
import javax.inject.Inject

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository @Inject constructor(private val loginDataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    init {
        user = null
    }

    fun login(username: String, callback:(uid:String?, name:String?, result:Result<LoggedInUser>)->Unit){
        loginDataSource.login(username,callback)
    }


}