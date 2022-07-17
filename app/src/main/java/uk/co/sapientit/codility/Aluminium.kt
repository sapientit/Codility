package uk.co.sapientit.templateapp

import kotlin.random.Random

class Aluminium {
    /*
    Calculating the max value of a single slice is easy - if you sum up the numbers left to right then the max sum
    ending on any digit is total after adding that digit - the minimum total found so far.
    The same approach by starting from the right (highest index) can calculate maximum slice beginning on a given digit.
    The maximum value with one swap requires considering each digit i (0<= i < N) in turn.
    If digit i is being swapped then the maximum value obtainable is the maximum of
    maxValueOfSlice(starting at i+1) + maxValueOfSliceWithOneSwap(ending at i-1)
    and

    maxValueOfSliceWithOneSwap(starting at i+1) + maxValueOfSlice(ending at i-1)

    In this case a slice my be 0 length/0 value

    maxValueofSliceWithOneSwap is the maximum value of (a slice  + the largest value not included).
    For a sequence such as 10, -100,1,2,3 this is easy - 10 is not part of maximum slice, so add it to 6 and get 16.
    For a sequence such as 10,-1,1,2,3 this is harder.  as the maximum slice includes 10.
    For this reason we have to store both a maximum definitely not included (current) and a maximum (pending) that is currently
    in the maximum slice but might drop out later.
    We also force it to drop out if the benefits of it dropping out are greater than in staying in.
     */
    fun solution(A: IntArray): Int {
        if (A.size == 1)
            return A[0]
        val valueWithoutSwap = IntArray(A.size + 1) // max slice + 1 extra digit
        val valueWithSwap = IntArray(A.size + 1) // This is straight max slice
        valueWithSwap[0] = -10000000
        var minValue = 0
        var minValueAfterCurrent = A[0]
        var minValueAfterPending = Int.MIN_VALUE
        var maxCurrent = A[0]
        var maxPending = Int.MIN_VALUE
        var total = 0
    //    var valueAtPending = 0
        var pointer = 1
        A.forEach {
            total += it
            minValue = minOf(minValue, total)
            if (total < minValueAfterCurrent) {
                minValueAfterCurrent = total
                maxCurrent = maxPending
                minValueAfterPending = total
            }
            if (total < minValueAfterPending) {
                minValueAfterPending = total
                if (maxCurrent == Int.MIN_VALUE) {
                    maxCurrent = maxPending
                    minValueAfterCurrent = total
                }
                else {
   //                 if ( maxPending - maxCurrent < valueAtPending - minValueAfterPending) {
                    if ( maxPending - maxCurrent >  minValueAfterPending - minValueAfterCurrent) {
                        maxCurrent = maxPending
                        minValueAfterCurrent = minValueAfterPending
                    }
                }
            } else {
                if (maxCurrent < 0 ) {
                    maxCurrent = 0
                    minValueAfterCurrent = total
                }
            }
            if (it > maxPending) {
                maxPending = it
   //             valueAtPending = total
                minValueAfterPending = total

            }
            valueWithoutSwap[pointer] = total - minValue
            if (maxPending <= 0) {
                valueWithSwap[pointer] = maxPending
             } else {
                 if (maxCurrent == Int.MIN_VALUE) {
                     valueWithSwap[pointer] = maxOf(maxPending,valueWithoutSwap[pointer])
                 } else {
                     valueWithSwap[pointer] = maxOf(maxPending, maxCurrent + total - minValueAfterCurrent)
                 }
            }

            pointer += 1
        }
         maxCurrent = 0
        minValue = 0
        total = 0
        minValueAfterCurrent = A[A.size - 1]
        minValueAfterPending = Int.MIN_VALUE
        maxCurrent = minValueAfterCurrent
        maxPending = Int.MIN_VALUE
        var maxSum : Int
        // The approach would fail if all digits are non -ve as we don't want to exclude any number
        if (A[0] >= 0) // There is at least 1 non -ve number
            maxSum = maxOf(valueWithoutSwap[A.size],valueWithoutSwap[A.size -1]) // Will be max if everything is >= 0 or all but last
        else
            maxSum = Int.MIN_VALUE
        // Have calculated from left to right.  No do the same right to left.
        for (i in A.size - 1 downTo 1) {
            val newVal = A[i]
            total += newVal
            minValue = minOf(minValue, total)
            if (total < minValueAfterCurrent) {
                minValueAfterCurrent = total
                maxCurrent = maxPending
                minValueAfterPending = total
            }
            if (total < minValueAfterPending) {
                minValueAfterPending = total
                if (maxCurrent == Int.MIN_VALUE) {
                    maxCurrent = maxPending
                    minValueAfterCurrent = total
                }
                else {
  //                  if ( maxPending - maxCurrent < valueAtPending - minValueAfterPending) {
                    if ( maxPending - maxCurrent > minValueAfterPending - minValueAfterCurrent) {
                        maxCurrent = maxPending
                        minValueAfterCurrent = minValueAfterPending
                    }
                }
            } else {
                if (maxCurrent < 0 ) {
                    maxCurrent = 0
                    minValueAfterCurrent = total
                }
            }
            if (newVal > maxPending) {
                maxPending = newVal
                minValueAfterPending = total

            }
            val rightValueWithoutSwap = total - minValue
            val rightValueWithSwap : Int
            if (maxPending <= 0) {
                rightValueWithSwap = maxPending
            } else {
                if (maxCurrent == Int.MIN_VALUE) {
                    rightValueWithSwap = maxOf(maxPending,valueWithoutSwap[pointer])
                } else {
                    rightValueWithSwap = maxOf(maxPending, maxCurrent + total - minValueAfterCurrent)
                }
            }

            maxSum = maxOf(maxSum, rightValueWithSwap + valueWithoutSwap[i-1])
            maxSum = maxOf(maxSum, rightValueWithoutSwap + valueWithSwap[i-1])

        }

        return maxSum // Max possible is 1,000,000,000
    }
    /*
    Remaining code is to generate test data and compare against simple brute force method
     */
    fun bruteForceNoSwap(A: IntArray) : Int {
        var max = Int.MIN_VALUE
        var minValue = 0
        var total = 0
        A.forEach {
            total += it
            max = maxOf(max, total - minValue)
            if (total < minValue) {
                minValue = total
            }
        }
        return max
    }
    fun doSwaps(A: IntArray): Int {
        var max = bruteForceNoSwap(A)
        val plus = IntArray(A.size)
        val minus = IntArray(A.size)
        var plusCount = 0
        var minusCount = 0
        A.forEachIndexed {
            index, value ->
            if (value < 0) {
                minus[minusCount] = index
                minusCount += 1
            } else {
                plus[plusCount] = index
                plusCount += 1

            }
        }
        if (plusCount > 0 && minusCount > 0) {
            for (i in 0 until plusCount)
                for (j in 0 until minusCount) {
                    val newA = A.clone()
                    val temp = newA[plus[i]]
                    newA[plus[i]] = newA[minus[j]]
                    newA[minus[j]] = temp
                    max = maxOf(max,bruteForceNoSwap(newA))
                    val x = 1
                }
        }
        return max
    }
    fun generateData(size: Int, rand: Random) : Boolean {
        val A = IntArray(size) {rand.nextInt(-1000,1000)}
        val t1 = doSwaps(A)
        val t2 = solution(A)
        if (t1 != t2) {
            println("")
            println("source")
            for (i in A.indices) {
                print(A[i])
                print(" ")
            }
            println("")
            println("Gives results " + t1 + " " + t2)

        }
        return (t1 == t2)


    }

}