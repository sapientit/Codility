package uk.co.sapientit.templateapp

import kotlin.random.Random

class Lambda {
    fun solution(T: IntArray): Int {


        val counts = IntArray(T.size)
        T.forEach {
            counts[it] += 1
        }
        val distance = LongArray(T.size)  // 100,000 in a line gives overflow with Int
        val nodes = IntArray(T.size)
        val connected = IntArray(T.size)
        var highWater = 0
        for (i in counts.indices) {
            if (counts[i] == 0) {
                nodes[highWater] = i
                highWater += 1
            }
        }
        var index = 0
        while (index < highWater) {
            val current = nodes[index]
            val target = T[current]
            counts[target] -= 1
            if (counts[target] == 0) {
                nodes[highWater] = target
                highWater += 1
            }
            distance[target] += distance[current] + connected[current] + 1
            connected[target] += connected[current] + 1
            index += 1
        }
        while (index > 0) {
            index -= 1
            val current = nodes[index]
            val parent = T[current]
            val addition = distance[parent] + connected[parent] + 1
            val subtraction = distance[current] +  2 * connected[current] + 2
            distance[current] += addition - subtraction
            connected[current] = connected[parent]

        }
        var min = Long.MAX_VALUE
        var minN = -1
        for (i in distance.indices) {
            if (distance[i] < min) {
                min = distance[i]
                minN = i
            }
          //  println(distance[i])
        }
        return minN


    }
    fun generateLineData(length: Int, rand: Random) : Boolean {
        val mid = rand.nextInt(0, length)
        val data = IntArray(length) {
            if (it == mid) {
                it
            } else {
                if (it > mid)
                    it - 1
                else
                    it + 1
            }

        }
        val expected = (length -1) / 2
        return (solution(data) == expected)
    }
}