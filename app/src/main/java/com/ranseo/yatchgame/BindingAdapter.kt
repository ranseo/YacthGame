package com.ranseo.yatchgame

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

private val TAG = "BindingAdapter"

@BindingAdapter("setSelector")
fun bindRollDice(imageView: ImageView, dice:Drawable) {
    log(TAG,"bindRollDice() : ${dice}", LogTag.I)
    imageView.setImageDrawable(dice)
}