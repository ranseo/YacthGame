package com.ranseo.yatchgame.ui.lobby.statis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.databinding.FragmentStatisBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.Observer
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.data.model.GameInfo
import com.ranseo.yatchgame.data.model.statis.BestScore
import com.ranseo.yatchgame.data.model.statis.WinDrawLose
import com.ranseo.yatchgame.log

@AndroidEntryPoint
class StatisFragment : Fragment() {

    private lateinit var binding : FragmentStatisBinding
    private lateinit var adapter: StatisViewPagerAdapter
    private val statisViewModel : StatisViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statis, container, false)

        binding.viewModel = statisViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        adapter = StatisViewPagerAdapter(childFragmentManager, lifecycle)


        with(binding) {
            viewPagerStatis.adapter = adapter
        }

        TabLayoutMediator(binding.tabLayoutStatis, binding.viewPagerStatis) { tab, position ->
            when(position) {
                0 -> {
                    tab.text = "총 전적"
                }
                1 -> {
                    tab.text = "최고의 점수"
                }
                2 -> {
                    tab.text = "상대 전적"
                }
                3 -> {
                    tab.text = "명예의 전당"
                }
            }
        }.attach()


        with(statisViewModel) {
            player.observe(viewLifecycleOwner, playerObserver())
        }


        return binding.root
    }

    private fun playerObserver() =
        Observer<Player> { player ->
            log(TAG, "playerObserver() : ${player}", LogTag.I)
            statisViewModel.getWinDrawLose(player)
            statisViewModel.getBestScore(player)

            adapter.addFragment(TotalRecordFragment())
            adapter.addFragment(BestRecordFragment())
        }

    companion object{
        private const val TAG = "StatisFragment"
    }
}