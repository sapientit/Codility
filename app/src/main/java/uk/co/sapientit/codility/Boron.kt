package uk.co.sapientit.templateapp

class Boron {
    fun solution(A: IntArray): Int {
        var max = Math.sqrt((A.size - 2).toDouble()).toInt() + 1
        var surplus = A.size - max * max + max
        val peaks = IntArray(A.size)
        var numPeaks = getPeaks(peaks, A)

        if (numPeaks <= 2)
            return numPeaks
        var wasted = 0
        val gaps = IntArray(max + 2)
        for (i in 1..numPeaks - 1) {
            var gap = peaks[i] - peaks[i-1]
            if (gap > max) {
                wasted += gap - max
                gap = max
            }
            gaps[gap] += 1
        }
        wasted += peaks[0]
        wasted += A.size - 1 - peaks[numPeaks - 1]
        while (surplus < wasted) {
            wasted += gaps[max]
            max -= 1
            gaps[max] += gaps[max + 1]
            surplus = A.size - max * max + max
        }
        var index = max
        var totalGaps = gaps[max] + 1
        while (totalGaps < index) {
            index -= 1
            totalGaps += gaps[index]
        }
        var min = index
        max += 1 // max is known to be too high
        while (max - min > 1) {
            val mid = (max + min + 1) / 2
            if (testValidSolution(mid, peaks, numPeaks)) {
                min = mid
            } else
                max = mid
        }
        return min

    }
    fun testValidSolution(flags: Int, peaks: IntArray, numPeaks: Int) : Boolean {
        val range = numPeaks / (flags - 1)
        var startFrom = 0
        for (i in 0..flags - 2) {
            val targDistance  = peaks[startFrom] + flags
            var min = startFrom
            var max = minOf(min + range, numPeaks - 1)
            while (min < max  && peaks[max] < targDistance) {
                min = max
                max = minOf(min + range, numPeaks - 1)
            }
            if (min == max)
                return false
            while (max - min > 1) {
                val mid = (max + min + 1) / 2
                if (peaks[mid] >= targDistance) {
                    max = mid
                } else
                    min = mid
            }
            startFrom = max
        }
        return true
    }
    fun getPeaks(peaks: IntArray, A: IntArray) : Int {
        var currentHeight = A[0]
  /*      var index = 1
        while (index < A.size && A[index] <= currentHeight) {
            currentHeight = A[index]
            index += 1
        } // This preceding section takes care of a downwards slope

        if (index >= A.size)
            return 0

        currentHeight = A[index]
        index += 1

        while (index < A.size && A[index] > currentHeight) {
            currentHeight = A[index]
            index += 1
        }
        if (index >= A.size)
            return 0
        currentHeight = A[index]

        peaks[0] = index - 1 */
        var numPeaks = 0
        var onDownward = true

        for (i in 0..A.size - 1) {
            if (A[i] == currentHeight)
                onDownward = true  // Don't count a plateau
            else {
                if (A[i] > currentHeight) {
                    onDownward = false
                } else {
                    if (!onDownward) {
                        // Found peak
                        peaks[numPeaks] = i - 1
                        numPeaks += 1
                        onDownward = true
                    }
                }
            }
            currentHeight = A[i]
        }
        return numPeaks
    }
}