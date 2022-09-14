package uk.co.sapientit.templateapp.incomplete

import java.util.HashSet
import kotlin.random.Random

class Utility {
    fun multiplyMatrix(matrix1: Array<LongArray>, matrix2: Array<LongArray>): Array<LongArray> {
        val result = Array(matrix1.size){LongArray(matrix2[0].size)}
        for (i in 0 until result.size) {
            for (j in 0 until result[0].size) {
                var sum = 0L
                for (k in 0 until matrix1[0].size) {
                    sum += matrix1[i][k] * matrix2[k][j]
                }
                result[i][j] = sum
            }
        }
        return result
    }
    fun matrixPower(matrix: Array<LongArray>, power: Int) : Array<LongArray>{
        var result = Array(matrix.size){row -> LongArray(matrix.size){ col -> if (col == row) 1 else 0} }
        var p2 = matrix
        var rem = power
        while (rem > 0) {
            if (rem and 1 == 1) {
                result = multiplyMatrix(p2,result)
            }
            rem = rem shr 1
            if (rem > 0)
                p2 = multiplyMatrix(p2,p2)
        }
        return result
    }
    fun KMP(S: String, W: String): Int {  // Count occurrences of Word in String
        val T = buildT(W)
        var j = 0  //(the position of the current character in S)
        var k = 0  //(the position of the current character in W)

        var nP = 0
        var count = 0
        while (j < S.length) {
            if (W[k] == S[j]) {
                j = j + 1
                k = k + 1
                if (k == W.length) {
                    count += 1
                    // count + 1 or j-k
                    nP =  nP + 1
                    k = T[k] //(T[length(W)] can't be -1)
                }
            } else {
                k = T[k]
                if (k < 0) {
                    j = j + 1
                    k = k + 1
                }
            }
        }
        return count

    }

    fun buildT(W: String) : IntArray {
        /*
        From Wikipedia
         */
        var  pos = 1 // (the current position we are computing in T)
        var cnd = 0 // (the zero-based index in W of the next character of the current candidate substring)
        val T = IntArray(W.length + 1)
        T[0] = -1

        while (pos < W.length) {
            if (W[pos] == W[cnd]) {
                T[pos] = T[cnd]
                /*
                Consider the case AABACAAB.  The value for the final B needs to be 1, not 0 because AA is a valid prefix
                This continues where you have a repeating string
                 */
            } else {

                T[pos] = cnd

                while (cnd >= 0 && W[pos] != W[cnd]) {
                    cnd = T[cnd]
                }

                /*
                This code picks up cases such as ABACCABABx - if the final x is wrong then we need to test if we
                are a new word starting AB.
                 */

            }
            pos = pos + 1
            cnd = cnd + 1
        }


        T[pos] = cnd
        return T
    }
    fun booth(source: String) : Int { // rotate string to alphabetically smallest result
        val S = source + source //# Concatenate string to it self to avoid modular arithmetic
        val f = IntArray(S.length){-1} // # Failure function
        var k = 0  //# Least rotation of string found so far
        for (j in 1 until S.length) {
            val sj = S[j]
            var i = f[j - k - 1]
            while (i != -1 && sj != S[k + i + 1]) {
                if (sj < S[k + i + 1]) {
                    k = j - i - 1
                }
                i = f[i]
            }
            if (sj != S[k + i + 1]) { //  # if sj != S[k+i+1], then i == -1
                if (sj < S[k]) { //  # k+i+1 = k
                    k = j
                }
                f[j - k] = -1
            } else {
                f[j - k] = i + 1
            }
        }
        return k
    }
    class CountingTree(val size: Int) { //Fenwick tree
        /* WARNING
        This tree is 1 indexed - never set value 0.
         */
        val powers = ArrayList<Int>()
        val counts = IntArray(size + 1)
        init {
            var current = 1
            powers.add(current)
            while (current < size) {
                current += current
                powers.add(current)
            }
        }
        constructor(source: IntArray) : this(source.size) {
            val count = sumLevel(powers.lastIndex, 0, source)
            if (powers[powers.lastIndex] < counts.size) {
                counts[powers[powers.lastIndex]] = count
            }
        }
        fun sumLevel(level: Int, current: Int, source: IntArray): Int {
            if (current >= source.size) return 0
            if (level == 0) {
                return source[current]
            }
            val valueFirst = sumLevel(level - 1, current, source)
            val toSet = current + powers[level - 1]
            if (toSet < counts.size)
                counts[toSet] = valueFirst
            return valueFirst +  sumLevel(level - 1, current + powers[level - 1], source)
        }
        fun increment(num: Int, count: Int = 1) {
            var current = num
            counts[current] += count
            for (power in powers) {
                if (current and power != 0) {
                    current += power
                    if (current > size) break
                    counts[current] += count
                }
            }

        }
        fun decrement(num: Int, count : Int = 1) {
            increment(num, -count)
        }
        fun total(num: Int) : Int {
            var current = 0
            var total = 0
            for (i in powers.size - 1 downTo 0) {
                if (powers[i] and num != 0) {
                    current = current + powers[i]
                    total += counts[current]
                }
            }
            return total
        }
    }
    class TarjanNode(val index: Int) { //  Find islands in a maze (only 1 way in or out
        var onStack = false
        var lowIndex = index
    }
    fun tarjans(edges: Array<List<Int>>, n: Int) : IntArray{
        val nodes = Array<TarjanNode?>(n){null}
        var index = 1
        val result = IntArray(n)
        val stack = ArrayDeque<Int>()
        fun connectedNode(node: Int, prev: Int) {
            val current = TarjanNode(index++)
            nodes[node] = current
            current.onStack = true
            stack.addLast(node)
            for (edge in edges[node]) {
                if (edge != prev) {
                    if (nodes[edge] == null) {
                        connectedNode(edge, node)
                        nodes[node]!!.lowIndex = minOf(current.lowIndex, nodes[edge]!!.lowIndex)
                    } else {
                        if (nodes[edge]!!.onStack) {
                            current.lowIndex =
                                minOf(current.lowIndex, nodes[edge]!!.index)
                        }
                    }

                }
            }
            if (current.index == nodes[stack.last()]!!.index) {
                if (current.index == current.lowIndex) {
                    stack.removeLast()
                    current.onStack = false
                }
            } else {
                if (current.index == current.lowIndex) {
                    var lastIndex = stack.removeLast()
                    var last = nodes[lastIndex]!!
                    last.onStack = false
                    result[lastIndex] = current.lowIndex
                    while (last.index != current.index) {
                        lastIndex = stack.removeLast()
                        last = nodes[lastIndex]!!
                        last.onStack = false
                        result[lastIndex] = current.lowIndex

                    }
                }

            }

        }
        connectedNode(0, -1)
        return result
    }


