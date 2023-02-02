package com.ranseo.yatchgame.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class AlertDialog(
    private val title: String,
    private val description : CharSequence,
    private val posMsg: String,
    private val negMsg: String

) : DialogFragment() {

    internal lateinit var onClickListener: OnAlertDialogListener

    interface OnAlertDialogListener {
        fun onPositiveBtn() {}
        fun onNegativeBtn() {}
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireParentFragment().let {
            try {
                val builder = AlertDialog.Builder(it.context)
                builder?.setTitle(title)
                    ?.setPositiveButton(posMsg) { dialogInterface, i ->
                        onClickListener.onPositiveBtn()
                        this.dismiss()
                    }
                    ?.setNegativeButton(negMsg) { dialogInterface, i ->
                        onClickListener.onNegativeBtn()
                        this.dismiss()
                    }
                    ?.setMessage(description)
                    ?.create()

            } catch (error: Exception) {
                throw IllegalArgumentException("error : ${error.message}")
            }
        } ?: throw IllegalStateException("Fragment cannot be null")
    }

    fun setOnClickListener(listener: OnAlertDialogListener) {
        onClickListener = listener
    }
}