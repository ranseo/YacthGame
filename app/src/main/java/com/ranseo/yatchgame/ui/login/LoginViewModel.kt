package com.ranseo.yatchgame.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.data.repo.LoginRepository
import com.ranseo.yatchgame.data.Result

import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.domain.usecase.InsertPlayerUseCase
import com.ranseo.yatchgame.domain.usecase.write.WritePlayerUseCase
import com.ranseo.yatchgame.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
        private val loginRepository: LoginRepository,
        private val insertPlayerUseCase: InsertPlayerUseCase,
        private val writePlayerUseCase: WritePlayerUseCase
    ) :
    ViewModel() {
    private val TAG = "LoginViewModel"

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult


    fun login(email: String, nickName: String) {
        // can be launched in a separate asynchronous job

        viewModelScope.launch(Dispatchers.Main) {
            loginRepository.login(email) { uid, _, result ->
                if (uid != null && nickName != null) {

                    log(TAG, "loginRepositery.login - uid : ${uid}, name : ${nickName}", LogTag.I)

                    viewModelScope.launch {
                        launch {
                            val player = Player(
                                uid,
                                nickName
                            )
                            insertPlayerUseCase(player)
                            writePlayerUseCase(player) { isWrite ->
                                viewModelScope.launch {
                                    if (isWrite) {
                                        if (result is Result.Success) {
                                            _loginResult.postValue(
                                                LoginResult(
                                                    LoggedInUserView(
                                                        result.data.displayName
                                                    )
                                                )
                                            )
                                        } else {
                                            _loginResult.postValue(LoginResult(error = R.string.login_failed))
                                        }
                                    }
                                }
                            }
                        }.join()
                    }
                }
            }
        }
    }

    fun loginDataChanged(username: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }
}