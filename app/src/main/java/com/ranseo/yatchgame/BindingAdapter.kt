package com.ranseo.yatchgame

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

private val TAG = "BindingAdapter"

@BindingAdapter("diceSrc")
fun bindRollDice(imageView: ImageView, diceSrc:Int) {
    log(TAG,"bindRollDice() : ${diceSrc}", LogTag.I)
    when(diceSrc) {
        1 -> {imageView.setImageResource(R.drawable.selector_roll_dice_first)}
        2 -> {imageView.setImageResource(R.drawable.selector_roll_dice_second)}
        3 -> {imageView.setImageResource(R.drawable.selector_roll_dice_third)}
        4 -> {imageView.setImageResource(R.drawable.selector_roll_dice_fourth)}
        5 -> {imageView.setImageResource(R.drawable.selector_roll_dice_fifth)}
        6 -> {imageView.setImageResource(R.drawable.selector_roll_dice_sixth)}
    }
}

@BindingAdapter("diceFix")
fun bindFixDice(imageView:ImageView, diceFix:Boolean) {
    imageView.isSelected = diceFix
}

@BindingAdapter("setProfileSrc")
fun bindProfile(imageView: ImageView, nameTag:Int) {
    log(TAG,"bindProfile() : ${nameTag}", LogTag.I)
    when(nameTag) {
        1 -> {imageView.setImageResource(R.drawable.name_tag_cat)}
        2 -> {imageView.setImageResource(R.drawable.name_tag_bear)}
        3 -> {imageView.setImageResource(R.drawable.name_tag_deer)}
        4 -> {imageView.setImageResource(R.drawable.name_tag_frog)}
        5 -> {imageView.setImageResource(R.drawable.name_tag_rabbit)}
        else -> {imageView.setImageResource(R.drawable.name_tag_rabbit)}
    }
}

@BindingAdapter("setProfileSrcReverse")
fun bindProfileReverse(imageView: ImageView, nameTag:Int) {
    log(TAG,"bindProfileReverse() : ${nameTag}", LogTag.I)
    when(nameTag) {
        1 -> {imageView.setImageResource(R.drawable.name_tag_cat_reverse)}
        2 -> {imageView.setImageResource(R.drawable.name_tag_bear_reverse)}
        3 -> {imageView.setImageResource(R.drawable.name_tag_deer_reverse)}
        4 -> {imageView.setImageResource(R.drawable.name_tag_frog_reverse)}
        5 -> {imageView.setImageResource(R.drawable.name_tag_rabbit_reverse)}
        else -> {imageView.setImageResource(R.drawable.name_tag_rabbit_reverse)}
    }
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