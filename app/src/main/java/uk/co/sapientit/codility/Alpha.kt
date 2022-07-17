package uk.co.sapientit.templateapp

class Alpha {
    fun solution(A: IntArray): Int{
        val countNum = IntArray(1000000){0}
        for (i in 0 until A.size) {
            countNum[A[i]] += 1
        }
        for (i in A.size - 1 downTo 0) {
            countNum[A[i]] -= 1
            if (countNum[A[i]] == 0)
                return i
        }
        return 0
    }
}