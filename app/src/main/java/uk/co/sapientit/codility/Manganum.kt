package uk.co.sapientit.codility

import kotlin.random.Random

/*
100% solution

This is made more complicated by the facts that:
 - they are on diagonals.
 - two adjacent pieces form a block,
 - they can be any number up to 100000000

To solve the first problem, we translate X/Y to X + Y and Y - X.  That makes the trianlge that we can reacah into
a quadrant X >=0 and Y >= 0.  Adjacent squares are actually 2 apart, so 0,0 is adjacent to 2,0 and 0,2.
The rotated coordinates are stored in the Piece class as origX and origY

To solve the second problem we have to sort and then work out which pieces are adjacent horizontally and vertically
(origX is the same and origY differs by 2 and vv) This is the meaning of verticalBlock and horizontalBlock in the Piece class.

And finally we can collapse the range into (0..N) by translating origX to x and origY to y, adding 1 every time we find
a new original value.

Now we have a simpler problem.

Still not simple though...

The line that goes through the node with increasing X (horizontal) is different from the vertial line of increasing y.

The horizontal is simpler.  It either has already passed through a node on this row (currentRow) or it is from a vertical
row on a previous row (but that column must be AFTER the previous node on this row.

So I need a query of maximum on each of the verticals between PrevNode.X + 1 and This Node.X - 1.

This value goes along horizontally until either we reach the end of the row or hit another node (stored in currentRow,
and in prevY we store the ThisNode.X + 1 (which is also the value we needed above).

To do this I need a data structure that gives me the maximum value between in an array between n1 and n2

The vertical line is either a vertical line directly below this node or it is a horizontal line that runs above that previous
node.

So I need to know the previous value on this value of X and be able to query the maximum value of line travelling
horizontally ABOVE the previous node and still in effect for this value X.

Consider:
__?
_P_
_Q_
__P
_Q
P
O

We have to go up through the first P
We can then turn right throgh the Q and up through the P - vertically value 12
Or we can carry on up and then turn right through the Q - horizontally value 11
Or go further and turn right through the P - horizontally value 2

So for the vertical through the ?  We have a line travelling vertically 12 to compare againsst the highesst value horizontal
after that the P on row 4 (maxOf(2,11) = 11

For this I need a data structure that enables me to set a minimum value for a range N1 to N2.

In both cases I need a segment tree.  The SegmentTreeMax maintains just the maximum value and supports queries
of max value between N1 and N2.  This is a fairly standard segment use of a segment tree

The ssecond segment tree allows for the update of a MINIMUM value for a range.  It does that by allowing for deferred
updates.

Deferred updates can be stored at any level of a tree.  When querying a value you have to propagate the value to the
next level of the tree (both left and right before drilling down).  This is still a logN operation.

For minimum deferred you have to be careful to not just overwrite a previous deferred value - you must take the maximum
of the 2 deferred entries.

Once you have got your data structures, the rest is just coding.  In order to use 0 as no path exists the maxValue is
always 1 higher than the real maximum value.
There is a small challenge of the starting condition, as although we start off going horizontally and vertically with
value 1 (1 more than real value) we can't turn in the same way we can with other lines - we have to go though a node
first.
 */
