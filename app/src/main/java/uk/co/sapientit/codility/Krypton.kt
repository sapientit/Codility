package uk.co.sapientit.codility

class Krypton {
    /*
    In order to get a trailing 0 you have to multiply by 10.  The number of 0s is therefore
    the nymber of pairs of "2" powers and "5" powers. (So 8 is 2**3 and 25 is 5**2 Multiply them together
    and you get 2 trailing zeroes.
    So the number of trailing zeroes is the minimum of the powers of 2 and the powers of 5 in all the cells you go
    through.  There is a special case.  If any cell is 0 then the maximum answer will be 1 (if we go through the
    0 then the final result will be 0 - 1 trailing zero.

    This is standard dynamic programming.  We ccould creat and N by N matrix to store the minim 2s and 5s for each cell
    but it isn't actually necessary, sicne the same can be achieved by just processing one row at a time.

    Cell 3,3 is the minimum of cell 3,2 or cell 2,3

    Using just a single dimensional array, entry twos[0] just add the number of powers of two from A[i].  For all other
    entries we take the minimum number of twos from A[i-1] and A[i] (A[i] refers to the previous line still).  Then add
    the number of twos from this cell A[i][j]) and we have a new minimum to get to that cell.

    Obviously we also do the same with fives.

    And then when we get to the end, we take the minimum of twos or fives from the last position and that is our answer
    (or if we have a zero anywhere in the grid, then as above the maximum answer is 1).

    O(N**2) - we process each cell once.
     */
    var minResult = Int.MAX_VALUE
    fun solution(A: Array<IntArray>): Int {

        val twos = IntArray(A.size)
        val fives = IntArray(A.size)
        val (two0, five0) = factorise(A[0][0])
        twos[0] = two0
        fives[0] = five0
        for (i in 1 until A.size) {
            val (two,five) = factorise(A[0][i])
            twos[i] = twos[i-1] + two
            fives[i] = fives[i-1] + five
        }
        for (i in 1 until A.size) {
            val (twoi,fivei) = factorise(A[i][0])
            fives[0] += fivei
            twos[0] += twoi
            for (j in 1 until A.size) {
                val (two,five) = factorise(A[i][j])
                twos[j] = minOf(twos[j],twos[j-1]) + two
                fives[j] = minOf(fives[j],fives[j-1]) + five
            }
        }
        return minOf(minResult,minOf(twos[twos.lastIndex], fives[fives.lastIndex]))
    }
    fun factorise(n: Int): Pair<Int,Int> {
        if (n == 0) {
            minResult = 1
            return Pair(1,1)
        }
        var fives = 0
        var twos = 0
        var rem = n
        while (rem % 5 == 0) {
            fives++
            rem = rem / 5
        }
        while (rem % 2 == 0) {
            twos++
            rem = rem / 2
        }
        return Pair(twos,fives)
    }
}