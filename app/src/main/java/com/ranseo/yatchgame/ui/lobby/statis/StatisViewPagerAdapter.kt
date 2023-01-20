package com.ranseo.yatchgame.ui.lobby.statis

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ranseo.yatchgame.data.model.statis.WinDrawLose

class StatisViewPagerAdapter(fm:FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle){
    var list : List<Fragment> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }

    fun addTotalRecordFragment(fragment:Fragment, winDrawLose: WinDrawLose) {

        list = list + listOf(fragment)
    }

}