package com.ranseo.yatchgame.ui.lobby.statis

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ranseo.yatchgame.databinding.LayoutBestRecordBinding
import com.ranseo.yatchgame.databinding.LayoutHallOfFameBinding
import com.ranseo.yatchgame.databinding.LayoutRelativeRecordBinding
import com.ranseo.yatchgame.databinding.LayoutTotalRecordBinding

class StatisViewPagerAdapter() : ListAdapter<StatisDataItem, RecyclerView.ViewHolder>(StatisDataItemCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is StatisDataItem.TotalRecordDataItem -> VIEW_TYPE_TOTAL_RECORD
            is StatisDataItem.BestRecordDataItem -> VIEW_TYPE_BEST_RECORD
            is StatisDataItem.RelativeRecordDataItem -> VIEW_TYPE_RELATIVE_RECORD
            is StatisDataItem.HallOfFameDataItem-> VIEW_TYPE_HALL_OF_FAME
            else -> super.getItemViewType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_TOTAL_RECORD -> TotalRecordViewHolder.from(parent)
            VIEW_TYPE_BEST_RECORD -> BestRecordViewHolder.from(parent)
            VIEW_TYPE_RELATIVE_RECORD -> RelativeRecordViewHolder.from(parent)
            VIEW_TYPE_HALL_OF_FAME -> HallOfFameViewHolder.from(parent)
            else -> TotalRecordViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is TotalRecordViewHolder -> holder.bind( getItem(position) as StatisDataItem.TotalRecordDataItem)
            is BestRecordViewHolder -> holder.bind(getItem(position) as StatisDataItem.BestRecordDataItem)
            is RelativeRecordViewHolder -> holder.bind(getItem(position) as StatisDataItem.RelativeRecordDataItem)
            is HallOfFameViewHolder -> holder.bind(getItem(position) as StatisDataItem.HallOfFameDataItem)
            else -> throw IllegalArgumentException("UNKNOWN HOLDER")
        }

    }

    class TotalRecordViewHolder(val binding: LayoutTotalRecordBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item:StatisDataItem.TotalRecordDataItem) {

        }
        companion object {
            fun from(parent:ViewGroup) : TotalRecordViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LayoutTotalRecordBinding.inflate(layoutInflater, parent, false)
                return TotalRecordViewHolder(binding)
            }
        }
    }
    class BestRecordViewHolder(val binding:LayoutBestRecordBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item:StatisDataItem.BestRecordDataItem) {

        }
        companion object {
            fun from(parent:ViewGroup) : BestRecordViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LayoutBestRecordBinding.inflate(layoutInflater, parent, false)
                return BestRecordViewHolder(binding)
            }
        }
    }
    class RelativeRecordViewHolder(val binding: LayoutRelativeRecordBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item:StatisDataItem.RelativeRecordDataItem) {

        }
        companion object {
            fun from(parent:ViewGroup) : RelativeRecordViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LayoutRelativeRecordBinding.inflate(layoutInflater, parent, false)
                return RelativeRecordViewHolder(binding)
            }
        }
    }
    class HallOfFameViewHolder(val binding: LayoutHallOfFameBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item:StatisDataItem.HallOfFameDataItem) {

        }
        companion object {
            fun from(parent:ViewGroup) : HallOfFameViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LayoutHallOfFameBinding.inflate(layoutInflater, parent, false)
                return HallOfFameViewHolder(binding)
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_TOTAL_RECORD = 0
        private const val VIEW_TYPE_BEST_RECORD = 1
        private const val VIEW_TYPE_RELATIVE_RECORD = 2
        private const val VIEW_TYPE_HALL_OF_FAME = 3
    }
}

class StatisDataItemCallback() : DiffUtil.ItemCallback<StatisDataItem>() {
    override fun areItemsTheSame(oldItem: StatisDataItem, newItem: StatisDataItem): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: StatisDataItem, newItem: StatisDataItem): Boolean = oldItem == newItem

}

sealed class StatisDataItem() {
    class TotalRecordDataItem() : StatisDataItem() {

    }
    class BestRecordDataItem() : StatisDataItem() {

    }
    class RelativeRecordDataItem() : StatisDataItem() {

    }
    class HallOfFameDataItem() : StatisDataItem() {

    }
}