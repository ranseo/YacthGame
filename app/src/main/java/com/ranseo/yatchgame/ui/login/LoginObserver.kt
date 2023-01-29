package com.ranseo.yatchgame.ui.login

import android.content.IntentSender
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.log

class LoginObserver(val registry: ActivityResultRegistry, val oneTapClient: SignInClient, val viewModel: LoginViewModel) :
    DefaultLifecycleObserver {
    lateinit var getContent: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(owner: LifecycleOwner) {
        log(TAG,"onCreate() : create", LogTag.I)
        getContent = registry.register(REQ_ONE_TAP, ActivityResultContracts.StartIntentSenderForResult()) { result ->
            result.data?.let{ data ->
                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken

                when{
                    idToken != null -> {
                        val firebaseCredential : AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
                        viewModel.setCredential(firebaseCredential)
                        log(TAG,"signInWithCredential:Success", LogTag.I)
                    }

                    else -> {
                        log(TAG, "No ID Token!", LogTag.I)
                        //viewModel.setLoginErrorType(SolaroidLoginViewModel.LoginErrorType.CREDENTIALERROR)
                    }
                }
            }

        }
    }

    fun startIntentSenderResult(result: IntentSender) {
        val intentSenderResult = IntentSenderRequest.Builder(result).build()
        getContent.launch(intentSenderResult)

    }

    companion object {
        private const val REQ_ONE_TAP = "KEY"
        private const val TAG = "LoginObserver"
    }
}