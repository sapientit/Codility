package uk.co.sapientit.templateapp

class FutureMobility {
    /*
    Similar to a previous challenge we start with a delta from target in each space.
    There are 3 cases:
    1. The delta from the following space is the same sign (or zero) as current space
    2. The absolute delta from the following space is less than current delta
    3. The absolute delta is >- current delta

    For case 1 we just borrow all of the required stones from the space 2 further along
    For case 2 we borrow all the delta from the following space and the rest from 2 spaces further along
    For case 3 we just reduce the delta from the following space.

    Add processing for mod 1000000007 and tests for invalid solutions and it is done
     */
    fun solution(A: IntArray, B: IntArray): Int {
        var count =0L
        val difference = LongArray(A.size){B[it].toLong() - A[it]}
        for (i in 0 .. A.size - 2) {
            val diff = difference[i]
            count = (count + Math.abs(diff)) % 1000000007 //  Not sure if this is necessary - the number could get very large
            val diff1 = difference[i+1]
            if ((diff > 0 && diff1 < 0) || (diff < 0 && diff1 > 0)) {
                if (Math.abs(diff) > Math.abs(diff1)) {
                    difference[i + 1] = 0
                    difference[i + 2] += diff + diff1
                } else {
                    difference[i + 1] += diff
                }
            } else {
                if (i < A.size -2)
                    difference[ i + 2] += diff
            }
        }
        if (difference[A.size - 1] != 0L)
            return -1
        return count.toInt()
    }
}