    class SegmentTree(size: Int) {
        class SegmentTreeNode () {
            var left = 0
            var right = 0
            val value : Int
            get() = left + right
            fun setChild(pos: Int, newValue: Int) {
                if (pos and 1 == 0) {
                    left = newValue
                } else {
                    right = newValue
                }
            }
            fun addChild(pos: Int, addValue: Int) {
                if (pos and 1 == 0) {
                    left += addValue
                } else {
                    right += addValue
                }
            }
            fun getChild(pos: Int) : Int {
                if (pos and 1 == 0) {
                    return left
                } else {
                    return right
                }

            }
        }
        val leaves: Int
        val tree: Array<SegmentTreeNode>
        var levels: IntArray
        init {
            var tempSize = size
            var leaf = 1
            var levelCount = 1
            while (tempSize != 1) {
                leaf = leaf shl 1
                tempSize = tempSize shr 1
                levelCount++
            }
            if (leaf == size) {
                leaves = leaf
                levelCount--
            } else {
                leaves = leaf shl 1
            }
            tree = Array(leaves - 1){ SegmentTreeNode() }
            levels = IntArray(levelCount)
            levels[levels.lastIndex] = tree.lastIndex
            var current = 2
            for (i in levels.size - 2 downTo 0) {
                levels[i] = levels[i+1] - current
                current = current shl 1
            }
        }
        fun add(leaf: Int, value: Int) {
            var prev = leaf
            for (i in 0 until levels.size) {
                val current = prev shr 1
                val index = levels[i] + current
                tree[index].addChild(prev, value)
                prev = current
            }
        }
        fun set(leaf: Int, value: Int) {
            val diff = value - tree[leaf shr 1].getChild(leaf)
            add(leaf, diff)
        }
        fun getLeaf(leaf: Int) : Int {
            return tree[leaf shr 1].getChild(leaf)
        }
         fun findNth(n: Int): Int {
            // Find the node that has makes the total >= n (return -1 if none)
            var target = n
            if (n > tree[levels[levels.lastIndex]].value) return -1 // Not enough in the tree
            var current = 0
            for (i in levels.lastIndex downTo 0) {
                val index = levels[i] + current
                if (tree[index].left >= target) {
                    current = current shl 1
                } else {
                    target -= tree[index].left
                    current = (current shl 1) + 1
                }
            }
            return current

        }
    }
   class SegmentTreeRange(size: Int) {
        class SegmentTreeNode () {
            var left = 0
            var right = 0
            var defer: Int? = null
            val value : Int
            get() = left + right
            fun setChild(pos: Int, newValue: Int) {
                if (pos and 1 == 0) {
                    left = newValue
                } else {
                    right = newValue
                }
            }
            fun getChild(pos: Int) : Int {
                if (pos and 1 == 0) {
                    return left
                } else {
                    return right
                }

            }
        }
        val leaves: Int
        val tree: Array<SegmentTreeNode>
        var levels: IntArray
        init {
            var tempSize = size
            var leaf = 1
            var levelCount = 1
            while (tempSize != 1) {
                leaf = leaf shl 1
                tempSize = tempSize shr 1
                levelCount++
            }
            if (leaf == size) {
                leaves = leaf
                levelCount--
            } else {
                leaves = leaf shl 1
            }
            tree = Array(leaves - 1){ SegmentTreeNode() }
            levels = IntArray(levelCount)
            levels[levels.lastIndex] = tree.lastIndex
            var current = 2
            for (i in levels.size - 2 downTo 0) {
                levels[i] = levels[i+1] - current
                current = current shl 1
            }
        }
        fun set(leaf: Int, value: Int) {
           setLevel(leaf,value,levels.lastIndex, 0)
        }
        private fun setLevel(leaf: Int, value: Int, level: Int, current: Int) : SegmentTreeNode{
            val index = levels[level] + current
            if (level == 0) {
                if (leaf and 1 == 0) {
                    tree[index].left = value
                } else {
                    tree[index].right = value
                }
                return tree[index]
            } else {
                tree[index].defer?.let {
                    propagateDefer(level, current,index)
                }

                if (leaf and (1 shl level) == 0) {
                    tree[index].left =  setLevel(leaf, value, level - 1, current shl 1).value
                } else {
                    tree[index].right =  setLevel(leaf, value, level - 1, (current shl 1) + 1).value
                }
                return tree[index]
            }
        }
        fun getLeaf(leaf: Int) : Int {
            var mask = 1 shl levels.lastIndex
            var current = 0
            for (i in levels.lastIndex downTo 1) {
                val index = levels[i] + current
                if (tree[index].defer != null) {
                    return tree[index].defer!!
                }
                if (leaf and mask == 0) {
                    current = current shl 1
                } else {
                    current = (current shl 1) + 1
                }
                mask = mask shr 1
            }
            return tree[current].getChild(leaf)

        }
       private fun propagateDefer(level: Int, current: Int, index: Int) {

           val left = (current shl 1) + levels[level - 1]
           val right = left + 1
           if (level == 1) {
               tree[left].left = tree[index].defer!!
               tree[left].right = tree[index].defer!!
               tree[right].left = tree[index].defer!!
               tree[right].right = tree[index].defer!!
           } else {
               tree[left].defer = tree[index].defer
               tree[right].defer = tree[index].defer
           }
           tree[left].left = tree[index].left / 2
           tree[left].right = tree[left].left
           tree[right].left = tree[left].left
           tree[right].right = tree[left].left
           tree[index].defer = null
       }
         fun findNth(n: Int): Int {
            // Find the node that has makes the total >= n (return -1 if none)
            var target = n
            if (n > tree[levels[levels.lastIndex]].value) return -1 // Not enough in the tree
            var current = 0
            for (i in levels.lastIndex downTo 0) {
                val index = levels[i] + current
                tree[index].defer?.let {propagateDefer(i, current, index)}
                if (tree[index].left >= target) {
                    current = current shl 1
                } else {
                    target -= tree[index].left
                    current = (current shl 1) + 1
                }
            }
            return current

        }
       fun setRange(from: Int, to: Int, newValue: Int) {
            setRangeLevel(from, to, newValue, levels.lastIndex, 0)
       }
       /*
       Consider a size of 8
       level 2 - is from <= 0 and 2 >= 7
       Level 1 - left.  Is 0-3  and right is 4-7
        */
       fun setRangeLevel(from: Int, to: Int, newValue: Int, level: Int, current: Int) : SegmentTreeNode {
           val index = levels[level] + current
           val left = current shl (level + 1)
           val right = ((current shl 1) + 1) shl level
           if (level == 0) {
               if (left >= from) {
                   tree[index].left = newValue
               }
               if (right <= to) {
                   tree[index].right = newValue
               }
               return tree[index]

           }
           val limit = (current + 1) shl (level + 1)
           if (from <= left && to >= limit - 1) {
               tree[index].defer = newValue
               tree[index].left = newValue * (1 shl level)
               tree[index].right = tree[index].left
           } else {
               tree[index].defer?.let{propagateDefer(level,current,index)}
               if (right > from) {
                   tree[index].left = setRangeLevel(from, to, newValue, level - 1, current shl 1).value
               }
               if (to >= right) {
                   tree[index].right = setRangeLevel(from, to, newValue, level - 1, (current shl 1) + 1).value
               }
           }
           return tree[index]


       }
    }
    class SegmentTreeMax(size: Int) {
        class SegmentTreeNode () {
            var left = 0
            var maxLeft = 0
            var right = 0
            var maxRight = 0
            val value : Int
                get() = left + right
            val maxValue: Int
                get() = maxOf(maxLeft, maxRight)
            fun setChild(pos: Int, newValue: Int, newMax: Int) {
                if (pos and 1 == 0) {
                    left = newValue
                    maxLeft = newMax
                } else {
                    right = newValue
                    maxRight = newMax
                }
            }
   /*         fun addChild(pos: Int, addValue: Int, maxValue: Int) {
                if (pos and 1 == 0) {
                    left += addValue
                    maxLeft = maxValue
                } else {
                    right += addValue
                    maxRight = maxValue
                }
            } */
            fun getChild(pos: Int) : Int {
                if (pos and 1 == 0) {
                    return left
                } else {
                    return right
                }

            }
        }
        val leaves: Int
        val tree: Array<SegmentTreeNode>
        var levels: IntArray
        init {
            var tempSize = size
            var leaf = 1
            var levelCount = 1
            while (tempSize != 1) {
                leaf = leaf shl 1
                tempSize = tempSize shr 1
                levelCount++
            }
            if (leaf == size) {
                leaves = leaf
                levelCount--
            } else {
                leaves = leaf shl 1
            }
            tree = Array(leaves - 1){ SegmentTreeNode() }
            levels = IntArray(levelCount)
            levels[levels.lastIndex] = tree.lastIndex
            var current = 2
            for (i in levels.size - 2 downTo 0) {
                levels[i] = levels[i+1] - current
                current = current shl 1
            }
        }
        fun add(leaf: Int, value: Int) {

            val newValue = value + tree[leaf shr 1].getChild(leaf)
            set(leaf, newValue)
        }
        fun set(leaf: Int, value: Int) {
            var prev = leaf
            var newMax = value
            var newValue = value
            for (i in 0 until levels.size) {
                val current = prev shr 1
                val index = levels[i] + current
                tree[index].setChild(prev, newValue, newMax)
                newMax = tree[index].maxValue
                newValue = tree[index].value
                prev = current
            }
        }
        fun getLeaf(leaf: Int) : Int {
            return tree[leaf shr 1].getChild(leaf)
        }
        fun findNth(n: Int): Int {
            // Find the node that has makes the total >= n (return -1 if none)
            var target = n
            if (n > tree[levels[levels.lastIndex]].value) return -1 // Not enough in the tree
            var current = 0
            for (i in levels.lastIndex downTo 0) {
                val index = levels[i] + current
                if (tree[index].left >= target) {
                    current = current shl 1
                } else {
                    target -= tree[index].left
                    current = (current shl 1) + 1
                }
            }
            return current

        }
        fun findMax(value: Int): Int {
            var current = 0
            if (tree[tree.lastIndex].maxValue < value) return -1

            for (i in levels.lastIndex downTo 0) {
                val index = levels[i] + current
                if (tree[index].maxLeft >= value) {
                    current = current shl 1
                } else {
                    current = (current shl 1) + 1
                }
            }
            return current
        }
        fun zeroUpTo(n: Int) {
            var prev = n
            var newMax = 0
            var newTotal = 0
            for (i in levels.indices) {
                val current = prev shr 1
                val index = levels[i] + current
                if (prev and 1 == 1) {
                    tree[index].left = 0
                    tree[index].right = newTotal
                    tree[index].maxLeft = 0
                    tree[index].maxRight = newMax
                } else {
                    tree[index].left = newTotal
                    tree[index].maxLeft = newMax
                }
                newTotal = tree[index].value
                newMax = tree[index].maxValue
                prev = current
            }

        }
    }
    class Manacher () { // Longest palindromic substring
        fun longestPalindrome(s: String): String {
            val size = s.length * 2 + 3
            val chars = CharArray(size){
                    n-> when {
                n == 0 -> '@'
                n == size - 1 -> '$'
                n % 2 == 1 -> '%'
                else -> s[n / 2 - 1]
            }
            }
            val count = IntArray(size)
            var currentEnd = -1
            var currentMid = -1
            var max = 1
            var maxMid = 0
            var index = 2
            while  (index < size - 2) {
                var length = 1
                if (index < currentEnd) {
                    length = count[2 * currentMid - index]
                    while (index + length < currentEnd) {
                        index++
                        length = count[2 * currentMid - index]
                    }
                    length = currentEnd - index
                }
                while (chars[index + length] == chars[index - length]) {
                    length ++
                }
                count[index] = length
                if (index + length > currentEnd) {
                    currentEnd = index + length - 1
                    currentMid = index
                }
                if (length > max) {
                    max = length
                    maxMid = index
                }
                index++
            }
            return s.substring((maxMid - max) / 2 , (maxMid + max) / 2 - 1 )

        }
    }
}
class SuffixTree (source: String) {
    val modSource = source + '$' // Ensure all suffixes can be counted.
    var globalEnd = 0

