package uk.co.sapientit.templateapp

class Pi {
    lateinit var largest: IntArray
    lateinit var position: IntArray
    var highWater = 0
    fun solution(A: IntArray): IntArray {
        largest = IntArray(A.size)
        position = IntArray(A.size)
        if (A.size == 0) {
            return intArrayOf()
        }
        if (A.size == 1)
            return intArrayOf(0)
        val result = IntArray(A.size)
        largest[0] = A[0]
        for (i in 1..A.size - 1) {
            val closest = getClosest(A, i)
            if (closest == null) {
                largest[0] = A[i]
                position[0] = i
                highWater = 0
            } else {
                result[i] = i - position[closest]
                highWater = closest + 1
                largest[highWater] = A[i]
                position[highWater] = i
            }
        }
        largest[0] = A[A.size - 1]
        position[0] = A.size -1
        highWater = 0

        for (i in A.size - 2 downTo 0) {
            val closest = getClosest(A, i)
            if (closest == null) {
                largest[0] = A[i]
                position[0] = i
                highWater = 0
            } else {
                val res = position[closest] - i
                if (result[i] == 0 || result[i] > res)
                    result[i] =  res
                highWater = closest + 1
                largest[highWater] = A[i]
                position[highWater] = i
            }
        }
        return result

    }
    fun getClosest(A: IntArray, i: Int) : Int? {

        if (A[i] >= largest[0]) {
            return null
        } else {
            var min = 0
            var max = highWater + 1
            while (max - min > 1) {
                val mid = (max + min + 1) / 2
                if (A[i] >= largest[mid])
                    max = mid
                else
                    min = mid
            }
            return min
        }

    }
}