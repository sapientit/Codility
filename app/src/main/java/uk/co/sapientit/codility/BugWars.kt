package uk.co.sapientit.codility

class BugWars {
    //https://app.codility.com/cert/view/certK8SHRZ-BDAJR7NER34AX7WF/
    /*
    Since the fastest time was 7 minutes, I can't help but feel that there must be a simpler way of doing this.
    It says that this runs in N**2 or N**2 logN.  I think it is N**2.

    In order to avoid having to add up fuel all the way I pre-calculate total fuel up to each point.  This is
    the fuel array.

    starting at point 0:
         attempt a path that goes backwards a bit and then ends up going forwards
         skip forward to start from the first point that I couldn't reach

     Then do the same starting at the end and looking for a path that goes forward and then doubles back.


     The code is similar, so I will only describe the first case.
     Ignoring all the fuel I pick up on the way, I can get backwards to a distance of X[i] - A[i].
     I ignore the fuel picked up because I need that to get back to i and go forward.  I know that
     the fuel I pick up must be less than the distance back to X[i] (otherwise the previous iteration
     could have gone to X[i] and i would not have been a starting point).
     Then see how far I can go forward with the remaining fuel looking 1 step at a time.
     Then reduce the distance we go backwards by 1 town and look forward again.

     At worst case, we are doing 2(fwd and back) * NlogN operations to find the "loop"
     Then stepping back to i could take N operations.  If we only go 1 step forward then we end up with
     N**2 for that operation.  So O(N**2) is correct.
     */
    fun solution(A: IntArray, X: IntArray): Int { // A is fuel, X distance
        val fuel = IntArray(A.size)
        var fuelTotal = 0
        for (i in 0..X.size - 1) { // better to use index so don't do it twice?
            fuelTotal = fuelTotal + A[i]
            fuel[i] = fuelTotal
        }
        var max = 1
        var index = 0
        while (index < A.size - 1) {
            var back = findDistanceFront(X[index + 1] - A[index], index, X )
            //       var back = findDistanceFront(fuel[index] - 2 * A[index], index, X )
            var fwdIndex = index + 1
            var fwdFuel = 0
            while (back <= index) {
                val spareDistance = fuel[index] - fuel[back] + A[back] - 2 * (X[index] - X[back]) + X[index]
                while (fwdIndex < A.size && spareDistance + fwdFuel >= X[fwdIndex]) {
                    fwdFuel += A[fwdIndex++]
                }
                max = maxOf(max, fwdIndex - back)
                back++
            }
            index = fwdIndex
        }
        index = A.size - 1
        //  All this is wrong... or at least needs checking!
        while (index > 0) {
            var back = findDistanceBack(X[index - 1] + A[index], index, X )
            //var back = findDistanceBack(fuel[index] + A[index], index, X )
            // We can reach back.
            //       println("found backwards "+ back + " " + index)
            var fwdIndex = index - 1
            var fwdFuel = 0
            while (back >= index) {
                //          println("trying " + back + " " + index)
                val spareDistance = fuel[index] - fuel[back] + 2 * (X[back] - X[index]) + X[index] - A[index]
                while (fwdIndex >= 0 && spareDistance - fwdFuel <= X[fwdIndex]) {
                    fwdFuel += A[fwdIndex--]
                }
                max = maxOf(max, back - fwdIndex)
                back--
            }
            index = fwdIndex
        }

        return max

    }
    fun findDistanceFront(target: Int, maxIndex: Int, X: IntArray) : Int{
        // find first where X[i] > target
        var min = 0
        var max = maxIndex
        while (min < max) {
            val mid = (min + max) / 2
            if (X[mid] > target) {
                max = mid
            } else {
                min = mid + 1
            }
        }
        return min
    }

    fun findDistanceBack(target: Int, minIndex: Int, X: IntArray) : Int{
        // find last where X[i] < target
        var min = minIndex
        var max = X.size -1
        while (min < max) {
            val mid = (min + max + 1) / 2
            if (X[mid] < target) {
                min = mid
            } else {
                max = mid - 1
            }
        }
        return min
    }
}