package com.ranseo.yatchgame.ui.lobby.statis

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.data.model.statis.WinDrawLose
import com.ranseo.yatchgame.log

class StatisViewPagerAdapter(fm:FragmentManager, lifecycle: Lifecycle, ) : FragmentStateAdapter(fm, lifecycle){
    var list :List<Fragment> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment {
        log(TAG,"createFragment : ${list[position]}", LogTag.I)
        list[position]?.let { return it } ?: return Fragment()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun addFragment(fragment:Fragment) {
        list = list + listOf(fragment)

    }

    companion object {
        private const val TAG = "StatisViewPagerAdapter"
    }


}