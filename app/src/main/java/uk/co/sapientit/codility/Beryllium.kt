package uk.co.sapientit.templateapp

class Beryllium {
    lateinit var first: IntArray
    lateinit var last: IntArray
    var total = 0L
    fun solution(A: IntArray) : Int {
        if (A.size == 1)
            return 1
        first = IntArray(A.size){-1}
        last = IntArray(A.size){Int.MAX_VALUE}
        populateFirstLast(A)
        var countLeft = 0
        var countRight = 0
        var pointLeft = 0
        var pointRight = A.size - 1
        var targetLeft = 0
        var targetRight = A.size -1

        targetRight = minOf(last[pointLeft], targetRight)
        targetLeft = maxOf(first[pointRight], targetLeft)
        var counting = false
        var oldTargetLeft = 0
        var oldTargetRight = 0
        while (pointLeft <= pointRight) {
            if (counting) {
                if (oldTargetRight == targetRight ) {

                    pointLeft += 1
                    countLeft += 1
                    targetRight = minOf(last[pointLeft], targetRight)
                } else {

                    if (oldTargetLeft == targetLeft) {
                        pointRight -= 1
                        countRight += 1
                        targetLeft = maxOf(first[pointRight], targetLeft)
                    } else {
                        total += countLeft * countRight
                        counting = false
                    }
                }
            } else {
                if (pointLeft < targetLeft) {
                    pointLeft += 1
                    targetRight = minOf(last[pointLeft], targetRight)
                } else {
                    if (pointRight > targetRight) {
                        pointRight -= 1
                        targetLeft = maxOf(first[pointRight], targetLeft)
                    } else {
                        counting = true
                        oldTargetLeft = targetLeft
                        oldTargetRight = targetRight
                        countLeft = 1
                        countRight = 1

                        pointLeft += 1
                        targetRight = minOf(last[pointLeft], targetRight)
                        pointRight -= 1
                        targetLeft = maxOf(first[pointRight], targetLeft)
                    }
                }
            }
        }
        total += (A.size - targetLeft) * (targetRight + 1)
        if (total > 1000000000)
            return 1000000000
        else
            return total.toInt()
    }

    fun populateFirstLast(A: IntArray) {
        val firstMap = HashMap<Int,Int>()
        val lastMap = HashMap<Int,Int>()
        for (i in 0..A.size -1) {
            lastMap[A[i]] = i
        }
        for (i in A.size -1 downTo  0) {
            firstMap[A[i]] = i
        }
        firstMap.forEach {
            val lastIndex = lastMap[it.key]!!
            last[it.value] = lastIndex
            first[lastIndex] = it.value
        }
    }

}