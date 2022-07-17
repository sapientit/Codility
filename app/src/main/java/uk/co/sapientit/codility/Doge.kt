package uk.co.sapientit.templateapp

class Doge {
    fun solution(P: IntArray, T: IntArray, A: IntArray, B: IntArray): Boolean {
        var toyCounter = 0
        val firstFriend = IntArray(P.size) { Int.MAX_VALUE }
        val lastFriend = IntArray(P.size) { Int.MAX_VALUE }
        val nextA = IntArray(A.size) { Int.MAX_VALUE }
        val nextB = IntArray(B.size) { Int.MAX_VALUE }
        for (i in 0..A.size - 1) {
            val a = A[i]
            val b = B[i]
            if (firstFriend[a] == Int.MAX_VALUE) {
                firstFriend[a] = i
                lastFriend[a] = i
            } else {
                val prev = lastFriend[a]
                if (A[prev] == a)
                    nextA[prev] = i
                else
                    nextB[prev] = i
                lastFriend[a] = i
            }
            if (firstFriend[b] == Int.MAX_VALUE) {
                firstFriend[b] = i
                lastFriend[b] = i
            } else {
                val prev = lastFriend[b]
                if (A[prev] == b)
                    nextA[prev] = i
                else
                    nextB[prev] = i
                lastFriend[b] = i
            }
        }
        val pendingList = IntArray(P.size)
        var pending : Int
        val processed = BooleanArray(P.size) { false }
        for (i in 0..P.size - 1) {
            if (!processed[i]) {
                var toyCount = 0
                processed[i] = true
                pendingList[0] = i
                pending = 1
                while (pending > 0) {
                    pending -= 1
                    val current = pendingList[pending]
                    toyCount += P[current] - T[current]
                    var next = firstFriend[current]
                    while (next != Int.MAX_VALUE) {
                        var nextPerson: Int
                        if (A[next] == current) {
                            nextPerson = B[next]
                            next = nextA[next]
                        } else {
                            nextPerson = A[next]
                            next = nextB[next]
                        }
                        if (!processed[nextPerson]) {
                            pendingList[pending] = nextPerson
                            pending += 1
                            processed[nextPerson] = true
                        }

                    }
                }
                if (toyCount != 0)
                    return false

            }

        }
        return true

    }
}