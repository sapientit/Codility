package uk.co.sapientit.codility

import kotlin.random.Random

class CodeAlone {
    /*
    There are two possible cases.  An extreme example of the first case is
    aabbbbbbbbbbbbbbbbbba.  Clearly the answer is to move the a all the way to the beginning.  You can work out that
    the steps required is "index of 3rd a - index of 1st a - 2".
    In this case, there are already 3b's together somewhere in the string.  The reverse is obviously also true - if
    there are 3 consecutive a's the answer will be the minimum of (3rd b - 1st b - 2).
    But what about the case where we don;t have 3 consecutive a's or b's.  My firs thought was that we only needed
    to consider 6 consecutive characters, but then realised that there were cases where it made a difference if you consider
    7 consecutive characters (ok - I submitted and got errors :-( ).
    So then I tried it with 7 - and guess what.  Still there were special cases where the 8th character made a difference!
    At this point I started to get a bit concerned.  Would I find that I needed to go to 9?  Or maybe my assumption was
    wrong and I couldn't just consider a subset of the string.
    It is fairly obvious that if we eliminate 3 consecutive identical characters then the worst case is 4. abbabba for
    example,
    But intuitively it seemed right, and indeed 8 is the correct answer.  I am sure wsomeone will have provided a proof.
    So how to easily consider 8 characters?
    Since each character is a or b,  we can turn them into bits.  256 different combinations.  Not too many.
    It seems best to just pre-calculate all the valid possibilities.  Out of hte 256, a lot can be eliminated because
    they have >5 or < 3 a's (so within those 8 characters you can't make aaa and bbb.).
    To do this I start with all the valid solutions where the 111 is to the left of the 000 (when processing I also store
    the inverse of each string (1s and 0s flipped) and then jut add the option with a trailing 0 to the queue for
    processing.  This halves the nuber of steps again.
    Then we build the table - starting from the valid solutions I see what happens if I swap 2 consecutive bits.  To do
    that I start with a table of possible positions ... 110000, 11000, 1100, 110, 11,  If I "and" that with the current
    number then if the result is 0 or the swap mask then reversing achieves nothing. Otherwise an xor will swap those
    two bits.
    Check I haven't got that number already and add 1 to the steps to get to this new number.  Add it to the queue to
    process.
    Once I have the table, then just cycle through the string, checking the bit equivalent of every 8 string substring.

     */
    class Deque(size: Int) {
        val array = IntArray(size)
        var hwm = 0
        var lwm = 0
        fun addLast(num: Int) {
            array[hwm++] = num
        }
        fun removeFirst(): Int {
            return array[lwm++]
        }
        fun isNotEmpty() : Boolean {
            return lwm != hwm
        }
    }
    fun solution(S: String) : Int {
        val solutions = intArrayOf(
            "00111000".toInt(2),
            "01110000".toInt(2),
            "01111000".toInt(2),
            "01110001".toInt(2),
            "11110000".toInt(2),
            "11110001".toInt(2),
            "11111000".toInt(2),
            "10111000".toInt(2),
            "11100000".toInt(2),
            "11100001".toInt(2),
            "11100010".toInt(2),
            "11100011".toInt(2)
        )
        val swaps = intArrayOf(
            "110000".toInt(2),
            "11000".toInt(2),
            "1100".toInt(2),
            "110".toInt(2),
            "11".toInt(2),
            "1100000".toInt(2),
            "11000000".toInt(2)
        )
        val steps = IntArray(1 shl 8) {Int.MAX_VALUE}
        val queue = Deque(256)
        if (S.length < 6) return -1
        val solCount: Int
        val flip : Int
        val swapCount: Int
        when (S.length) {
            6 -> {
                solCount = 1
                flip = "111111".toInt(2)
                swapCount = 5
            }
            7 -> {
                solCount = 4
                flip = "1111111".toInt(2)
                swapCount = 6
            }
            else -> {
                solCount = 12
                flip = "11111111".toInt(2)
                swapCount = 7
            }
        }
        for (i in 0 until solCount) {
            val sol = solutions[i]
            steps[sol] = 0
            steps[sol xor flip] = 0
            if (sol and 1 == 0) {
                queue.addLast(sol)
            } else {
                queue.addLast(sol xor flip)
            }
        }
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            for (i in 0 until swapCount) {
                val swap = swaps[i]
                val next = current xor swap
                val different = current and swap
                if (different != 0 && different != swap  &&  steps[next] == Int.MAX_VALUE) {
                    val nextX = next xor flip
                    steps[next] = steps[current] + 1
                    steps[nextX] = steps[current] + 1
       //             println(Integer.toBinaryString(current) + " " + Integer.toBinaryString(next)+ " " + steps[current])
       //             println(Integer.toBinaryString(current xor flip) + " " + Integer.toBinaryString(nextX) + " " + steps[current])

                    if (next and 1 == 0) {
                        queue.addLast(next)
                    } else {
                        queue.addLast(nextX)
                    }
                }
            }
        }
        var min1 = Int.MAX_VALUE
        var min0 = Int.MAX_VALUE
        var prev0 = Int.MIN_VALUE
        var current0 = Int.MIN_VALUE
        var prev1 = Int.MIN_VALUE
        var current1 = Int.MIN_VALUE
        for (i in 0 until S.length) {
            if (S[i] == 'a') {
                if (prev1 != Int.MIN_VALUE) {
                    min1 = minOf(min1, i - prev1 - 2)
                }
                prev1 = current1
                current1 = i
            } else {
                if (prev0 != Int.MIN_VALUE) {
                    min0 = minOf(min0, i - prev0 - 2)
                }
                prev0 = current0
                current0 = i

            }
        }
        if (min1 == Int.MAX_VALUE || min0 == Int.MAX_VALUE) {
            return -1
        }
        if (min1 == 0) return min0
        if (min0 == 0) return min1
        var mapKey = 0
        var minSwaps = Int.MAX_VALUE
        for (i in 0 until S.length) {
            mapKey = (mapKey shl 1) and flip
            if (S[i] == 'a') {
                mapKey++
            }
            if (i >= 7) {
                minSwaps = minOf(minSwaps,steps[mapKey])
            }
        }
        minSwaps = minOf(minSwaps,steps[mapKey])
        if (minSwaps == Int.MAX_VALUE) {
            return -1
        }
        return minSwaps
    }

    fun generateData(rand: Random, result: Int) : Boolean{
        val chars = CharArray(8){if (rand.nextInt(2) == 1) 'a' else 'b'}
        val s = String(chars)
        if (solution(s) == result) println(s)
        return true
    }
}