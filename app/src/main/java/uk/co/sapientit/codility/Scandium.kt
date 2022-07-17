package uk.co.sapientit.templateapp

class Scandium {
    /*
    If there are an even number of odd numbers then just take everything and you win.
    If all even numbers are to the left or right of all odd numbers
    If there is an odd number then the winning strategy is to leave a single odd number with an equal number of
    odd numbers on either side of it.
    For the purpose of descriptions in comments 0 is any even and 1 is any odd
    There are a number of ways that you can leave a single 1, but it must be either the first or last 1.
    Where it is possible that there is more than one solution I collect all possible and work out the
    one that starts at the beginning or is shortest at the end.
    a = number of zeroes before first 1
    b = number of zeroes between first and second
    c = number between penultimate and last 1
    d = number of zeroes after last 1
     */
    val nosol = "NO SOLUTION"
    fun solution(A: IntArray): String {
        val firstNum = IntArray(5){Int.MAX_VALUE}
        val secondNum = IntArray(5){Int.MAX_VALUE}
        var solutionsFound = 0
        var count = 0
        var firstOdd = -1
        var secondOdd = -1
        var lastOdd = -1
        var prev = -1
        for (i in A.indices) {
            if (A[i] % 2 == 1) {
                if (firstOdd == -1)
                    firstOdd = i
                else
                    if (secondOdd == -1)
                        secondOdd = i

                prev = lastOdd
                lastOdd = i
                count += 1
            }
        }
        if (count % 2 == 0) {
            return "0," + (A.size - 1)
        }
        if (A.size == 1)
            return nosol  // Must be odd
        val a = firstOdd
        val b = secondOdd - firstOdd - 1
        val c = lastOdd - prev - 1
        val d = A.size - lastOdd - 1
   //     val distanceToEnd = A.size - lastOdd - 1
        if (firstOdd == 0) {
            firstNum[solutionsFound] = 1
            secondNum[solutionsFound] = A.size - 1
            solutionsFound += 1
        }

        if (count == 1) {
            if (d == firstOdd) {  // equal on both sides
                return nosol
            }
            if (d > firstOdd) {
                val endPoint = A.size - firstOdd - 1
                val first = firstOdd + 1
        //        firstNum[solutionsFound] = first
        //        secondNum[solutionsFound] = endPoint
                return "" + first +"," + endPoint
            } else {
                val end = a - d - 1
                return "0,"  + end
        //        firstNum[solutionsFound] = 0
        //        secondNum[solutionsFound] = end
        //        solutionsFound += 1
            }
        }
        if (d == 0) {
            firstNum[solutionsFound] = 0
            secondNum[solutionsFound] = A.size - 2
            solutionsFound += 1
        } else {
            /*  Now look for 0(*a)xxxxxxx0(*c)10(*d)  where d <= a+c
                That will take d - c 0s from a and then finish with prev (adjusted if c > d)
             */
            if (d <= a + c) {

                firstNum[solutionsFound] = maxOf(0,  d - c)
                secondNum[solutionsFound] = maxOf(prev, lastOdd - d - 1)
                solutionsFound += 1
            } else {
                /*
            Now look for 0(a)10(b)xxxxxx0(c)1(0(d)  where d > a + c  && a > 0
             */
                if (a > 0) {

                    val first = firstOdd  + 1
                    firstNum[solutionsFound] = first
                    secondNum[solutionsFound] = A.size - a - 1
                    solutionsFound += 1
                }
            }
            if (a == 0) {
                // Starts with 1, so only thing left is to just take everything except first digit

                firstNum[solutionsFound] = 1
                secondNum[solutionsFound] = A.size - 1
                solutionsFound += 1
            }
        }
        var minIndex = 0
        for (i in 1 until firstNum.size) {
            if (firstNum[i] < firstNum[minIndex]) {
                minIndex = i
            } else {
                if (firstNum[i] == firstNum[minIndex]) {
                    if (secondNum[i] < secondNum[minIndex]) {
                        minIndex = i
                    }
                }
            }
        }
        return "" + firstNum[minIndex] + "," + secondNum[minIndex]
    }
}