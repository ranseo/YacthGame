package com.ranseo.yatchgame.ui.lobby

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ranseo.yatchgame.Event
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.LobbyRoom
import com.ranseo.yatchgame.databinding.FragmentLobbyBinding
import com.ranseo.yatchgame.log
import com.ranseo.yatchgame.ui.dialog.EditTextDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LobbyFragment : Fragment() {

    private val TAG = "LobbyFragment"
    private lateinit var binding : FragmentLobbyBinding

    private val viewModel : LobbyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lobby, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val lobbyRoomAdapter =  LobbyRoomAdapter(LobbyRoomClickListener { lobbyRoom ->
            viewModel.accessWaitingFragment(lobbyRoom)
        })

        binding.lobbyRoomRec.adapter = lobbyRoomAdapter

        viewModel.lobbyRooms.observe(viewLifecycleOwner, lobbyRoomsObserver(lobbyRoomAdapter))
        viewModel.makingRoom.observe(viewLifecycleOwner, makingRoomObserver())
        viewModel.makeWaitRoom.observe(viewLifecycleOwner, makeWaitRoomObserver())
        viewModel.accessWaitRoom.observe(viewLifecycleOwner, accessWaitRoomObserver())
        return binding.root
    }

    /**
     * viewModel.lobbyRooms.observe의 인수로 들어갈 Observer<List<LobbyRoom>>을 반환하는 함수.
     *
     * lobbyRooms가 null 아닐 시, adapter.submitList() 로 전달된다.
     * */
    private fun lobbyRoomsObserver(adapter:LobbyRoomAdapter) =
        Observer<List<LobbyRoom>> { list ->
            list?.let{
                log(TAG, "lobbyRoomsObserver : ${list}", LogTag.I)
                adapter.submitList(list)
            }
        }

    /**
     * lobby_fragment.xml 에서 R.id.btn_make_room을 클릭하여 viewModel의 makeingRoom 변수가 트리거 되면
     *
     * Observer<Event<Any?>>에 등록된 showRoomSetDialog()가 호출된다.
     * */
    private fun makingRoomObserver() =
        Observer<Event<Any?>> {
            it.getContentIfNotHandled()?.let{
                showRoomSetDialog()
            }
        }

    /**
     * WaitingFragment 으로 이동 (호스트로서 대기실 생성)
     * */
    private fun makeWaitRoomObserver() =
        Observer<Event<String>> {
            it.getContentIfNotHandled()?.let { roomKey ->
                log(TAG, "makeWaitRoomObserver : ${roomKey}", LogTag.I)
                val newKeyForHost =  requireContext().getString(R.string.make_wait_room) + requireContext().getString(R.string.border_string_for_parsing) + roomKey
                findNavController().navigate(
                    LobbyFragmentDirections.actionLobbyToWaiting(newKeyForHost)
                )
            }
        }

    /**
     * WaitingFragment 으로 이동 (게스트로서 대기실 입장)
     * */
    private fun accessWaitRoomObserver() =
        Observer<Event<LobbyRoom>> {
            it.getContentIfNotHandled()?.let{ lobbyRoom->
                viewModel.removeLobbyRoomValue(lobbyRoom.roomKey)

                findNavController().navigate(
                    LobbyFragmentDirections.actionLobbyToWaiting(lobbyRoom.roomKey)
                )
            }
        }

    /**
     * 방의 이름을 설정한 뒤, 방을 만드는 대화상자를 호출
     * */
    private fun showRoomSetDialog() {
        val dialog = EditTextDialog(requireContext())
        dialog.setOnClickListener(
            object  : EditTextDialog.OnEditTextClickListener {
                override fun onPositiveBtn(text: String) {
                    log(TAG,"onPositiveBtn : ${text}", LogTag.I)
                    viewModel.makeRoom(text)
                }
            }
        )
        dialog.showDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.removeEventListener()
    }
}