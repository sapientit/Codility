package uk.co.sapientit.templateapp

import kotlin.random.Random

class Iota {
    fun solution(A: IntArray): Int {
        if (A.size == 1)
            return 1
        val found = mutableMapOf<Int, Boolean>()
        found[A[0]] = true
        val target = A[A.size - 1]
        val occurences = mutableMapOf<Int,Int>()
        val next = IntArray(A.size){Int.MAX_VALUE}
        for (i in 1 until A.size) {
            val num = A[i]
            val currentPointer = occurences[num]
            if (currentPointer != null) {
                next[i] = currentPointer
            }
            occurences[num] = i
        }
        var currentList = IntArray(A.size)
        var nextList = IntArray(A.size)
        var currentLength = 1
        var nextHigh: Int
        var currentHigh = 1
        found[A[1]] = true
        if (occurences.containsKey(A[0])) {
            currentList[0] = A[0]
            nextList[0] = A[1]
            nextHigh = 1
        } else {
            currentList[0] = A[1]
            nextHigh = 0
            currentLength = 2
        }
        while (!found.containsKey(target)) {
            currentLength += 1
            for (i in 0 until currentHigh) {
                val num = currentList[i]
                var pointer = occurences[num]!!
                while (pointer < Int.MAX_VALUE) {
                    val preceding = A[pointer - 1]
                    if (!found.containsKey(preceding)) {
                        found[preceding] = true
                        nextList[nextHigh] = preceding
                        nextHigh += 1
                    }
                    val succeeding = A[pointer + 1]
                    if (!found.containsKey(succeeding)) {
                        found[succeeding] = true
                        nextList[nextHigh] = succeeding
                        nextHigh += 1
                    }
                    pointer = next[pointer]
                }

            }
            val tempList = currentList
            currentList = nextList
            nextList = tempList
            currentHigh = nextHigh
            nextHigh = 0
        }
        return currentLength

    }
    fun generateData(size: Int, rand: Random)  {
        val A = IntArray(size)
        val range = size / 2
        for (i in A.indices) {
            A[i] = rand.nextInt(0, range)
        }
        println(solution(A))
    }
}