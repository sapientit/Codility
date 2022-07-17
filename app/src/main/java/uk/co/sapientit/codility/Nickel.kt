package uk.co.sapientit.templateapp

class Nickel {
    /*
    Note that the description is wrong - P is BooleanArray, not Array<Boolean>
    Each series of false inputs creates a triangle.  So it is the big triangle - all the little triangles.
    The Triangle is sum of all numbers up to N = N(N+1)/2
     */
    fun solution(P: BooleanArray): Int {
        var total = P.size.toLong() * (P.size + 1) / 2
        var count = 0L
        P.forEach {
            if (!it)
                count += 1
            else {
                total -= (count * (count + 1) / 2)
                count = 0
            }
        }
        total = (total - count * (count + 1) / 2)
        if (total > 1000000000)
            return 1000000000
        return total.toInt()
    }
}