package com.ranseo.yatchgame.data.source

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.ranseo.yatchgame.data.Result
import com.ranseo.yatchgame.data.model.LoggedInUser
import java.io.IOException
import javax.inject.Inject

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource @Inject constructor(private val auth : FirebaseAuth){

    fun login(username: String, callback:(uid:String?, name:String?,Result<LoggedInUser>)->Unit) {
        try {
            // TODO: handle loggedInUser authentication
            createAccount(username, "123456789", callback)
            val user = LoggedInUser(auth.currentUser?.uid.toString(), username.substringBefore('@'))

            Result.Success(user)
        } catch (e: Throwable) {
            callback(null, null, Result.Error(IOException("Error logging in", e)))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }

    private fun createAccount(email:String, password: String, callback:(uid:String?, name:String?, Result<LoggedInUser>)->Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    loginWithEmail(email, password, callback)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)

                    when(task.exception) {
                        is FirebaseAuthUserCollisionException -> {
                            loginWithEmail(email, password, callback)
                            Log.w(TAG, "이메일이 이미 등록되어 있으므로 로그인 실행")
                        }
                        else -> {

                        }
                    }
                }
            }
    }

    private fun loginWithEmail(email:String, password: String, callback:(uid:String?, name:String?, Result<LoggedInUser>)->Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val uid = auth.currentUser?.uid
                    val email = auth.currentUser?.email
                    callback(uid,email, Result.Success(LoggedInUser(uid.toString(),email.toString().substringBefore('@'))))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    callback(null, null, Result.Error(IOException("Error logging in", task.exception)))
                    throw IllegalStateException("Firebase Auth SignIn Error : ${task.exception}")
                }
            }
    }

    companion object {
        private const val TAG = "LoginDataSource"
    }
}