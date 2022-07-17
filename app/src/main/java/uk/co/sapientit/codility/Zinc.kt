package uk.co.sapientit.templateapp

import kotlin.random.Random

class Zinc {
    /*
    Consider doing this as 3 passes over the data.  The code does it all in 1 pass, but logically we do it in 3.
    The first pass counts the number of unique shows up to each day.  For duplicates it also builds a map of
    the previous occurrence of each show.
    The second pass counts the number of unique 2 shows.  If every show is different then each day just adds the current
    day's show * the number of preceding shows to the count from the previous day.
    If the show is a duplicate then we have to subtract the number of unique 2 shows that the previous occurences of that
    show generated 9which is the number of unique shows before the previous occurence of this show).
    The 3rd pass does the same but with 2 shows rather than 1 show.
     */
    fun solution(A: IntArray): Int {
        if (A.size < 3)
            return 0
        val last = IntArray(100001){Int.MAX_VALUE}
        val prev = IntArray(A.size)
        val unique1 = LongArray(A.size) // unique shows up to this point (including self)
        val unique2 = LongArray(A.size) // unique 2 show combos up to this point
        val unique3 = LongArray(A.size) // unique 3 show combos up to this point
        unique1[0] = 1
        last[A[0]] = 0
        for (i in 1 until A.size) {
            val current = A[i]
            if (last[current] == Int.MAX_VALUE)
                unique1[i] = unique1[i-1] + 1
            else
                unique1[i] = unique1[i-1]
            prev[i] = last[current]
            last[current] = i
            val prevCurrent = prev[i]
            if (prevCurrent == Int.MAX_VALUE || prevCurrent == 0) {
                unique2[i] = unique2[i -1] + unique1[i - 1]
                unique3[i] = unique3[i -1] + unique2[i - 1]
            } else {
                unique2[i] = unique2[i -1] + unique1[i - 1] - unique1[prevCurrent - 1]
                unique3[i] = unique3[i -1] + unique2[i - 1] - unique2[prevCurrent - 1]
            }

        }
        return (unique3[A.size - 1] % 1000000007).toInt()
    }
    fun bruteForce(A: IntArray) : Int {
        val solSet = HashSet<String>()
        val end = A.size - 1
        for (i in 0..end - 2) {
            for (j in i + 1 .. end - 1)
                for (k in j+1 .. end) {
                    val sol = "" + A[i] +"," + A[j] + "," + A[k]
                    solSet.add(sol)
                }
        }
        return solSet.size
    }
    fun generateData(rand: Random, size: Int): Boolean {
        val A = IntArray(size)
        for (i in A.indices) {
            A[i] = rand.nextInt(A.size - 1)
        }
        val t1 = solution(A)
        val t2 = bruteForce(A)
        if (t1 != t2) {
            println("Failed " + t1 + " " + t2)
            for (i in A.indices) {
                print (A[i])
                print(" ")
            }
            return false
        }
        return true
    }
}