    inner class Edge {
        var start: Int = 0
        var m_end: Int = 0
        var leaf = false
        var end: Int
            get() = if (leaf) globalEnd else m_end
            set(newValue) {
                m_end = newValue
            }
        var nextNode: Node? = null
        fun breakAt(pos: Int): Node {
            val newNode = Node()
            val newEdge = Edge()
            if (leaf) {
                newEdge.leaf = true
            } else {
                newEdge.nextNode = nextNode
                newEdge.end = m_end
            }
            nextNode = newNode
            newNode.edges[modSource[start + pos]] = newEdge
            leaf = false
            newEdge.start = start + pos
            end = start + pos - 1
            return newNode
        }
    }

    inner class Node {
        val edges = HashMap<Char, Edge>()
        fun getEdge(c: Char): Edge {
            return edges.getOrPut(c) { Edge().apply { start = globalEnd; leaf = true } }
        }
    }

    inner class CurrentPos(var node: Node, var edge: Char, var position: Int) {

        fun nextExists(): Boolean {
            var currentEdge = node.getEdge(edge)
            while (currentEdge.end - currentEdge.start < position) {
                node = currentEdge.nextNode!!
                position -= currentEdge.end + currentEdge.start + 1
                edge = modSource[currentEdge.end + 1]
                currentEdge = node.getEdge(edge)
            }

            if (currentEdge.end - currentEdge.start > position) {
                position++
                if (modSource[globalEnd] == modSource[currentEdge.start + position]) {
                    return true
                }
                node = currentEdge.breakAt(position)

                edge = modSource[globalEnd]
                node.getEdge(edge)
                return false
            } else {
                node = currentEdge.nextNode!!
                edge = modSource[globalEnd]
                position = 0
                if (node.edges.contains(edge)) {
                    return true
                }
                node.getEdge(edge)
                return false
            }

        }

        fun checkNewEdge(c: Char): Boolean {
            if (node.edges.contains(c)) {
                return false
            }
            node.edges[c] = Edge().apply { start = globalEnd; leaf = true }
            return true
        }

        fun catchUp(index: Int) {
            var count = globalEnd - index - 1
            var currentEdge = node.getEdge(edge)
            while (count > 0) {
                if (count <= currentEdge.end - currentEdge.start - position) {
                    position += count
                    count = 0
                } else {
                    count -= currentEdge.end - currentEdge.start - position
                    node = currentEdge.nextNode!!
                    position = -1
                    edge = modSource[globalEnd - count]
                    currentEdge = node.getEdge(edge)
                }
            }
        }
    }

