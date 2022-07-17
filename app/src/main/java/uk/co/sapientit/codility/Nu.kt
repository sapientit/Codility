package uk.co.sapientit.templateapp

class Nu {
    fun solution(A: IntArray, B: IntArray, P: IntArray, Q: IntArray, R: IntArray, S: IntArray): Int {
        val medians = IntArray(R.size) {0}
        for (i in 0..R.size -1) {
            val aSize = Q[i] - P[i]
            val bSize = S[i] - R[i]
            val big : IntArray
            val small : IntArray
            val bigf : Int
            val smallf: Int
            val smalle : Int
            val base : Int
            var max : Int
            if (aSize > bSize) {
                big = A
                small = B
                bigf = P[i]
                smallf = R[i]
                smalle = S[i]
                //  base = aSize - bSize - 1 + bigf
                base = (aSize + bSize) / 2 - bSize + bigf
                max = bSize + 1
            } else {
                big = B
                small = A
                bigf = R[i]
                smallf = P[i]
                smalle = Q[i]
                // base = bSize - aSize - 1 + bigf
                base = (bSize + aSize) / 2 - aSize + bigf
                max = aSize + 1
            }
            val sum = max - 1
            var min = 0
            if (big[base] > small[smalle]) {
                medians[i] = big[base]
            } else {
                if (big[base + max] < small[smallf]) {
                    medians[i] = big[base + max]
                } else {
                    while (max - min > 1) {
                        val mid = (max + min + 1) / 2
                        val smallIndex = sum - mid
                        if (big[base + mid] > small[smallIndex + smallf]) {
                            max = mid
                        } else
                            min = mid
                    }
                    if (big[base + max] <= small[sum - min + smallf])
                        medians[i] = big[base + max]
                    else
                        medians[i] = small[sum - min + smallf]
                }
            }
            println(medians[i])
        }

        medians.sort()
        val midPoint = medians.size / 2
        return medians[midPoint]

    }
}