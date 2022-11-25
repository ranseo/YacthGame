package com.ranseo.yatchgame.ui.lobby.waiting

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.ranseo.yatchgame.Event
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.GameInfoFirebaseModel
import com.ranseo.yatchgame.data.model.WaitingRoom
import com.ranseo.yatchgame.databinding.FragmentWaitingBinding
import com.ranseo.yatchgame.log
import com.ranseo.yatchgame.ui.game.GameActivity
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

        waitingViewModel.waitingRoom.observe(viewLifecycleOwner, waitingRoomObserver())
        waitingViewModel.waiting.observe(viewLifecycleOwner, waitingObserver())
        waitingViewModel.gameInfo.observe(viewLifecycleOwner, gameInfoObserver())
        return binding.root
    }

    private fun gameInfoObserver() =
        Observer<Event<Boolean>> {
            it.getContentIfNotHandled()?.let{ flag ->
                if(flag) {
                    log(TAG,"gameInfoObserver 게임룸 이동 성공", LogTag.I)
                    startGameActivity()
                } else {
                    log(TAG,"gameInfoObserver 게임룸 이동 실패", LogTag.I)
                }

            }
        }



    private fun waitingRoomObserver() =
        Observer<WaitingRoom>{
            it?.let { waitingRoom ->
                if(navArgs.roomId != requireContext().getString(R.string.make_wait_room)) {
                    log(TAG,"waitingRoomObserver() Guest의 update 시작", LogTag.I)
                    waitingViewModel.updateWaitingRoom(waitingRoom)
                }
            }
        }

    /**
     * Host와 Guest플레이어가 모두 대기실에 입장했을 때,
     *
     * 1.Firebase Database 의 waitingRoom 데이터를 삭제
     * 2.Firebase Database와 Room Database에 GameInfos 데이터를 생성.
     * 3.GameActivity로 이동.
     * */
    private fun waitingObserver() =
        Observer<Boolean>{
            if(it) {
                waitingViewModel.removeWaitingRoomValue()
                waitingViewModel.writeGameInfo()

            }
        }


    /**
     * GameActivity 로 이동
     * */
    private fun startGameActivity() {
        val intent = Intent(requireActivity(), GameActivity::class.java )
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        waitingViewModel.removeListener()
    }
}