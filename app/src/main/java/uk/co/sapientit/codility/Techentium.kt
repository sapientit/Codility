package uk.co.sapientit.templateapp

class Techentium {
    /*
    The trick to this is to solve one diagonal at a time.  Starting with 0,0 that means 0,1 and 1,0 become
    candidates for example.
    At each step we build a list of candidates in the next diagonal that are accessible form the current
    candidates whose value is equal to the maximum of all the candidates.
    So in the matrix
    1 3 2
    1 2 1
    5 3 1

    0,1 amd 1,0 are accessible from the first candidate (0,0)
    1,1 and 0,2 re accessible from the value 3 - note that the 5 in 2,0 is accessible from 1,0 but the value of 1,0
    is less than the mximum of the candidates (3 in 0,1))

    The other bit of code is to avoid candidates from being included twice.  For example the "1" at 1,2 is accessible
    from the candidates 1,1 and 0,2.

    As a note, I maintain candidatesX and candidatesY. technically candidatesxY is computable as index - candidatesX[i].

    Also rather than building a string that is modified, build a CharArray of fixed size and then do a one off conversion
     */
    fun solution(A: Array<IntArray>): String {
        val M = A.size
        val N = A[0].size
        val ascii0 = '0'.toInt()
        val MAXDEPTH = N + M - 1
        val soln = CharArray(MAXDEPTH)
        val maxCandidates = maxOf(N,M)
        var candidatesX = IntArray(maxCandidates)
        var newCandX = IntArray(maxCandidates)
        var candidatesY = IntArray(maxCandidates)
        var newCandY = IntArray(maxCandidates)
        var candidates = 1
        var newCand = 0
        var depth = 0
        while (depth < MAXDEPTH) {
            var checkRight = -1
            var max = -1
            for (i in 0 until candidates) {
                val char = A[candidatesX[i]][candidatesY[i]]
                if (char > max) {
                    newCand = 0
                    max = char
                    checkRight = -1
                }
                if (char >= max) {
                    if (candidatesX[i] < M - 1 && candidatesX[i] != checkRight) {
                        newCandX[newCand] = candidatesX[i] + 1
                        newCandY[newCand] = candidatesY[i]
                        newCand += 1
                    }

                    if (candidatesY[i] < N - 1) {
                        newCandX[newCand] = candidatesX[i]
                        newCandY[newCand] = candidatesY[i] + 1
                        newCand += 1
                        checkRight = candidatesX[i] - 1
                    }
                }
            }
            soln[depth] = (max + ascii0).toChar()
            depth += 1
            val tempX = candidatesX
            val tempY = candidatesY
            candidatesX = newCandX
            candidatesY = newCandY
            newCandX = tempX
            newCandY = tempY
            candidates = newCand
            newCand = 0
        }
        return String(soln)
    }
}