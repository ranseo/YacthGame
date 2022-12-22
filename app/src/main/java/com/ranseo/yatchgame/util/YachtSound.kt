package com.ranseo.yatchgame.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import com.ranseo.yatchgame.R
import javax.inject.Inject

class YachtSound @Inject constructor() {

    fun initSound(context:Context) {
        mediaPlayerMap = HashMap(6)

        mediaPlayerMap[ROLL_DICE_SOUND] = MediaPlayer.create(context, ROLL_DICE_SOUND)
        mediaPlayerMap[BOARD_CONFIRM_SOUND] = MediaPlayer.create(context, BOARD_CONFIRM_SOUND)
        mediaPlayerMap[KEEP_SOUND] = MediaPlayer.create(context, KEEP_SOUND)
        mediaPlayerMap[MY_TURN_SOUND] = MediaPlayer.create(context, MY_TURN_SOUND)
        mediaPlayerMap[BGM_CHRISMAS] = MediaPlayer.create(context, BGM_CHRISMAS).apply {
            this.isLooping = true
        }
        mediaPlayerMap[YACHT_SOUND] = MediaPlayer.create(context, YACHT_SOUND)

    }

    fun play(raw_id:Int) {
        if(mediaPlayerMap.containsKey(raw_id)) {
            mediaPlayerMap[raw_id]?.start()
        }
    }

    fun release() {
        for(value in mediaPlayerMap.values) {
            value.release()
        }
    }

    fun muteBgm(raw_id:Int) {
        if(mediaPlayerMap.containsKey(raw_id)) mediaPlayerMap[BGM_CHRISMAS]?.pause()
    }

    companion object {
        private lateinit var mediaPlayerMap : HashMap<Int,MediaPlayer>
        const val ROLL_DICE_SOUND = R.raw.roll_dice
        const val BOARD_CONFIRM_SOUND = R.raw.click_sound
        const val KEEP_SOUND = R.raw.keep_sound
        const val MY_TURN_SOUND = R.raw.my_turn_sound
        const val BGM_CHRISMAS = R.raw.chrismas_sound
        const val YACHT_SOUND = R.raw.yacht_sound
    }



}