package uk.co.sapientit.templateapp

class Beta {
    fun solution(A: IntArray): Int {
        val leftEdge = IntArray(A.size){0}
        val active = IntArray(A.size)
        for (i in A.size -1 downTo 0) {
            val edge = maxOf(0L, i.toLong()- A[i])
            leftEdge[edge.toInt()] += 1
        }
        var totalEdges = 0
        for (i in 0 until A.size) {
            totalEdges += leftEdge[i]
            active[i] = totalEdges
        }
        var total = 0L
        for (i in 0 until A.size) {
            val rightEdge = minOf((A.size - 1).toLong(), i.toLong() + A[i])

            total += active[rightEdge.toInt()] - i - 1
        }
        if (total > 10000000)
            return -1
        return total.toInt()
    }
}