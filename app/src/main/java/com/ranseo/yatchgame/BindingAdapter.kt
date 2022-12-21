package com.ranseo.yatchgame

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

private val TAG = "BindingAdapter"

@BindingAdapter("setSelector")
fun bindRollDice(imageView: ImageView, dice:Drawable) {
    log(TAG,"bindRollDice() : ${dice}", LogTag.I)
    imageView.setImageDrawable(dice)
}

@BindingAdapter("setProfileSrc")
fun bindProfile(imageView: ImageView, nameTag:Drawable) {
    log(TAG,"bindRollDice() : ${nameTag}", LogTag.I)
    imageView.setImageDrawable(nameTag)
}

@BindingAdapter("setEmoji")
fun bindEmoji(imageView:ImageView, emoji:Int) {
    when(emoji) {
        1 -> {imageView.setImageResource(R.drawable.emoji_laughing)}
        2 -> {imageView.setImageResource(R.drawable.emoji_love)}
        3 -> {imageView.setImageResource(R.drawable.emoji_angry)}
        4 -> {imageView.setImageResource(R.drawable.emoji_crying)}
        5 -> {imageView.setImageResource(R.drawable.emoji_surprised)}
        6 -> {imageView.setImageResource(R.drawable.emoji_thinking)}
        else -> {}
    }

}