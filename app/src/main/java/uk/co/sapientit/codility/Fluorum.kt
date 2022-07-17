package uk.co.sapientit.templateapp

import kotlin.random.Random

class Fluorum {
    /*
    The array of cities describes a tree structure.  Note that the answer will consist of the starting city followed by all
    the leaf nodes.
    The approach for calculating the distance is similar to the approach for a previous challenge (with routers). First
    process the leaf nodes and then process hte nodes that have all it's branches processed.
    Starting from K start to count distance.
    Having calculated the distance, propagate that towards K.  When you meet a node with multiple branches propagate the
    longest one (and record the leaf node that you are propagating.
    The final parse starts from K and goes out through it's children recounting.
    If the propagated node changes, reset the count to 1
    Then just sort the leaf nodes (which may also include the root) into ascending sequence of newly calculated length.
     */
    fun solution(K: Int, T: IntArray): IntArray {
        // Parent is the city directly connected to this city but closer to K
        val parent = IntArray(T.size){-1} // -1 means not populated yet
        // City K has a parent of itself just to mark it as populated
        parent[K] = K
        val counts = IntArray(T.size){1}
        var root = -1
        // count nuber of connections and find the root ( i = T[i])
        for (i in T.indices) {
            if (i == T[i])
                root = i
            else
                counts[T[i]] += 1
        }
        // This holds the node numbers in processing sequence.
        val toProcess = IntArray(T.size + 2)
        var highWater = 0
        // distance is initially how far we are from K.  Later modified to how far from last visited city
        val distance = IntArray(T.size){0}
        // This is used to hold the city number that is furthese away from this city (moving away from K)
        val furthestNode = IntArray(T.size)
        // This holds a pointer into "children" for the first child of the city
        val firstChild = IntArray(T.size + 1)
        var startPoint = 0
        // Populate firstChild
        for (i in counts.indices) {
            firstChild[i] = startPoint
            startPoint += counts[i]
            // Also populate "toProcess" with the leaf nodes
            if (counts[i] == 1 && i != root) {
                toProcess[highWater] = i
                highWater += 1
                // For all leaf nodes, firthestNode is itself.
                furthestNode[i] = i
            }
        }
        // Don't think this is required now
        firstChild[firstChild.size - 1] = startPoint
        // Keep track of number of connected cities for later (we will modify counts)
        val totalNodes = counts.clone()
        // Keep track of number of leaf nodes for later
        var leafCount = highWater
        var index = 0
        // Children are cities that are directly connected to this city travelling away from K
        val children = IntArray(T.size * 2)
        // This keeps track of number of found children.  Together with firstChild we can use it to list children of a city
        val childrenFound = IntArray(T.size)
        // K is at distance 0 from itself
        distance[K] = 0
        while (index < T.size - 1) {
            val current = toProcess[index]
            val target = T[current]

            if (parent[current] != -1) {  // We have found a connection from K for this city
                // in which case current is the parent and T[i] is the child
                parent[target] = current
                children[firstChild[current] + childrenFound[current]] = target
                childrenFound[current] += 1
                // And add 1 to distrance from K for target
                distance[target] = distance[current] + 1
            } else {
                // Otherwise current must be child
                children[firstChild[target] + childrenFound[target]] = current
                childrenFound[target] += 1
            }
            // We have found another onnection to target.  Reduce counts by 1
            counts[target] -= 1
            // All upwards connections found.  Only the T[target] connection remains, so add to process list
            if (counts[target] == 1) {
                toProcess[highWater] = target
                highWater += 1
            }
            index += 1
        }
        // We now can calculate size of the result.  Is the root also a leaf? If so, add 1.
        // Copy the leaf numbers into result
        // Note that we add the initial city at the end if necessary.
        val result: IntArray
        if (childrenFound[root] == 0) {
            result = IntArray(leafCount + 1) {i -> toProcess[i]}
            // furthestNode[root] is also itself
            result[leafCount] = root
            furthestNode[root] = root
        } else {
            result = IntArray(leafCount ) {i -> toProcess[i]}

        }
        // Now process the toProcess list in reverse.
        // Since root must be now connected to K we can calculate parent and distance for all remaining nodes
        for (i in T.size -2 downTo 0) {
            val current = toProcess[i]
            val from = T[current]
            if ((parent[current] == -1)) { // Unless it is already in path from K to root this will be true
                parent[current] = from
                distance[current] = distance[from] + 1
            }
        }
        // We are about to process all the nodes using the parents.  Need to add root to leaf nodes if it is a leaf
        if (childrenFound[root] == 0 ) {
            toProcess[leafCount] = root
            leafCount += 1
        }
        // Counts is set to 1 for everything except root currently.  Set it to 1
        counts[root] = 1
        highWater = leafCount
        val limit: Int
        // If K is a leaf then it will be included in the leaf nodes, so add 1 to number to process
        // If K is not a leaf it will never be added to the list to process
        if (childrenFound[K] == 1) {
            limit = T.size
        } else
            limit = T.size - 1
        /*
        This loop will populate the furthestNode for everything between leaves and K
        distance and furthestNode are propagated up the chain
        If a node has more than 1 child, loop through children and work out furthest Node
         */
        for (i in 0 until limit) {
            val current = toProcess[i]
            if (current != K) {
                if (childrenFound[current] > 1) {
                    val first = firstChild[current]
                    val last = first + childrenFound[current] - 1
                    var longest = Int.MIN_VALUE
                    var longestNode = 0
                    for (j in first..last) {
                        val child = children[j]
                        if (distance[child] > longest || (distance[child] == longest && furthestNode[child] < furthestNode[longestNode])) {
                            longest = distance[child]
                            longestNode = child
                        }
                    }
                    // Found the child with the most cities on it.  So prpagate that path
                    furthestNode[current] = furthestNode[longestNode]
                    distance[current] = distance[longestNode]
                }
                // Propagate upstream.  If this is last child, add to process list (unless it is K)
                val upStream = parent[current]
                if (upStream != K) {
                    distance[upStream] = distance[current]
                    furthestNode[upStream] = furthestNode[current]
                    counts[upStream] += 1
                    if (counts[upStream] > childrenFound[upStream]) {
                        toProcess[highWater] = upStream
                        highWater += 1
                    }
                }
            }
        }


        highWater = 1
        toProcess[0] = K
        var lowWater = 0
        distance[K] = 0
        furthestNode[K] = -1
        //  Now parse the cities starting from K and going through the children.
        //  Set the distance to 1 if we are on a branch from the path to furthest node.  Otherwise add 1.
        //  This means each city ends up with a distanec that represents the distance adter all further cities have
        // been processed
        while (lowWater < highWater) {
            val current = toProcess[lowWater]
            val prev = parent[current]
            if (furthestNode[prev] == furthestNode[current])
                distance[current] = distance[prev] + 1
            else
                distance[current] = 1
            val first = firstChild[current]
            val last = first + childrenFound[current] - 1
            if (first <= last) {
                for (i in first..last) {
                    toProcess[highWater] = children[i]
                    highWater += 1
                }
            }

            lowWater += 1
        }
        val comparator = Comparator <Int>{ o1: Int, o2: Int ->
            return@Comparator if (distance[o1] > distance[o2] || (distance[o1] == distance[o2] && o1 < o2))  -1 else 1
        }
        distance[K] = Int.MAX_VALUE
        val sortedRes  = result.sortedWith(comparator).toMutableList()
        // This is messy - need to add K if it is not a leaf (note that root == K will have a single child if it is a leaf
        if (childrenFound[K] > 1 || (childrenFound[K] == 1 && root == K)) {
            sortedRes.add(0,K)
        }

        return sortedRes.toIntArray()
    }
    /*
    Remaining code is inefficient brute force approach to validate correctness of the solution
     */
    fun bruteForce(K: Int, T: IntArray) : IntArray {

        val nodes = Array<Node>(T.size){ i -> Node(i) }
        buildNode(nodes[K], T, -1, nodes)
        nodes[K].complete = true
        var allDone = false
        val cities = mutableListOf<Int>(K)
        while (!allDone) {
            var distance = 0
            var furthestNode = -1
            for (i in nodes.indices) {
                val length = getLength(nodes[i])
                if (length > distance) {
                    furthestNode = i
                    distance = length
                }
            }
            if (distance <= 0)
                allDone = true
            else {
                setDone(nodes[furthestNode])
                cities.add(furthestNode)
            }

        }
        return cities.toIntArray()
    }
    fun buildNode(node: Node, T: IntArray, ignore: Int, nodes: Array<Node>) {
        for (i in 0 until T.size) {
            if (i == node.num) {
                if (i != T[i] && T[i] != ignore) {
                    val newNode = nodes[T[i]]
                    node.children.add(newNode)
                    newNode.parent = node
                    buildNode(newNode,T,i,nodes)
                }
            } else {
                val linkedTo = T[i]
                if (linkedTo == node.num && i != ignore) {
                    val newNode = nodes[i]
                    node.children.add(newNode)
                    newNode.parent = node
                    buildNode(newNode,T,T[i],nodes)
                }
            }
        }
    }
    fun getLength(node: Node) : Int {
        if (node.children.size != 0)
            return Int.MIN_VALUE
        var count = 0
        var procNode = node
        while (!procNode.complete) {
            count += 1
            procNode = procNode.parent!!
        }
        return count
    }
    fun setDone(node: Node)  {
        var procNode = node
        while (!procNode.complete) {
            procNode.complete = true
            procNode = procNode.parent!!
        }
    }
    data class Node(val num: Int) {
        var parent: Node? = null
        val children =  mutableListOf<Node>()
        var complete = false
    }
    /* Generate test data

     */
    fun buildTestCase(size: Int, rand: Random) : Boolean{
        val array = IntArray(size)
        val used = BooleanArray(size){false}
        val root = rand.nextInt(size)
        array[root] = root
        used[root] = true
        val listUsed = IntArray(size)
        listUsed[0] = root
        for (i in 0 until size - 1) {
            var x = rand.nextInt(size - i -1)
            var j = 0
            while (x > 0 || used[j]) {
                if (!used[j])
                    x -= 1
                j += 1
            }
            val target = rand.nextInt(i + 1)
            array[j] = listUsed[target]
            listUsed[i +1] = j
            used[j] = true
        }
        val k = rand.nextInt(size)
        val t1 = bruteForce(k, array)
        val t2 = solution(k, array)
        if (t1.size != t2.size) {
            println("different lengths")
            return false
        } else {
            for (i in t1.indices) {
                if (t1[i] != t2[i]) {
                    println("Sequence")
                    return false
                }
            }
        }
        return true
    }
}