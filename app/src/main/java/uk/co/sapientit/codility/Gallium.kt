package uk.co.sapientit.templateapp

class Gallium {
    /*
    We want to maximise the powers of 5 and the powers of 2 in the 3 numbers we choose.
    The first step is to extract just those powers from each number.
    Then we sort into decreasing powers of 5 (and within that decreasing powers of 2).
    Since 5**13 is > max possible value, we only need to cater for powers of 0-12.
    And since within the powers of 5 we sorted into powers of 2 only the first 3 in each
    power of 5 could be useful.  So we are limited to 39 numbers
    The actual loop will use all of those 39 numbers.  It would be possible to eliminate some
    but the additional tests would probably not gain us much.

     */
    fun solution(A: IntArray): Int {
        // Extract powers of 5 and powers of 2.  Entry i is the equivalent number in A[i]
        val power5 = IntArray(A.size)
        val power2 = IntArray(A.size)
        for (i in A.indices) {
            while (A[i] % 5 == 0) {
                power5[i] += 1
                A[i] = A[i] / 5
            }
            while (A[i] % 2 == 0) {
                power2[i] += 1
                A[i] = A[i] / 2
            }
        }
        val comp1 = Comparator<Int>{
            a1,a2 ->
            if (power5[a1] == power5[a2])
                power2[a2] - power2[a1]
            else
                power5[a2] - power5[a1]
        }
        //  This is the indices of A in descending powers of 5 (and power of 2 within)
        val decrease5 = IntArray(A.size){it}.sortedWith(comp1).toIntArray()

        //  We will cycle through the entries finding the first entry for each power of 5
        val p5 = IntArray(13){Int.MIN_VALUE}
        var prev5 = Int.MAX_VALUE
        // Find first occurence of each power of 5
        for (i in decrease5.indices) {
            val current = power5[decrease5[i]]
            if (current != prev5)  {
                p5[current] = i
                prev5 = current
            }
        }
        //   No point in starting at 13 if the largest number is 5**N (N < 13)
        val max5 = power5[decrease5[0]]
        // And the main loop
        val max = getMax(power5, power2, p5, decrease5, max5 + 1, 0, 1, 0, 0)

        return max
    }
    fun getMax(power5: IntArray, power2: IntArray,p5:  IntArray, decrease5: IntArray, from: Int, prev: Int, level : Int, count5: Int, count2 : Int) : Int {
        // For simplicity let the function terminate itself rather than having tests everywhere for level = 3
        if (level == 4) {
            return minOf(count5, count2)
        }
        var max = 0
        //  If we have used the first entry for 5**N then need to try with the next entry.  Not applicable for first run
        if (level != 1) {
            // prev is the index into decrease5
            val sameLevel = prev + 1 // it is decreasing so add 1 for next entry
            if (sameLevel < decrease5.size) { // check still in bounds
                val slIndex = decrease5[sameLevel]
                if (power5[slIndex] == from) {// and that it is still same power of 5
                    val nCount5 = count5 + power5[slIndex]
                    val nCount2 = count2 + power2[slIndex]
                    max = getMax(power5, power2, p5, decrease5, from, sameLevel, level + 1, nCount5, nCount2)
                }
            }
        }
        if (from > 0) {
            for (i in from - 1 downTo 0) { // loop down through powers of 5 and iterate
                val maxForPower = p5[i]
                if (maxForPower != Int.MIN_VALUE) {
                    val mfpIndex = decrease5[maxForPower]
                    val nCount5 = count5 + power5[mfpIndex]
                    val nCount2 = count2 + power2[mfpIndex]
                    max = maxOf(max,getMax(power5, power2, p5, decrease5, i, maxForPower, level + 1, nCount5, nCount2))

                }
            }
        }
        return max
    }
}