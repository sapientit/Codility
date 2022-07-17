package uk.co.sapientit.templateapp

class Matrix {
    /*
    We maintain a list of maximums, and the count for these maximums.  If we find a lower number then
    we go down through these maximums adding the counts as we go until we find a value where the count >= the maximum.
    Note that in the case 1,6,6,1, the result is 2 although the number 2 never appears.  As we are going down through the
    maximums we have to take the minimum of the count and the value and try that out as a new maximum.
    Finally we reach the end and we go down until we get to a value lower than the maximum.
     */
    fun solution(A: IntArray) : Int {
        var max = 1
        val count = IntArray(A.size + 1)
        var high = 1
        val maxes = IntArray(A.size + 1)
        var highWater = 0
        maxes[0] = 1
        A.forEach {

            var excessCount = 0
            while (it < high) {
                val newMax = minOf(count[highWater] + excessCount, maxes[highWater])
                max = maxOf(max, newMax)
                excessCount += count[highWater]
                highWater -= 1
                high = maxes[highWater]

            }
            if (it > high ) {
                highWater += 1
                maxes[highWater] = it
                count[highWater] = 1 + excessCount
                high = it
            } else {
                count[highWater] += 1 + excessCount
            }
        }
        var excess = 0
        while (high > max) {
            val newMax = minOf(count[highWater] + excess, maxes[highWater])

            max = maxOf(max, newMax)
            excess += count[highWater]

            highWater -= 1
            high = maxes[highWater]
        }
        return max
    }

}