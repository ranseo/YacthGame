package com.ranseo.yatchgame.ui.lobby

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.databinding.FragmentWaitingBinding
import com.ranseo.yatchgame.log
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class WaitingFragment : Fragment() {
    private val TAG = "WaitingFragment"
    private lateinit var binding : FragmentWaitingBinding

    private val navArgs by navArgs<WaitingFragmentArgs>()

    @Inject lateinit var waitingViewModelFactory: WaitingViewModel.AssistedFactory
    private val waitingViewModel : WaitingViewModel by viewModels {
        WaitingViewModel.provideFactory(waitingViewModelFactory, navArgs.roomId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log(TAG,"onViewCreated", LogTag.I)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_waiting, container, false)
        binding.viewModel = waitingViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        log(TAG,"onViewCreated", LogTag.I)

        return binding.root
    }
}