    var currIndex = 1
    val root = Node()
    val positions = Array<CurrentPos>(modSource.length) { CurrentPos(root, modSource[it], 0) }
    var currPos: CurrentPos = CurrentPos(root, modSource[0], 0)

    init {
        root.getEdge(modSource[0])
        for (i in 1 until modSource.length) {
            globalEnd = i
            //  positions[i] = CurrentPos(root,source[i],-1)
            var current = positions[currIndex]
            if (currIndex == i) {
                if (current.checkNewEdge(modSource[i])) {
                    currIndex++
                }
            } else {
                while (true) {
                    if (current.nextExists()) {
                        break
                    } else {
                        currIndex++
                        if (currIndex < i) {
                            current = positions[currIndex]
                            current.catchUp(currIndex)
                        } else {
                            current = positions[currIndex]
                            if (current.checkNewEdge(modSource[currIndex])) {
                                currIndex++
                            }
                            break
                        }
                    }
                }
            }
        }
    }

    fun print() {
        printLevel("", root)
    }

    private fun printLevel(current: String, root: Node) {
        for (edge in root.edges.values) {
            if (edge.leaf) {
                println(current + modSource.substring(edge.start, edge.end + 1))
            } else {
                printLevel(current + modSource.substring(edge.start, edge.end + 1), edge.nextNode!!)
            }
        }
    }

