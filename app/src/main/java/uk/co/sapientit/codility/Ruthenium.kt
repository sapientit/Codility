package uk.co.sapientit.templateapp

class Ruthenium {
    /*
    We maintain two count arrays - one for the starting point and one for the endpoint.  For each startpoint we just
    extend the endpoint until it no longer satisfies the criteria of only K swaps.
    Consider the case ABAAAAAB. With K 1. The initial comparison will stop once we get to the second B.  Then we will
    use a start point of B.  Since the second B already doesn't satisfy the criteria we won't move the endpoint.
    The value of endpoint - startpoint is meaningless at this point, but the key point is that it is less than the
    maximum we already obtained with the A's, so doesn't affect the result.
    The two special cases are where the maximum is at the end. For these the maximum is the number of each character we find
    + K.  This can give the second special case that this is > A.size.
     */
    fun solution(A: IntArray, K: Int): Int {
        val highCount = IntArray(100001)
        val lowCount = IntArray(100001)
        var highIndex = 0
        var lowIndex = 0
        var max = 0
        while (highIndex < A.size) {
            val start = A[lowIndex]
            while (highIndex < A.size && highCount[start] - lowCount[start] + K >= highIndex - lowIndex) {
                highCount[A[highIndex]] += 1
                highIndex += 1
            }
            max = maxOf(max, highIndex - lowIndex - 1)
            lowCount[start] += 1
            lowIndex += 1
        }
        // We need to process the previous lowindex again
        lowIndex -= 1
        highIndex -= 1 // A.size - 1
        lowCount[A[lowIndex]] -= 1 // Reverse the add that we did at the end of the main loop
        // Deal with the end case differently
        while (lowIndex < A.size) {
            val start = A[lowIndex]
            val total = highCount[start] - lowCount[start] + K
            val checkOverflow = minOf(total, A.size)
            max = maxOf(max,checkOverflow)
            lowIndex += 1
        }
        return max
    }
}