class Manganum {
    class SegmentTreeMax(size: Int) {
        /*
        This segment tree just maintains the maximum value and provides a function to find the maximum between 2 points
        It doesn't provide for range updates.
         */
        class SegmentTreeMaxNode () {
            var left = 0
            var right = 0
            val value : Int
                get() = maxOf(left, right)
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
        val tree: Array<SegmentTreeMaxNode>
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
            tree = Array(leaves - 1){ SegmentTreeMaxNode() }
            levels = IntArray(levelCount)
            levels[levels.lastIndex] = tree.lastIndex
            var current = 2
            for (i in levels.size - 2 downTo 0) {
                levels[i] = levels[i+1] - current
                current = current shl 1
            }
        }
        fun set(leaf: Int, value: Int) {
            var prev = leaf
            var newValue = value
            for (i in 0 until levels.size) {
                val current = prev shr 1
                val index = levels[i] + current
                tree[index].setChild(prev, newValue)
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
        fun findMax(from: Int, to: Int): Int {
            return findMaxLevel(from, to, levels.lastIndex, 0)

        }
        fun findMaxLevel(from: Int, to: Int, level: Int, current: Int): Int {
            val index = levels[level] + current
            val left = current shl (level + 1)
            val right = ((current shl 1) + 1) shl level
            val limit = 2 * right - left
            var leftValue = Int.MIN_VALUE
            var rightValue = Int.MIN_VALUE
            if (level == 0) {
                if (from <= left) {
                    leftValue = tree[index].left
                }
                if (to >= right) {
                    rightValue = tree[index].right
                }
            }else {
                if (from < right) {
                    if (from <= left && to >= right - 1) {
                        leftValue = tree[index].left
                    } else {
                        leftValue = findMaxLevel(from, to, level - 1, current shl 1)
                    }
                }
                if (to >= right) {
                    if (from <= right && to >= limit - 1) {
                        rightValue = tree[index].right
                    } else {
                        rightValue = findMaxLevel(from, to, level - 1, (current shl 1) + 1)
                    }
                }
            }
            return maxOf(leftValue, rightValue)
        }
    }
    class SegmentTreeMinRange(size: Int) {
        /*
        This segment tree does provide for range updates of the minimum value.
        So you can say that for lines 5-10 make the minimum value 7.
        Retrieval is on a specific point, there is no range retrieval.
         */
        class SegmentTreeNode () {
            var left = 0
            var right = 0
            var defer: Int? = null
            val value : Int
                get() = maxOf(left, right)
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
        private fun setLevel(leaf: Int, value: Int, level: Int, current: Int) : SegmentTreeNode {
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
                    propagateDefer(i,current, index)
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
            tree[left].left = maxOf(tree[index].defer!!, tree[left].left)
            tree[left].right = maxOf(tree[index].defer!!, tree[left].right)
            tree[right].left = maxOf(tree[index].defer!!, tree[right].left)
            tree[right].right = maxOf(tree[index].defer!!, tree[right].right)
            if (level > 1) {
                tree[left].defer = maxOf(tree[index].defer!!, tree[left].defer ?: 0)
                tree[right].defer = maxOf(tree[index].defer!!, tree[right].defer ?: 0)
            }
            tree[index].defer = null
        }
        fun setRangeMin(from: Int, to: Int, newValue: Int) {
            setRangeLevel(from, to, newValue, levels.lastIndex, 0)
        }
        /*
        Consider a size of 8
        level 2 - is from <= 0 and 2 >= 7
        Level 1 - left.  Is 0-3  and right is 4-7
         */
        fun setRangeLevel(from: Int, to: Int, newValue: Int, level: Int, current: Int) : SegmentTreeNode {
            val index = levels[level] + current
            if (tree[index].defer ?: 0 >= newValue) return tree[index]
            val left = current shl (level + 1)
            val right = ((current shl 1) + 1) shl level
            if (level == 0) {
                if (left >= from) {
                    tree[index].left = maxOf(newValue, tree[index].left)
                }
                if (right <= to) {
                    tree[index].right = maxOf(newValue, tree[index].right)
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
    data class Piece (val origX: Int, val origY: Int, val size: Int) {
        var x = 0
        var y = 0
        var horizontalBlock = false  // There exists left or right pice (x axis)
        var verticalBlock = false // There exists another piece directly above or below (y axis)
    }
    fun solution(X: IntArray, Y: IntArray, T: String): Int {

        var maxValue = 1
        var xOrigin = Int.MIN_VALUE
        var yOrigin = Int.MIN_VALUE
        val pieces = Array<Piece>(X.size){
            val x = X[it] + Y[it]
            val y = Y[it] - X[it]
            val value: Int = when (T[it]) {
                'X' -> {
                    xOrigin = x
                    yOrigin = y
                    0
                }
                'p' -> 1
                'q' -> 10
                else -> Int.MIN_VALUE
            }
            Piece(x,y,value)
        }

        pieces.sortWith(compareBy<Piece> { it.origX }.thenBy { it.origY })

        val dummy = Piece(Int.MIN_VALUE, Int.MIN_VALUE,0).apply{x = xOrigin - 1;y=yOrigin - 1}
        var prev = dummy
        var prevX = xOrigin - 1
        var index = 0
        for (piece in pieces) {
            if (piece.origX > prevX) {
                index++
                prevX = maxOf(piece.origX, prevX)
            } else {
                if (piece.origY - 2 == prev.origY && prev.size != 0) {
                    piece.verticalBlock = true
                    prev.verticalBlock = true
                }
            }
            prev = piece
            piece.x = index
        }
        val distinctX = index + 2

        pieces.sortWith(compareBy<Piece> { it.origY }.thenBy { it.origX })
      /*  pieces.sortWith(Comparator<Piece>{
            p1,p2 ->
            if (p1.origY == p2.origY) {
                p1.origX - p2.origX
            } else {
                p1.origY - p2.origY
            }
        }) */
        index = 0
        prev = dummy
        var prevY = yOrigin - 1
        for (piece in pieces) {
            if (piece.origY > prevY) {
                index++
                prevY = maxOf(prevY,piece.origY)
            } else {
                if (piece.origX - 2 == prev.origX && prev.size != 0) {
                    piece.horizontalBlock = true
                    prev.horizontalBlock = true
                }
            }
            prev = piece
            piece.y = index
        }

        val vTree = SegmentTreeMax(distinctX)
        val hTree = SegmentTreeMinRange(distinctX)
        hTree.set(1,1)
        var prevRow = 1
        var prevCol = -1
        var currentRow = 1
        for (piece in pieces) {
            if (piece.x < 1 || piece.y < 1 || piece.size == 0) continue  // not reachable
            if (piece.y > prevRow) {
                if (prevCol != -1) {
                    hTree.setRangeMin(prevCol, distinctX, currentRow)
                }
                prevCol = -1
                currentRow = 0
                prevRow = piece.y
            } else {
                if (prevCol != -1) {
                    hTree.setRangeMin(prevCol, piece.x - 1, currentRow)
                }

            }
            var horizontal : Int  // The value going through from low x to high x
            if (piece.horizontalBlock) {
                horizontal = 0
                maxValue = maxOf(maxValue, currentRow)
            } else {
                horizontal = maxOf(currentRow,vTree.findMax(prevCol, piece.x - 1))
            }
            var vertical : Int
            if (piece.verticalBlock) {
                vertical = 0
            } else {
                vertical = hTree.getLeaf(piece.x)
            }
            if (vertical > 0) {
                vertical += piece.size
            }
            if (horizontal > 0) {
                horizontal += piece.size
            }
            currentRow = horizontal
            prevCol = piece.x + 1
            maxValue = maxOf(maxValue,maxOf(vertical,horizontal))
            vTree.set(piece.x, vertical)
            hTree.set(piece.x,vertical)

        }
    //    println("REsult " + (maxValue -1))
        return maxValue - 1

    }
    /*

    All of the rest of the section is for generating test data and testing the solution against a brute force
    approach









     */
    fun generateData(rand: Random, pieces: Int, size : Int): Boolean {
        val unsorted = mutableListOf<GenPiece>()
        unsorted.add(GenPiece(size / 2, 0, 'X', rand))
        while (unsorted.size < pieces) {
            val y = rand.nextInt(size + 1)
            val odd = y % 2
            val x = rand.nextInt (size / 2) * 2 + odd
            val piece = if (rand.nextInt(2) == 1) 'q' else 'p'
            var found = false
            unsorted.forEach {
                if (it.x == x && it.y == y)
                    found = true
            }
            if (!found)
                unsorted.add(GenPiece(x, y, piece, rand))
        }
        val index = IntArray(unsorted.size){rand.nextInt(1000000)}
        unsorted.sortBy { it.index }
        val sort = unsorted.toTypedArray()
        val X = IntArray(unsorted.size){sort[it].x}
        val Y = IntArray(unsorted.size){sort[it].y}
        var T = ""
        for (i in sort.indices) {
            T = T + sort[i].type
        }
        val t1 = solution(X,Y,T)
        val t2 = bruteForce(X,Y,T)
        if (t1 != t2) {
            println("t1 = " + t1 + " " + t2)
            return false
        }
        return true
    }
    fun bruteForce(X: IntArray, Y: IntArray, T: String): Int {
        val x = IntArray(X.size) {X[it] + Y[it]}
        val y = IntArray(Y.size){Y[it] - X[it]}
        var maxX = 0
        var maxY = 0
        for (i in x.indices) {
            maxX = maxOf(maxX,x[i])
            maxY = maxOf(maxY,y[i])
        }
        val pieceValue = IntArray(Y.size) {
            when (T[it]) {
                'X' -> -1
                'p' -> 1
                'q' -> 10
                else -> Int.MIN_VALUE
            }
        }
        var start = 0
        for (i in pieceValue.indices) {
            if (pieceValue[i] == -1)
                start = i
        }
        var maxValue = 0
        var minY = Int.MAX_VALUE
        var xValue = 0
        for (i in x.indices) {
            if (x[i] == x[start] && y[i] > y[start]) {
                if (y[i] < minY ) {
                    minY = y[i]
                    xValue = pieceValue[i]
                }
            }
        }
        var minX = Int.MAX_VALUE
        var yValue = 0
        for (i in y.indices) {
            if (y[i] == y[start] && x[i] > x[start]) {
                if (x[i] < minX) {
                    minX =  x[i]
                    yValue = pieceValue[i]
                }
            }
        }
        if (minY < Int.MAX_VALUE) {
            maxValue = vertical(x,y,pieceValue, x[start], minY + 2, xValue,maxX,maxY)
        }
        if (minX < Int.MAX_VALUE) {
            maxValue = maxOf(maxValue, horizontal(x,y,pieceValue, minX + 2, y[start], yValue, maxX, maxY))
        }
        return maxValue
    }
    fun vertical(x: IntArray, y: IntArray, pieceValue: IntArray, fromX: Int, fromY: Int, value: Int, maxX: Int,maxY: Int) : Int {
        for (i in x.indices) {
            if (x[i] == fromX && y[i] == fromY)
                return 0
        }
        // This was a valid jump
        // Now find all possible next moves
        var minY = Int.MAX_VALUE
        var pieceVal = 0
        for (i in x.indices) {
            if (x[i] == fromX && y[i] > fromY && y[i] < minY) {
                minY =  y[i]
                pieceVal = pieceValue[i]
            }
        }
        var maxValue = 0
        if (minY < Int.MAX_VALUE) {
            maxValue = vertical(x,y,pieceValue, fromX, minY + 2, pieceVal, maxX, maxY)
        }
        val toY = minOf(maxY, minY - 2)
        for (i in fromY..toY step 2) {
            var minX = Int.MAX_VALUE
            for (j in x.indices) {
                if (y[j] == i && x[j] > fromX && x[j] < minX) {
                    minX = x[j]
                    pieceVal = pieceValue[j]
                }
            }
            if (minX < Int.MAX_VALUE) {
                maxValue = maxOf(maxValue,horizontal(x,y,pieceValue, minX + 2, i, pieceVal, maxX, maxY))
            }
        }
        return maxValue + value
    }
    fun horizontal(x: IntArray, y: IntArray, pieceValue: IntArray, fromX: Int, fromY: Int, value: Int, maxX: Int,maxY: Int) : Int {
        for (i in x.indices) {
            if (x[i] == fromX && y[i] == fromY)
                return 0
        }
        // This was a valid jump
        // Now find all possible next moves
        var minX = Int.MAX_VALUE
        var pieceVal = 0
        for (i in x.indices) {
            if (y[i] == fromY && x[i] > fromX && x[i] < minX) {
                minX =  x[i]
                pieceVal = pieceValue[i]
            }
        }
        var maxValue = 0
        if (minX < Int.MAX_VALUE) {
            maxValue = horizontal(x,y,pieceValue, minX + 2, fromY, pieceVal, maxX, maxY)
        }
        val toX = minOf(maxX, minX - 2)
        for (i in fromX..toX step 2) {
            var minY = Int.MAX_VALUE
            for (j in x.indices) {
                if (x[j] == i && y[j] > fromY && y[j] < minY) {
                    minY = y[j]
                    pieceVal = pieceValue[j]
                }
            }
            if (minY < Int.MAX_VALUE) {
                maxValue = maxOf(maxValue,vertical(x,y,pieceValue, i, minY + 2, pieceVal, maxX, maxY))
            }
        }
        return maxValue + value
    }
    data class GenPiece (val x: Int, val y: Int, val type: Char, var rand: Random) {
        var index = rand.nextInt(10000)
    }

}