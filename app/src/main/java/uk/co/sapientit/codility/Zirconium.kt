package uk.co.sapientit.templateapp

class Zirconium {
    /*
    We want the F programmers that have the greatest A[i] - B[i].  So we just sort them into tat sequence and
    read the first F of them.  For efficiency on the sort it would have been better to have create a new index of differences
    but this keeps it simpler.
     */
    fun solution(A: IntArray, B: IntArray, F: Int): Int {
        val comparator = Comparator<Int>{p1,p2 -> (A[p2] - A[p1]) - (B[p2] - B[p1])}
        val sorted = IntArray(A.size){it}.sortedWith(comparator)
        var total = 0
        for (i in 0 until sorted.size) {
            if (i < F) {
                total += A[sorted[i]]
            } else
                total += B[sorted[i]]
        }
        return total

    }
}