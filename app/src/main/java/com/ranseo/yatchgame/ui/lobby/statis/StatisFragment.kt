package com.ranseo.yatchgame.ui.lobby.statis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.databinding.FragmentStatisBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisFragment : Fragment() {

    private lateinit var binding : FragmentStatisBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statis, container, false)

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



        return binding.root
    }


}