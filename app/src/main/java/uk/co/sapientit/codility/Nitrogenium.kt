package uk.co.sapientit.templateapp

class Nitrogenium {

    fun solution(A: IntArray, B: IntArray): IntArray {
        val islands = IntArray(100001)
        var goingUp = true
        var prev = -1
        A.forEach {
            if (it > prev) {
                if (!goingUp) {
                    islands[prev] += 1
                    goingUp = true
                }
                prev = it
            } else {
                if (it < prev) {
                    if (goingUp) {
                        islands[prev] -= 1
                        goingUp = false
                    }
                }
                prev = it
            }
        }
        if (goingUp)
            islands[prev] -= 1
        var count = 1
        for (i in islands.indices) {
            count += islands[i]
            islands[i] = count
        }
        var result = IntArray(B.size) { index -> islands[B[index]]}
        return result
    }

}