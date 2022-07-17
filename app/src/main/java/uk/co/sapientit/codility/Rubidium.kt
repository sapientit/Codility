package uk.co.sapientit.templateapp

class Rubidium {
    /*
    First notice that with N sheep, the biggest sunshade is obtained if they are arranged in a square grid.  So
    take the square root of N amd divide that into 100000 to give the biggest shade.  That gives us a starting point.
    Create a sum of X and Y and sort into that sequence.
    If the current sun shade is too big then that is because of two sheep that have a sum that is less than twice the
    maximum sum. (For a sheep at X/Y with a current maximum of 10, then a new sheep at X +9/ Y + 9 has a sum of 18 more than
    the current sheep - less than 20 (twice current sum)
    We only need to consider sheep with greater sum if we start from the botth left corner
    So for each sheep, look through all subsequent sheep until the sum is > 2 * max - 1
    If both delta X/Y are less than max then the max will be the greatest of delta X and delta Y.
    The answer is ha;f the maximum since they want the size from the sheep outwards, not total width.
     */
    fun solution(X: IntArray, Y: IntArray): Int {
        val sum = IntArray(X.size){X[it] + Y[it]}
        val comparator = Comparator<Int>{
            s1,s2 ->
            sum[s1]- sum[s2]
        }
        val sorted = IntArray(X.size){it}.sortedWith(comparator).toIntArray()
        val sheepSqrt = Math.sqrt(X.size.toDouble()).toInt()
        var max = 100000 / sheepSqrt
        for (i in 0..X.size - 2) {
            val current = sorted[i]
            var index = i + 1
            var next = sorted[index]
            var maxSum = sum[current] + (max - 1) * 2
            while (index < X.size && sum[next] <= maxSum) {
                val xDelta = Math.abs(X[current] - X[next])
                val yDelta = Math.abs(Y[current] - Y[next])
                if (xDelta < max && yDelta < max) {
                    max = maxOf(xDelta, yDelta)
                    maxSum = sum[current] + (max - 1) * 2
                }
                index += 1
                if (index < X.size)
                    next = sorted[index]

            }
        }
        return max / 2
    }
}