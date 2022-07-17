package uk.co.sapientit.templateapp

class Ferrum {
    /*
    Simple challenge - just keep a running total and the last index for each total.
    Then read values again and look for last occurrence of each value.
     */
    fun solution(A: IntArray): Int {
        val total = IntArray(A.size)
        // last is twice the size of A so that we can cope with all +1 and all -1
        val last = IntArray(A.size * 2 + 1){ -1} // -1 means never found
        var subTotal = A.size
        for (i in A.indices) {
            subTotal += A[i]
            last[subTotal] = i
            total[i] = subTotal
        }
        var index = 0
        val limit = A.size
        if (subTotal >= limit) { // Check the case that we can take the entire array
            return limit
        }
        var maxLength = last[limit] + 1 //  Start from original subtotal.  Will give a zero result if not found
        while (maxLength < limit - index) { // No point in continuing to the end
            val current = total[index]
            maxLength = maxOf(maxLength,last[current] - index)
            index += 1
        }
        return maxLength
    }
}