package uk.co.sapientit.templateapp

class Chi {
    fun solution(A: IntArray, B: IntArray): IntArray {
        val heights = A.clone()
        var min = Int.MIN_VALUE
        heights.forEachIndexed {
            index,item ->
            if (item > min) {
                min = item
            } else
                heights[index] = min
        }
        B.forEach {
            var min = 0
            var max = heights.size - 1
            if (it <= heights[max] && it > heights[0]) {
                while (max - min > 1) {
                    val mid = (min + max + 1) / 2
                    if (it <= heights[mid])
                        max = mid
                    else
                        min = mid
                }
                A[min] += 1
                if (A[min] > heights[min])
                    heights[min] = A[min]
            }
        }
        return A
    }
}