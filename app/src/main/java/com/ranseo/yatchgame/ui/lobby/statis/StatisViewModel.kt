package com.ranseo.yatchgame.ui.lobby.statis

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.ranseo.yatchgame.domain.usecase.get.GetPlayerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class StatisViewModel(
    getPlayerUseCase: GetPlayerUseCase,
    application: Application) : AndroidViewModel(application) {

}