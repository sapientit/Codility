package uk.co.sapientit.codility

import kotlin.random.Random

class Silicium {
    /*
    First do some preparation.  What we are interested in is the width of each
    slice rather than the start positions.  So calculate all the widths and heights
    and then sort them.
    We know that the value we want is somewhere in range
    0 .. largest width * largest height + 1
    Then we do a binary search and for each value calculate the number of slices
    that are equal to or bigger than that slice.
    We are looking for a slice size 1 less than the point where that drops below K.

    So how to calculate number of slices for each size?

    We start at the middle of the cake (on the X axis) and do a binary search.

    In increasing slice order across the X axes, we know that each Y index must be
    <= the preceding Y index until it eventually gets to zero.
    So rather than binary searching all indexes, we just decrease the Y index 1 step at
    a time until we find each value that we want.
    For decreasing X axes and X size, the opposite is true.

    That is all that is required (see solutionNoOpt), but I also made further optimisations.
    I removed all duplicate widths and heights and kept a count of each value.  This
    means I also need to keep a running total of how many slice heights are > the slice
    height at index i (totalY array).  So when I find that index i is the value that
    gives a slice size > target size I can add totalY[i] slices  * countX (number
    of slices of that width).

    Rather than doing a multiplication for each value I work out target / x value for a
    column and then search for that.

    And when I reach the top end of the indexes for Y, then I know there are no more
    slices to the left of it and I can stop.

    Similarly when I reach a y index of 0, I want all the slices for all columns to the
    right (totalX * totalY[0])

    Finally, each time I do the binary search I can narrow down the extremes of that
    binary search.

    For the last case these combined optimisations reduced the run time by 60%

Detected complexity O(N*log(N+X+Y)) - I think it should be Nlog(X + Y)
medium_random
random test with ~400 cuts ✔OK
1. 0.044 s OK
2. 0.044 s OK
3. 0.044 s OK
4. 0.044 s OK
▶ large_range
range distances ✔OK
1. 0.052 s OK
2. 0.132 s OK
3. 0.100 s OK
▶ large_random
random test with ~100,000 cuts ✔OK
1. 0.276 s OK
2. 0.252 s OK
3. 0.256 s OK
4. 0.280 s OK
5. 0.072 s OK
▶ fail_heur
regular test with a large number of items between A[p]B[p] and A[p+1]B[p+1] ✔OK
1. 0.060 s OK
▶ extreme_cake
extreme size of cake and the biggest distances ✔OK
1. 0.080 s OK
2. 0.084 s OK
3. 0.080 s OK
     */
    fun solution(X: Int, Y: Int, K: Int, A: IntArray, B: IntArray): Int {

        val countX = IntArray(10001)
        val countY = IntArray(10001)
        val x = ArrayList<Int>()
        val y = ArrayList<Int>()
        var prev = 0
        A.forEach {
            val width = it - prev
            if (countX[width] == 0) {
                x.add(width)
            }
            countX[width] += 1
            prev = it
        }
        var lastWidth = X - prev
        if (countX[lastWidth] == 0) {
            x.add(lastWidth)
        }
        countX[lastWidth] += 1
        prev = 0
        B.forEach {
            val width = it - prev
            if (countY[width] == 0) {
                y.add(width)
            }
            countY[width] += 1
            prev = it
        }
        lastWidth = Y - prev
        if (countY[lastWidth] == 0) {
            y.add(lastWidth)
        }
        countY[lastWidth] += 1
        x.sort()
        y.sort()
        val distinctX = x.size
        val distinctY = y.size
        val totalY = IntArray(distinctY + 1)
        for (i in y.size - 1 downTo 0) {
            totalY[i] = totalY[i + 1] + countY[y[i]]
        }
        val totalX = IntArray(distinctX + 1)
        for (i in x.size - 1 downTo 0) {
            totalX[i] = totalX[i + 1] +countX[x[i]]
        }
        var min = 1
        var max = x[x.lastIndex] * y[y.lastIndex] + 1
        var minPoss = 0
        var maxPoss = distinctY
        var midPoss = 0
        val midPoint = distinctX / 2
        while (min != max) {
            val mid = (min + max) / 2
            midPoss = solve( minPoss, maxPoss, mid, x[midPoint], y ) // first index above required value
            var count =  totalY[midPoss] * countX[x[midPoint]]
            var rowIndex = midPoss
            var colIndex = midPoint - 1
            while (rowIndex < distinctY && colIndex >= 0) {
                val target = (mid - 1)  / x[colIndex] //
                while (rowIndex < distinctY && y[rowIndex] <= target) {
                    rowIndex++
                }
                count +=  totalY[rowIndex] * countX[x[colIndex]]
                colIndex--
            }
            colIndex = midPoint + 1
            rowIndex = midPoss
            while (colIndex < distinctX && rowIndex > 0) {
                val target = (mid - 1) / x[colIndex]
                while (rowIndex > 0 && y[rowIndex - 1] > target) {
                    rowIndex--
                }
                count +=  totalY[rowIndex] * countX[x[colIndex]]
                colIndex++
            }
            count += totalX[colIndex] * totalY[0]
            if (count >= K) {
                min = mid + 1
                minPoss = midPoss
            } else {
                max = mid
                maxPoss = midPoss
            }
        }
        return min - 1
    }
    fun solve(low: Int, high: Int, product: Int, xValue: Int, y: ArrayList<Int>) : Int {
        // Find the first index where y * x > product
        // target = product / x
        val target = (product - 1)/ xValue
        var min = low
        var max = high
        while (min < max) {
            val mid = (min + max) / 2
            if (y[mid] <= target) {
                min = mid + 1
            } else {
                max = mid
            }
        }
        return min
    }
    fun solutionNoOpt(X: Int, Y: Int, K: Int, A: IntArray, B: IntArray): Int {
        // I started calculating the Kth smallest instead of Kth largest :-(


        val x = ArrayList<Int>()
        val y = ArrayList<Int>()
        var prev = 0
        A.forEach {
            val width = it - prev
            x.add(width)
            prev = it
        }
        var lastWidth = X - prev
        x.add(lastWidth)

        prev = 0
        B.forEach {
            val width = it - prev

            y.add(width)
            prev = it
        }
        lastWidth = Y - prev
        y.add(lastWidth)
        x.sort()
        y.sort()
        val distinctX = x.size
        val distinctY = y.size
        var min = 1
        var max = x[x.lastIndex] * y[y.lastIndex] + 1
        var midPoss = 0
        val midPoint = distinctX / 2
        while (min != max) {
            val mid = (min + max) / 2
            midPoss = solve( 0 , distinctY, mid, x[midPoint], y ) // first index > mid
            //     var count = (distinctX - midPoss) * totalY[midPoint] * countX[x[midPoint]]
            var count =  (distinctY - midPoss)
            var rowIndex = midPoss
            var colIndex = midPoint - 1
            while (rowIndex < distinctY && colIndex >= 0) {
                val target = (mid - 1)  / x[colIndex] //
                while (rowIndex < distinctY && y[rowIndex] <= target) {
                    rowIndex++
                }
                //        count += (distinctX - rowIndex) * totalY[colIndex] * countX[rowIndex]
                count +=  (distinctY - rowIndex)
                colIndex--
            }
            colIndex = midPoint + 1
            rowIndex = midPoss
            while (colIndex < distinctX ) {
                val target = (mid - 1) / x[colIndex]
                while (rowIndex > 0 && y[rowIndex - 1] > target) {
                    rowIndex--
                }
                //          count += (distinctX - rowIndex) * totalY[colIndex] * countX[rowIndex]
                count +=  (distinctY - rowIndex)
                colIndex++
            }
            if (count >= K) {
                min = mid + 1
            } else {
                max = mid
            }
        }
        return min - 1
    }
    fun bruteForce(X: Int, Y: Int, K: Int, A: IntArray, B: IntArray): Int {
        val numSmaller = (A.size + 1) * (B.size + 1) - K + 1
        val sizes = IntArray((A.size + 1) * (B.size + 1))
        var prev = 0
        val sizeX = IntArray(A.size + 1)
        val sizeY = IntArray(A.size + 1)
        for (i in 0 until A.size) {
            val width = A[i] - prev
            sizeX[i] = width
            prev = A[i]
        }
        sizeX[A.size ] = X - prev
        prev = 0
        for (i in 0 until B.size) {
            val width = B[i] - prev
            sizeY[i] = width
            prev = B[i]
        }
        sizeY[B.size] = Y - prev
        var index = 0
        for (i in sizeX.indices) {
            for (j in sizeY.indices) {
                sizes[index] = sizeX[i] * sizeY[j]
                index += 1
            }
        }
        sizes.sort()
   /*     println()
        for (size in sizes) {
            print(size)
            print(' ')
        } */
        return sizes[numSmaller - 1]

    }
    fun generateData(size: Int, rand: Random) :Boolean {
        val A = IntArray(size)
        val B = IntArray(size)
        var x = size * 5
        for (i in A.size - 1 downTo 0) {
            val min = maxOf(1, (x - i * 10) - 9)
            val max = minOf(11, x -i - 1)
            val width = rand.nextInt(min,max)
            x -= width
            A[i] = x
        }
        var y = size * 5
        for (i in B.size - 1 downTo 0) {
            val min = maxOf(1, (y - i * 10) - 9)
            val max = minOf(11, y -i - 1)
            val width = rand.nextInt(min,max)
            y -= width
            B[i] = y
        }
        val K = rand.nextInt(1,(size ) * (size ))
        val t1 = solutionNoOpt(size * 5, size * 5, K, A,B)
        val t2 = solution(size * 5, size * 5, K, A,B)
        println(" Compare " + t1 + " " +t2)
        if (t1 != t2) {
            println()
            for (a in A) {
                print(a)
                print(' ')
            }
            println()
            for (a in B) {
                print(a)
                print(' ')
            }
            println()
            println("K = " + K + " size = " + (size * 5))
        }
        return t1== t2
    }
}