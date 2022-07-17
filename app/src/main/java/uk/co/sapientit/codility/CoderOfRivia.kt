package uk.co.sapientit.templateapp

class CoderOfRivia {
 //  Golden award   https://app.codility.com/cert/view/certKAJ3JG-34UXSDP6TUEZEX23/
    /*
    We simply want to add 1 to every row and column that isn't at maximum until it is at maximum.
    Obviously we don't do it 1 at a time, we just add either enough to make row maximum or column maximum
    and then move on to the next column/row.
    The first place for this competition was 4 minutes.  Admittedly I misread the question to start with, which made
    it more complicated than the real question, (and my wife decided she needed to talk to me about something in the middle)
    but still... 4 minutes??? Reading the question must take at least 1 of those 4 minutes.
    My guess is that they must just take real short cuts like use r for variable name instead of rows. The Kotlin compiler also doesn't like
    maxOf(a,b,c,d) only maxOf(a,b).  Then don't run the test case, just submit. Still - there are some very fast typists out
    there... :-)
     */
    fun solution(A: IntArray): IntArray {

        val rows = IntArray(3)
        val cols = IntArray(3)
        rows[0] = A[0] + A[1]+ A[2]
        rows[1] = A[3] + A[4]+ A[5]
        rows[2] = A[6] + A[7]+ A[8]
        cols[0] = A[0] + A[3] + A[6]
        cols[1] = A[1] + A[4] + A[7]
        cols[2] = A[2] + A[5] + A[8]
        var maxVal = maxOf(rows[0],rows[1])
        maxVal = maxOf(maxVal,rows[2])
        maxVal = maxOf(maxVal,cols[0])
        maxVal = maxOf(maxVal,cols[1])
        maxVal = maxOf(maxVal,cols[2])
     // All rows and columns will end up equalling maxVal.

        val deltaRows = IntArray(3) {maxVal - rows[it] }
        val deltaCols = IntArray(3) {maxVal - cols[it] }
        var colIndex = 0
        for (i in 0..2) {
            while (deltaRows[i] > 0) {
                if (deltaCols[colIndex] > deltaRows[i]) {
                    A[i * 3 + colIndex] += deltaRows[i]
                    deltaCols[colIndex] -= deltaRows[i]
                    deltaRows[i] = 0
                } else {
                    A[i * 3 + colIndex] += deltaCols[colIndex]
                    deltaRows[i] -= deltaCols[colIndex]
                    deltaCols[colIndex] = 0
                    colIndex += 1
                }
            }
        }
        return A

    }

}