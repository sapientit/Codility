package uk.co.sapientit.codility

import kotlin.random.Random

class  Gamma {
    fun solution(S: String): Int {
        /*
        This is basically Manacher's algorithm.

        The important point that makes it O(N) time is that if a palindrome contains a palindrome on the left side
        then it contins the same on the right.  So we only need to examine each character at most twice.

        For example
        A B A D A B A
        ABA is a palindrom centred on the B.
        The whole string is a plaindrom centred on D.

        This means, without checking, that there is another palindrom ABA centred on the B after the D.

        Note that this second plaindrome may be longer if it reaches the end of the enclosing palindrom.

        A B A D A B A D

        There is a plaindrom ABA
        Pal ABADABA contains the original ABA, so we know there is a palindrom ABA on the right side.
        Bit this reaches to the current end, so we have to check whether it continues past it.
         */

        val size = S.length * 2 + 3
        val chars = CharArray(size){
                n-> when {
            n == 0 -> '@'
            n == size - 1 -> '$'
            n % 2 == 1 -> '%'
            else -> S[n / 2 - 1]
        }
        }
        val count = IntArray(size)
        var currentEnd = -1
        var currentMid = -1
        var index = 2
        var countPal = 0
        while  (index < size - 2) {
            var length = 1
            if (index < currentEnd) {
                length = count[2 * currentMid - index]
                while (index + length < currentEnd) {
                    count[index] = length
                    countPal += (length - 1) /2
                    index++
                    length = count[2 * currentMid - index]
    //                countPal += (length - 1) / 2
                }
                length = currentEnd - index
            }
            while (chars[index + length] == chars[index - length]) {
                length ++
            }
            count[index] = length
            countPal += (length - 1) /2
  //          countPal += (length - 1) / 2
            if (index + length > currentEnd) {
                currentEnd = index + length - 1
                currentMid = index
            }
            index++
            if (countPal > 100000000) return -1
        }
        return countPal

    }
    fun validateSolution(S: String): Int {
        var total = 0
        for (i in 1 until S.length) {
            var offset = 1
            while (i - offset >= 0 && i + offset < S.length && S[i-offset] == S[i + offset]) {
                offset += 1
            }
            total += offset - 1
            offset = 1
            while (i - offset >= 0 && i + offset - 1 < S.length && S[i-offset] == S[i + offset - 1]) {
                offset += 1
            }

            total += offset - 1

        }
        return total
    }
    fun generateData(length: Int, rand: Random) : Boolean {
        val S = CharArray(length)
        for (i in 0 until length) {
            val next = rand.nextInt(0,2)
            S[i] = if (next == 0) 'a' else 'b'
        }
        val string = String(S)
        val r1 = solution(string)
        val r2 = validateSolution(string)
        if (r1 != r2)
            println(string + " " + r1 + " " + r2)
        return r1 == r2
    }
}