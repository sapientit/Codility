package uk.co.sapientit.templateapp

class Psi {


    lateinit var linkHorizontal: Array<BooleanArray> // horizontal links exists
    lateinit var linkVertical: Array<BooleanArray> // vertical links exists

    lateinit var nodeConnected: Array<BooleanArray>// Disconnected to top or right edge
    var connected = false

    lateinit var toDealWithA: IntArray // Connected and to be processed
    lateinit var toDealWithB: IntArray // Connected and to be processed

    fun solution(N: Int, A: IntArray, B: IntArray, C: IntArray): Int {
        nodeConnected = Array<BooleanArray>(N) { BooleanArray(N) { false } } // Disconnected to top or right edge
        linkHorizontal = Array<BooleanArray>(N) { BooleanArray(N) { true }
        }
        linkVertical = Array<BooleanArray>(N) { BooleanArray(N) {  true } } // One of vertical links missing

        toDealWithA = IntArray(N * N)
        toDealWithB = IntArray(N * N)
        for (i in 0..A.size -1) {
            if (C[i] == 0) {
                linkVertical[A[i]][B[i]] = false
            } else {
                linkHorizontal [A[i]][B[i]] = false
            }
        }
        var restored = A.size
        if (connect(N -1, N -1))
            return -1
        while (restored > 0) {
            restored -= 1
            val x = A[restored]
            val y = B[restored]
            if (C[restored] == 0) {
                linkVertical[x][y] = true
                if (nodeConnected[x ][y] && !nodeConnected[x][y +1]) {
                    if (connect(x ,y + 1))
                        return restored + 1
                } else {
                    if (nodeConnected[x][y +1] && !nodeConnected[x][y]) {
                        if (connect(x, y))
                            return restored + 1
                    }

                }

            } else {
                linkHorizontal [x][y] = true
                if (nodeConnected[x ][y] && !nodeConnected[x + 1][y]) {
                    if (connect(x +1 ,y))
                        return restored + 1
                } else {
                    if (nodeConnected[x +1][y] && !nodeConnected[x][y]) {
                        if (connect(x, y))
                            return restored + 1
                    }

                }
            }

        }
        return 0

    }
    fun connect(a: Int, b: Int) : Boolean {
        var highWater = 0
        toDealWithA[0] = a
        toDealWithB[0] = b
        nodeConnected[a][b] = true
        while (highWater >= 0) {
            val x = toDealWithA[highWater]
            val y = toDealWithB[highWater]
            highWater -= 1
            if (validNode(x+1,y)  && linkHorizontal[x][y] && !nodeConnected[x+1][y]) {
                highWater += 1
                toDealWithA[highWater] = x+1
                toDealWithB[highWater] = y
                nodeConnected[x +1][y] = true
            }
            if (validNode(x-1,y)  && linkHorizontal[x -1][y] && !nodeConnected[x -1][y]) {
                highWater += 1
                toDealWithA[highWater] = x -1
                toDealWithB[highWater] = y
                nodeConnected[x -1][y] = true
            }
            if (validNode(x,y - 1)  && linkVertical[x][y -1] && !nodeConnected[x][y -1]) {
                highWater += 1
                toDealWithA[highWater] = x
                toDealWithB[highWater] = y -1
                nodeConnected[x][y - 1] = true
            }
            if (validNode(x,y + 1)  && linkVertical[x][y] && !nodeConnected[x][y +1]) {
                highWater += 1
                toDealWithA[highWater] = x
                toDealWithB[highWater] = y + 1
                nodeConnected[x][y + 1] = true
            }
            if (nodeConnected[0][0])
                return true

        }
        return false
    }
    fun validNode(a: Int, b: Int) : Boolean {
        return (a >=0 && b>= 0 && a < nodeConnected.size && b <nodeConnected.size)
    }
}