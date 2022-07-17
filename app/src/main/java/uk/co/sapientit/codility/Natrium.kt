package uk.co.sapientit.templateapp

class Natrium {
    fun solution(A: IntArray) : Int {
        val lowest = IntArray(A.size)
        val lowestIndex = IntArray(A.size){0}
        var highWater = 0
        lowest[0] = A[0]
        if (A.size == 1) {
            return 0
        }
        for (i in 1 until A.size - 1) {
            if (A[i] < lowest[highWater]) {
                highWater += 1
                lowest[highWater] = A[i]
                lowestIndex[highWater] = i
            }
        }
        var max = 0
        var index = A.size - 1
        while (index > max) {
            if (A[index] >= lowest[highWater]) {
                max = maxOf(max, index - lowestIndex[highWater])
                highWater -= 1
            } else {
                index -= 1
            }
        }
        return max
    }
}