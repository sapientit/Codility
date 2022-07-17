package uk.co.sapientit.templateapp

class Lithium {
    /*
    To be honest - I looked at the solution for this one.  I had worked out how to do it, except for Booth's algorithm
    for finding the rotated value that is lowest.  Obviously this is something you need to know.  It is based on the KMP
    algorithm, which looks useful for other challenges as well.
    Anyway, that means that the code follows the solution provided by Codility.
    Step 1 - create a string that represents each clock rotated in the same way.  This rotation is such that the differences
    between the hands are least - so if the gaps between the hands are 30, 10 and 5 "minutes" then we rotate it to  so that
    the 5 comes first 5,30,10  (or for 10,5,30,5 it would be 5,10,5,30 etc).
    Since we need to sort these, I create a string of the number as base 36 (the highest base that Kotlin can do).  This means
    that each hand takes 6 bytes instead of potentially 10.  Probably not required, but with 500 hands that is a string
    of 3000 rather than a string of 5000 to compare nlog(n) times.
    Note that Booth's algorithm is for strings, so it required a minor modification to look at an integer array rather than
    an element of a string.
    Having got all the different strings for the different clocks we sort them and then the maths is n (n-1) / 2 for n
    identical clocks.

     */
    fun solution(A: Array<IntArray>, P: Int): Int {
        val clocks = Array<String>(A.size){""}
        for (i in A.indices) {
            clocks[i] = buildClockKey(A[i],P)
        }
        clocks.sort()
        var prev =""
        var count = 0
        var pairs = 0
        for (i in clocks.indices) {
            if (clocks[i] == prev) {
                count += 1
            } else {
                pairs +=  count * (count - 1) / 2
                prev = clocks[i]
                count = 1
            }
        }
        pairs +=  count * (count - 1) / 2

        return pairs
    }

    fun buildClockKey(hands: IntArray, P: Int) : String {
        val differences = IntArray(hands.size)
        hands.sort()
        for (i in 1 until hands.size) {
            differences[i] = hands[i] - hands[i - 1]
        }
        differences[0] = hands [0] + P - hands[hands.size - 1]
        val start = booth(differences)
        val result = CharArray(hands.size * 6)
        val base = differences[start]
        var index = 0
        for (i in start until differences.size) {
            val num = (differences[i]).toString(radix = 36)
            for (j in 0 until num.length) {
                result[index + j] = num[j]
            }
            index += 6
        }
        for (i in 0 until start) {
            val num = (differences[i]).toString(radix = 36)
            for (j in 0 until num.length) {
                result[index + j] = num[j]
            }
            index += 6
        }
        return String(result)
    }

    fun booth(source: IntArray) : Int { // rotate string to alphabetically smallest result
        val size = source.size
        val S = IntArray(size * 2) {if (it >= size) source[it - size] else source[it]} //# Concatenate array to it self to avoid modular arithmetic
        val f = IntArray(S.size){-1} // # Failure function
        var k = 0  //# Least rotation of string found so far
        for (j in 1 until S.size) {
            val sj = S[j]
            var i = f[j - k - 1]
            while (i != -1 && sj != S[k + i + 1]) {
                if (sj < S[k + i + 1]) {
                    k = j - i - 1
                }
                i = f[i]
            }
            if (sj != S[k + i + 1]) { //  # if sj != S[k+i+1], then i == -1
                if (sj < S[k]) { //  # k+i+1 = k
                    k = j
                }
                f[j - k] = -1
            } else {
                f[j - k] = i + 1
            }
        }
        return k
    }
}
