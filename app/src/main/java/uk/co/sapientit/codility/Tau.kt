package uk.co.sapientit.codility


class Tau {
    /*
    There are various techniques that come together for this solution.  Let's start with the brute force approach.
    for (fromX in0 to  ...)
        for (toX in fromX  to  ...)
            for (fromY in 0 to ...)
                for (toY in fromY  to ...)
                    We now have the size of every possible inner square.  We can do similar for the other
                    possible rectangles that overlap an edge.
                    Brute force now does ...
                    sum = 0
                    for (x in fromX to toX)
                        for (y in fromY to toY)
                            sum = sum + C[x][y]
                    max = maxOf(max, sum)

     This approach is O(X**3 * Y**3)     (X/Y are sizes of dimensions x and y)

     We can speed up the sum part of this with precalculation (Prefix Sum)

     We define an array profit where profit[i][j] = sum of all values C where x <= i and y <<= j

     This can be calculated in O(XY).

     The sum part of the brute force approach can be replaced by a simple sum

     sum(i1->i2,j1->j2) = profit[i2][j2]   - profit[i1][j2] - profit[i2][j1] + profit[i1][j1]
             D           = (A + B + C + D) - (A + C)        - (A + B)        +    A

            i1                i2
     __________________________
     |       |                 |
     |   A   |        B        |
  j1____________________________
     |       |                 |
     |   C   |         D       |
  j2_____________________________

  That means that the sum part of the brute force approach is now O(1)  and the overall complexity is
  reduced to O(X**2 * Y**2)

  But that is not enough.  It still times out.

  So how can we find the maximum rectangle in less time?

  Consider a 1D array instead.  If we were to asked to find the maximum subsequence in an array, how would we do it?
  Obviously we cna use the prefix sum approach but then we would work out the maximum by comparing every possible i1 and
  i2 - O(N**2).

  But we can do this faster.  Consider the array

  1 -5  3  4  -2  1
  That gives a prefix sum of
  1 -4  -1 3  1   2

  The largest possible sum is just taking the 3 amd the 4 (= 7).  Looking at the prefix sum we see that the value before
  the 3 is -4 (position 2) - this is the minimum.  The maximum is then the 3 (from the prefix sum in position 4) and
  subtracting the minimum gives us the value 7.

  This is always true - the maximum will be the current sum - the minimum previously found

  So the algorithm to do this in O(N) time is:

  sum = 0
  min = 0
  for (num in nums)
    sum += num
    if (sum < min)
        min = sum
    else
        max = maxOf(max, sum - min)

    This is O(N) for a 1 dimensional array

    For a 2 dimensional array we still have to do every possibility in one direction, but then we can us this
    approach in the other direction

    for (fromX in 0 ...)
        for (toX in fromX to ...)

            min = 0
            for (y in 0 ...)
                sum = sum(fromX, toX, 0, y)  (calculated from the profit prefix sum)
                if (sum < min)
                    min = sum
                else
                    max = maxOf(max, sum - min)

     This is for the internal rectangle (x1 <x2, y1 < y2)

     The calculations for the rectangles crossing the edges is similar (but the calculation is generally the
     total - the internal rectangle).

    Case 2 for maximum where x2 > x1 and y1 > y2 rectangle crosses the x axis
    The profit is profit (x1,x2,0,Ymax)  - the profit calculated for case 1  (profit (x1,x2,y1,y2))


    Case 3 - x1 < x2 and y2 > y1 - rectangle crosses the y axis
    The profit is (0, Xmax, y1,y2)   - the profit calculated above (profit (x1,x2,y1,y2))

    And finally case 4 - x2 < x1 y2 < y1

    This is the total for the entire grid - a cross shape in the middle

    profit(0,Xmax, 0, Ymax)  - profit(x1, x2, 0, Ymax) - profit(0, Xmax, y1, y2)  + profit (x1,x2,y1,y2)

    The code uses slightly different calculations but if you resolve them they amount to the same as described above.

    In cases 2 and 4, as y gets bigger, the rectangle gets smaller.  For this reason we have to reverse the calculation
    we are looking for the maximum found so far and then subtract current from it (eventually we will find the
    minimum and we end up as maximum - minimum which is what we want).

    */
    lateinit var profit: Array<IntArray>
    fun solution(C: Array<IntArray>): Int {
        buildProfit(C)
        var max = 0
        for (toX in 1 until profit.size) {
            for (fromX in 0 until toX) {
                var min1 = 0
                var max2 = 0
                var min3 = 0
                var max4 = 0
                var current1 = 0
                val total2 = profit[toX][profit[0].lastIndex]  - profit[fromX][profit[0].lastIndex]
                for (y in 1 until (profit[0].size)) {
                    /*
                    This is looking for the maxium x2 > x1, y2 > y1
                     */
                    current1 = profit[toX][y] - profit[fromX][y]
                    if (current1 < min1) {
                        min1 = current1
                    } else {
                        max = maxOf(max,current1 - min1)
                    }
                    /*
                    Now look for maximum where x2 > x1 and y1 > y2
                     */

                    if (current1 > max2) {
                        max2 = current1
                    } else {
                        max = maxOf(max, total2 - current1 + max2)
                    }
                    /*
                    x2  < x1 and y2 > y1
                     */

                    val current3 = profit[profit.lastIndex][y] - current1
                    if (current3 < min3) {
                        min3 = current3
                    } else {
                        max = maxOf(max, current3 - min3)
                    }

                    /*
                    And finally x2 < x1 and y2 < y1
                     */
                    if (current3 > max4) {
                        max4 = current3
                    } else {
                        max = maxOf(max, profit[profit.lastIndex][profit[0].lastIndex] + max4 - total2 - current3)
                    }

                }

            }
        }
        return max

    }
    fun buildProfit(C: Array<IntArray>) {
        profit = Array<IntArray>(C.size + 1 ){IntArray(C[0].size + 1 )}  // Make it 1 bigger
        for (i in 1 until profit.size) {
            var rowProfit = 0
            for (j in 1 until profit[0].size) {
                rowProfit += C[i - 1][j - 1]
                profit[i][j] = rowProfit + profit[i - 1][j]
            }
        }
    }


}