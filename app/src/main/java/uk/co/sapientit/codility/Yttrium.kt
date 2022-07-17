package uk.co.sapientit.templateapp

import kotlin.random.Random

class Yttrium {
/*
The tricky part of this is accuracy, keeping track of pointers within pointers and making sure you are using the right
pointer at the right level.  You can read into this that I messed up :-)
I renamed the variables with a prefix to make it clear which level is which and then it became a lot simpler.
But how to solve this?
We are removing the middle section of the string, so keeping the end and keeping the start (with the special case that end
or start could be zero length).
We want K different characters in the bit we keep.
So if we start removing with the first occurrence of the nth character then we need to finish removing with the last
occurrence of the mth character (counting from the end) where m + n = K + 2
So for example in aaaabbbbccccddddd....xxxxyyyyyzzzz and K = 4
If we start cutting out from the first occurrence of c (the 3rd character) then we need to end cutting with the last
occurrence of x (the 3rd from the end).
We could generate a list of which characters occur first and last by reading front to back and then back to front.
With only 26 characters it is easier to just find the first occurence of each character and the last occurence and then sort
p_first[n] - pointer to the first occurrence of the nth character (alphabetically).  p_first[2] is first occurence of C
p_last[n] - pointer to the last occurrence of the nth character (alphabetically).
s_starting[n] is an array of numbers 0..25 sorting the p_first values.  So if S_starting[0] = 3 then the first character
in the string is d, and if s_starting[1] = 5 then after 1 or more d's the next character is f etc.
s_ending[n] - the same as s_starting except sorted on values of p_last

So we know that the substring to remove must start with one of the values in p_first and must end with one of the values
in p_last.
The calculation is which p_last relates to which p_first.
Starting from s_starting[K] (it can't be any higher or there would be > K values in the prefix part) we work our way down
through the s_ending values.  If the same character occurs at the end as at the start then we don't need to remove it.  If
it is a new character that we have encountered then that is the p_last to use (since we have K distinct values in the prefix).
Now look at s_starting[K -1].  This means that the character represented by s_starting[K - 1] is no longer part of the prefix
If it is already part of the suffix then the previous s_ending/p_last is sufficient. This is true if p_last for this character
is > the end value we are already looking at. (exclude is left as 0)
Otherwise we carry on down through the s_ending values until we have included exactly 1 new character. (exclude is set to 1)
 */

    fun solution(S: String, K: Int): Int {
        val asciiA = 'a'.toInt()
        val p_first = IntArray(26){ Int.MAX_VALUE}
        val p_last = IntArray(26){Int.MAX_VALUE}
        val size = S.length
        var h_count = 0
        for (i in S.indices) {
            val current = S[i].toInt() - asciiA
            if (p_first[current] == Int.MAX_VALUE) {
                h_count += 1
                p_first[current] = i
            }
            p_last[current] = i
        }
        if (h_count < K)
            return -1
        if (h_count == K)
            return 0
        if (h_count == 0)
            return size
        val compStart = Comparator<Int>{f1,f2 -> p_first[f1] - p_first[f2]}
        val s_starting = IntArray(26){it}.sortedWith(compStart)
        val compEnd = Comparator<Int>{f1,f2 -> p_last[f1] - p_last[f2]}
        val s_ending = IntArray(26){it}.sortedWith(compEnd)
        var h_maxEnd = h_count - 1
        var exclude = 0
        var min = Int.MAX_VALUE

        for (i in K downTo 0) {
            val p_currentStart = p_first[s_starting[i]]
            var s_end = s_ending[h_maxEnd]
            var p_endFirst = p_first[s_end]
            while (h_maxEnd >= 0 && (p_endFirst < p_currentStart || exclude > 0 )  ) {
                if (p_endFirst > p_currentStart)
                    exclude -= 1
                h_maxEnd -= 1
                if (h_maxEnd >= 0) {
                    s_end = s_ending[h_maxEnd]
                    p_endFirst = p_first[s_end]
                }
            }
            if (h_maxEnd < 0) {
                break
            }
            val length: Int
            if (p_last[s_end] == size - 1)
                length = size - p_currentStart
            else {
                val p_endLast = p_last[s_end]
                length = p_endLast -  p_currentStart + 1
            }
            min = minOf(min, length)

            val h_nextI = i - 1
            if (h_nextI >= 0) {
                val s_nextChar = s_starting[h_nextI]
                val p_nextLast = p_last[s_nextChar]
                if (p_nextLast < p_last[s_end])
                    exclude = 1
            }

        }
        return min
    }
    /*
    Test the solution against random test data
     */
    fun bruteForce(S: String, K: Int) : Int {
        val found = HashMap<Char, Boolean>()
        var min = trySol(found, S, -1, K)
        for (i in S.indices) {
            found[S[i]] = true
            if (found.size > K) {
                if (min == Int.MAX_VALUE)
                    return -1
                return min
            }
            min = minOf(min, trySol(found, S, i, K))
        }
        if (found.size == K)
            return 0
        return -1
    }
    fun trySol(found: HashMap<Char, Boolean>, S: String, begin: Int, K: Int) : Int {
        val foundEnd = HashMap<Char, Boolean>()
        var index = S.length - 1
        while (index > begin) {
            val current = S[index]
            if (!found.containsKey(current)) {
                foundEnd[current] = true
                if (found.size + foundEnd.size > K) {
                    return index - begin
                }
            }
            index -= 1
        }
        return Int.MAX_VALUE
    }
    fun generateData(rand: Random, size: Int) : Boolean {
        var string = ""
        val asciiA = 'a'.toInt()
        for (i in 0 until size) {
            val new = (rand.nextInt(5) + asciiA).toChar()
            string = string + new
        }
        var k = 1
        while (true){
            val t1 = solution(string,k)
            val t2 = bruteForce(string,k)
            if (t1 != t2) {
                println (string + " " + k + " " + t1 + " " + t2)
                return false
            }
            if (t1 == -1)
                return true
            k += 1
        }
    }
}