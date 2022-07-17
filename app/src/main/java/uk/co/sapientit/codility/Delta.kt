package uk.co.sapientit.templateapp

class Delta {
    fun solution(A: IntArray): Int {
        var total = 0
        val counts = IntArray(101)
        for (i in 0 until A.size) {
            A[i] = Math.abs(A[i])
            total += A[i]
            counts[A[i]] += 1
        }
        val possible = IntArray(total + 1){-1}
        possible[0] = 0
        var max = 0
        for (i in 1..100) {
            if (counts[i] > 0) {
                max = minOf(total / 2, max + i * counts[i])
                for (j in 0..max) {
                    if (possible[j] == 0)
                        possible[j] = counts[i] + 1
                    if (possible[j] > 1) {
                        val target = j + i
                        if (possible[target] == -1)
                            possible[target] = possible[j] - 1
                        possible[j] = 0
                    } else {
                        if (possible[j] == 1) {
                            possible[j] = 0
                        }
                    }
                }
            }
        }
        for (i in total / 2 downTo 0)
            if (possible[i] != -1)
                return (total - i - i)
        return 0
    }

}