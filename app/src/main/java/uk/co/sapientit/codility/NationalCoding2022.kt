package uk.co.sapientit.codility

class NationalCoding2022 {
//https://app.codility.com/cert/view/certQUCXB4-UFMD6NNKFVHV97UC/

    fun solution(A: IntArray, B: IntArray): Int {
        val nodes = A.size + 1
        val group = IntArray(nodes){it}
        val odds = IntArray(nodes)
        val evens = IntArray(nodes){1}
        val isOdd = BooleanArray(nodes){false}
        var total = 0
        for (i in A.indices) {
            val a = getGroup((A[i]), group, isOdd)
            val b = getGroup((B[i]), group, isOdd)
            // At any point, a is a member of group "C" and "b" is a member of group D.
            // From the viewpoint of c, there are evens[c] nodes attached with 0,2,4... length
            // and  isOdd[a] tells us if a-> c is an odd number of steps.  If that is true then
            // evens/odds should be swapped
            // So for each connection we make we add evens * evens + odds * odds
            // But what about parity? Both roots start as even.  One stays as even as the new root. The
            // other may now be odd - if the nodes that join have the same parity.
            //  So take the example of A-C  joining to B-D.  A and B are both odd
            // (relative to C/D respectively), so the new chain of C-A-B-D means
            // that D is now odd parity (3 steps away).
            /*
            So to explain the code step by step...
            We are going to build the tree one link at a time without sorting.
            So that means we have O(N) complexity.
            Building one link at a time means that we will have lots of little
            islands that get joined.
            We maintain a table "group" that shows which group any node belongs to
            and we maintain the "isOdd" array to say whether we are an even or
            odd number of steps away from that root.
            So what happens when we join 2 islands.
            Let's take the simple case of single nodes being joined.
            Initially island 0 and island 1 have just 1 node each at a distance
            0 (the node itself).  So evens[0] and evens[1] are both 1 and
            odds[0/1] are zero
            After we join we have one odd length connection.  If the new root is
            node 1, then node 0 is an odd distance away and node 1 is still even.
            Now link node 2 to node 1.
            Node 2 is o distance away from itself.  Node 1 is 1 away from its root (0)
            The calculation boils down to are both the nodes an even distance away
            from the root, both odd, or one odd and one even?
            If they are the same, then we create evens[root-a] * evens[root-b]
            + odds[root-a] * odds[root-b] new odd length links.
            If they are different then one of the even/odd numbers needs to be
            swapped (evens[root-a] * odds[root-b] + odds[root-a] * evens[root-b])
            Then we make the root of a the new root and adjust the number of even/odd
            nodes attached to it.
             */
            if (isOdd[A[i]] xor isOdd[B[i]]) {
                total += odds[a] * evens[b] + evens[a] * odds[b]
                evens[a] += evens[b]
                odds[a] += odds[b]
            } else {
                total += evens[a] * evens[b] + odds[a] * odds[b]
                evens[a] += odds[b]
                odds[a] += evens[b]
                isOdd[b] = true
            }
            group[b] = a

        }
        return total
    }
    fun getGroup(node: Int, group: IntArray, isOdd: BooleanArray) : Int {
        var parity = isOdd[node]
        var current = node
        while (current != group[current]) {
            current = group[current]
            parity = parity xor isOdd[current]
        }
        group[node] = current
        isOdd[node] = parity
        return current
    }
    fun solutionFailed(A: IntArray, B: IntArray): Int {
        /*
        This was the first attempt that failed with timeout
         */
        val nodes = A.size + 1
        val count = IntArray(nodes)
        val odds = IntArray(nodes)
        val evens = IntArray(nodes)
        val cons = Array(nodes){HashSet<Int>()}
        for (i in A.indices) {
            cons[A[i]].add(B[i])
            cons[B[i]].add(A[i])
        }
        val tree = IntArray(nodes)
        var hwm = 0
        val visited = BooleanArray(nodes){false}
        for (i in 0 until nodes) {
            if (cons[i].size == 1) {
                tree[hwm++] = i
            } else {
                count[i] = cons[i].size
            }
        }
        var index = 0
        var total = 0
        while (index < hwm) {
            val current = tree[index]
            visited[current] = true
            for (c in cons[current]) {
                if (!visited[c]) {
                    total += evens[current] * evens[c] + odds[current] * odds[c] + 1 + evens[current] + evens[c]
                    odds[c] += evens[current] + 1
                    evens[c] += odds[current]
                    count[c]--
                    cons[c].remove(current)
                    if (count[c] == 1) {
                        tree[hwm++] = c
                    }
                }
            }
            index++
        }
        return total
    }
}