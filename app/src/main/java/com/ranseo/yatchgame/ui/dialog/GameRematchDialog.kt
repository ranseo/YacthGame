package com.ranseo.yatchgame.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.ranseo.yatchgame.R

class GameRematchDialog(
    context: Context,
    private val rematchText:String
) {

    interface OnGameRematchDialogClickListener {
        fun onAccept()
        fun onDecline()
    }

    private lateinit var onClickListener: OnGameRematchDialogClickListener

    private val dialog = Dialog(context)


    fun showDialog() {
        dialog.setContentView(R.layout.dialog_rematch)

        val gameRematchView = dialog.findViewById<TextView>(R.id.tv_rematch)
        val acceptBtn = dialog.findViewById<Button>(R.id.btn_accept)
        val declineBtn = dialog.findViewById<Button>(R.id.btn_decline)

        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        dialog.setCancelable(true)

        gameRematchView.text = rematchText


        acceptBtn.setOnClickListener {
            onClickListener.onAccept()
            dialog.dismiss()
        }

        declineBtn.setOnClickListener {
            onClickListener.onDecline()
            dialog.dismiss()
        }

        dialog.show()
    }

    fun setOnClickListener(listener : OnGameRematchDialogClickListener) {
        onClickListener = listener
    }
}