package uk.co.sapientit.templateapp

class Silver {
    /*
    The use of a hashmap makes this simple.  Just keep a count of all the different dimensions and then find the
    maximum.  If it is square make sure you only count once.
     */
    fun solution(A: IntArray, B: IntArray): Int {
        // write your code in Kotlin 1.3.11 (Linux)
        val counts = HashMap<Int,Int>()
        for (i in A.indices) {
            if (counts.containsKey(A[i])) {
                counts[A[i]] = counts[A[i]]!! + 1
            } else {
                counts[A[i]] = 1
            }
            if (A[i] != B[i]) { // Don't count squares twice

                if (counts.containsKey(B[i])) {
                    counts[B[i]] = counts[B[i]]!! + 1
                } else {
                    counts[B[i]] = 1
                }
            }
        }
        var maxV = 0
        for (v in counts.values) {
            if (v > maxV) {
                maxV = v
            }
        }
        return maxV
    }
}