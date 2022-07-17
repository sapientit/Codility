package uk.co.sapientit.templateapp

import kotlin.random.Random

class Carbo {
    fun solution(S: String) : Int {
        /*
        This is based on the buildT function as part of the KMP algorithm From Wikipedia (Knuth-Morris-Pratt).
        The bruteForce solution uses the full algorithm.
        This algorithm is well described on Wikipedia so you should read about it there.
        It is an algorithm for counting the number of occurrences of a string W in a longer string S.
        It does that by building a table "T" that tells you how much overlap there is with the string you have just compared
        with the start of the string W.
        So for example, searching for a string W ABABAC in the string S ABABABAC you will see that the first 5 characters
        match but not the 6th.  So it fails at character 6.  But by this time we have also matched the first 3 characters
        of W starting at charcter 3.  The T function shows that if we fail with the 6th character then we already know that
        characters 1,2 and 3 have already been matched so we start comparing with character 4.

        So that is the pure KMP algorith, but this is then modified for this problem.
        What we need is to count the number of occurrences of all the prefixes of S.

        In the basic case, we have a string
        1 2 3 4 5 6 7 8 9
        a b c a b c a b c
        We create an array (contains) that shows that position 5 ends with a prefix of length 2, position 6 ends with
        a prefix of length 3, etc.
        0 0 0 1 2 3 4 5 6
        We then have an array of counts.  Initially we have a count of 1 for the length of the string.
        (Note that a prefix of length 9 also implies that there is a string of length 8,7,6,5..1 )
        But since the 9th character (from our contain table) finishes with a prefix of length 6, then we add 1
        to our counts table for length 6.
        We don't do the same for 8 and 7 since we have already added a length 6 prefix (which means we have also added
        a length 5,4,...2 prefix).
        When we get to position 6 we now have the original count of 1 + 1 that we stored in the counts table.  So we have
        2 prefixes of length 6 (giving a result of 12).  But 6 ends with a prefix of length 3 so we add 1 (the
         number of additional 6s that we had found) to the counts table for length 3.
        So for 5 and 4 we have a count of 2 (one from the original full string + 1 from the additional 6 that we found)
        When we get to 3 we add the additional 1 that we stored and we have a total of 3 * 3s.  Total is 9 and we never
        got more than the 12 we stored from the 6 * 2.

        That was the basic case, but if we go to a different setup...
        1 2 3 4 5 6 7 8 9 10 11 12 13
        b b a b b c b b a b  b  a  b

        This gives a contains of
        0 1 0 1 2 0 1 2 3 4  5  3  4

        Consider what would happen if we just followed the above logic.
        Poisiton 13 adds a prefix of length 4.  Position 11 adds a prefix of length 5.  Both of those (by definition)
        include the b b  at positions 10 and 11.  So we end up adding 1 more 2 than we should.

        So when the contains doesn't just continue to go up in 1s then we need to remove 1 count for the overlap.
        So when we process position 11 we notices that position 12 was "contains 3" and we are now contains 5.

        There is an overlap of 2 (3 - 1) and we therefore subtract the previous count from counts[2].



         */
        var  pos = 1 // (the current position we are computing in T)
        var cnd = 0 // (the zero-based index in W of the next character of the current candidate substring)
        val T = IntArray(S.length + 1) // We still need this from the original KMP algorithm
        val counts = IntArray(S.length + 1) // Additional instances of each prefix
        val contains = IntArray(S.length + 1)
        T[0] = -1

        while (pos < S.length) {
            if (S[pos] == S[cnd]) {
                T[pos] = T[cnd]
                contains[pos + 1] = cnd + 1
                /*
                Consider the case AABACAAB.  The value for the final B needs to be 1, not 0 because AA is a valid prefix
                This continues where you have a repeating string
                 */
            } else {
                //               counts[cnd] += 1
                T[pos] = cnd

                while (cnd >= 0 && S[pos] != S[cnd]) {
                    cnd = T[cnd]
                }
                contains[pos + 1] = cnd + 1

                /*
                This code picks up cases such as ABACCABABx - if the final x is wrong then we need to test if we
                are a new word starting AB.
                 */

            }
            pos = pos + 1
            cnd = cnd + 1
        }

        T[pos] = cnd
        var max = S.length.toLong()
        var count = 1
        var prev = Int.MIN_VALUE
        for (i in counts.size - 1 downTo 2) {
            count += counts[i]
            max = maxOf(max, count.toLong() * i)
            if (contains[i] == prev - 1 ) {

                counts[contains[i]] += counts[i]
            } else {
                //  This code is the bit where we eliminate the overlapping section (see last section of description)
                if (prev > 0) {
                    val child = prev - 1
                    counts[child] -= count - counts[i]
                }
                counts[contains[i]] += count

            }
            prev = contains[i]
        }
        if (max > 1000000000)
            return 1000000000
        else
            return max.toInt()
    }


    /*
    Following code is for proving the solution with bruteforce - also includes original KMP algorithm
     */
    fun bruteForce(S: String) : Int {
        var max = S.length
        for (i in 2..S.length - 1) {
            val W = S.substring(0..i -1)
            val res = KMP(S,W)
            max = maxOf(max, i * res )
        }
        return max
    }
    fun generateData(rand: Random, size: Int) : Boolean {
        val char = CharArray(size) {
            val num = rand.nextInt(4)
            if (num == 0)
                'a'
            else
                'b'
        }
        val S = String(char)
        val t1 = solution(S)
        val t2 = bruteForce(S)
        if (t1 != t2) {
            println(S + " " + t1 + " " + t2)
            return false
        }
        return true
    }

    fun KMP(S: String, W: String): Int {  // Count occurrences of Word in String
        val T = buildT(W)
        var j = 0  //(the position of the current character in S)
        var k = 0  //(the position of the current character in W)

        var nP = 0
        var count = 0
        while (j < S.length) {
            if (W[k] == S[j]) {
                j = j + 1
                k = k + 1
                if (k == W.length) {
                    count += 1
                    // count + 1 or j-k
                    nP =  nP + 1
                    k = T[k] //(T[length(W)] can't be -1)
                }
            } else {
                k = T[k]
                if (k < 0) {
                    j = j + 1
                    k = k + 1
                }
            }
        }
        return count

    }
    fun buildT(W: String) : IntArray {
        /*
        From Wikipedia
         */
        var  pos = 1 // (the current position we are computing in T)
        var cnd = 0 // (the zero-based index in W of the next character of the current candidate substring)
        val T = IntArray(W.length + 1)
        T[0] = -1

        while (pos < W.length) {
            if (W[pos] == W[cnd]) {
                T[pos] = T[cnd]
                /*
                Consider the case AABACAAB.  The value for the final B needs to be 1, not 0 because AA is a valid prefix
                This continues where you have a repeating string
                 */
            } else {

                T[pos] = cnd

                while (cnd >= 0 && W[pos] != W[cnd]) {
                    cnd = T[cnd]
                }

                /*
                This code picks up cases such as ABACCABABx - if the final x is wrong then we need to test if we
                are a new word starting AB.
                 */

            }
            pos = pos + 1
            cnd = cnd + 1
        }


        T[pos] = cnd
        return T
    }

}