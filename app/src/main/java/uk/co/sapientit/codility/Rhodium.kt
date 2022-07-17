package uk.co.sapientit.templateapp

class Rhodium {
    /*
    The key to this challenge is building a matrix of what is possible and under what conditions.
    For example, to go from village 2 to village 3 we must go through village 5.  That means that a route ending with
    maximum village 3 is never possible (except single village case).
    2-4 is not possible either, but 2-5 may be possible (as my 3-4 or 3-5).
    Now consider if 6-7 goes through 3.  This means it is not possible to have a sequence that starts from 4,5 or 6 and
    goes to 7 - it is only possible for a sequence that starts from 3 or below.
    So for each N-> N+1 we work out what is the maximum and minimum numbers we pass through.  We start from N-2.
    Obviosuly the maximum is the last village (N-1) but we need to record the minimum.
    If N-3 -> N-2 goes through N-1 then we record that from N-3 the next village must be N-1.  WE also keep the
    minimum value village we go through.
    Once we have all these we start from i=0 and count the possible solutions - stopping when we find once that goes
    through a village < the current village.

     */
    fun solution(T: IntArray): Int {
        val size = T.size
        // Starting from n the next possible stopping village
        val nextVillage = IntArray(size){Int.MIN_VALUE}
        // Going from n (to nextVillage) what is the minimum number village we have to go through.
        val minVillage = IntArray(size){it}
        val tree = IntArray(size)
        val count = IntArray(size)
        T.forEach {
            count[it] += 1
        }
        var highWater = 0
        for (i in count.indices) {
            if (count[i] == 0) {
                tree[highWater] = i
                highWater += 1
            }
        }
        var index = 0
        while (index < highWater) {
            val next = T[tree[index]]
            count[next] -= 1
            if (count[next] == 0) {
                tree[highWater] = next
                highWater += 1
            }
            index += 1
        }
        index -= 1
        tree[highWater] = T[tree[index]] // root
        val depth = IntArray(size)
        // For each village, find its depth in the tree (for ease of finding where paths intersect)
        while (index >= 0) {
            val current = tree[index]
            val parent = T[tree[index]]
            depth[current] = depth[parent] + 1
            index -= 1
        }
        // For each city calculate the minimum size of subtree (maximum) subject to it possibly also going through
        // a lower value village (minimum)
        for (i in size - 2 downTo 0) {
            var target = i + 1
            val sourceDepth = depth[i]
            val targDepth = depth[target]
            var dp1: Int
            val dp2: Int
            var city1: Int
            var city2: Int
            var minCity = i
            var maxCity = target
            // We just want to find the max and min between the ywo villages so work out which is deepest in the tree
            // That makes the code simpler.
            if (sourceDepth > targDepth) {
                dp1 = sourceDepth
                dp2 = targDepth
                city1 = i
                city2 = target
            } else {
                dp2 = sourceDepth
                dp1 = targDepth
                city2 = i
                city1 = target
            }
            while (dp1 > dp2) {
                city1 = T[city1]
                dp1 -= 1
                minCity = minOf(minCity, city1)
                maxCity = maxOf(maxCity, city1)
            }
            while (city1 != city2) {
                city1 = T[city1]
                minCity = minOf(minCity, city1)
                maxCity = maxOf(maxCity, city1)
                city2 = T[city2]
                minCity = minOf(minCity, city2)
                maxCity = maxOf(maxCity, city2)
            }
            minVillage[i] = minCity
            while (target < maxCity) {
                minVillage[i] = minOf(minVillage[i], minVillage[target])
                maxCity = maxOf(maxCity, nextVillage[target])
                target += 1
            }
            nextVillage[i] = maxCity
        }
        var journeys = 1 //single city journey for last city
        for (i in 0 until size - 1 ) {
            journeys += 1 //  single city journey
            var j = i
            while (j < size && minVillage[j] >= i) {
                if (nextVillage[j] == j + 1)  {
                    journeys += 1
                    j += 1
                } else {
                    j = nextVillage[j] - 1
                }
            }
        }
        return journeys

    }
}