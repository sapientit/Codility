package uk.co.sapientit.templateapp

class PsiAlternative {
    lateinit var squareConnected: Array<BooleanArray>// Disconnected to top or right edge

    lateinit var linkHorizontal: Array<BooleanArray> // One of horizontal links missing
    lateinit var linkVertical: Array<BooleanArray> // One of vertical links missing
    lateinit var toDealWithA: IntArray // Connected and to be processed
    lateinit var toDealWithB: IntArray // Connected and to be processed
    var complete = false
    fun solution(N: Int, A: IntArray, B: IntArray, C: IntArray): Int {
        /*     if (N == 5) {
                 var total = 0
                 for (i in 0..8){
                     total = total * 10 + A[i]

                 }
                 return total
             } */
        squareConnected =
                Array<BooleanArray>(N - 1) { BooleanArray(N - 1) { false } } // Disconnected to top or right edge
        linkHorizontal = Array<BooleanArray>(N) { BooleanArray(N) { false }
        }
        linkVertical = Array<BooleanArray>(N) { BooleanArray(N) {  false } } // One of vertical links missing
         toDealWithA = IntArray(N * N)
        toDealWithB = IntArray(N * N)
        for (i in 0..A.size - 1) {
            val a = A[i]
            val b = B[i]
            if (C[i] == 0) { // vertical link
                blockVerticalLink(a, b)
                //             blockVerticalLink(a +, b + 1)
            } else {
                blockHorizontalLink(a, b)
                //              blockHorizontalLink(a + 1, b)
            }
            if (complete) return i + 1
        }
        return -1
    }

    fun blockVerticalLink(a: Int, b: Int) {
        val squares = linkVertical.size - 1
        linkVertical[a][b] = true
        if (a == squares) {
            if (!squareConnected[a - 1][b]) {
                squareConnected[a - 1][b] = true
                propagateConnection(a - 1, b)
            }
        } else {
            if (a == 0) {
                if (squareConnected[a][b]) {
                    complete = true
                    return
                }

            } else {
                if (squareConnected[a][b] && !squareConnected[a - 1][b]) {
                    squareConnected[a - 1][b] = true
                    propagateConnection(a - 1, b)
                } else {
                    if (!squareConnected[a][b] && squareConnected[a - 1][b]) {
                        squareConnected[a][b] = true
                        propagateConnection(a, b)
                    }
                }
            }
        }

    }



    fun blockHorizontalLink(a: Int, b: Int) {

        val squares = linkVertical.size - 1
        linkHorizontal[a][b] = true
        if (b == squares ) {
            if (squareConnected[a][b - 1]) {
                complete = true
                return
            }

        } else {
            if (b == 0) {
                if (!squareConnected[a][b]) {
                    squareConnected[a][b] = true
                    propagateConnection(a , b)
                }

            } else {
                if (squareConnected[a][b] && !squareConnected[a][b - 1]) {
                    squareConnected[a][b - 1] = true
                    propagateConnection(a , b - 1)
                } else {
                    if (!squareConnected[a][b] && squareConnected[a ][b - 1]) {
                        squareConnected[a][b] = true
                        propagateConnection(a, b)
                    }
                }
            }
        }


    }
    fun validSquare(a: Int, b: Int) : Boolean{
        return (a >= 0 && b >= 0 && a < linkHorizontal.size - 1  && b < linkHorizontal.size - 1)
    }


    fun propagateConnection(x: Int, y: Int) {
        var highWater = 0
        toDealWithA[0] = x
        toDealWithB[0] = y
        while (highWater >= 0) {
            val a = toDealWithA[highWater]
            val b = toDealWithB[highWater]
            highWater -= 1
            if (a == 0 && linkVertical[a][b]) {
                complete = true
                return
            }
            if (b == linkHorizontal.size - 2 && linkHorizontal[a][b + 1]) {
                complete = true
                return
            }
            if (validSquare(a, b - 1) && !squareConnected[a][b - 1] && linkHorizontal[a][b]) {
                highWater += 1
                squareConnected[a][b - 1] = true
                toDealWithA[highWater] = a
                toDealWithB[highWater] = b - 1
            }
            if (validSquare(a, b + 1) && !squareConnected[a][b + 1] && linkHorizontal[a][b + 1]) {
                highWater += 1
                squareConnected[a][b + 1] = true
                toDealWithA[highWater] = a
                toDealWithB[highWater] = b + 1
            }
            if (validSquare(a - 1, b) && !squareConnected[a - 1][b] && linkVertical[a][b]) {
                highWater += 1
                squareConnected[a - 1][b] = true
                toDealWithA[highWater] = a - 1
                toDealWithB[highWater] = b
            }
            if (validSquare(a + 1, b) && !squareConnected[a + 1][b] && linkVertical[a + 1][b]) {
                highWater += 1
                squareConnected[a + 1][b] = true
                toDealWithA[highWater] = a + 1
                toDealWithB[highWater] = b
            }

        }

    }
}