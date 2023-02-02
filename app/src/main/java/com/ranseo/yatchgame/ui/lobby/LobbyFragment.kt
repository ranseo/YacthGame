package com.ranseo.yatchgame.ui.lobby

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ranseo.yatchgame.Event
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.LobbyRoom
import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.data.model.Rematch
import com.ranseo.yatchgame.databinding.FragmentLobbyBinding
import com.ranseo.yatchgame.log
import com.ranseo.yatchgame.ui.dialog.EditTextDialog
import com.ranseo.yatchgame.ui.dialog.GameRematchDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LobbyFragment : Fragment() {

    private val TAG = "LobbyFragment"
    private lateinit var binding: FragmentLobbyBinding

    private val viewModel: LobbyViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lobby, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val lobbyRoomAdapter = LobbyRoomAdapter(LobbyRoomClickListener { lobbyRoom ->
            viewModel.accessWaitingFragment(lobbyRoom)
        })

        with(binding) {
            lobbyRoomRec.adapter = lobbyRoomAdapter
        }

        with(viewModel) {
            host.observe(viewLifecycleOwner, hostObserver())
            lobbyRooms.observe(viewLifecycleOwner, lobbyRoomsObserver(lobbyRoomAdapter))
            makingRoom.observe(viewLifecycleOwner, makingRoomObserver())
            makeWaitRoom.observe(viewLifecycleOwner, makeWaitRoomObserver())
            accessWaitRoom.observe(viewLifecycleOwner, accessWaitRoomObserver())
            isRematch.observe(viewLifecycleOwner, isRematchObserver())
            rematchDialog.observe(viewLifecycleOwner, rematchDialogObserver())
        }

        //임시
//        binding.ivTmpAnimation.setBackgroundResource(R.drawable.animation_roll_dice)
//        val animation = binding.ivTmpAnimation.background as AnimationDrawable
//
//        binding.ivTmpAnimation.setOnClickListener {
//            animation.stop()
//            animation.start()
//        }


        binding.btnMakeRoom.setOnClickListener {
            viewModel.onRoomMakeBtn()
        }

        return binding.root
    }

    /**
     * viewModel.lobbyRooms.observe의 인수로 들어갈 Observer<List<LobbyRoom>>을 반환하는 함수.
     *
     * lobbyRooms가 null 아닐 시, adapter.submitList() 로 전달된다.
     * */
    private fun lobbyRoomsObserver(adapter: LobbyRoomAdapter) =
        Observer<List<LobbyRoom>> { list ->
            list?.let {
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
            it.getContentIfNotHandled()?.let {
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
                val newKeyForHost =
                    requireContext().getString(R.string.make_wait_room) + requireContext().getString(
                        R.string.border_string_for_parsing
                    ) + roomKey
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
            it.getContentIfNotHandled()?.let { lobbyRoom ->
                viewModel.removeLobbyRoomValue(lobbyRoom.roomKey)

                findNavController().navigate(
                    LobbyFragmentDirections.actionLobbyToWaiting(lobbyRoom.roomKey)
                )
            }
        }

    /**
     *
     * */
    private fun hostObserver() =
        Observer<Player> {
            it?.let { player ->
                log(TAG, "hostObserver : ${player}", LogTag.I)
            }
        }

    /**
     *
     * */
    private fun isRematchObserver() =
        Observer<Boolean> {
            if (it == true) {
                binding.fabRematch.visibility = View.VISIBLE
                vibrate(binding.fabRematch)
            } else {
                binding.fabRematch.visibility = View.GONE
            }
        }

    /**
     *
     * */
    private fun rematchDialogObserver() =
        Observer<Event<Rematch>> {
            it.getContentIfNotHandled()?.let { rematch ->
                showGameRematchDialog(rematch)
            }
        }

    /**
     * 방의 이름을 설정한 뒤, 방을 만드는 대화상자를 호출
     * */
    private fun showRoomSetDialog() {
        val dialog = EditTextDialog(requireContext(), getString(R.string.make_room), getString(R.string.set_room_name)).apply {
            setOnClickListener(
                object : EditTextDialog.OnEditTextClickListener {
                    override fun onPositiveBtn(text: String) {
                        log(TAG, "onPositiveBtn : ${text}", LogTag.I)
                        viewModel.makeRoom(text)

                    }

                    override fun onNegativeBtn() {
                        super.onNegativeBtn()
                    }
                }
            )
        }

        dialog.showDialog()
    }


    /**
     *
     * */
    private fun showGameRematchDialog(rematch: Rematch) {
        val dialog = GameRematchDialog(requireContext(), rematch.message)
        dialog.setOnClickListener(
            object : GameRematchDialog.OnGameRematchDialogClickListener {
                override fun onAccept() {
                    viewModel.removeRematch()
                    //accept 시, Host Player가 만든 waiting Room 으로 이동.
                    findNavController().navigate(
                        LobbyFragmentDirections.actionLobbyToWaiting(rematch.newGameKey)
                    )
                }

                override fun onDecline() {
                    viewModel.removeRematch()

                }
            }
        )

        dialog.showDialog()
    }


    /**
     * fab_rematch를 위한 animator 구축
     * */
    private fun vibrate(view: View) {
        val animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 10F)
        animator.repeatCount = 1
        animator.repeatCount = ObjectAnimator.REVERSE
        animator.start()
    }

}