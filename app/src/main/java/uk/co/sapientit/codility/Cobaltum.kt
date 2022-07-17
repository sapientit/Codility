package uk.co.sapientit.templateapp

class Cobaltum {
    /*
    The key to this challenge is to split the arrays into sections.  Whenever we find a case where both A[i] and B[i]
    are > A[i-1] and B[i-1] then we have a new section.  It doesn't matter what has happened before, we can start
    processing again from this point.
    For each section, we move all the highest numbers to one side.  Which side is irrelevent, but we count the number of
    swaps to do so (in the code this is moving the highest numbers to the B side.  We get a subcount of the number
    of swaps.
    When we hit a new section we either add subcount or we add number of entries in the section - subcount (which
    is the number of swaps necessary to put the highest over to A.
    A: 1,5,3, 21,15,23
    B: 4,2,6, 14,22,16
    We have 2 sections.    21 and 14 are both > 3 and 6 so this is a new section
    In the first section subcount would be 1 (swap 5 to B side).  So count is 1 for that section
    The second section would have a subcount of 2 (moving 21,23 to B side).  With only 3 entries we add 3-2 = 1.
    So the total is 2.
     */
    fun solution(A: IntArray, B: IntArray): Int {
        var count = 0
        var subCount = 0
        var lowPoint = 0
        var prevHigh = Int.MIN_VALUE
        var prevLow = Int.MIN_VALUE
        for (i in A.indices) {
            if (A[i] < B[i]) {
                if (A[i] > prevHigh) {
                    count += minOf(subCount, i - lowPoint - subCount)
                    lowPoint = i
                    subCount = 0
                } else {
                    if (A[i] <= prevLow || B[i] <= prevHigh)
                        return -1
                }
                prevHigh = B[i]
                prevLow = A[i]
            } else {
                if (B[i] > prevHigh) {
                    count += minOf(subCount, i - lowPoint - subCount)
                    lowPoint = i
                    subCount = 0
                } else {
                    if (A[i] <= prevHigh || B[i] <= prevLow)
                        return -1
                }
                subCount += 1
                prevLow = B[i]
                prevHigh = A[i]
            }
        }
        count += minOf(subCount, A.size  - lowPoint - subCount)

        return count
    }

}