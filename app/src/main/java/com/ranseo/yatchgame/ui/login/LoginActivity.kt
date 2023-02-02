package com.ranseo.yatchgame.ui.login

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.ranseo.yatchgame.BuildConfig
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.databinding.ActivityLoginBinding

import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.LoggedInUser
import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.log
import com.ranseo.yatchgame.ui.dialog.EditTextDialog
import com.ranseo.yatchgame.ui.lobby.LobbyActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var auth: FirebaseAuth

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private lateinit var loginObserver: LoginObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOneTapLogin()

        val userEmail: EditText = binding.etEmail!!
        val userNickName: EditText = binding.etNickname!!
        val login: Button = binding.btnLogin!!
        val googleLogin: ConstraintLayout = findViewById(R.id.layout_google_login)


        with(loginViewModel) {
            loginFormState.observe(this@LoginActivity, Observer {
                val loginState = it ?: return@Observer

                // disable login button unless both username / password is valid
                login.isEnabled = loginState.isDataValid

                if (loginState.usernameError != null) {
                    userEmail.error = getString(loginState.usernameError)
                }
            })

            loginResult.observe(this@LoginActivity, Observer {
                val loginResult = it ?: return@Observer

                if (loginResult.error != null) {
                    showLoginFailed(loginResult.error)
                }
                if (loginResult.success != null) {
                    updateUiWithUser(loginResult.success)
                }
                setResult(Activity.RESULT_OK)

                //Complete and destroy login activity once successful
                finish()
            })

            credential.observe(this@LoginActivity) {
                it.getContentIfNotHandled()?.let { credential ->
                    signInWithCredential(credential)
                }
            }

            nickName.observe(this@LoginActivity) {
                it.getContentIfNotHandled()?.let { result ->
                    loginViewModel.insertAndWritePlayer(result)
                }
            }
        }

        userEmail.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    userEmail.text.toString()
                )
            }
        }

        googleLogin.setOnClickListener {
            loginWithGoogle()
        }


        login.setOnClickListener {
            loginViewModel.login(userEmail.text.toString(), userNickName.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser ?: return
        updateUiWithUser(LoggedInUserView(currentUser.displayName.toString()))
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()

        val intent = Intent(this, LobbyActivity::class.java)
        startActivity(intent)
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    //Google Login
    fun setOneTapLogin() {
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()


        log(TAG, "setOneTapLogin : ${signInRequest}", LogTag.I)
        loginObserver = LoginObserver(this.activityResultRegistry, oneTapClient, loginViewModel)
        lifecycle.addObserver(loginObserver)
        log(TAG, "setOneTapLogin : ${loginObserver}", LogTag.I)
    }

    fun loginWithGoogle() {
        val request = GetSignInIntentRequest.builder()
            .setServerClientId(BuildConfig.WEB_CLIENT_ID)
            .build()

//        Identity.getSignInClient(this)
//            .getSignInIntent(request)
//            .addOnSuccessListener { result ->
//                try {
//                    loginObserver.startIntentSenderResult(result.intentSender)
//                } catch (error: Exception) {
//                    log(TAG, "IntentSender Exception : ${error.message}", LogTag.I)
//                }
//            }
//            .addOnFailureListener {
//                log(TAG, "onGoogleLogin() fail : ${it}\n${it.message}", LogTag.I)
//            }

        oneTapClient.getSignInIntent(request).addOnSuccessListener(this) { result ->
            try {
                loginObserver.startIntentSenderResult(result.intentSender)
                loginViewModel.writeLog(result.toString())
            } catch (error: Exception) {
                log(TAG, "IntentSender Exception : ${error.message}", LogTag.I)
                loginViewModel.writeLog(error.message.toString())
            }
        }
            .addOnFailureListener {
                log(TAG, "onGoogleLogin() fail : ${it}\n${it.message}", LogTag.I)
                loginViewModel.writeLog(it.message.toString())
            }
    }

    private fun signInWithCredential(credential: AuthCredential) {
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                try {

                    val uid = it.user!!.uid
                    showDialog(uid)
                    log(TAG, "SignInWithCredentail success", LogTag.I)
                    loginViewModel.writeLog("SignInWithCredential success")
                } catch (error: Exception) {
                    log(TAG, "SignInWithCredentail error: ${error.message}", LogTag.E)
                    auth.signOut()
                }
            }
            .addOnFailureListener {
                log(TAG, "SignInWithCredentail Failure", LogTag.I)
                loginViewModel.writeLog("SignInWithCredential Failure ${it.message}")
            }
        //setProgressbar(false)
    }

    private fun showDialog(uid:String) {
        val dialog = EditTextDialog(this, "별명 입력", "게임에서 사용될 별명을 입력해주세요").apply {
            setOnClickListener(object : EditTextDialog.OnEditTextClickListener {
                override fun onPositiveBtn(text: String) {

                    log(TAG, "onPositiveBtn : ${text}", LogTag.I)
                    val loggedInUser = LoggedInUser(uid, text)
                    loginViewModel.makeNickName(loggedInUser)

                }

                override fun onNegativeBtn() {
                    log(TAG, "onNegativeBtn", LogTag.I)
                    auth.signOut()
                }
            })
        }

        dialog.showDialog()
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}


