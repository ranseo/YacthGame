package com.ranseo.yatchgame.util

import com.ranseo.yatchgame.data.model.Board
import kotlin.random.Random
import kotlin.random.nextInt

class YachtGame {
    private val smalls = arrayOf(listOf(1,2,3,4), listOf(2,3,4,5), listOf(3,4,5,6))
    private val larges = arrayOf(listOf(1,2,3,4,5), listOf(2,3,4,5,6))

    /**
     * 주사위를 k개를 굴려 나온 숫자들을 list(+keep)에 담아서 반환.
     * */
    fun rollDice(dices:Array<Int>, keepIdx:Array<Boolean>)  {
        val random = Random


        if(!keepIdx[0]) dices[0] = random.nextInt(1..6)
        if(!keepIdx[1]) dices[1] = random.nextInt(1..6)
        if(!keepIdx[2]) dices[2] = random.nextInt(1..6)
        if(!keepIdx[3]) dices[3] = random.nextInt(1..6)
        if(!keepIdx[4]) dices[4] = random.nextInt(1..6)

    }

    fun getScore(dices:List<Int>) : String {

        val numberCnt = dices.groupingBy { it }.eachCount()
        val ranks = getRankScore(dices, numberCnt)

        val result = StringBuilder()

        for((key,value) in numberCnt.toSortedMap(compareBy{ it })) {
            result.append("$key : ${value*key}\n")
        }

        result.append("choice : ${ranks[0]}\nfour of a kind : ${ranks[1]}\nfull house : ${ranks[2]}\ns.Straight : ${ranks[3]}\nl.Straight : ${ranks[4]}\nyacht : ${ranks[5]}")
        return result.toString()
    }

    private fun getRankScore(dices: List<Int>, numberCnt: Map<Int, Int>): IntArray {
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