    companion object {

        fun testit(size: Int, rand: Random) {
            val c = CharArray(size) { (rand.nextInt(10) + 'a'.toInt()).toChar() }
            val tree = SuffixTree(String(c))
        }
    }

}
class RedBlack() {
    /*
    This is a self balancing binary array. The maximum height is guaranteed to be <= twice the minimum
    height.  All operations are log n.
    Note that nodes move around, so combinging this with a fenwick style of accumulation is not trivial
     */
    class RedBlackNode (var value: Int) {
        var red = true
        var left: RedBlackNode? = null
        var right: RedBlackNode? = null
        fun copy(from: RedBlackNode) {
            value = from.value
        }
    }
    var root = RedBlackNode(Int.MAX_VALUE)
    fun insert(value: Int) {
        val stack = ArrayList<RedBlackNode>()
        val newNode = RedBlackNode(value)
        var current :RedBlackNode? = root
        var parent = root
        while (current != null) {
            stack.add(current)
            parent = current
            if (value < current.value) {
                current = current.left
            } else {
                current = current.right
            }
        }
        if (parent === root) {
            newNode.red = false
            root.left = newNode
            return
        }
        if (value < parent.value) {
            parent.left = newNode
        } else {
            parent.right = newNode
        }
        checkUncle(newNode, stack)



    }
    fun checkUncle(current: RedBlackNode, stack: ArrayList<RedBlackNode>) {
        if (stack.size == 1) {
            current.red = false
            return
        }
        val parent = stack.removeLast()
        if (!parent.red) return
        val gParent = stack.removeLast()
        val uncle: RedBlackNode?
        if (gParent.left === parent) {
            uncle = gParent.right
        } else {
            uncle = gParent.left
        }
        if (uncle != null && uncle.red) {
            uncle.red = false
            parent.red = false
            gParent.red = true
            checkUncle(gParent, stack)
        } else {
            val ggParent = stack.last()
            if (current === parent.left) {
                if (parent === gParent.right) {
                    // RL case
                    rightRotate(current, parent, gParent)
                    leftRotate(current, gParent, ggParent)
                    gParent.red = true
                    current.red = false
                } else {
                    // LL case
                    rightRotate(parent, gParent, ggParent)
                    gParent.red = true
                    parent.red = false
                }

            } else {

                if (parent === gParent.left) {
                    // LR Case
                    leftRotate(current, parent, gParent)
                    rightRotate(current, gParent, ggParent)
                    gParent.red = true
                    current.red = false

                } else {
                    // RR case
                    leftRotate(parent, gParent, ggParent)
                    gParent.red = true
                    parent.red = false
                }
            }
        }
    }
    fun rightRotate(node: RedBlackNode, parent: RedBlackNode, gParent: RedBlackNode) {
        if (gParent.left === parent) {
            gParent.left = node
        } else {
            gParent.right = node
        }
        parent.left = node.right
        node.right = parent

    }
    fun leftRotate(node: RedBlackNode, parent: RedBlackNode, gParent: RedBlackNode) {
        if (gParent.left === parent) {
            gParent.left = node
        } else {
            gParent.right = node
        }
        parent.right = node.left
        node.left = parent

    }
    fun findPath(stack: ArrayList<RedBlackNode>, target: Int) : RedBlackNode?{
        return findLevel(stack, target, root)
    }
    tailrec private fun findLevel(stack: ArrayList<RedBlackNode>, target: Int, root: RedBlackNode?): RedBlackNode? {
        if (root == null || root.value == target) return root
        stack.add(root)
        if (root.value > target) {
            return findLevel(stack, target, root.left)
        } else {
            return findLevel(stack, target, root.right)
        }
    }
    fun delete(target: Int) {
        val stack = ArrayList<RedBlackNode>()
        var current = findPath(stack, target)
        if (current == null)
            return
        if (current.right != null) {
            stack.add(current)
            val successor = findSuccessor(stack, current.right!!)
            current.copy(successor)
            current = successor
        }
        // We are now deleting something that has only 0 or 1 children.
        // stack starts with parent of node to be deleted.
        val parent = stack.removeAt(stack.lastIndex)
        val child = current.left ?: current.right
        if (current.red || child?.red == true || stack.size == 0) {
            child?.red = false
        } else {
            fixDoubleBlack(stack, current, parent)
        }

        if (parent.left === current) {
            parent.left = child
        } else {
            parent.right = child
        }
    }
    tailrec fun findSuccessor(stack: ArrayList<RedBlackNode>, node: RedBlackNode) : RedBlackNode{
        if (node.left == null) {
            return node
        } else {
            stack.add(node)
            return findSuccessor(stack, node.left!!)
        }
    }
     fun fixDoubleBlack(stack: ArrayList<RedBlackNode>, current: RedBlackNode, parent: RedBlackNode, grandParent: RedBlackNode? = null) {
        if (current.red || parent === root) {
            return // No longer a double black
        }
        val gParent = grandParent ?: stack.removeAt(stack.lastIndex)
        if (parent.left === current) {
            val sibling = parent.right
            if (sibling == null ) {
                fixDoubleBlack(stack, parent, gParent)
                return
            }
            if (sibling.red ) {
                leftRotate(sibling,parent,gParent)
                sibling.red = false
                parent.red = true
                stack.add(gParent)
                fixDoubleBlack(stack, current, parent, sibling)
            } else {
                //sibling black
                if (sibling.right?.red == true ) {
                //RR case
                    leftRotate(sibling, parent, gParent)
                    sibling.red = parent.red
                    parent.red = false
                    sibling.right!!.red = false
                } else {
                    if (sibling.left?.red == true) {
                        val child = sibling.left!!
                        //RL case
                        rightRotate(child, sibling, parent)
                        child.red = false
                        sibling.red = true
                        leftRotate(child,parent,gParent)
                        child.red = parent.red
                        parent.red = false
                        child.right?.red = false
                    } else {
                        sibling.red = true
                        if (parent.red) {
                            parent.red = false
                        } else {
                            fixDoubleBlack(stack,parent,gParent)
                        }
                        //both nephews are black
                    }
                }
            }
        } else {

            val sibling = parent.left
            if (sibling == null ) {
                val newParent = stack.removeAt(stack.lastIndex)
                fixDoubleBlack(stack, parent, newParent)
                return
            }
            if (sibling.red ) {
                rightRotate(sibling,parent,gParent)
                sibling.red = false
                parent.red = true
                stack.add(gParent)
                fixDoubleBlack(stack, current, parent, sibling)
            } else {
                // sibling black
                if (sibling.right?.red == true ) {
                    //LR case
                    val child = sibling.right!!
                    leftRotate(child, sibling, parent)
                    child.red = false
                    sibling.red = true
                    rightRotate(child,parent,gParent)
                    child.red = parent.red
                    parent.red = false
                    child.left?.red = false
                } else {
                    if (sibling.left?.red == true) {
                        //LL case
                        rightRotate(sibling, parent, gParent)
                        sibling.red = parent.red
                        parent.red = false
                        sibling.left!!.red = false
                    } else {
                        //both nephews are black
                        sibling.red = true
                        if (parent.red) {
                            parent.red = false
                        } else {
                            fixDoubleBlack(stack,parent,gParent)
                        }
                    }
                }
            }



        }

    }
    fun checkFunctionality(rand: Random) : Boolean{
        val inserted = BooleanArray(10000)
        var count = 0
        for (i in 0..1500) {
            val num = rand.nextInt(1000)
            if (inserted[num]) {
                delete(num)
                count--
                if (traverse() != count) {
                    println("and failed after delete "+ num)
                    traverse(true)
                    return false
                }

                inserted[num] = !inserted[num]
            } else{
                insert(num)
                count++
                if (traverse() != count) {
                    println("and failed after insert " + num)
                    traverse(true)
                    return false
                }

                inserted[num] = !inserted[num]
            }
        }
        if (traverse() != count) {
            println("Counts mismatch ")
            return false
        }
        return true
    }
    var maxLevel = 0
    var minLevel = Int.MAX_VALUE
    fun traverse(print: Boolean = false) : Int{
        minLevel = Int.MAX_VALUE
        maxLevel = 0
        val list = mutableListOf<Int>()
        traverseNode(root.left, list, 0, "", print)
        var prev = Int.MIN_VALUE
        for (item in list) {
            if (item > prev) {
                prev = item
            } else {
                println("Failure - out of sequence")
                return -1
            }
        }
        if (maxLevel - minLevel > 0) {
            println("Too many level differences " + minLevel + " " + maxLevel)
            return -1
        }
        return list.size


    }
    fun traverseNode(node: RedBlackNode?, list: MutableList<Int>, level: Int, string: String, print: Boolean = false) {
        if (node == null) {
            minLevel = minOf(minLevel, level)
            maxLevel = maxOf(maxLevel, level)
            if (print)
               println(string)
        } else {
            val colour = if (node.red) 'R' else 'B'
            val nextLevel = if(node.red) level else level + 1
            traverseNode(node.left, list, nextLevel, string + node.value+ colour + '-', print)
            list.add(node.value)
            traverseNode(node.right, list, nextLevel, string + node.value + colour + '+', print)
        }
    }
}
class Dinic (val flow: Array<IntArray>, val source: Int, val drain: Int) {
    // Max flow algorithm
    // WARNING!!!!!!!!!!!!  - I think I delete a link that I shouldn't.
    val links = Array(flow.size){ HashSet<Int>() }
    var maxFlow = 0
    init {
        for (i in flow.indices) {
            for (j in flow.indices) {
                if (flow[i][j] != 0) {
                    links[i].add(j)
                }
            }
        }
        links[drain].clear() // This must be the end - don't want to flow from drain
        var newTotal = findPaths()
        while (newTotal > 0) {
            maxFlow += newTotal
            newTotal = findPaths()
        }
    }
    fun findPaths() : Int{
        val depth = IntArray(flow.size)
        depth[source] = 1
        val queue = java.util.ArrayDeque<Int>()
        queue.addFirst(source)
        while (queue.isNotEmpty()) {
            val current = queue.removeLast()
            for (link in links[current]) {
                if (depth[link] == 0) {
                    depth[link] = depth[current] + 1
                    queue.addFirst(link)
                }
            }
        }
        if (depth[drain] == 0) return 0
        return followPath(source,depth, Int.MAX_VALUE)
    }
    fun followPath(node: Int, depth: IntArray, maxFlow: Int) : Int{
        if (node == drain) return maxFlow
        var used = 0
        val iterator = links[node].iterator()
        while (iterator.hasNext()) {
            val link = iterator.next()
            if (depth[link] > depth[node]) {
                val newUsed = followPath(link, depth, minOf(maxFlow - used, flow[node][link]))
                used += newUsed
                if (newUsed == 0) {
                    iterator.remove() //  WARNING - I think this is wrong!!!
                } else {
                    flow[node][link] -= newUsed
                    if (flow[node][link] == 0) {
                        iterator.remove()
                    }
                    flow[link][node] += newUsed
                }
            }
        }
        return used

    }
    fun print() {
        println("Max flow " + maxFlow)
        for (i in flow.indices) {
            println()
            for (j in flow.indices) {
                print(flow[i][j])
                print(' ')
            }
        }
    }

}
