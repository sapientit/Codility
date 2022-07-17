package uk.co.sapientit.templateapp

class Molybdenum {
    /*
    In order to get to MORE than half, there can only be 2 possible solutions and they are only 1 different.
    So we only have to count 3 different numbers.  If the possible solutions are S and S + 1, then we need to count
    those numbers and also S - 1
    In the code, the possible solutions recorded are actually S -1 and S (hence the need to add 1 at the end).
    Step 1 - count occurences of all numbers
    Step 2 - look for possible solutions
    Step 3 - read the first K numbers and record counts of the 3 different numbers of interest
    Step 4 - read the rest of the array.  Take count[S] + (number of S - 1 values in last K) - (number of S values in last K)

    It doesn't feel right to store the count of S-1, S, S + 1 for every element, but they alternative is to maintain
    counts up to index and counts up to index - K.  That is more efficient in space, but not much different in
    processing time
     */
    fun solution(K: Int, M: Int, A: IntArray): IntArray {
        val count = IntArray(M + 2)
        val halfSize = (A.size + 2) / 2
        A.forEach {
            count[it] += 1
        }
        val possibleSol = IntArray(2){Int.MIN_VALUE}
        var possSolsFound = 0
        for (i in 0..M) {
            if (count[i] + count[i+1] >= halfSize) {
                possibleSol[possSolsFound] = i
                possSolsFound += 1
            }
        }
        if (possSolsFound == 0)
            return intArrayOf()
        val numsFound = Array<IntArray>(3){IntArray(A.size + 1)}
        var index = 1
        while (index < K) {
            val diff = A[index - 1] - possibleSol[0]
            for (i in 0..possSolsFound) {
                if (i == diff) {
                    numsFound[i][index] = numsFound[i][index - 1] + 1
                } else
                    numsFound[i][index] = numsFound[i][index - 1]
            }
            index += 1
        }
        val isSolution = BooleanArray(2){false}
        while (index <= A.size) {
            val diff = A[index - 1] - possibleSol[0]
            for (i in 0..possSolsFound) {
                if (i == diff) {
                    numsFound[i][index] = numsFound[i][index - 1] + 1
                } else
                    numsFound[i][index] = numsFound[i][index - 1]
            }
            for (i in 1..possSolsFound) {
                if (count[possibleSol[i - 1] + 1] +
                        numsFound[i - 1][index] +
                        numsFound[i][index - K] -
                        numsFound[i ][index] -
                        numsFound[i - 1][index - K] >= halfSize) {
                    isSolution[i - 1] = true
                }
            }
            index += 1
        }
        if (isSolution[1] && isSolution[0])
            return intArrayOf(possibleSol[0] + 1, possibleSol[1] + 1)
        else {
            if (isSolution[0])
                return intArrayOf(possibleSol[0] + 1)
            else
                if (isSolution[1])
                    return intArrayOf(possibleSol[1] + 1)
        }
        return intArrayOf()
    }
}