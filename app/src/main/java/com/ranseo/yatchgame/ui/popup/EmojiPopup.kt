package com.ranseo.yatchgame.ui.popup

import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.databinding.PopupProfileEmojiBinding

class EmojiPopup(
    val context: Context,
    private val emojiPopupClickListener: OnEmojiPopupClickListener
) {

    interface OnEmojiPopupClickListener {
        fun onEmojiClick(emoji: Int)
    }

    private val popupWindow = PopupWindow(context)

    fun showPopupWindow(parent: ViewGroup, view: View) {

        val layoutInflater = LayoutInflater.from(context)
        val binding = PopupProfileEmojiBinding.inflate(layoutInflater, parent, false)
        popupWindow.contentView = binding.root
        popupWindow.isOutsideTouchable = true

        binding.layoutLaughing.setOnClickListener {
            emojiPopupClickListener.onEmojiClick(LAUGHING)
            popupWindow.dismiss()
        }
        binding.layoutLove.setOnClickListener {
            emojiPopupClickListener.onEmojiClick(LOVE)
            popupWindow.dismiss()
        }
        binding.layoutAngry.setOnClickListener {
            emojiPopupClickListener.onEmojiClick(ANGRY)
            popupWindow.dismiss()
        }
        binding.layoutCrying.setOnClickListener {
            emojiPopupClickListener.onEmojiClick(CRYING)
            popupWindow.dismiss()
        }
        binding.layoutSurprised.setOnClickListener {
            emojiPopupClickListener.onEmojiClick(SURPRISED)
            popupWindow.dismiss()
        }
        binding.layoutThinking.setOnClickListener {
            emojiPopupClickListener.onEmojiClick(THINKING)
            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(view)

    }


    companion object {
        const val LAUGHING = 1
        const val LOVE = 2
        const val ANGRY = 3
        const val CRYING = 4
        const val SURPRISED = 5
        const val THINKING = 6
    }
}