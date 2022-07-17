package uk.co.sapientit.templateapp

import kotlin.random.Random

class Epsilon {
    /*
    We need two lines - one which maps the maximum and one that maps the minimums
    If we start at - infinity then then minimum will be the highest A value, and the maximum will be the lowest (most -ve)
    And the opposite is true at + infinity.
    So the first step is to work out where each line intersects with the previous line (in sorted order of A and reverse
    sorted order of B).
    Starting from the most -ve or lowest A that gives the line for maximum and starting at the other end gives us the line
    for minimums.
    You can prove that the minimum of U-V must occur at a point where two values of F meet.  So loop through the intersections
    looking for the minimum difference.
    Note that Double.MIN_VALUE is the smallest +ve number - hence the use of -Double.MAX_VALUE (yep - I fell for that!  When
    in real life do you use these values???)
     */

    fun solution(A: IntArray, B: IntArray): Double {
        val comparator = Comparator<Int> {
            k1,k2 ->
            if (A[k1] == A[k2])
                B[k2] - B[k1]  // largest constant within smae gradient
            else
                A[k1] - A[k2]
        }
        val sorted = IntArray(A.size){it}.sortedWith(comparator).toIntArray()
        val maxes = IntArray(A.size)
        val maxIntersect = DoubleArray(A.size + 1){Double.MAX_VALUE}
        maxIntersect[0] = -Double.MAX_VALUE
        var maxHWM = 0
        maxes[0] = sorted[0]
        var prevMax = sorted[0]
        var lastIntersect = -Double.MAX_VALUE
        // find all the lines that make up the maximum
        for (i in 0 until sorted.size) {
            val current = sorted[i]
            if (A[current] > A[prevMax]) {
                var intersect = getIntersect(A,B,current, prevMax)
                // If the intersection is before the previously found intersection we have a line that we don't want to include
                // It never gets to be the maximum.
                while (intersect <= lastIntersect) {
                    maxHWM -= 1
                    lastIntersect = maxIntersect[maxHWM]
                    prevMax = maxes[maxHWM]
                    intersect = getIntersect(A,B,current, prevMax)
                }
                maxHWM += 1
                maxIntersect[maxHWM] = intersect
                lastIntersect = intersect
                prevMax = current
                maxes[maxHWM] = current
            }
        }
        val mins = IntArray(A.size)
        val minIntersect = DoubleArray(A.size + 1){Double.MAX_VALUE}
        minIntersect[0] = -Double.MAX_VALUE
        var minHWM = 0
        mins[0] = sorted[A.size - 1]
        var prevMin = mins[0]
        lastIntersect = -Double.MAX_VALUE
        // Same code again but for minimums
        for (i in sorted.size - 1 downTo 0) {
            val current = sorted[i]
            if (A[current] < A[prevMin]) {
                var intersect = getIntersect(A,B,current, prevMin)
                while (intersect <= lastIntersect) {
                    minHWM -= 1
                    lastIntersect = minIntersect[minHWM]
                    prevMin = mins[minHWM]
                    intersect = getIntersect(A,B,current, prevMin)
                }
                minHWM += 1
                minIntersect[minHWM] = intersect
                lastIntersect = intersect
                prevMin = current
                mins[minHWM] = current
            }
        }
        // We now have a graph for maximum and minimum.  The minimum value of S must appear at one of the
        // points where 2 function are equal (ie where we have recorded an intersect.
        // If there is no intersect then the slopes must be equal so take the difference in B.
        if (minHWM == 0) {
            return (B[prevMax] - B[prevMin]).toDouble()
        }
        var maxIndex = 0
        var minIndex = 0

        prevMax = maxes[maxIndex] // current maximum line
        prevMin = mins[minIndex] // current minimum line
        var maxInt = maxIntersect[maxIndex + 1] //maximum line changes here
        var minInt = minIntersect[minIndex + 1] //minimum line changes here
        var minDiff = Double.MAX_VALUE
        while (maxIndex < maxHWM || minIndex < minHWM) {
            if (maxInt > minInt) {
                minDiff = minOf(minDiff, getValue(A,B, prevMax, prevMin, minInt))
                minIndex += 1
                minInt = minIntersect[minIndex + 1]
                prevMin = mins[minIndex]
            } else {
                minDiff = minOf(minDiff, getValue(A,B, prevMax, prevMin, maxInt))
                maxIndex += 1
                maxInt = maxIntersect[maxIndex + 1]
                prevMax = maxes[maxIndex]
            }
            // Note that the edge case of maximum and minimum changing at the same point works
            // so the additional test for equality is not worth the processing time
        }
        // Don't forget the check of the final values
        minDiff = minOf(minDiff, getValue(A,B, prevMax, prevMin, minInt))
        return minDiff
    }
    fun getIntersect(A: IntArray, B: IntArray, current: Int, prev: Int) : Double {
        //  A[current]X + B[current] = A[prev]X + B[prev] - simultaneous linear equation
        // X = (B[prev] - B[current]) / {A[current] - A[[prev]]
        return (B[prev] - B[current]).toDouble() / (A[current] - A[prev]).toDouble()
    }
    fun getValue(A: IntArray, B: IntArray, max: Int, min: Int, X: Double) : Double {
        return (A[max] - A[min]) * X + (B[max] - B[min])
    }
    /*
    Proving that the solution works
     */
    fun bruteForce(A: IntArray, B: IntArray): Double {
        var min = Double.MAX_VALUE
        var minB = Double.MAX_VALUE
        if (A.size == 1)
            return 0.0
        for (i in 0..A.size - 2) {
            for (j in i + 1 until A.size) {
                if (A[i] != A[j]) {
                    val intersect = getIntersect(A, B, i, j)
                    min = minOf(min, getMax(A, B, intersect) - getMin(A, B, intersect))
                } else {
                    minB = minOf(minB,(Math.abs(B[i] - B[j])).toDouble())
                }
            }
        }
        if (min == Double.MAX_VALUE)
            return minB
        return min
    }
    fun getMax(A: IntArray, B: IntArray, X: Double): Double {
        var max = - Double.MAX_VALUE
        for (i in A.indices) {
            max = maxOf(max, A[i] * X + B[i])
        }
        return max
    }
    fun getMin(A: IntArray, B: IntArray, X: Double): Double {
        var min =  Double.MAX_VALUE
        for (i in A.indices) {
            min = minOf(min, A[i] * X + B[i])
        }
        return min
    }
    fun generateData(rand: Random, size: Int) : Boolean{
        val A = IntArray(size)
        val B = IntArray(size)
        for (i in A.indices) {
            A[i] = rand.nextInt(-10,10)
            B[i] = rand.nextInt(-10,10)
        }
        val t1 = solution(A,B)
        val t2 = bruteForce(A,B)
        if (Math.abs(t1 - t2) > 0.000002) {
            println ("Got result " + t1 + " " + t2)
            return false
        }
        return true
    }
}