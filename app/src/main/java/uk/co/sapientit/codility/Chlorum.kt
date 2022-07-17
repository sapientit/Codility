package uk.co.sapientit.templateapp

import kotlin.random.Random

class Chlorum {

    /*
    If each value in D were unique this would be a lot simpler, so I will explain that algorithm first.

    The structure of the tree is similar to many of the challenges (start with the leaf entries and then
    work our way up through the tree until you hit the root and then work back down through the tree back to the leaves.

    While parsing the tree, starting from the maximum value calculate the minimum attractiveness between the root and
    the current entry.


    If we sort the values in D, then the Kth value is the minimum possible value that can be included.

    Any node that requires a minimum attractiveness of less than minimum possible must be excluded.  The minimum
    possible then becomes the value that we just excluded (+1 since this city was excluded).

    If we go through the cities in reverse order of attrctiveness, then we find the minimum possible attractiveness
    and since all values of D are unique that gives you the result.

    With D not unique this becomes more complex.

    We start with a random city of maximum attractiveness.  Any solution that has a minimum attractiveness less than
    the maximum must include all of the cities of maximum, so it doesn't matter which one we start with.

    This introduces an edge case.  If the solution only includes cities of maximum atrractiveness we can't assume that we
    can start with any of them.  Therefore we need a bit of code to find the maximum possible among just those cities.

    Note that if we exclude a city of attractiveness A, then the minimum possible is potentially now A (not A + 1) because there may
    be other cities of attractiveness A that we can include.  We have to check that we find at least one city that can be
    included of attractiveness A.

    Also we must be aware of a situation such as     2 - 1 - 1 - 2 with K = 3.  Just using the minimum possible approach
    we would say that the two 2's can be included.  But since K=3 we can't visit enough cities to make that true.

    For that reason we also need to calculate the maximum attractiveness working our way towards the starting node.  So if the first 2
    is the randomly selected starting node then both of the 1's have a maximum required of 2 beyond them.
    Any node with a maximum required > than itself must either be included or (if excluded) then the minimum
    value becomes the value beyond them that can no longer be reached.
    We then count the nodes 2 * 2 and the 2 1's that are also required because their maximum required is > 1.
    That makes 4 which is too many, so the minimum possible is 2 and the answer is 1.
    This eventually gives us a minimum value.
    The solution is to add all nodes with attractiveness > minimum value + all required nodes of minimum value
    + all possible nodes of minimum value (and then limit it to K if we have too many).



     */
    fun solution(K: Int, C: IntArray, D: IntArray): Int {
        val indices = IntArray(D.size){index -> index}
        val comparator = Comparator<Int>{
                c1,c2 -> D[c2] - D[c1]}
        val sorted = indices.sortedWith(comparator) // Sorted indices in descending order
        var minValue = D[sorted[K -1]]
        val maxValue = D[sorted[0]]
        val tree = IntArray(D.size)
        val counts = IntArray(D.size)
        val requiresMax = IntArray(D.size)
        for ( i in C.indices) {
            if (i != C[i])
                counts[C[i]] += 1
            requiresMax[i] = D[i]
        }
        var highWater = 0
        for (i in C.indices) {
            if (counts[i] == 0) {
                tree[highWater] = i
                highWater += 1
                requiresMax[i] = D[i]

            }
        }
        val onPath = BooleanArray(D.size)
        val requiresMin = IntArray(D.size)
        requiresMin[sorted[0]] = maxValue
        onPath[sorted[0]] = true
        var index = 0
        while (index < highWater) {
            val current = tree[index]
            val target = C[current]
            counts[target] -= 1
            if (counts[target] == 0) {
                tree[highWater] = target
                highWater += 1
            }
            if (onPath[current]) {
                onPath[target] = true
                requiresMin[target] = minOf(requiresMin[current], D[target])
            } else {
                requiresMax[target] = maxOf(requiresMax[current],requiresMax[target])
            }
            index += 1
        }

        for (i in counts.size - 2 downTo 0 ) {
            val current = tree[i]
            val target = C[current]
            if (onPath[current]) {
                requiresMax[current] = maxOf(requiresMax[target],requiresMax[current])
            } else {
                requiresMin[current] = minOf(requiresMin[target], D[current])

            }

        }
        /*
        Need to check in the next section that we actually find something that is valid
         */
        var prev = minValue
        var requiredCountFound = 0
        var candidates = 0
        for (i in sorted.size - 1 downTo 0) {
            val ind = sorted[i]
            val attract = D[ind]
            if (attract >= minValue) {
                if (attract == prev) {
                    if (requiresMax[ind] > minValue)
                        requiredCountFound += 1

                } else {
                    if (prev == minValue && (candidates == 0 || requiredCountFound + i + 1 > K)) {
                        minValue = attract
                    }
                    prev = attract
                    candidates = 0
                    requiredCountFound = 0
                }

                if (requiresMin[ind] < minValue) {
                    minValue = attract
                } else {
                    candidates += 1
                }
            }

        }
        if (minValue == maxValue) {
            //  Find clusters of maxValue
            index = 0
            var maxCount = 1
            val maxTreeCount = IntArray(D.size)
            while (index < highWater) {
                val current = tree[index]
                if (D[current] == maxValue) {
                    maxTreeCount[current] += 1
                    val parent = C[current]
                    if (parent != current && D[parent] == maxValue)
                        maxTreeCount[parent] += maxTreeCount[current]
                    maxCount = maxOf(maxTreeCount[current], maxCount)
                }
                index += 1
            }
            return minOf(K,maxCount)
        } else {
            var count = 0
            for (i in sorted.indices) {
                val city = sorted[i]
                if (D[city] < minValue)
                    return minOf(K,count)
                if (requiresMin[city] >= minValue)
                    count += 1
            }
            return minOf(K,count)
        }
    }
    fun bruteforce(K: Int, C: IntArray, D: IntArray): Boolean {
        /*
        There isn't a good way of doing brute force here.  The best option is to take the answer
        given by the solution and prove it is valid.  If necessary we could run this for every value 1->K
        Initially we will assume that counting maximums is working.  In order o do a proper brute
        force attempt we would need to include code to calculate the result amongst the maximums.
         */
        var count = K
        val indices = IntArray(D.size){index -> index}
        val comparator = Comparator<Int>{
                c1,c2 -> D[c2] - D[c1]}
        val sorted = indices.sortedWith(comparator) // Sorted indices in descending order
        val maxValue = D[sorted[0]]
        val minValue = D[sorted[K -1]]
        var index = 1

        if (minValue == maxValue)
            return true
        val cities = Array<City>(C.size){ i -> City(i, D[i]) }
        for (i in C.indices) {
            if (i != C[i]) {
                cities[i].linked.add(cities[C[i]])
                cities[C[i]].linked.add(cities[i])
            }
        }
        val included = BooleanArray(D.size){false}
        val potentialCities = HashSet<City>()
        var include: City? = cities[sorted[0]]
        potentialCities.add(include!!)
        while (include != null) {
            included[include.number] = true
            potentialCities.remove(include)
            include.linked.forEach{
                if (included[it.number]) {
                    include!!.connected.add(it)
                    it.connected.add(include!!)
                } else {
                    potentialCities.add(it)
                }
            }
            count -= 1
            include = null
            potentialCities.forEach {
                if (it.attractive >= minValue) {
                    include = it
                }
            }

        }
        /*
        We have included everything possible up to minValue.  Now we have to remove minValue leaf nodes to get
        number down to K
         */
        var previousCount = included.filter{it}.count() + 1
        if (previousCount < K)
            return false
        while (included.filter{it}.count() < previousCount) {
            previousCount = included.filter{it}.count()
            for (i in cities.indices) {
                val attract = D[i]
                if (attract == minValue && included[i]) {
                    val city = cities[i]
                    if (city.connected.size == 1) {
                        city.connected[0].connected.remove(city)
                        included[i] = false
                    }
                }
            }
        }
        if (K < previousCount) {
            return false
        }
        var maxExcluded = -1
        var minIncluded = 0
        count = previousCount
        index = 0
        while (count > 0) {
            val current = sorted[index]
            if (included[current]) {
                minIncluded = D[current]
                count -= 1
            }
            else
                maxExcluded = D[current]
            index += 1
        }
        return (maxExcluded == -1 || maxExcluded == minValue)
    }
    data class City (val number: Int, val attractive: Int) {
        val linked = mutableListOf<City>()
        val connected = mutableListOf<City>()
    }
    fun generateData( size: Int, rand: Random) : Boolean{
        val unsorted = IntArray(size)
        for (i in 1 until unsorted.size) {
            unsorted[i] = rand.nextInt(0, i)
        }
        val sort = IntArray(size){rand.nextInt(100)}
        val indices = IntArray(size){i->i}
        val comparator = Comparator<Int>{a1, a2 -> sort[a1] - sort[a2]}
        val sorted = indices.sortedWith(comparator)
        for (i in unsorted.indices) {
            val old = unsorted[i]
            unsorted[i] = sorted[old]
        }
        //   val tree = IntArray(size){i->unsorted[sorted[i]]}
        val tree = IntArray(size)
        for (i in sorted.indices) {
            tree[sorted[i]] = unsorted[i]
        }
        val attract = IntArray(size){rand.nextInt(1,10)}
        val K = rand.nextInt(2, size -1)
        val t1 = solution(K, tree, attract)

        if (!bruteforce(t1,tree, attract))
                return false
        return true

    }
}