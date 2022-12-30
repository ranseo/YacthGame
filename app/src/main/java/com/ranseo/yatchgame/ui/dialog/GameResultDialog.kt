package com.ranseo.yatchgame.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.ranseo.yatchgame.R

class GameResultDialog(
    context: Context,
    private val firstScore : String,
    private val firstPlayerName:String,
    private val secondScore :String,
    private val secondPlayerName:String,
    private val gameResult:String,
    private val isRematch:Boolean
) {
    interface OnGameResultDialogClickListener {
        fun onExitBtn()
        fun onRematchBtn()
    }

    private lateinit var onClickListener: OnGameResultDialogClickListener

    private val dialog = Dialog(context)


    fun showDialog() {
        dialog.setContentView(R.layout.dialog_game_result)

        val exitBtn = dialog.findViewById<Button>(R.id.btn_exit)
        val rematchBtn = dialog.findViewById<Button>(R.id.btn_rematch)
        val gameResultView = dialog.findViewById<TextView>(R.id.tv_game_result)
        val firstScoreView = dialog.findViewById<TextView>(R.id.tv_first_player_score)
        val firstNameView =  dialog.findViewById<TextView>(R.id.tv_first_player_name)
        val secondScoreView = dialog.findViewById<TextView>(R.id.tv_second_player_score)
        val secondNameView =  dialog.findViewById<TextView>(R.id.tv_second_player_name)

        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        //dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        gameResultView.text = gameResult

        firstScoreView.text = firstScore
        firstNameView.text = firstPlayerName

        secondScoreView.text = secondScore
        secondNameView.text = secondPlayerName

        rematchBtn.isEnabled = isRematch

        exitBtn.setOnClickListener {
            onClickListener.onExitBtn()
            dialog.dismiss()
        }

        rematchBtn.setOnClickListener {
            onClickListener.onRematchBtn()
            dialog.dismiss()
        }

        dialog.show()
    }

    fun setOnClickListener(listener: OnGameResultDialogClickListener) {
        onClickListener = listener
    }

    fun dismiss() {
        dialog.dismiss()
    }

}