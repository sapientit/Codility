package uk.co.sapientit.templateapp

class DigitalGold {
    /*
    I am left wondering if there is the intended solution to this.  I sort X and Y, so the solution is O(XlogX).
    It passes the tests, and makes it very simple (the number of columns between X(Xsize /2) and X(XSize /2 -1) and
    same for Y.
    Another approach would be to have an array of size M and one of size N and then just count the mines in each row
    and column.  Then cycle up through these arrays to find the midpoint.  For X being >> max(M.N) that would be
    faster.
     */
    fun solution(N: Int, M: Int, X: IntArray, Y: IntArray): Int {
        if (X.size == 0) {
            return M + N - 2
        }
        if (X.size % 2 == 1)
            return 0
        X.sort()
        Y.sort()
        val xS = X.size / 2
        val yS= Y.size / 2
        return X[xS] + Y[yS] - X[xS -1] - Y[yS - 1]
    }
}