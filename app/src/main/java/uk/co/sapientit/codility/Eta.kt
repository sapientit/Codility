package uk.co.sapientit.templateapp

class Eta {
    fun solution(A: IntArray): Int {
        val lowest = IntArray(A.size + 1)
        val highest = IntArray(A.size + 1)
        val M = A.size/2 + 1
        lowest[0] - Int.MIN_VALUE
        highest[0] - Int.MIN_VALUE
        val count = IntArray(M)
        count[A[0]] += 1
        var highwater = 0
        for (i in 1 until A.size) {
            if (A[i] == A[i - 1])
                return -2
            val high: Int
            val low: Int
            count[A[i]] += 1
            if (A[i] > A[i - 1]) {
                high = A[i]
                low = A[i - 1]
            } else {
                high = A[i - 1]
                low = A[i]
            }
            if (lowest[highwater] == low && highest[highwater] == high)
                highwater -= 1
            else {
                highwater += 1
                lowest[highwater]= low
                highest[highwater] = high
            }
        }
        if (highwater > 1)
            return -2
        count.forEach {
            if (it != 1 && it != 3)
                return -2
        }
        return 3
    }
}