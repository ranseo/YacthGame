package com.ranseo.yatchgame.data.repo

import com.ranseo.yatchgame.data.Result
import com.ranseo.yatchgame.data.model.LoggedInUser
import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.data.source.PlayerDataSource
import com.ranseo.yatchgame.data.source.LoginDataSource
import javax.inject.Inject

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository @Inject constructor(private val loginDataSource: LoginDataSource, private val playerDataSource: PlayerDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = null
    }

    fun logout() {
        user = null
        loginDataSource.logout()
    }

    fun login(username: String, callback:(uid:String?, name:String?)->Unit): Result<LoggedInUser> {
        // handle login
        val result = loginDataSource.login(username,callback)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
    }

    suspend fun insertPlayer(player: Player) {
        playerDataSource.insert(player)
    }

}