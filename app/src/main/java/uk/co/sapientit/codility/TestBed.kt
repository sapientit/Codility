package uk.co.sapientit.templateapp

import android.os.Build
import androidx.annotation.RequiresApi
import java.lang.Math.abs

//import kotlin.math.abs

class TestBed {
    lateinit var nodeConnected : Array<BooleanArray>// Disconnected to top or right edge
    lateinit var linkHorizontal : Array<BooleanArray> // One of horizontal links missing
    lateinit var linkVertical : Array<BooleanArray> // One of vertical links missing
    lateinit var linkStatus : Array<IntArray> // One of vertical links missing
    lateinit var toDealWithA : IntArray // One of vertical links missing
    lateinit var toDealWithB : IntArray // One of vertical links missing
    var complete = false
    fun solution(N: Int, A: IntArray, B: IntArray, C: IntArray): Int {
        nodeConnected = Array<BooleanArray>(N){BooleanArray(N){false}} // Disconnected to top or right edge
        linkHorizontal = Array<BooleanArray>(N){col ->
            if (col == 0 || col == N -1)  BooleanArray(N){true} else BooleanArray(N){false}}
        linkVertical = Array<BooleanArray>(N){col -> BooleanArray(N){row ->
            if (row == 0 || row == N -1) true else false}} // One of vertical links missing
        linkStatus = Array<IntArray>(N){IntArray(N){0} }
        toDealWithA = IntArray(A.size)
        toDealWithB = IntArray(A.size)
        for (i in 0..A.size - 1) {
            val a = A[i]
            val b = B[i]
            if (C[i] == 0) { // vertical link
                blockVerticalLink(a,b)
                blockVerticalLink(a,b + 1)
            } else {
                blockHorizontalLink(a,b)
                blockHorizontalLink(a + 1,b)
            }
            if (complete) return i + 1
        }
        return -1
    }
     fun blockVerticalLink(a: Int, b: Int) {
         if (linkVertical[a][b]) {
             linkStatus[a][b] += 1
         } else {
             linkVertical[a][b] = true
         }
         if (linkVertical[a][b] && linkHorizontal[a][b]) {
             linkStatus[a][b] += 2
         }
         if (linkStatus[a][b] >= 2) {
             checkConnection(a,b)
         }
     }
     fun blockHorizontalLink(a: Int, b: Int) {
         if (linkHorizontal[a][b]) {
             linkStatus[a][b] += 1
         } else {
             linkHorizontal[a][b] = true
         }
         if (linkVertical[a][b] && linkHorizontal[a][b]) {
             linkStatus[a][b] += 2
         }
         if (linkStatus[a][b] >= 2) {
             checkConnection(a,b)
         }
     }
    fun checkConnection(a: Int, b:Int) {
        if ((a != 0 && b== 0) || (b != nodeConnected.size -1 && a == nodeConnected.size -1 )) {
            nodeConnected[a][b] = true
        } else {
            if (checkRange(a - 1, b)) {
                if (nodeConnected[a - 1][b]) {
                    nodeConnected[a][b] = true
                } else {

                    if (checkRange(a + 1, b) && nodeConnected[a + 1][b]) {
                        nodeConnected[a][b] = true
                    } else {

                        if (checkRange(a, b - 1) && nodeConnected[a][b - 1]) {
                            nodeConnected[a][b] = true
                        } else {
                            if (checkRange(a, b + 1) && nodeConnected[a][b + 1]) {
                                nodeConnected[a][b] = true
                            } else {
                                if (linkStatus[a][b] > 2) {

                                    if (checkRange(
                                            a + 1,
                                            b + 1
                                        ) && ((nodeConnected[a + 1][b + 1] && linkStatus[a + 1][b + 1] > 2))
                                    )
                                        nodeConnected[a][b] = true
                                    else

                                        if (checkRange(
                                                a - 1,
                                                b + 1
                                            ) && (nodeConnected[a - 1][b + 1] && linkStatus[a - 1][b + 1] > 2)
                                        )
                                            nodeConnected[a][b] = true
                                        else

                                            if (checkRange(
                                                    a - 1,
                                                    b - 1
                                                ) && (nodeConnected[a - 1][b - 1] && linkStatus[a - 1][b - 1] > 2)
                                            )
                                                nodeConnected[a][b] = true
                                            else

                                                if (checkRange(
                                                        a + 1,
                                                        b - 1
                                                    ) && (nodeConnected[a + 1][b - 1] && linkStatus[a + 1][b - 1] > 2)
                                                )
                                                    nodeConnected[a][b] = true
                                }
                            }
                        }
                    }
                }
            }
        }

        if (nodeConnected[a][b])
            propagateNode(a,b)
    }
    fun checkRange(a: Int, b: Int) : Boolean {
        return (a >= 0 && b >= 0 && a < nodeConnected.size && b < nodeConnected.size)
    }
    fun propagateNode(x: Int, y: Int) {
        var highWater = 0
        toDealWithA[0] = x
        toDealWithB[0] = y
        while (highWater >=0) {
            val a = toDealWithA[highWater]
            val b = toDealWithB[highWater]
            if ((a == 0 && b != 0) || (b == nodeConnected.size -1 && a != nodeConnected.size - 1)) {
                complete = true
                return
            }
            highWater -= 1
            if (checkRange(a-1,b))
                if (!nodeConnected[a-1][b] && linkStatus[a-1][b] >= 2) {
                    nodeConnected[a - 1][b] = true
                    highWater += 1
                    toDealWithA[highWater] = a - 1
                    toDealWithB[highWater] = b
                }

            if (checkRange(a+1,b)&& linkStatus[a+1][b] >= 2)
                if (!nodeConnected[a+1][b]) {
                    nodeConnected[a + 1][b] = true
                    highWater += 1
                    toDealWithA[highWater] = a + 1
                    toDealWithB[highWater] = b
                }


            if (checkRange(a,b + 1)&& linkStatus[a][b+ 1] >= 2)
                if (!nodeConnected[a][b + 1]) {
                    nodeConnected[a][b + 1] = true
                    highWater += 1
                    toDealWithA[highWater] = a
                    toDealWithB[highWater] = b + 1
                }

            if (checkRange(a,b - 1)&& linkStatus[a][b - 1] >= 2)
                if (!nodeConnected[a][b - 1]) {
                    nodeConnected[a][b - 1] = true
                    highWater += 1
                    toDealWithA[highWater] = a
                    toDealWithB[highWater] = b - 1
                }

            if (linkStatus[a][b] > 2) {

                if (checkRange(a + 1,b + 1))
                    if (!nodeConnected[a + 1][b + 1] && linkStatus[a+1][b+1] > 2) {
                        nodeConnected[a+ 1][b + 1] = true
                        highWater += 1
                        toDealWithA[highWater] = a + 1
                        toDealWithB[highWater] = b + 1
                    }


                if (checkRange(a - 1,b + 1))
                    if (!nodeConnected[a - 1][b + 1] && linkStatus[a-1][b+1] > 2) {
                        nodeConnected[a - 1][b + 1] = true
                        highWater += 1
                        toDealWithA[highWater] = a - 1
                        toDealWithB[highWater] = b + 1
                    }


                if (checkRange(a - 1,b - 1))
                    if (!nodeConnected[a - 1][b - 1] && linkStatus[a-1][b-1] > 2){
                        nodeConnected[a - 1][b - 1] = true
                        highWater += 1
                        toDealWithA[highWater] = a - 1
                        toDealWithB[highWater] = b - 1
                    }


                if (checkRange(a +1,b - 1))
                    if (!nodeConnected[a + 1][b - 1] && linkStatus[a+1][b-1] > 2){
                        nodeConnected[a + 1][b - 1] = true
                        highWater += 1
                        toDealWithA[highWater] = a + 1
                        toDealWithB[highWater] = b - 1
                    }

            }
        }

    }

