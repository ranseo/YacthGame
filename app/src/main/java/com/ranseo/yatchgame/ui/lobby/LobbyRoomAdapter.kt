package com.ranseo.yatchgame.ui.lobby

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.LobbyRoom
import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.databinding.ListItemRoomRobbyBinding
import javax.inject.Inject


class LobbyRoomAdapter(private val clickListener: LobbyRoomClickListener) : ListAdapter<LobbyRoom, LobbyRoomAdapter.LobbyRoomViewHolder>(LobbyRoom.getItemCallback()){

    class LobbyRoomViewHolder(private val binding:ListItemRoomRobbyBinding) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(item:LobbyRoom, clickListener: LobbyRoomClickListener) {
            val hostName= item.host.getOrDefault("host", Player("","Unknown"))

            binding.room = item
            binding.tvRoomHost.text = hostName.name
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): LobbyRoomViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemRoomRobbyBinding.inflate(layoutInflater, parent, false)
                return LobbyRoomViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyRoomViewHolder {
        return LobbyRoomViewHolder.from(parent)
    }



    override fun onBindViewHolder(holder: LobbyRoomViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }
}

class LobbyRoomClickListener(val onClickListener: (room:LobbyRoom)->Unit) {
    fun onClick(room:LobbyRoom) {
        onClickListener(room)
    }
}