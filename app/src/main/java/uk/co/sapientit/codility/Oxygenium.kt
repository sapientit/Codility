package uk.co.sapientit.templateapp

class Oxygenium {
    lateinit var minimums: IntArray
    lateinit var maximums: IntArray
    var minHighWater = 1
    var maxHighWater = 1
    var minLowWater = 0
    var maxLowWater = 0
    fun solution(K: Int, A: IntArray): Int{
        if (A.size == 1)
            return 1
        minimums = IntArray(A.size)
        maximums = IntArray(A.size)
        minimums[0] = 0
        maximums[0] = 0
        var range = -1
        var count = 1L
        for (i in 1 until A.size) {
            val current = A[i]
            if (current < A[minimums[minLowWater]]) {
                minimums[0] = i
                minLowWater = 0
                minHighWater = 1
                minimums[1] = Int.MAX_VALUE
                if (current + K < A[maximums[maxLowWater]]) {
                    if (current + K < A[maximums[maxHighWater - 1]]) {
                        maximums[0] = i
                        maxLowWater = 0
                        maxHighWater = 1
                        range = i - 1
                    } else {
                        maxLowWater = findMaxValue(current + K, A) + 1
                        range = maximums[maxLowWater - 1]
                        maximums[maxHighWater] = i
                        maxHighWater += 1
                    }

                } else {
                    maximums[maxHighWater] = i
                    maxHighWater += 1
                }
            } else {
                if (current > A[maximums[maxLowWater]]) {
                    maximums[0] = i
                    maxLowWater = 0
                    maxHighWater = 1
                    if (current - K > A[minimums[minLowWater]]) {
                        if (current - K > A[minimums[minHighWater - 1]]) {
                            minimums[0] = i
                            minLowWater = 0
                            minHighWater = 1
                            range = i - 1
                        } else {
                            minLowWater = findMinValue(current - K, A) + 1
                            range = minimums[minLowWater - 1]
                            minimums[minHighWater] = i
                            minHighWater += 1
                        }

                    } else {
                        minimums[minHighWater] = i
                        minHighWater += 1
                    }
                } else {
                    val minInsertPoint = findMinValue(current, A)
                    minimums[minInsertPoint + 1] = i
                    minHighWater = minInsertPoint + 2
                    val maxInsertPoint = findMaxValue(current, A)
                    maximums[maxInsertPoint + 1] = i
                    maxHighWater = maxInsertPoint + 2
                    // this is within range of both top and bottom
                }
            }
            count += i - range
        }
        if (count > 1000000000) return 1000000000
        return count.toInt()
    }
    fun findMaxValue(target: Int, A: IntArray) : Int {
        var min = maxLowWater
        var max = maxHighWater
        while (max - min > 1) {
            val mid = (min + max + 1) / 2
            val midIndex = maximums[mid]
            if (A[midIndex] <= target)
                max = mid
            else
                min = mid
        }
        return min
    }
    fun findMinValue(target: Int, A: IntArray) : Int {
        var min = minLowWater
        var max = minHighWater
        while (max - min > 1) {
            val mid = (min + max + 1) / 2
            val midIndex = minimums[mid]
            if (A[midIndex] >= target)
                max = mid
            else
                min = mid
        }
        return min
    }
}