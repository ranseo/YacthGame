package com.ranseo.yatchgame.ui.lobby

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.LobbyRoom
import com.ranseo.yatchgame.databinding.ListItemRoomRobbyBinding
import javax.inject.Inject


class LobbyRoomAdapter(val clickListener: LobbyRoomClickListener) : ListAdapter<LobbyRoom, LobbyRoomAdapter.LobbyRoomViewHolder>(LobbyRoom.getItemCallback()){

    class LobbyRoomViewHolder(private val binding:ListItemRoomRobbyBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item:LobbyRoom) {
            binding.room = item
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
        holder.bind(item)
    }
}

class LobbyRoomClickListener(val onClickListener: (room:LobbyRoom)->Unit) {
    fun onClick(room:LobbyRoom) {
        onClickListener(room)
    }
}