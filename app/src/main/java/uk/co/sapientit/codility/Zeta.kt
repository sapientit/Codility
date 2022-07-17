package uk.co.sapientit.templateapp

class Zeta {
    fun solution(A: Array<IntArray>, K: Int): Int {
        val current = IntArray(A[0].size)
        current[0] = K
        for (i in 0 until A.size) {
            var right = 0
            for (j in 0 until A[0].size) {
                if (A[i][j] == -1) {
                    val total = current[j] + right
                    current[j] = (total + 1) / 2
                    right = total / 2
                } else {
                    if (A[i][j] == 1) {
                        val total = current[j] + right
                        right = (total + 1) / 2
                        current[j] = total / 2
                    }
                }
            }
        }
        return current[A[0].size - 1]
    }
}