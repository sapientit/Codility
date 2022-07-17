package uk.co.sapientit.templateapp

class Niobium {
    /*
    The solution is based on all values in a row being identical to another row or for all elements to be
    the inverse of another row.
    I considered using bits for efficiency but then I end up with a bytearray that needs to be stored as a string.
    I think it can be done without losing data, but am not sure.
    Therefore tried it with just creating strings of 0s and 1s.  That seems to be efficient enough for the purpose
    of this exercise.  Then use a hashmap to store the count for each combo.
    Note that you can't add 1 character to a string at a time, since that creates a new string every time you do it.
    For a string of length 100000 that would be slow.  Therefore create a chararray up front and then just create the string
    once.
     */
    fun solution(A: Array<IntArray>): Int {
        val sols = HashMap<String, Int>()
        val chars = CharArray(A[0].size)
        for (i in A.indices) {
            var inverse: Boolean
            val a = A[i]
            if (a[0] == 1)
                inverse = true
            else
                inverse = false
            for (j in a.indices) {
                if (a[j] == 1)
                    if (inverse) {
                        chars[j] = '1'
                    } else {
                        chars[j] = '0'
                    }
                else {
                    if (inverse) {
                        chars[j] = '0'
                    } else {
                        chars[j] = '1'
                    }
                }
            }
            val string = String(chars)
            val solVal = sols[string]
            if (solVal == null)
                sols[string] = 1
            else
                sols[string] = solVal + 1

        }
        var max = 0
        for (v in sols.values)
            max = maxOf(max,v)
        return max
    }
}