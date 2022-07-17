package uk.co.sapientit.templateapp

class Selenium {
    /*
    We separate into the rows and the columns, since each move only affects the row or column of a piece.
    If we have 5 pieces in row 1, then 4 of them must move.  That means at some point there will be 4 more sprinklers
    in row 2.
    Similarly if there are 0 in column 0, then we need to move 1 from column 1.  That reduces the number available in
    column 1 by 1.
    Just cycle through the rows and columns until we reach the end.

    Note that this starts at column/row 1, not 0.
     */

    fun solution(X: IntArray, Y: IntArray): Int {
        val rows = IntArray(X.size)
        val cols = IntArray(X.size)
        Y.forEach {
            rows[it - 1] += 1
        }
        X.forEach {
            cols[it - 1] += 1
        }
        var total = 0L
        for (i in rows.indices) {
            val newMoves = 1 - rows[i]
            total += Math.abs(newMoves)
            if (newMoves != 0) {
                rows[i+1] -= newMoves
            }
        }
        for (i in cols.indices) {
            val newMoves = 1 - cols[i]
            total += Math.abs(newMoves)
            if (newMoves != 0) {
                cols[i+1] -= newMoves
            }
        }
        return (total % 1000000007).toInt()
    }

}