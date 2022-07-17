package uk.co.sapientit.templateapp

class Bromum {
    /*
    No tests for Kotlin again :-(
    Simply sort into bucket / colour / Time sequence and read through looking for the earliest time we reach Q
    There is an alternative method (shown in Swift below) that avoids the sort but uses a map instead.  The
    overhead of the map (and creating a key for the map) is more than the sort if you have to read the entire array
    but it has the advantage that you can stop when you find a solution.  So sometimes it is quicker and sometimes
    slower.
     */
    fun solution(N: Int, Q: Int, B: IntArray, C: IntArray) : Int {
        val compare = Comparator<Int> { b1, b2 ->
            if (B[b1] == B[b2])
                if (C[b1] == C[b2])
                    b1 - b2
                else
                    C[b1] - C[b2]
            else
                B[b1] - B[b2]
        }
        val sorted = IntArray(B.size) { it }.sortedWith(compare)
        var minCount = Int.MAX_VALUE
        var currentColour = -1
        var currentBucket = -1
        var count = 0
        for (i in sorted.indices) {
            val current = sorted[i]
            if (B[current] == currentBucket) {
                if (C[current] == currentColour) {
                    count += 1
                } else {
                    count = 1
                    currentColour = C[current]
                }
            } else {
                count = 1
                currentBucket = B[current]
                currentColour = C[current]
            }

            if (count == Q) {
                minCount = minOf(minCount, current)
            }
        }
        if (minCount == Int.MAX_VALUE)
            return -1
        return minCount

    }
    /* 100% solution converted to Swift
       public func solution(_ N : Int, _ Q : Int, _ B : inout [Int], _ C : inout [Int]) -> Int {
        var sorted = Array<Int>(repeating: 0, count: B.count)
        for i in 0...sorted.count - 1 {
            sorted[i] = i
        }
        sorted = sorted.sorted(by: {
            if (B[$0] == B[$1]) {
                if (C[$0] == C[$1]) {
                    return $1 > $0
                } else {
                    return C[$1] > C[$0]
                }
            } else {
                return B[$1] > B[$0]
            }
        })
        var minCount = Int.max
        var currentColour = -1
        var currentBucket = -1
        var count = 0
        for i in sorted.indices {
            let current = sorted[i]
            if (B[current] == currentBucket) {
                if (C[current] == currentColour) {
                    count += 1
                } else {
                    count = 1
                    currentColour = C[current]
                }
            } else {
                count = 1
                currentBucket = B[current]
                currentColour = C[current]
            }
            if (count == Q) {
                minCount = min(minCount, current)
            }
        }
        if minCount == Int.max {
            return -1
        }
        return minCount

    }
    /*
     Performance is consistent - we always sort and we always read all the entries
     1. 0.068 s OK
     2. 0.068 s OK
     3. 0.068 s OK
     */
    public func solutionMap(_ N : Int, _ Q : Int, _ B : inout [Int], _ C : inout [Int]) -> Int{
        var map: [Int64:Int] = [:]
        let n = Int64(N)
        var count = 0
        for i in B.indices {
            // We know the number of buckets so we can create a 64 bit key by multiplying colour by N and add
            // bucket number.  Then use a dictionary (map) to store the count
            let index: Int64 = Int64(C[i]) * n + Int64(B[i])
            if let prev = map[index] {
                count = prev + 1
                map[index] = count
            } else {
                count = 1
                map[index] = 1
            }
            if count == Q {
                return i
            }
        }
        return -1

    }
    /*
    The performance is better for cases 1 and 2, but worse for case 3.  Presumably case 3 we need to read to the end

     1. 0.044 s OK
     2. 0.040 s OK
     3. 0.072 s OK
     */
     */
}