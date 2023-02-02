package com.ranseo.yatchgame.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.ranseo.yatchgame.R

class EditTextDialog(
    context: Context,
    private val titleText: String = "",
    private val hint: String = ""
) {
    interface OnEditTextClickListener {
        fun onPositiveBtn(text: String) {}
        fun onNegativeBtn() {}
    }

    private lateinit var onClickListener: OnEditTextClickListener

    private val dialog = Dialog(context)


    fun showDialog() {
        dialog.setContentView(R.layout.dialog_edit_text)

        val okBtn = dialog.findViewById<Button>(R.id.btn_accept)
        val cancelBtn = dialog.findViewById<Button>(R.id.btn_cancel)
        val editText = dialog.findViewById<EditText>(R.id.et_room_name)
        val title = dialog.findViewById<TextView>(R.id.tv_title)
        val textInputLayout = dialog.findViewById<TextInputLayout>(R.id.layout_room_name)

        title.text = titleText
        textInputLayout.hint = hint

        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        //dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        okBtn.setOnClickListener {
            onClickListener.onPositiveBtn(editText.text.toString())
            dialog.dismiss()
        }

        cancelBtn.setOnClickListener {
            onClickListener.onNegativeBtn()
            dialog.dismiss()
        }

        dialog.show()
    }

    fun setOnClickListener(listener: OnEditTextClickListener) {
        onClickListener = listener
    }

}