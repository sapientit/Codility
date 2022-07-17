package uk.co.sapientit.codility

class Cuprum {
    /*
    There are only 36 possible values, so we use each possible values as a bit in a LONG integer.
    If we xor a value as we go through the string then anything with the same resulting value has only even numbers
    of each character between them.
    Judging when a sort is more efficient than a hashmap is tricky.  On this occasion the sort wins significantly - the
    hashmap solution fails with a hard timeout.

    Codility don't give the option to solve this in Kotlin for some reason though.

    So the Kotlin solution is not correct  - use a sort as per the Swift solution
     */
    fun solution(S: String) : Int {
        val mapChar = HashMap<Char, Long>()
        var bit = 1L
        for (c in "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray()) {
            mapChar[c] = bit
            bit = bit shl 1
        }
        var total = 0L
        val values = LongArray(S.length)
        val last = HashMap<Long,Int>()
        for (i in 0 until S.length) {
            total = total xor mapChar[S[i]]!!
            values[i] = total
            last[total] = i
        }
        var index = 0
        var max = 0
        val zero = last[0]
        if (zero != null)
            max = zero + 1
        while (S.length - index > max) {
            max = maxOf(max, last[values[index]]!! - index)
            index += 1
        }
        return max
    }
}
/* Swift Solution
    // This is the equivalent of the Kotlin code above with a hashmap.  It fails the performance tests with a hard
    // timeout.
    public func solutionMap(_ S : inout String) -> Int {
        var mapChar : [Character: Int64] = [:]
        var bit : Int64 = 1
        for c in "abcdefghijklmnopqrstuvwxyz0123456789" {
            mapChar[c] = bit
            bit = bit << 1
        }
        var total: Int64 = 0
        var values : [Int64] = Array(repeating: 0, count: S.count )
        var last : [Int64 : Int] = [:]
        var index = 0
        for c in S {
            total = total ^ mapChar[c]!
            values[index] = total
            last[total] = index
            index += 1
        }
        index = 0
        var maxLength = 0
        let zero = last[0]
        if zero != nil {
            maxLength = zero! + 1
        }
        while (S.count - index > maxLength) {
            maxLength = max(maxLength, last[values[index]]! - index)
            index += 1
        }
        return maxLength
    }

    // The below solution is 100% - we sort instead of using a hashmap.



    public func solution(_ S : inout String) -> Int {
        var mapChar : [Character: Int64] = [:]
        var bit : Int64 = 1
        for c in "abcdefghijklmnopqrstuvwxyz0123456789" {
            mapChar[c] = bit
            bit = bit << 1
        }
        var total: Int64 = 0
        var values : [Int64] = Array(repeating: 1, count: S.count + 1)
        var index = 1
        // Note that the array is 1 extra length - that is so we can store 0 (our starting value) as position 0
        // Otherwise we would not find passwords that started at the very beginning.
        values[0] = 0 // This is starting value
        for c in S {
            total = total ^ mapChar[c]!
            values[index] = total
            index += 1
        }
        // Up to this point the code is identical to the first try.  Now instead of keeping track of "last" with a hashmap
        // we sort the results into value and position sequence.
        //  value is the xor'd value - the same value means that there are even numbers of all characters between them.
        var sorted = Array<Int>(repeating: 0, count: S.count + 1)
        for i in 0...sorted.count - 1 {
            sorted[i] = i
        }
        sorted = sorted.sorted(by: {
            if values[$1] == values[$0] {
                return $1 > $0 // Ascending position within value
            }
          return values[$1] > values[$0]
        })
        // Now simply go through the sorted list and find first and last entries for each value.  Take the maximum
        // difference
        index = 0
        var maxLength = 0
        var prev = Int64(-1)  // Not a possible value
        var length = 0
        var prevIndex = 0
        for i in sorted.indices {
            let current = sorted[i]
            if values[current] == prev {
                length = current - prevIndex
            } else {
                maxLength = max(maxLength,length)
                prev = values[current]
                prevIndex = current
            }
        }
        // A final check for maximums that finish at the end of the string
        maxLength = max(maxLength,length)
        return maxLength
    }
 */