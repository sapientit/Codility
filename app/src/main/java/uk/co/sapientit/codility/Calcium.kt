package uk.co.sapientit.templateapp

class Calcium {
    /*
    Surprisingly, there isn't a way of doing this very efficiently. I was expecting this solution to fail on performance
    but it was 100%, so clearly this is the correct method.
    You have to work out the minimum and maximum possible and then do a binary search for the longest possible solution.
    So the question is to how to do this binary search as efficiently as possible.
    We build a directional tree (similar to other challenges) and start from the leaves and work towards the root.
    At each node, we look at the lengths of all the children.  We need cameras for all links longer than half the target
    length -1 if the (largest length <= half the length) + (the smallest < target) <= target
    We then forward the largest length that we haven't put a camera on +1 to the parent node.
    To make this more efficient we calculate up front where to store the forwarded length so that it is in the list of
    child lengths for the parent.
    Simples...
     */

    fun solution(A: IntArray, B: IntArray, K: Int): Int {
        if (K >= A.size)
            return 0
        /*
        The first few loops are to build the tree structure
         */
        val counts = IntArray(A.size + 1)
        /*
        firstRoad is the first A/B pair that references each node
        nextRoad is a reference to the next A/B pair.
        In both cases 0..A.size -1 means the reference to this node is A,
        A.size .. 2 * A.size -1 means the reference is B
         */
        val firstRoad = IntArray(A.size + 1) { Int.MIN_VALUE } // 1 more intersection than roads
        val nextRoad = IntArray(A.size * 2)
        val roads = A.size
        for (i in A.indices) {
            val a = A[i]
            val b = B[i]
            counts[a] += 1
            counts[b] += 1
            nextRoad[i] = firstRoad[a]
            firstRoad[a] = i
            nextRoad[i + roads] = firstRoad[b]
            firstRoad[b] = roads + i
        }
        val children = IntArray(2 * (A.size + 1))
        val firstChild = IntArray(A.size + 1)
        val childCount = IntArray(A.size + 1)
        val childLength = IntArray(A.size + 1)
        var index = 0
        /*
        Since Kotlin has fixed length Arrays we need to use a single array for all the children and
        use pointers to build slices within that array.
        The root node has 1 more child than we would otherwise calculate so we double the size of the
        array so that we can just allow 1 extra space for all nodes.
         */
        for (i in counts.indices) {
            firstChild[i] = index
            index += counts[i]
        }

        val toProcess = IntArray(roads + 1)
        var highWater = 0
        for (i in counts.indices) {
            if (counts[i] == 1) {
                toProcess[highWater] = i
                highWater += 1
            }
        }

        val treeFormat = IntArray(roads + 1) { -1 }
        index = 0
        while (index < highWater) {
            val current = toProcess[index]
            var roadIndex = firstRoad[current]
            var target = -1
            while (roadIndex != Int.MIN_VALUE && target == -1) {
                val other: Int
                if (roadIndex >= roads) {
                    other = A[roadIndex - roads]
                } else {
                    other = B[roadIndex]
                }
                if (treeFormat[other] < 0) {
                    // First link where we haven't already processed the other end of the link must be to parent
                    target = other
                    treeFormat[current] = other
                    childLength[current] = firstChild[target] + childCount[target]
                    childCount[target] += 1
                    if (childCount[target] + 1 == counts[target]) {
                        toProcess[highWater] = target
                        highWater += 1
                    }
                }
                roadIndex = nextRoad[roadIndex]
            }
            index += 1
        }
        childLength[toProcess[index - 1]] = A.size // Add a dummy entry
        /*
                Tree format is now built.  Each town knows where to store its residual length
                Now do the binary search
                 */
        var maxLength = minOf(900, (A.size +-K))
        var minLength = 0
        while (maxLength - minLength > 1) {
            val mid = (maxLength + minLength + 1) / 2
            if (checkValid(mid, K, toProcess, firstChild, childLength, children, childCount)) {
                maxLength = mid
            } else {
                minLength = mid
            }
        }
        return maxLength
    }
    fun checkValid(length: Int, cameras: Int, toProcess: IntArray, firstChild: IntArray, childLengths: IntArray, children: IntArray, childCount: IntArray) : Boolean {
  /*
      Any child <= half the length we can disregard (other than to find the maximum of them)
      We need a camera for all but one child > half the length + 1 if the lowest of these long nodes + the shortest of the
      short children is > target.
      We actually calculate this the other way - add 1 for each link > half the lngth and then take 1 away of necessary.
   */
        var camerasRequired = 0
        val halfLength = (length) / 2
        for (i in firstChild.indices) {
            val current = toProcess[i]
            val target = childLengths[current]
            if (childCount[current] == 0) {
                children[target] = 1
            } else {
                var maxLow = 0
                var minHigh = Int.MAX_VALUE
                for (j in 0 until childCount[current]) {
                    val childIndex = firstChild[current] + j
                    if (children[childIndex] > halfLength) {
                        minHigh = minOf( children[childIndex] , minHigh)
                        camerasRequired += 1
                    } else {
                        maxLow = maxOf(maxLow, children[childIndex])
                    }
                }
                if (minHigh > length ) {
                    children[target] = maxLow + 1
                } else {
                    if (minHigh + maxLow > length) {
                        children[target] = maxLow + 1
                    } else {
                        camerasRequired -= 1
                        children[target] = minHigh + 1
                    }
                }
            }
            if (camerasRequired > cameras)
                return false

        }
        return true
    }
}