package uk.co.sapientit.templateapp

class Upsilon {
    fun solution(A: IntArray): Int {
        var neighbour = IntArray(A.size){Int.MIN_VALUE}
        val position = IntArray(A.size + 2){0}
        val values = IntArray(A.size + 2){Int.MAX_VALUE}
        var maxChain = 0

        values[1] = Int.MIN_VALUE
        var highWater = 0
        A.forEachIndexed { index, value ->
            var min = 0
            var max = highWater + 1
            while (max - min > 1) {
                val mid = (max + min + 1) / 2
                if (value > values[mid])
                    max = mid
                else
                    min = mid
            }
            highWater -= 1
            while (highWater > min) {
                neighbour[position[highWater]] = position[highWater + 1]
                highWater -= 1
            }
            highWater = min + 1
            position[highWater] = index
            values[highWater] = value
            values[highWater + 1] = Int.MAX_VALUE
        }
        highWater -= 1
        while (highWater > 0) {
            neighbour[position[highWater]] = position[highWater + 1]
            highWater -= 1
        }
        val rightNeighbour = neighbour
        neighbour = IntArray(A.size){Int.MIN_VALUE}
        highWater = 0
        values[1] = Int.MIN_VALUE

        for (index in (0..A.size - 1).reversed()) {
            val value = A[index]
            var min = 0
            var max = highWater + 1
            while (max - min > 1) {
                val mid = (max + min + 1) / 2
                if (value > values[mid])
                    max = mid
                else
                    min = mid
            }
            highWater -= 1
            while (highWater > min) {
                neighbour[position[highWater]] = position[highWater + 1]
                highWater -= 1
            }
            highWater = min + 1
            position[highWater] = index
            values[highWater] = value
            values[highWater + 1] = Int.MAX_VALUE
        }
        highWater -= 1
        while (highWater > 0) {
            neighbour[position[highWater]] = position[highWater + 1]
            highWater -= 1
        }
        val leftNeighbour = neighbour
        val goLeft = BooleanArray(A.size){true}
        val attempt = IntArray(A.size)
        attempt[0] = position[1]
        // start with largest which will always be in pos 1
        while (highWater >= 0) {
            val pos = attempt[highWater]
            if (goLeft[highWater]) {
                if (leftNeighbour[pos] == Int.MIN_VALUE) {
                    goLeft[highWater] = false
                } else {
                    highWater += 1
                    attempt[highWater] = leftNeighbour[pos]
                    goLeft[highWater] = true
                }
            } else {
                if (rightNeighbour[pos] == Int.MIN_VALUE) {
                    maxChain = maxOf(maxChain, highWater + 1)
                    highWater -= 1
                    while (highWater >= 0 && !goLeft[highWater])
                        highWater -= 1
                    if (highWater >= 0)
                        goLeft[highWater] = false
                }else {
                    highWater += 1
                    attempt[highWater] = rightNeighbour[pos]
                    goLeft[highWater] = true

                }
            }
        }
        return maxChain
    }


}