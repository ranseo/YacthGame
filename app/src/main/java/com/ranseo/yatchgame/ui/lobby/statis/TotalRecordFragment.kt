package com.ranseo.yatchgame.ui.lobby.statis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelStoreOwner
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.databinding.FragmentTotalRecordBinding

class TotalRecordFragment : Fragment() {
    private lateinit var binding: FragmentTotalRecordBinding
    private val statisViewModel: StatisViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_total_record, container, false)

        binding.viewModel = statisViewModel
        binding.lifecycleOwner = viewLifecycleOwner


        return binding.root
    }
}
