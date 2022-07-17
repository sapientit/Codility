package uk.co.sapientit.templateapp

import kotlin.random.Random

class Strontium {
    /*
    This coding test is more a question of accuracy than problem solving.  There are 5 types of words.
    Assuming that c is the character with the longest subsequence possible, then we can have words such as
    cccccccccc, cccccccxxxx, xxxxxccccccccc,  xxxxccccccccxxx or cccccccxxxxxxccccc
    The complicated case is the last one.  The longest suffix may be in the same words as the longest prefix, which means
    you can't use both.
    The first case you can add to anything other than the 4th case.  So we total all of these up as midTotal.
    Cases 2 and 3 we store the longest as leftMax and rightMax.
    The 4th case we just have to count the 'c's and make that the starting point for maximum.
    The 5th case is therefore the only interesting one.
    We loop through all such words and store both the longest and second longest suffix and prefix.
    Then it is just a matter of working out which to use.
     */

    fun solution(words: Array<String>): Int {
        val asciiA = 'a'.toInt()
        val leftMax = IntArray(26){0}
        val rightMax = IntArray(26){0}
        val midTotal = IntArray(26){0}
        val firstBoth = IntArray(26){Int.MAX_VALUE}
        val nextBoth = IntArray(words.size){Int.MAX_VALUE}
        val rightVal = IntArray(words.size)
        val leftVal = IntArray(words.size)
        var maxLength = 0
        for (i in words.indices) {
            val current = words[i]
            val first = current[0]
            var index = 1
            while(index < current.length && current[index] == first)
                index += 1
            val left = index
            index = current.length - 1
            val last = current[index]
            while(index >= 0  && current[index] == last)
                index -= 1
            val right = current.length - 1 - index
            val fIndex = first.toInt() - asciiA
            val lIndex = last.toInt() - asciiA
            if (first == last) {
                if (left + right >= current.length) {
                    midTotal[fIndex] += current.length
                } else {
                    nextBoth[i] = firstBoth[fIndex]
                    leftVal[i] = left
                    rightVal[i] = right
                    firstBoth[fIndex] = i
                    maxLength = maxOf(maxLength,getMaxInternal(current))
                }
            } else {
                leftMax[fIndex] = maxOf(leftMax[fIndex], left)
                rightMax[lIndex] = maxOf(rightMax[lIndex], right)
                maxLength = maxOf(maxLength,getMaxInternal(current))
            }
        }
        /*
        We have the longest suffix and prefix.  Not look through all words that start and end with smae letter.
         */
        for (i in firstBoth.indices) {
            var leftC = leftMax[i]
            var rightC = rightMax[i]
            var next = firstBoth[i]

            var leftBMax = next // Longest prefix
            var rightBMax = next // longest suffix
            var leftBSecond = Int.MAX_VALUE // second longest prefix
            var rightBSecond = Int.MAX_VALUE // second longest suffix
            if (next != Int.MAX_VALUE)
                next = nextBoth[next]
            while (next != Int.MAX_VALUE) {
                val leftN = leftVal[next]
                val rightN = rightVal[next]
                if (leftBSecond == Int.MAX_VALUE || leftN > leftVal[leftBSecond]) {
                    if (leftN > leftVal[leftBMax]) {
                        leftBSecond = leftBMax
                        leftBMax = next
                    } else {
                        leftBSecond = next
                    }
                }
                if (rightBSecond == Int.MAX_VALUE || rightN > rightVal[rightBSecond]) {
                    if (rightN > rightVal[rightBMax]) {
                        rightBSecond = rightBMax
                        rightBMax = next
                    } else {
                        rightBSecond = next
                    }
                }
                next = nextBoth[next]
            }
            // Check that we have any words to look at
            if (leftBMax != Int.MAX_VALUE) {
                /* If this is false there is only 1 word (which is the maximum)
                If true then if second longest is > than standalone prefix then this must be better
                 */
                if (leftBSecond != Int.MAX_VALUE) {
                    leftC = maxOf(leftC, leftVal[leftBSecond])
                }
                if (rightBSecond != Int.MAX_VALUE) {
                    rightC = maxOf(rightC, rightVal[rightBSecond])
                }
                val lbVal = leftVal[leftBMax]
                val rbVal = rightVal[rightBMax]
                // If longest suffix is also longest prefix
                if (leftBMax == rightBMax && lbVal > leftC && rbVal > rightC) {
                    // Then work out whether it is better to use it as suffix or prefix
                    if (lbVal - leftC > rbVal - rightC)
                        leftC = lbVal
                    else
                        rightC = rbVal
                } else {
                    // Otherwise use it as suffix or prefix if longer than standalone
                    leftC = maxOf(leftC, lbVal)
                    rightC = maxOf(rightC, rbVal)
                }
            }
            val total = midTotal[i] + leftC + rightC
            maxLength = maxOf(maxLength, total)
        }
        return maxLength
    }
    fun getMaxInternal(S: String): Int {
        // Look for longest subsequnce internal to the word (4th case in decription above)
        var prev = ' '
        var maxC = 1
        var count = 0
        for (i in 0 until S.length) {
            if (S[i] == prev) {
                count += 1
            } else {
                maxC = maxOf(maxC, count)
                prev = S[i]
                count = 1

            }
        }
        return maxC // doesn't return max including last character as we get that anyway
    }
    fun bruteForce(words: Array<String>): Int {
        val used = BooleanArray(words.size){false}
        var max = tryCombos(words, used, "")
        return max
    }
    fun tryCombos(words: Array<String>, used: BooleanArray, string: String): Int {
        var allUsed = true
        var max = 0
        for (i in words.indices ) {
            if (!used[i]) {
                allUsed = false
                val newString = string + words[i]
                used[i] = true
                max = maxOf(max, tryCombos(words, used, newString))
                used[i] = false
            }
        }
        if (allUsed)
            return getLongest(string)
        return max
    }
    fun getLongest(string: String) : Int {
        var max = 0
        var count = 0
        var prev = string[0]
        for (i in string.indices) {
            if (string[i] == prev)
                count += 1
            else {
                max = maxOf(max, count)
                count = 1
                prev = string[i]
            }
        }
        return max
    }
    fun generateData(rand: Random, size: Int) : Boolean {
        val words = Array<String>(size){""}
        for (i in 0 until size) {
            var string = ""
            for (i in 0..10) {
                if (rand.nextInt(3) == 1)
                    string = string + 'a'
                else
                    string = string + 'b'
            }
            words[i] = string
        }
        val t1 = solution(words)
        val t2 = bruteForce(words)
        if (t1 != t2) {
            println( "Failed " + t1 + " " + t2)
            for (i in words.indices)
                println (words[i])
            return false
        }
        return true
    }
}