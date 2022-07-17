package uk.co.sapientit.templateapp

class FastAndFurious {
    // https://app.codility.com/cert/view/cert6436U3-ED2EMSFT2E3BGRB9/

    fun solution(A: IntArray): Int {
        val endDistance = A[A.size - 1]
        var cities = A.size - 1
        var maxSaving = Long.MIN_VALUE
        var total = 0L
        for (i in A.size - 2 downTo 0) {
            val distance = (endDistance - A[i]).toLong()
            val saving = distance * cities
            maxSaving = maxOf(maxSaving, saving)
            total += (A[i + 1] - A[i]).toLong() * cities
            cities -= 1
        }
        total = (total - maxSaving) % 1000000007
        return total.toInt()
    }

}