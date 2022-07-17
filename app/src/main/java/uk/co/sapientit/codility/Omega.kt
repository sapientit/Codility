package uk.co.sapientit.templateapp

class Omega {
    fun solution(A: IntArray, B: IntArray): Int {
        val well = IntArray(A.size + 1)
        var minSize = Int.MAX_VALUE
        for (i in 0..A.size -1) {
            well[i] = minOf(minSize,A[i])
            minSize = well[i]
        }
        var maxDepth = A.size
        for (i in 0..B.size - 1) {

            well[maxDepth] = 0
            if (well[0] < B[i])
                return i

            var min = 0
            var max = maxDepth
            while (max -min > 1) {
                val mid = (max + min + 1) /2
                if (B[i] > well[mid])
                    max = mid
                else
                    min = mid
            }
            maxDepth = min
        }
        return B.size

    }

}