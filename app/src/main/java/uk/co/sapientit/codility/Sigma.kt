package uk.co.sapientit.templateapp

class Sigma {
    fun solutionSigma(H: IntArray): Int {
        var highWater = 0
        val rects = IntArray(H.size + 1){0}
        var total = 0
        H.forEach{
            var min = 0
            var max = highWater
            while (max  - min > 1) {
                val mid = (min + max + 1) /2
                if (it >= rects[mid])
                    min = mid
                else
                    max = mid
            }
            if (it >= rects[max])
                min = max
            total += highWater - min
            if (it > rects[min]) {
                highWater = min + 1
                rects[highWater] = it
            } else
                highWater = min
        }
        total += highWater
        return total
    }

}