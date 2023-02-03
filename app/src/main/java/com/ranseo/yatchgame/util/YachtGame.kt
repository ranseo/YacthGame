package com.ranseo.yatchgame.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.ranseo.yatchgame.data.model.Board
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

//import java.security.SecureRandom

class YachtGame @Inject constructor() {
    private val smalls = arrayOf(listOf(1,2,3,4), listOf(2,3,4,5), listOf(3,4,5,6))
    private val larges = arrayOf(listOf(1,2,3,4,5), listOf(2,3,4,5,6))

    /**
     * 주사위를 k개를 굴려 나온 숫자들을 list(+keep)에 담아서 반환.
     * */
    suspend fun rollDice(dices:Array<Int>, keepIdx:Array<Boolean>, callback: suspend ()->Unit) : Array<Int> = withContext(Dispatchers.Default) {
        //val random = SecureRandom.getInstanceStrong()
        val list1 = listOf(1,2,3,4,5,6)
        val list2 = listOf(1,2,3,4,5,6)
        val list3 = listOf(1,2,3,4,5,6)
        val list4 = listOf(1,2,3,4,5,6)
        val list5 = listOf(1,2,3,4,5,6)

        val array = arrayOf(
            if(!keepIdx[0]) list1.shuffled().first() else dices[0],
            if(!keepIdx[1]) list2.shuffled().first() else dices[1],
            if(!keepIdx[2]) list3.shuffled().first() else dices[2],
            if(!keepIdx[3]) list4.shuffled().first() else dices[3],
            if(!keepIdx[4]) list5.shuffled().first() else dices[4])


//        if(!keepIdx[0]) array[0] = list1.shuffled().first()
//        if(!keepIdx[1]) array[1] = list2.shuffled().first()
//        if(!keepIdx[2]) array[2] = list3.shuffled().first()
//        if(!keepIdx[3]) array[3] = list4.shuffled().first()
//        if(!keepIdx[4]) array[4] = list5.shuffled().first()


        //callback()

        array
    }

    fun getScore(dices:Array<Int>) : Board {

        val numberCnt = dices.groupingBy { it }.eachCount()
        val ranks = getRankScore(dices, numberCnt)

        val result = IntArray(15){0}

        for((key,value) in numberCnt.toSortedMap(compareBy{ it })) {
            result[key-1] = (value*key)
        }

        result[8] = ranks[0]//choice
        result[9] = ranks[1]//fourCard
        result[10] = ranks[2]//fullHouse
        result[11] = ranks[3]//s.straight
        result[12] = ranks[4]//l.straight
        result[13] = ranks[5]//yacht


        //idx 6 = sum , idx 7 = bonus , idx 14 = total


        return Board(result)
    }

    private fun getRankScore(dices: Array<Int>, numberCnt: Map<Int, Int>): IntArray {
        var ranks = intArrayOf()

        //1.chocie
        ranks += dices.sumOf { it }

        //2.4 of a kind
        val kind = numberCnt.filter { it.value >= 4 }.takeIf { it.isNotEmpty() }?.map { it.key }?.first() ?: 0
        ranks += if (kind > 0) {
            val notKind = dices.filter{ it != kind }.takeIf { it.isNotEmpty() }?.first() ?: kind
            (kind*4) + notKind
        } else 0

        //3.full house
        val triple = numberCnt.filter { it.value == 3}.takeIf { it.isNotEmpty() }?.map { it.key }?.first() ?: 0
        val twoPair = numberCnt.filter { it.value == 2}.takeIf { it.isNotEmpty() }?.map{it.key}?.first() ?: 0
        ranks += if(triple>0 && twoPair >0) triple*3 + twoPair*2 else 0

        //4.small Straight
        val s = dices.sorted().takeIf { it.containsAll(smalls[0]) || it.containsAll(smalls[1]) || it.containsAll(smalls[2])} !=null
        ranks += if(s) 15 else 0

        //5.large Straight
        val l = dices.sorted().takeIf { it.containsAll(larges[0]) || it.containsAll(larges[1]) } != null
        ranks += if(l) 30 else 0

        //6.yacht
        val yacht = numberCnt.filter { it.value == 5}.takeIf { it.isNotEmpty() } != null
        ranks += if(yacht) 50 else 0


        return ranks
    }
}