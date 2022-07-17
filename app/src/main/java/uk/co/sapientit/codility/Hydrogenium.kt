package uk.co.sapientit.templateapp

class Hydrogenium {
    fun solution(A: IntArray, B: IntArray, C: IntArray, D: IntArray): Int {
        val links = Array<IntArray>(D.size + 1){IntArray(D.size + 1){Int.MAX_VALUE } }
        links[0][0] = 0
        for (i in 0..A.size -1) {
            val x : Int
            val y: Int
            if (A[i] > B[i]) {
                x = A[i]
                y = B[i]
            } else {
                x = B[i]
                y = A[i]
            }
            links[x][y] = minOf(links[x][y], C[i])
        }
        var searchFrom = 0
        while (links[searchFrom][searchFrom] < 1000000001) {
            val currentDist = links[searchFrom][searchFrom]
            if ( D[searchFrom] >= currentDist)
                return currentDist
            links[searchFrom][searchFrom] = Int.MAX_VALUE
            if (searchFrom < D.size - 1) {
                for (i in searchFrom + 1 .. D.size -1) {
                    val dist = links[i][searchFrom]
                    if (dist < Int.MAX_VALUE) {
                        links[i][searchFrom] = Int.MAX_VALUE
                        links[i][i] = minOf(links[i][i],currentDist + dist)
                    }
                }
            }
            if (searchFrom > 0) {
                for (i in 0..searchFrom + 1 ) {
                    val dist = links[searchFrom][i]
                    if (dist < Int.MAX_VALUE) {
                        links[searchFrom][i] = Int.MAX_VALUE
                        links[i][i] = minOf(links[i][i],currentDist + dist)
                    }
                }
            }
            var minDist = Int.MAX_VALUE
            for (i in 0..D.size -1) {
                if (links[i][i] < minDist) {
                    searchFrom = i
                    minDist = links[i][i]
                }
            }
        }
        return -1
    }
}