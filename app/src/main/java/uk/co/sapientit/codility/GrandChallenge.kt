package uk.co.sapientit.templateapp

import kotlin.random.Random

class GrandChallenge {
    /*
    With just 2 characters, we can keep a "balance indicator".  Add 1 for the first character and substract 1 for the
    second character.  Then we can cycle through looking for the last occurrence of this balance indicator.

    When we find a third character we substitute it for either char1 or char2. If the last char1 is before last char2 then
    we substitute it for char1 else char2.
    The last occurrence of balance indicator is before the character currently being processed (since we only cycle
    through once) but we do need to check that it includes both char1 and char2.
    We cycle from the first occurrence of the old char1 or char2 until the last occurence of the character being
    replaced and check whether this produces a new max
    At this point the first relevant char2 is the character after the last (previous) char1.
    We don't reset the balance indicator at this point, so all the stored balances remain valid.
     */
    fun solution(S: String) : Int {
        var last1 = 0
        var c1 = S[0]
        var index = 0
        //     var bal = 0
        var firstc1 = 0
        val lastBal = IntArray(S.length * 2 + 1)
        val balValue = IntArray(S.length + 1)
        var bal = S.length - 1
        // Cycle through while we only have 1 character
        while (index < S.length && S[index] == c1) {
            lastBal[bal] = index
            balValue[index] = bal
            bal = bal + 1
            last1 = index
            index += 1


        }
        if (index == S.length)
            return 0
        // We have found the first char2
        var c2 = S[index]
        var last2 = index
        var firstc2 = index
        var max = 0
        // And cycle through everything else
        while (index < S.length) {
            // Store the values BEFORE the effect of the current character
            lastBal[bal] = index
            balValue[index] = bal
            val current = S[index]
            if (current == c1) {
                last1 = index
                bal = bal + 1 // WE already have this character so add 1 to balance indicator
            } else {
                if (current == c2) {
                    last2 = index
                    bal = bal - 1 // or substract 1 if it is char2
                } else {
                    // start cycle from the lowest first occurrence of char1 or char2
                    val minFirst = minOf(firstc1, firstc2)
                    if (last1 > last2) {
                        for (i in minFirst..last2) {
                            val currentValue = balValue[i]
                            if (lastBal[currentValue] > firstc1)
                                max = maxOf(max, lastBal[currentValue] - i)
                        }
                        c2 = current
                        firstc1 = last2 + 1
                        firstc2 = index
                        last2 = index
                        bal = bal - 1
                    } else {
                        for (i in minFirst..last1) {
                            val currentValue = balValue[i]
                            if (lastBal[currentValue] > firstc2)
                                max = maxOf(max, lastBal[currentValue] - i)
                        }
                        c1 = current
                        firstc2 = last1 + 1
                        firstc1 = index
                        last1 = index
                        bal = bal + 1

                    }
                }
            }
            index += 1
        }
        //  We need to do a final check for the last character
        lastBal[bal] = index

        val minFirst = minOf(firstc1, firstc2)
        if (last1 > last2) {
            for (i in minFirst..last2) {
                val currentValue = balValue[i]

                if (lastBal[currentValue] > firstc1)
                    max = maxOf(max, lastBal[currentValue] - i)
            }
        } else {
            for (i in minFirst..last1) {
                val currentValue = balValue[i]

                if (lastBal[currentValue] > firstc1)
                    max = maxOf(max, lastBal[currentValue] - i)
            }

        }
        return max
    }
    /*
    Testing the accuracy of the solution using brute force for comparison
     */
    fun bruteForce(S: String) : Int {
        var max = 0
        for (i in 0..S.length - 2) {
            val c1 = S[i]
            var c1Count = 1
            var index = i + 1
            while (index < S.length && S[index] == c1) {
                c1Count += 1
                index += 1
            }
            if (index < S.length) {
                val c2 = S[index]
                var c2Count = 1
                max = maxOf(max, 2)
                index += 1
                while (index < S.length && (S[index] == c1 || S[index] == c2)) {
                    if (S[index] == c1)
                        c1Count += 1
                    else
                        c2Count += 1
                    if (c1Count == c2Count)
                        max = maxOf(max, c1Count + c2Count)
                    index += 1
                }
            }

        }
        return max
    }

    fun generateData(rand: Random, size: Int) : Boolean {
        var S = ""
        for (i in 0..size) {
            val next = rand.nextInt(21)
            val c: Char
            when (next) {
                0,1,2,3,4,5,6,7,8,9,10 -> c = 'a'
                11,12,13,14,15,16,17,18,19,20 -> c = 'b'
                21 -> c = 'c'
                else -> c = 'd'
            }
            S = S + c
        }

        val t1 = solution(S)
        val t2 = bruteForce(S)

    //    println(S + t1)
        if (t1 != t2) {

            println(S+ " " + t1 + " " + t2)
            return false
        }
        return true
    }

}