    fun solutionChi(A: IntArray, B: IntArray): IntArray {
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
    fun solutionUpsilon(A: IntArray): Int {
        var neighbour = IntArray(A.size){Int.MIN_VALUE}
        val position = IntArray(A.size + 2){0}
        val values = IntArray(A.size + 2){Int.MAX_VALUE}
        var maxChain = 0

        values[1] = Int.MIN_VALUE
        var highWater = 0
        A.forEachIndexed { index, value ->
            var min = 0
            var max = highWater + 1
            while (max - min > 1) {
                val mid = (max + min + 1) / 2
                if (value > values[mid])
                    max = mid
                else
                    min = mid
            }
            highWater -= 1
            while (highWater > min) {
                neighbour[position[highWater]] = position[highWater + 1]
                highWater -= 1
            }
            highWater = min + 1
            position[highWater] = index
            values[highWater] = value
            values[highWater + 1] = Int.MAX_VALUE
        }
        highWater -= 1
        while (highWater > 0) {
            neighbour[position[highWater]] = position[highWater + 1]
            highWater -= 1
        }
        val rightNeighbour = neighbour
        neighbour = IntArray(A.size){Int.MIN_VALUE}
        highWater = 0
        values[1] = Int.MIN_VALUE

        for (index in (0..A.size - 1).reversed()) {
            val value = A[index]
            var min = 0
            var max = highWater + 1
            while (max - min > 1) {
                val mid = (max + min + 1) / 2
                if (value > values[mid])
                    max = mid
                else
                    min = mid
            }
            highWater -= 1
            while (highWater > min) {
                neighbour[position[highWater]] = position[highWater + 1]
                highWater -= 1
            }
            highWater = min + 1
            position[highWater] = index
            values[highWater] = value
            values[highWater + 1] = Int.MAX_VALUE
        }
        highWater -= 1
        while (highWater > 0) {
            neighbour[position[highWater]] = position[highWater + 1]
            highWater -= 1
        }
        val leftNeighbour = neighbour
        val goLeft = BooleanArray(A.size){true}
        val attempt = IntArray(A.size)
        attempt[0] = position[1]
        // start with largest which will always be in pos 1
        while (highWater >= 0) {
            val pos = attempt[highWater]
            if (goLeft[highWater]) {
                if (leftNeighbour[pos] == Int.MIN_VALUE) {
                    goLeft[highWater] = false
                } else {
                    highWater += 1
                    attempt[highWater] = leftNeighbour[pos]
                    goLeft[highWater] = true
                }
            } else {
                if (rightNeighbour[pos] == Int.MIN_VALUE) {
                    maxChain = maxOf(maxChain, highWater + 1)
                    highWater -= 1
                    while (highWater >= 0 && !goLeft[highWater])
                        highWater -= 1
                    if (highWater >= 0)
                        goLeft[highWater] = false
                }else {
                    highWater += 1
                    attempt[highWater] = rightNeighbour[pos]
                    goLeft[highWater] = true

                }
            }
        }
        return maxChain
    }
    fun getShortest() {
        val found = IntArray(10)
        val TARGET = 77
        val TARGLENGTH = 8
        found[0] = 1
        found[1] = 2
        val indexes = IntArray(10)
        var pointer = 1
        val possible = Array<BooleanArray>(10){BooleanArray(TARGET){false} }
        possible[1][3] = true
        possible[1][4] = true
        while (pointer > 0) {
            var done = false
            if (pointer < TARGLENGTH) {
                for (i in found[pointer]..TARGET-1) {
                    if (possible[pointer][i]) {
                        done = true
                        possible[pointer][i] = false
                        found[pointer + 1] = i
                        possible[pointer + 1] = possible[pointer].clone()
                        pointer += 1
                        for (j in 0..pointer) {
                            if (i + found[j] < TARGET)
                                possible[pointer][i + found[j]] = true
                            else {
                                if (i + found[j] == TARGET && pointer < TARGLENGTH) {
                                    for (k in 0..pointer) {
                                        println(found[k])
                                    }
                                }
                            }
                        }
                        break
                    }
                }

            }
            if (!done)
                pointer -= 1
        }
    }



    fun solutionTry(A: Int): IntArray {
        var current = A
        val result = IntArray(100)
        var count = 0
        while (current > 1){
            result[count] = current
            count += 1
            if ( current % 2 == 0)
                current = current / 2
            else {
                if (current % 3 == 0)
                    current = current * 2 / 3
                else
                    if (current % 5 == 0)
                        current = current * 4 / 5
                    else
                        current -= 1
            }
        }
        val answer = IntArray(count + 1){1}
        var index = 1
        while (count > 0) {
            count -= 1
            answer[index] = result[count]
            index += 1
        }
        return answer
    }
    fun solutionSigma(H: IntArray): Int {
        highWater = 0
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


    lateinit var largest : IntArray
    lateinit var position : IntArray
    var highWater = 0

    fun solutionpi(A: IntArray): IntArray {
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
    fun solutionxi(S: String, T: String, K: Int): Int {
        val span = K + 1
        val maxLength =  T.length + span
        val sparse = IntArray(maxLength + 1){1 }
        var total = 1L
        for (i in span + 1..maxLength ) {
            total += sparse[i - span]
            total = total % 1000000007
            sparse[i] = total.toInt()
        }

        val sparseA = calcSparse(S, sparse, K, true)
        val sparseB = calcSparse(T, sparse, K, false)
        return ((sparseB - sparseA + 1000000007) % 1000000007).toInt()



    }
    fun calcSparse(number: String, sparse: IntArray, K: Int, excluded: Boolean): Long {
        val len = number.length
        var result = sparse[len + K + 1].toLong() - 1
        var Kcount = 0
        var doAdd = false
        for (i in 1..len - 1) {
            if (Kcount < K) {
                Kcount += 1
                if (number[i] == '1')
                    return result
            } else {
                if (doAdd) {
                    if (number[i] == '1') {
//                        result += sparse[len-i]
                        result = (result + sparse[len-i + K + 1] - 1) % 1000000007
 //                       println ("For number " + number + " K "+ K +" " + result)

                        Kcount = 0
                        doAdd = false
                    }
                } else {
                    if (number[i] == '0') {
                        result = (result - sparse[len - i + K + 1] + 1) % 1000000007
                        doAdd = true
                    } else {
                        Kcount = 0
                    }
                }
            }
        }

        if (excluded )
            return result -1 // If we get here then we have a sparse number
        return result

    }
    fun solutionNu(A: IntArray, B: IntArray, P: IntArray, Q: IntArray, R: IntArray, S: IntArray): Int {
        val medians = IntArray(R.size) {0}
        for (i in 0..R.size -1) {
            val aSize = Q[i] - P[i]
            val bSize = S[i] - R[i]
            val big : IntArray
            val small : IntArray
            val bigf : Int
            val smallf: Int
            val smalle : Int
            val base : Int
            var max : Int
            if (aSize > bSize) {
                big = A
                small = B
                bigf = P[i]
                smallf = R[i]
                smalle = S[i]
              //  base = aSize - bSize - 1 + bigf
                base = (aSize + bSize) / 2 - bSize + bigf
                max = bSize + 1
            } else {
                big = B
                small = A
                bigf = R[i]
                smallf = P[i]
                smalle = Q[i]
               // base = bSize - aSize - 1 + bigf
                base = (bSize + aSize) / 2 - aSize + bigf
                max = aSize + 1
            }
            val sum = max - 1
            var min = 0
            if (big[base] > small[smalle]) {
                medians[i] = big[base]
            } else {
                if (big[base + max] < small[smallf]) {
                    medians[i] = big[base + max]
                } else {
                    while (max - min > 1) {
                        val mid = (max + min + 1) / 2
                        val smallIndex = sum - mid
                        if (big[base + mid] > small[smallIndex + smallf]) {
                            max = mid
                        } else
                            min = mid
                    }
                    if (big[base + max] <= small[sum - min + smallf])
                        medians[i] = big[base + max]
                    else
                        medians[i] = small[sum - min + smallf]
                }
            }
            println(medians[i])
        }

        medians.sort()
        val midPoint = medians.size / 2
        return medians[midPoint]

    }
    fun solutionMu(S: String) : Int {
        var total = 0L
        var size = S.length
        var zeroes = 0
        var first = true
        var prevDigit = 1
        for (i in 0.. S.length -1) {
            val digit = Character.getNumericValue(S[i])
            total = total * 10
            if (first)
                first = false
            else {
                total += prevDigit * size
                if (digit == 0) {
                    total -= 1
                    zeroes += 1
                }
            }

            total +=  zeroes * digit
            total = total % 1410000017
            prevDigit = digit
            size -= 1
  //          println("after digit " + total)
        }
        total +=  zeroes + 1410000018

        total = total % 1410000017
        return total.toInt()
    }



    fun solutionFellowship(S: String, K: Int): String {
        // write your code in Kotlin
        val aAscii = 'a'.toInt()
        val AAscii = 'A'.toInt()
        val source = S.toCharArray()
        val position = IntArray(S.length) { -1 }
        //       val swaps = IntArray(S.length){-1}
        val current = IntArray(26) { -1 }
        val last = IntArray(26) { -1 }
        S.forEachIndexed { index, c ->
            val cPointer = c.toInt() - aAscii
            if (last[cPointer] == -1) {
                current[cPointer] = index
                last[cPointer] = index
            } else {
                position[last[cPointer]] = index
                last[cPointer] = index
            }
        }
        var target = 0
        val skipped = Array<IntArray>(26){IntArray(S.length + 1) { Int.MAX_VALUE }}
        for (i in 0..25)
            skipped[i][0] = -1
        val skippedMax = Array<IntArray>(26){IntArray(26) { 0 }}

        val skippedNext = IntArray(26){1}
        val result = CharArray(S.length)
        val skippedMin = IntArray(26){0}
        var swapsLeft = K
        for (i in 0..source.size - 1) {
            if (source[i] < 'a') {
                val cPointer = S[i].toInt() - aAscii
                skippedMin[cPointer] += 1
            } else {
                val cPointer = S[i].toInt() - aAscii
                var j = 0
                while (j < cPointer && swapsLeft > 0) {
                    val nextChar = current[j]
                    if (nextChar != -1) {
                        var skippedNum = 0 // Number of empties between i and nextchar
                        val sMax = skippedMax[j]  // int array for a,b,c,d,...
                        for (k in 0..j) {
                            val skip = skipped[k] // int array for a,b,c,d,...
                            if (skip[sMax[k] + 1] < nextChar) {

                                var min = sMax[k] + 1
                                var max = skippedNext[k]
                                while (skip[min + 1] < nextChar) {
                                    val mid = (min + max + 1) / 2
                                    if (skip[mid] < nextChar)
                                        min = mid
                                    else
                                        max = mid
                                }
                                sMax[k] = min
                            }
                            skippedNum += sMax[k] - skippedMin[k]
                        }
                        if (nextChar - i - skippedNum <= swapsLeft) {
                            swapsLeft -= nextChar - i - skippedNum
                            result[target] = S[nextChar]
                            target += 1
                            source[nextChar] = (AAscii + j).toChar()
                            skipped[j][skippedNext[j]] = nextChar
                            skippedNext[j] += 1
                            current[j] = position[nextChar]
                        } else {
                            j += 1
                        }
                    } else
                        j += 1
                }
                result[target] = source[i]
                target += 1
                current[cPointer] = position[i]
            }
        }

        return String(result)

    }

    fun solutionv2(S: String, K: Int): String {
        // write your code in Kotlin
        val aAscii = 'a'.toInt()
        var source = S.toCharArray()
        val position = IntArray(S.length) { -1 }
        //       val swaps = IntArray(S.length){-1}
        val current = IntArray(26) { -1 }
        val last = IntArray(26) { -1 }
        //      val count = IntArray(27){0}
        //      val included = IntArray(26){0}
        //      val done = IntArray(26){0}
        S.forEachIndexed { index, c ->
            val cPointer = c.toInt() - aAscii
            if (last[cPointer] == -1) {
                current[cPointer] = index
                last[cPointer] = index
                //           count[cPointer] = 1
            } else {
                position[last[cPointer]] = index
                last[cPointer] = index
                //           count[cPointer] += 1
            }
            /*          var swapReqs = 0
            for (i in cPointer + 1..26) {
                swapReqs += count[i]
            }
            swaps[index] = swapReqs */
        }
        var target = 0
        val skipped = IntArray(S.length + 1) { Int.MAX_VALUE }
        skipped[0] = -1
        val skippedMax = IntArray(26) { 0 }

        var skippedNext = 1
        val result = CharArray(S.length)
        var skippedMin = 0
        var swapsLeft = K
        for (i in 0..source.size - 1) {
            if (source[i] == ' ') {
                skippedMin += 1
            } else {
                val cPointer = S[i].toInt() - aAscii
                var j = 0
                while (j < cPointer && swapsLeft > 0) {
                    val nextChar = current[j]
                    if (nextChar != -1) {
                        if (skipped[skippedMax[j] + 1] < nextChar) {

                            var min = skippedMax[j] + 1
                            var max = skippedNext
                            while (skipped[min + 1] < nextChar) {
                                val mid = (min + max + 1) / 2
                                if (skipped[mid] < nextChar)
                                    min = mid
                                else
                                    max = mid
                            }
                            skippedMax[j] = min
                        }
                        val skippedNum = skippedMax[j] - skippedMin
                        if (nextChar - i - skippedNum <= swapsLeft) {
                            swapsLeft -= nextChar - i - skippedNum
                            result[target] = S[nextChar]
                            target += 1
                            source[nextChar] = ' '
                            skipped[skippedNext] = nextChar
                            skippedNext += 1
                            current[j] = position[nextChar]
                        } else {
                            j += 1
                        }
                    } else
                        j += 1
                }
                result[target] = source[i]
                target += 1
                current[cPointer] = position[i]
            }
        }

        return String(result)

    }

     /*   val current = first.clone()
        var swapsLeft = K
        for (i in 0..25) {
            if (current[i] >= 0) {
                var cPos = current[i]
                while (cPos >= 0 && swaps[cPos] <= swapsLeft ) {
                    if (swaps[cPos] > 0) {
                        included[i] += 1
                        swapsLeft -= swaps[cPos]
                    }
                    cPos = position[cPos]
                }
                current[i] = cPos
                if (swapsLeft == 0)
                    break
            }
        }
        val result = CharArray(S.length)
        var target = 0
        var index = 0
        for (i in 0..25) {
            var inc = 0
            if (included[i] > 0) {
                while (swaps[index] == 0 && S[index].toInt() - aAscii <= i) {
                    result[target] = S[index]
                    index += 1
                    target += 1
                }
            }
            while (inc < included[i]) {
                result[target] = (i + aAscii).toChar()
                target += 1
                inc += 1
            }
        }
        var skipped = 0
        while (index < S.length) {
            val c = S[index]
            val cPointer = c.toInt() - aAscii
   //         done[cPointer] += 1
            if (included[cPointer] > 0) {
                included[cPointer] -= 1
            } else {
                result[target] = S[index]
                current[cPointer] = position[index]
                target += 1
                if (swapsLeft > 0) {
                    skipped += 1
  //                  var countDone = 0
                    for (j in 0..25) {
                        if (current[j] >= 0) {
  //                          this next test is tricky...
                            if (swaps[current[j]] + index  + swapsLeft + skipped == current[j] ) {
                                result[target] = (aAscii +j).toChar()
                                included[j] += 1
                                target += 1
                                swapsLeft = 0
                                break
                            }
                        }
  //                      countDone =+ done[j]
                    }

                }

            }
            index += 1
        } */

    fun solutionXX(S: String, K: Int): String {
        // write your code in Kotlin
        val aAscii = 'a'.toInt()
        val position = IntArray(S.length) {-1}
        val swaps = IntArray(S.length){-1}
        val first = IntArray(26){-1}
        val last = IntArray(26){-1}
        val count = IntArray(27){0}
        val included = IntArray(26){0}
  //      val done = IntArray(26){0}
        S.forEachIndexed { index, c ->
            val cPointer = c.toInt() - aAscii
            if (last[cPointer] == -1) {
                first[cPointer] = index
                last[cPointer] = index
                count[cPointer] = 1
            } else {
                position[last[cPointer]] = index
                last[cPointer] = index
                count[cPointer] += 1
            }
            var swapReqs = 0
            for (i in cPointer + 1..26) {
                swapReqs += count[i]
            }
            swaps[index] = swapReqs
        }
        val current = first.clone()
        var swapsLeft = K
        for (i in 0..25) {
            if (current[i] >= 0) {
                var cPos = current[i]
                while (cPos >= 0 && swaps[cPos] <= swapsLeft ) {
                    if (swaps[cPos] > 0) {
                        included[i] += 1
                        swapsLeft -= swaps[cPos]
                    }
                    cPos = position[cPos]
                }
                current[i] = cPos
                if (swapsLeft == 0)
                    break
            }
        }
        val result = CharArray(S.length)
        var target = 0
        var index = 0
        for (i in 0..25) {
            var inc = 0
            if (included[i] > 0) {
                while (swaps[index] == 0 && S[index].toInt() - aAscii <= i) {
                    result[target] = S[index]
                    index += 1
                    target += 1
                }
            }
            while (inc < included[i]) {
                result[target] = (i + aAscii).toChar()
                target += 1
                inc += 1
            }
        }
        var skipped = 0
        while (index < S.length) {
            val c = S[index]
            val cPointer = c.toInt() - aAscii
   //         done[cPointer] += 1
            if (included[cPointer] > 0) {
                included[cPointer] -= 1
            } else {
                result[target] = S[index]
                current[cPointer] = position[index]
                target += 1
                if (swapsLeft > 0) {
                    skipped += 1
  //                  var countDone = 0
                    for (j in 0..25) {
                        if (current[j] >= 0) {
  //                          this next test is tricky...
                            if (swaps[current[j]] + index  + swapsLeft + skipped == current[j] ) {
                                result[target] = (aAscii +j).toChar()
                                included[j] += 1
                                target += 1
                                swapsLeft = 0
                                break
                            }
                        }
  //                      countDone =+ done[j]
                    }

                }

            }
            index += 1
        }
        return String(result)

    }
   /* val pending = mutableListOf<Nod>()

    class Link(val node1: Nod, val node2: Nod) {
        var comp1 = false // Sent values from node 2 to node 1
        var comp2 = false // Have we sent values from node 1 to node2
        fun pending(nod: Nod): Boolean {
            if (nod === node1)
                return !comp1
            else
                return !comp2
        }

        fun complete(nod: Nod, value: Int, nodes: Int) {
            if (nod === node1) {
                comp2 = true
                node2.inbound(value, nodes)
            } else {
                comp1 = true
                node1.inbound(value, nodes)
            }
        }
        fun propagate(nod: Nod, propValue: Int, propNodes: Int) {
            if (nod === node1) {
                if (!comp2) {
                    complete(nod, propValue, propNodes)
                }
            } else {
                if (!comp1) {
                    complete(nod, propValue, propNodes)
                }

            }
        }

    }

    class Nod(val pending: MutableList<Nod>) {
        val links = mutableListOf<Link>()
        var complete = 0
        var value = 0
        var numNodes = 1
        fun inbound(addVal: Int, addNum: Int) {
            value += addVal
            numNodes += addNum
            complete += 1
            if (complete >= links.size - 1) {
                pending.add(this)
            }
        }

        fun process() {
            if (complete == links.size) {

                links.forEach {
                    it.propagate(this, value + numNodes, numNodes)
                }
            } else {

                links.forEach {
                    if (it.pending(this)) {
                        it.complete(this, value + numNodes, numNodes)
                    }
                }
            }

        }
    }

    fun solution(A: IntArray): Int {
        val nodes = Array<Nod>(A.size) { Nod(pending) }
        for (i in 0..A.size - 1) {
            if (i != A[i]) {
                val nod1 = nodes[i]
                val nod2 = nodes[A[i]]
                val link = Link(nod1, nod2)
                nod1.links.add(link)
                nod2.links.add(link)
            }
        }
        nodes.forEach {
            if (it.links.size == 1)
                pending.add(it)

        }
        while (!pending.isEmpty()) {
            val nod = pending.removeAt(0)
            nod.process()
        }
        var minValue = Int.MAX_VALUE
        var minNode = -1
        nodes.forEachIndexed() {
            index, nod ->
            if (nod.value < minValue) {
                minValue = nod.value
                minNode = index
            }
        }
        return minNode
    }

    */
}
/*
class Node (val number: Int) {
    val adjacent = mutableMapOf<Int, Node>()
    var level = 0


    fun solutionAdjacent(A: IntArray): Int {
        val nodes = mutableMapOf<Int, Node>()
        var prev = Node(A[0])
        prev.level = 1
        if (A.size == 1)
            return 1
        nodes[A[0]] = prev
        var next: Node
        for (i in 1..A.size - 1) {
            if (nodes[A[i]] == null) {
                next = Node(A[i])
                nodes[A[i]] = next
            } else
                next = nodes[A[i]]!!

            prev.adjacent[next.number] = next
            next.adjacent[prev.number] = prev
            prev = next
        }
        val toProcess = mutableListOf<Node>(nodes[A[0]]!!)
        val target = A[A.size - 1]
        if (A[0] == target)
            return 1
        while (true) {
            val current = toProcess.removeAt(0)
            //       val current = toProcess.removeFirst()
            val nextLevel = current.level + 1
            current.adjacent.values.forEach {
                if (it.number == target)
                    return nextLevel
                if (it.level == 0) {
                    it.level = nextLevel
                    toProcess.add(it)
                }
            }
        }

    }
}
    /*




    data class Refuel( val price: Int, var volume : Int)

    fun solutionFuel(D: IntArray, P: IntArray, T: Int): Int {
        val refuels = Array<Refuel?>(D.size + 1){null}
        refuels[0] =(Refuel(0, T))
        var lastRefuel = 0
        for (i in D.size - 1 downTo 0) {
            if (D[i] > T)
                return -1
            var refuel = D[i]
            if (P[i] > refuels[lastRefuel]!!.price) {
                lastRefuel += 1
                refuels[lastRefuel] = Refuel( P[i], refuel)
            } else {
                while (refuel < T && P[i] <= refuels[lastRefuel]!!.price ) {
                    if (refuels[lastRefuel]!!.volume < T - refuel) {
                        refuel += refuels[lastRefuel]!!.volume
                        lastRefuel -= 1
                    } else {
                        refuels[lastRefuel]!!.volume -= T - refuel
                        refuel = T
                    }
                }
                lastRefuel += 1
                refuels[lastRefuel] = Refuel( P[i], refuel)
            }
        }
        var cost = 0
        for (i in 0..lastRefuel) {
            cost += refuels[i]!!.price * refuels[i]!!.volume
        //    if (cost > 1000000000)
            if (cost > 100000000)
                return -2
       //     if (cost > 726379967)
        }
        return cost

    }
    fun solutionold(D: IntArray, P: IntArray, T: Int): Int {
        var cost: Long = 0
        var lowestPrice = Int.MAX_VALUE
        var lowDistance = 0
        var nextLowestPrice = Int.MAX_VALUE
        var nextLowDistance = 0
        var distance = 0
        var prevRefillPrice = 0
        for (i in D.size - 1 downTo 0) {
            if (D[i] > T)
                return -1
            distance += D[i]
            if (distance > T) {
                cost += lowestPrice * lowDistance
                if (lowestPrice < prevRefillPrice) {
                    cost -= (T-lowDistance) * (prevRefillPrice - lowestPrice)
                }
                prevRefillPrice = lowestPrice
                distance -= lowDistance
                nextLowDistance -= lowDistance
                lowDistance = nextLowDistance
                lowestPrice = nextLowestPrice
                nextLowestPrice = Int.MAX_VALUE
            }
            if (P[i] <= lowestPrice)  {
                lowestPrice = P[i]
                lowDistance = distance
                nextLowDistance = distance
                nextLowestPrice = Int.MAX_VALUE
            } else {
                if (P[i] <= nextLowestPrice) {
                    nextLowestPrice = P[i]
                    nextLowDistance = distance
                }
            }
        }
        cost += lowestPrice * lowDistance
        if (lowestPrice < prevRefillPrice) {
            cost -= (T-lowDistance) * (prevRefillPrice - lowestPrice)
        }
        distance -= lowDistance
        cost += distance * P[0]

        if (cost > 10000000000)
            return -2
        if (cost > 726379960)
            return -2
        return cost.toInt()

    }



    data class Pair(val a : Int, val b: Int)
    fun solutionEta(A: IntArray, B: IntArray): Double {
        val pairs = Array<Pair>(A.size){i -> Pair(A[i],B[i])}
        pairs.sortBy {
            it.b
        }
        var minResult = Double.MAX_VALUE
  //      var testPoint = 0.0
  //      var result = 0.0
        if (A.size == 1)
            return 0.0
   //     var maxPointer = A.size - 1
   //     var minPointer = 0
        val minIntersect = DoubleArray(A.size)
        minIntersect[0] = 0.0
        val minPair = IntArray(A.size)
        minPair[0] = 0
        var minLast = 0
        pairs.forEachIndexed { index, pair ->
            val intersect = calcIntersect(pairs[minPair[minLast]], pair)
            if (intersect != null)
                if (intersect > minIntersect[minLast]) {
                    minLast += 1
                    minIntersect[minLast] = intersect
                    minPair[minLast] = index
                } else {
                    if (intersect > 0) {
                        var min = 0
                        var max = minLast
                        var testIntersect: Double? = calcIntersect(pairs[minPair[min]], pair)
                        while (max - min > 1) {
                            val mid = (max + min + 1) / 2
                            testIntersect = calcIntersect(pairs[minPair[mid]], pair)
                            if (testIntersect != null)
                                if (testIntersect!! >= minIntersect[mid]) {
                                    min = mid
                                } else
                                    max = mid
                            else
                                min = mid
                        }
                        if (testIntersect != null) {
                            minLast = min
                            testIntersect = calcIntersect(pairs[minPair[min]], pair)
                            minLast += 1
                            minPair[minLast] = index
                            minIntersect[minLast] = testIntersect!!
                        }
                    }else
                        if (intersect == 0.0) {
                            if (pair.b == pairs[minPair[0]].b && pair.a < pairs[minPair[0]].a)
                                minPair[0] = index
                        }
                }

        }

        val maxIntersect = DoubleArray(A.size)
        maxIntersect[0] = 0.0
        val maxPair = IntArray(A.size)
        maxPair[0] = A.size - 1
        var maxLast = 0
        for (index in pairs.size -2 downTo(0)){
            val pair = pairs[index]
            val intersect = calcIntersect(pairs[maxPair[maxLast]], pair)
            if (intersect != null)
                if (intersect > maxIntersect[maxLast]) {
                    maxLast += 1
                    maxIntersect[maxLast] = intersect
                    maxPair[maxLast] = index
                } else {
                    if (intersect > 0) {
                        var min = 0
                        var max = maxLast
                        var testIntersect: Double? = calcIntersect(pairs[maxPair[min]], pair)
                        while (max - min > 1) {
                            val mid = (max + min + 1) / 2
                            testIntersect = calcIntersect(pairs[maxPair[mid]], pair)
                            if (testIntersect != null)
                                if (testIntersect!! >= maxIntersect[mid]) {
                                    min = mid
                                } else
                                    max = mid
                            else
                                min = mid
                        }
                        if (testIntersect != null) {
                            maxLast = min
                            testIntersect = calcIntersect(pairs[maxPair[min]], pair)
                            maxLast += 1
                            maxPair[maxLast] = index
                            maxIntersect[maxLast] = testIntersect!!
                        }
                    } else {
                        if (intersect == 0.0) {
                            if (pair.b == pairs[maxPair[0]].b && pair.a > pairs[maxPair[0]].a)
                                maxPair[0] = index
                        }
                    }
                }

        }
        val difference = findMin( pairs, minPair, minIntersect, minLast , maxPair, maxIntersect, maxLast  )
        minLast = 0
        minPair[0] = 0
        pairs.forEachIndexed { index, pair ->
            val intersect = calcIntersect(pairs[minPair[minLast]], pair)
            if (intersect != null)
                if (intersect < minIntersect[minLast]) {
                    minLast += 1
                    minIntersect[minLast] = intersect
                    minPair[minLast] = index
                } else {
                    if (intersect < 0) {
                        var min = 0
                        var max = minLast
                        var testIntersect: Double? = calcIntersect(pairs[minPair[min]], pair)
                        while (max - min > 1) {
                            val mid = (max + min + 1) / 2
                            testIntersect = calcIntersect(pairs[minPair[mid]], pair)
                            if (testIntersect != null)
                                if (testIntersect!! <= minIntersect[mid]) {
                                    min = mid
                                } else
                                    max = mid
                            else
                                min = mid
                        }
                        if (testIntersect != null) {
                            minLast = min
                            testIntersect = calcIntersect(pairs[minPair[min]], pair)
                            minLast += 1
                            minPair[minLast] = index
                            minIntersect[minLast] = testIntersect!!
                        }
                    } else
                        if (intersect == 0.0) {
                            if (pair.b == pairs[minPair[0]].b && pair.a > pairs[minPair[0]].a)
                                minPair[0] = index
                        }
                }

        }

        maxLast = 0
        maxPair[0] = A.size - 1
        for (index in pairs.size -2 downTo(0)){
            val pair = pairs[index]
            val intersect = calcIntersect(pairs[maxPair[maxLast]], pair)
            if (intersect != null)
                if (intersect < maxIntersect[maxLast]) {
                    maxLast += 1
                    maxIntersect[maxLast] = intersect
                    maxPair[maxLast] = index
                } else {
                    if (intersect < 0) {
                        var min = 0
                        var max = maxLast
                        var testIntersect: Double? = calcIntersect(pairs[maxPair[min]], pair)
                        while (max - min > 1) {
                            val mid = (max + min + 1) / 2
                            testIntersect = calcIntersect(pairs[maxPair[mid]], pair)
                            if (testIntersect != null)
                                if (testIntersect!! < maxIntersect[mid]) {
                                    min = mid
                                } else
                                    max = mid
                            else
                                min = mid
                        }
                        if (testIntersect != null) {
                            maxLast = min
                            testIntersect = calcIntersect(pairs[maxPair[min]], pair)
                            maxLast += 1
                            maxPair[maxLast] = index
                            maxIntersect[maxLast] = testIntersect!!
                        }
                    }else
                        if (intersect == 0.0) {
                            if (pair.b == pairs[maxPair[0]].b && pair.a < pairs[maxPair[0]].a)
                                maxPair[0] = index
                        }
                }

        }

        val difference2 = findMin( pairs, minPair, minIntersect, minLast , maxPair, maxIntersect, maxLast  )

        return minOf(difference, difference2)

    }
    fun findMin( pairs: Array<Pair>, mins: IntArray, minIntersect: DoubleArray, minCount: Int, maxs: IntArray, maxIntersect: DoubleArray, maxCount: Int) : Double {
        var min = 0
        var max = 0
        var result =     calcValue(pairs[maxs[max]], 0.0) - calcValue(pairs[mins[min]], 0.0)

        while (min < minCount || max < maxCount) {
            val nextMin = if (min == minCount) Double.MAX_VALUE else minIntersect[min + 1]
            val nextMax = if (max == maxCount) Double.MAX_VALUE else maxIntersect[max + 1]
            val intersectValue : Double
            if (Math.abs(nextMin) < Math.abs(nextMax)) {
                min += 1
                intersectValue = nextMin
            } else {
                max += 1
                intersectValue = nextMax
            }
            result = minOf(result, calcValue(pairs[maxs[max]], intersectValue) - calcValue(pairs[mins[min]], intersectValue))

        }
        return result
    }
    fun calcValue(pair: Pair, x: Double) : Double {
  //      println("vars "+ x + " " + pair.a + " " + pair.b + " " + (x * pair.a + pair.b))
      //  println(x * pair.a + pair.b)
        return x * pair.a + pair.b
    }
    fun calcIntersect (prevPair: Pair, thisPair: Pair) : Double? {
        if (prevPair.a == thisPair.a)
            return null
        if (prevPair.a == 0) {
            return (prevPair.b - thisPair.b).toDouble() / thisPair.a
        }
        if (thisPair.a == 0) {
            return (thisPair.b - prevPair.b).toDouble() / prevPair.a // negative value
        }
        return (prevPair.b - thisPair.b).toDouble() / (thisPair.a - prevPair.a).toDouble()
    }
    fun solutionX(H: IntArray): Int {
        if (H.size == 1)
            return H[0]
        val ban1 = IntArray(H.size)
        var maxHeight = 0
        for (i in 0..H.size - 2) {
            maxHeight = maxOf(H[i],maxHeight)
            ban1[i] = maxHeight * (i + 1)
        }
        maxHeight = 0
        var minSize = Int.MAX_VALUE
        for (i in H.size -1 downTo 1) {
            maxHeight = maxOf(H[i],maxHeight)
            val ban2 = maxHeight * (H.size - i)
            minSize = minOf(minSize,ban2 + ban1[i-1])
        }
        return minSize
    }
    fun solution2(A: IntArray): Int {
        val highest = IntArray(A.size + 1)
        val position = IntArray(A.size + 1)
        var lastPos = 0
        var maxSquare = 0
        for (i in 0..A.size -1){
            val current = A[i]
            var minPos = i
            while (current < highest[lastPos]) {
                minPos = position[lastPos]
                maxSquare = maxOf(maxSquare, minOf(highest[lastPos], i - position[lastPos]))
                lastPos -= 1
            }
            if (current > highest[lastPos]){
                lastPos += 1
                highest[lastPos] = current
                position[lastPos] = minPos
            }
        }

        while (lastPos > 0) {
            maxSquare = maxOf(maxSquare, minOf(highest[lastPos], A.size - position[lastPos]))
            lastPos -= 1
        }
        return maxSquare
    }
    fun solutionB(A: Array<IntArray>, K: Int): Int {
        val rows = A.size
        val cols = A[0].size
        val toLeft = IntArray(rows ){0}
        var column = 0
        var fromAbove = K
        var result = 0
        for (i in 0..cols -1) {
            for (j in 0..rows - 1) {
                val input = fromAbove + toLeft[j]
                when (A[j][i]) {
                    -1 -> {
                        toLeft[j] = input / 2
                        fromAbove = (input + 1) / 2
                    }
                    1 -> {
                        toLeft[j] = (input + 1) / 2
                        fromAbove = (input) / 2
                    }
                }
            }
            result = fromAbove
            fromAbove = 0
        }
        return result

    }
    fun solutionX(S: String): Int {
        val R = S.reversed()
        if (S.length < 2)
            return 0
        var total: Int
        if (S[0] == S[1]) {
            total = 1
        } else {
            total = 0
        }
        var rPointer = S.length - 1
        var maxPE = 0
        var maxPO = 0
        var maxPEPos = 0
        var maxPOPos = 0
        val countOdd = IntArray(S.length)
        val countEven = IntArray(S.length)
        var inPair = false
        countEven[0] = total
        for (i in 1.. S.length -2) {
            maxPE -= 1
            maxPO -= 1
            rPointer -= 1  // rPointer points to same character
            if (maxPO > 0) {
                val reverse = (2 * maxPOPos - i)
                val mirrorV = countOdd[reverse]
                if (mirrorV < maxPO) {
                    countOdd[i] = mirrorV
                    total += mirrorV
                } else {
                    val count = countPailindromes(S, R, i + 1 , rPointer + 1, maxPO)
                    countOdd[i] = count
                    maxPO = count
                    maxPOPos = i
                    total += count
                }
            } else {
                val count = countPailindromes(S, R, i + 1, rPointer + 1, 0)
                countOdd[i] = count
                maxPO = count
                maxPOPos = i
                total += count
            }
            if (maxPE > 0  && !inPair) {
                val reverse = (2 * maxPEPos - i + 1)
                val mirrorV = countEven[reverse]
                if (mirrorV < maxPE) {
                    countEven[i] = mirrorV
                    total += mirrorV
                } else {
                    val count = countPailindromes(S,R, i + 1, rPointer, maxPE)
                    countEven[i] = count
                    maxPE = count
                    maxPEPos = i
                    total += count
                    inPair = true
                }
            } else {
                val count = countPailindromes(S,R, i + 1 , rPointer , 0)
                countEven[i] = count
                total += count
                if (!inPair || count > maxPE) {
                    maxPE = count
                    maxPEPos = i
                    inPair = true
                } else {
                    inPair = false
                }

            }

         //   if (S[i+1] == R[rPointer])
            if (total > 100000000) return -1
    //        println(" After " + i + " " + total)
        }
        return total


    }
    fun countPailindromes(S: String, R: String, sStart: Int, rStart: Int, min: Int) : Int {

        if ((rStart + min  >= R.length)
            || (sStart + min  >= S.length)
            || (S.substring(sStart , sStart + min + 1 ) != R.substring(rStart, rStart + min + 1))) {
            return min
        }
        var increment = 1

        var count = min
        while ((rStart + count +  increment  <= R.length )
            && (sStart + count + increment  <= S.length)
            && (S.substring(sStart+ count , sStart + count +  increment  ) == R.substring(rStart + count, rStart + count +  increment )))   {
    //        println (S.substring(sStart+ count , sStart + count +  increment  ) + " " + R.substring(rStart + count, rStart + count +  increment ))
                count += increment
                increment = increment * 2
    //        println("increasing "+ increment + " " + count)
        }
        increment = increment / 2
        while (increment > 0) {
            if ((rStart + count + increment <= R.length)
                && (sStart + count + increment <= S.length)
                && (S.substring(sStart + count, sStart + count + increment ) == R.substring(rStart + count, rStart + count + increment ))
            ) {
     //           println (S.substring(sStart + count, sStart + count + increment ) + " " +  R.substring(rStart + count, rStart + count + increment ))
                count += increment
            }
 //           if ((rStart + count + increment <= R.length)
 //               && (sStart + count + increment <= S.length))

   //             println (S.substring(sStart + count, sStart + count + increment ) + " " +  R.substring(rStart + count, rStart + count + increment ))

    //        println(rStart + increment + count )
            increment = increment / 2


    //        println("decreasing "+ increment + " " + count)
        }
        return count

    }
/*    fun solution(P: IntArray, T: IntArray, A: IntArray, B: IntArray): Boolean {
        var toyCounter = 0
        val groups = IntArray(P.size)
        val inGroup = IntArray(P.size) {-1}
        val mergeGroup = IntArray(P.size) {-1}
        for (i in 0..A.size -1) {
            val a = A[i]
            val b = B[i]
            if (inGroup[a] == -1) {
                if (inGroup[b] == -1) {
                    inGroup[a] = i
                    inGroup[b] = i
                    groups[i] = P[a] + P[b] - T[a] - T[b]
                } else {
                    inGroup[a] = inGroup[b]
                    groups[inGroup[b]] += P[a] - T[a]
                }
            } else {
                if (inGroup[b] == -1) {
                    inGroup[b] = inGroup[a]
                    mergeGroup[inGroup[b]] = inGroup[a]

                } else {
                    // Join 2 groups
                    groups[]
                }

            }
        }
        val friends = Array<MutableList<Int>>(P.size){ mutableListOf()}
        for (i in 0..A.size - 1) {
            friends[A[i]].add(B[i])
            friends[B[i]].add(A[i])
        }
        val toProcess = BooleanArray(P.size){true}
        val pendingList = IntArray(P.size)
        var pending = 0
        for (i in 0..P.size - 1) {
            if (toProcess[i]) {
                pending = 0
                toProcess[i] = false
                pendingList[0] = i
                while (pending >= 0) {
                    val process = pendingList[pending]
                    toyCounter += P[process] - T[process]
                    pending -= 1
                    friends[process].forEach {

                        if (toProcess[it]) {
                            pending += 1
                            pendingList[pending] = it
                            toProcess[it] = false
                        }
                    }

                }
                if (toyCounter != 0)
                    return false
            }
        }
        return true

    } */
    fun solutionA(P: IntArray, T: IntArray, A: IntArray, B: IntArray): Boolean {
        var toyCounter = 0
        val friends = Array<MutableList<Int>>(P.size){ mutableListOf()}
        for (i in 0..A.size - 1) {
            friends[A[i]].add(B[i])
            friends[B[i]].add(A[i])
        }
        val toProcess = BooleanArray(P.size){true}
        val pendingList = IntArray(P.size)
        var pending = 0
        for (i in 0..P.size - 1) {
            if (toProcess[i]) {
                pending = 0
                toProcess[i] = false
                pendingList[0] = i
                while (pending >= 0) {
                    val process = pendingList[pending]
                    toyCounter += P[process] - T[process]
                    pending -= 1
                    friends[process].forEach {

                        if (toProcess[it]) {
                            pending += 1
                            pendingList[pending] = it
                            toProcess[it] = false
                        }
                    }

                }
                if (toyCounter != 0)
                    return false
            }
        }
        return true

    }
    fun x() {
        val x = mutableMapOf<Int,Int>()

    }
    fun solutionX(P: IntArray, T: IntArray, A: IntArray, B: IntArray): Boolean {
        var toyCounter = 0
        val friends = Array<MutableList<Int>?>(P.size){ null}
        for (i in 0..A.size - 1) {
            val fA = friends[A[i]]
            if (fA == null) {
                friends[A[i]] = mutableListOf(B[i])
            } else {
                fA.add(B[i])
            }
            val fB = friends[B[i]]
            if (fB == null) {
                friends[B[i]] = mutableListOf(A[i])
            } else {
                fB.add(A[i])
            }
        }
        val toProcess = BooleanArray(P.size){true}
        val pendingList = IntArray(P.size)
        var pending = 0
        for (i in 0..P.size - 1) {
            if (toProcess[i]) {
                pending = 0
                toProcess[i] = false
                pendingList[0] = i
                while (pending >= 0) {
                    val process = pendingList[pending]
                    toyCounter += P[process] - T[process]
                    pending -= 1
                    friends[process]?.forEach {

                        if (toProcess[it]) {
                            pending += 1
                            pendingList[pending] = it
                            toProcess[it] = false
                        }
                    }

                }
                if (toyCounter != 0)
                    return false
            }
        }
        return true

    }
 /*   fun solutionX(A: IntArray, B: IntArray): String {
        // write your code in Kotlin
        val nodes = Array(A.size + 1){mutableListOf<Int>()}
        A.forEachIndexed {
            index, i ->
            nodes[i].add(index)
            nodes[B[i]].add(index)
        }
        val discarded = IntArray(A.size)
        val depthCount = IntArray(901)
        val countToLeft = IntArray(A.size){-1}
        val parents = IntArray(A.size)
        countToLeft.forEachIndexed {
            index, i ->
            if (i == -1) {
                var discardedPoint = 1
                depthCount[0] = 0
                discarded[0] = index
                var count = 0
                var depthPointer = 0
                var listA = nodes[A[i]]
                var listB = nodes[B[i]]
                listA.forEach{
                    if (it != index) {
                        discarded[discardedPoint] = it
                        parents[discardedPoint] = i
                        discardedPoint += 1
                    }
                }
                listB.forEach{
                    if (it != index) {
                        discarded[discardedPoint] = it
                        depth[discardedPoint] = 1
                        discardedPoint += 1
                    }
                }
                while (discardedPoint > 0) {
                    val currentDepth = depth[discardedPoint]
                    val bridge = discarded[discardedPoint]
                    var amLeaf = true
                    if (countToLeft[bridge] > 0) {
                        amLeaf = true
                        depthCount[currentDepth] = countToLeft[bridge]
                    } else {
                        need to prevent going backwards and forwards
                        listA = nodes[A[bridge]]
                        listB = nodes[B[bridge]]
                        listA.forEach {
                            if (it != bridge) {
                                discarded[discardedPoint] = it
                                depth[discardedPoint] = currentDepth + 1
                                discardedPoint += 1
                                amLeaf = false
                            }
                        }
                        listB.forEach {
                            if (it != bridge) {
                                discarded[discardedPoint] = it
                                depth[discardedPoint] = currentDepth + 1
                                discardedPoint += 1
                                amLeaf = false
                            }
                        }
                        depthCount[currentDepth] = 1
                    }
                    if (amLeaf) {
                        countToLeft[bridge] = depthCount[currentDepth]
                        depthCount[currentDepth - 1] += depthCount[currentDepth]
                        discardedPoint -= 1
                        val prevDepth = depth[discardedPoint]
                        val prevBridge = discarded[discardedPoint]
                        if (prevDepth != currentDepth) {
                            countToLeft[prevBridge] = depthCount[prevDepth]
                            depthCount[prevDepth] = 0

                        }
                    }
                }


            }
        }

    }
*/
    fun TreeConstructor(strArr: Array<String>): String {
        val children = mutableMapOf<Int,Int>()
        val targets = mutableMapOf<Int,Int>()

        strArr.forEach {
            val nums = it.substring(1,it.count() - 1).split(',')
            val num1 =  nums[0].toInt()
            val num2 = nums[1].toInt()
            targets[num1] = num2

            if (children.containsKey(num2)) {
                children[num2] = children[num2]!! + 1
            } else {
                children[num2] = 1
            }
        }
        // Eliminate trees element hsa > 2 children
        if( children.filter {
            it.value > 2
        }.count() > 0)
            return "false"

        val iterateLoop = mutableMapOf<Int,Int>()
        var iteration = 0
        var rootFound = false
        targets.forEach {
            if (!iterateLoop.containsKey(it.key)) {
                // Otherwise we have already processed
                iteration += 1
                iterateLoop[it.key] = iteration
                var nextTarget = it.value
                var nextIterate = iterateLoop[nextTarget]
                while (targets.containsKey(nextTarget) && nextIterate == null) {
                    iterateLoop[nextTarget] = iteration
                    nextTarget = targets[nextTarget]!!
                    nextIterate = iterateLoop[nextTarget]
                }
                // 3 possibilities.
                // 1. found a previous iteration - all good
                // 2. found same iteration - we are in a loop
                // 3. Found a root (if so check that this is the only root)
                when (nextIterate) {
                    null -> {
                        if (rootFound)
                            return "false"
                        rootFound = true
                    }
                    iteration -> {
                        return "false" // we are in a loop
                    }
                }

            }
        }
        // code goes here
        return "true"

    }
    fun solutionx(N: Int): Int {
        // write your code in Kotlin
        if (N == 0) return 1
        val results = IntArray(N + 1)
        results[0] = 1
        var minRelevant = 0
        for (i in 1..N) {
            var maxValue = 3 * results[minRelevant]
            val minValue = results[i - 1] + 1
            for (j in minRelevant..i) {
                val testResult = 2 * results[j]
                if ((testResult >= minValue ) && (testResult < maxValue))
                    maxValue = testResult
            }
            results[i] = maxValue
            while (results[minRelevant] * 3 <= maxValue)
                minRelevant += 1
        }
        return results[N]
    }
        fun MinWindowSubstring(strArr: Array<String>): String {
            val source = strArr[0]
            val search = strArr[1]
            val searchKeys = mutableMapOf<Char, Int>()
            search.forEach {
                if (searchKeys.containsKey(it)) {
                    searchKeys[it] = searchKeys[it]!! - 1
                } else {
                    searchKeys[it] = -1
                }
            }
            var missing = searchKeys.size
            var startPos = 0
            var minStart = 0
            var minLength = Int.MAX_VALUE
            source.forEachIndexed {
                    index, ch ->
                if (searchKeys.containsKey(ch)) {
                    val newCount = searchKeys[ch]!! + 1
                    if (newCount == 0)
                        missing -= 1
                    searchKeys[ch] = newCount
                }
                while (missing == 0) {

                    if (minLength > index - startPos) {
                        minLength = index - startPos
                        minStart = startPos
                    }
                    val ch2 = source[startPos]
                    startPos += 1
                    if (searchKeys.containsKey(ch2)) {

                        val newCount = searchKeys[ch2]!! - 1
                        if (newCount == -1)
                            missing += 1
                        searchKeys[ch2] = newCount

                    }
                }

            }

            return source.substring(minStart, minStart + minLength + 1)

            // code goes here

        }




}
/*
    fun solution(A: IntArray): Int {
        val stack = LongArray(A.size)
        var pointer = 0
        var count = 0L
        var positive = true
        var maxValue = Int.MIN_VALUE
        A.forEach {
            maxValue = maxOf(it, maxValue) // In case everything -ve
            if (it >= 0 && positive  || it <= 0 && !positive)
                count += it
            else {
                while (pointer > 1 && (
                            (positive && count > -stack[pointer - 1] && stack[pointer -2] > -stack[pointer -1]) ||
                                    (!positive && -count > stack[pointer - 1] && -stack[pointer -2] > pointer -1))) {
                    count = count + stack[pointer - 1] + stack[pointer -2]
                    pointer -= 2
                }
                if (count != 0L) {
                    stack[pointer] = count
                    pointer += 1
                }
                count = it.toLong()
                positive = !positive
            }
        }

        while (pointer > 1 && (
                    (positive && count > -(stack[pointer - 1]) && stack[pointer -2] > -stack[pointer -1]) ||
                            (!positive && -count > stack[pointer - 1] && -stack[pointer -2] > pointer -1))) {
            count = count + stack[pointer - 1] + stack[pointer -2]
            pointer -= 2
        }

        if (count > 0) {  // Ignore -ve here
            stack[pointer] = count
            pointer += 1
        }
        while( pointer > 0) {
            pointer -= 1
            maxValue = maxOf(maxValue.toLong(), stack[pointer]).toInt()
        }
        return maxValue

        // write your code in Kotlin
    }

}

*/
        */
*/