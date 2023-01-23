package com.ranseo.yatchgame.ui.lobby.statis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.OnBoardClickListener
import com.ranseo.yatchgame.databinding.FragmentBestRecordBinding


class BestRecordFragment : Fragment() {
    private lateinit var binding: FragmentBestRecordBinding

    private val statisViewModel : StatisViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_best_record, container,false)
        binding.viewModel = statisViewModel
        binding.lifecycleOwner = viewLifecycleOwner


        with(binding) {
            onClickListener = OnBoardClickListener {  }
        }
        return binding.root
    }
}