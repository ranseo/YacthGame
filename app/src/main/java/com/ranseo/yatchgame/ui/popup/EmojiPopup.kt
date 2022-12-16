package com.ranseo.yatchgame.ui.popup

import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.ranseo.yatchgame.R

class EmojiPopup(
    val context: Context,
    val root : ViewGroup
) {
    private val popupWindow = PopupWindow(context)


    fun showPopupWindow(parent: View) {
        val layout = LayoutInflater.from(context).inflate(R.layout.popup_profile_emoji, root)
        popupWindow.contentView = layout

        popupWindow.showAsDropDown(parent)
    }
}