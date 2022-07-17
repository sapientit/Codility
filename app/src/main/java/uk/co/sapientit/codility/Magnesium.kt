package uk.co.sapientit.templateapp

class Magnesium {
    /*
    Sort the links into ascending value sequence and then apply each link.  The node value
    is then maximum of existing node value or the connected node value + 1
    That would be if duplicate values were not allowed.
    Since they are we have to store the previous value for any node we touch
     */
    fun solution(N: Int, A: IntArray, B: IntArray, C: IntArray): Int {
        var nodeValue = IntArray(N)
        val preVNodeValue = IntArray(N)
        val lastValue = IntArray(N)
        val comparator = Comparator<Int>{
            i1, i2 ->  C[i1] - C[i2]
        }
        val processSequence = IntArray(A.size){i-> i}.sortedWith(comparator)
        processSequence.forEach {
            val i1 = A[it]
            val i2 = B[it]
            val value = C[it]
            val v1: Int
            val v2: Int
            if (lastValue[i1] == value) {
                // This is the second path with the same value to the same node.
                    // Therefore have to use the stored previous value
                v1 = preVNodeValue[i1]
            } else {
                v1 = nodeValue[i1]
                // This is a new value of link, so store value of link and value of node before this link
                lastValue[i1] = value
                preVNodeValue[i1] = nodeValue[i1]
            }
            if (lastValue[i2] == value) {
                v2 = preVNodeValue[i2]
            } else {
                v2 = nodeValue[i2]
                lastValue[i2] = value
                preVNodeValue[i2] = nodeValue[i2]
            }
            nodeValue[i1] = maxOf(nodeValue[i1],v2 + 1)
            nodeValue[i2] = maxOf(nodeValue[i2],v1 + 1)
            lastValue[i1] = value
            lastValue[i2] = value
        }
        var max = 0
        nodeValue.forEach {
            max = maxOf(max,it)
        }
        return max
    }
}