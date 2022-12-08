package uk.co.sapientit.codility

class GameOfCodes {
    /*
    We can afford to maintain a cost to position x (as first of new block) and with n blocks to go.
    so when we get to each position, we look for next of same character and calculate cost to that
    character without starting a new block.
    Then the minimum cost for a block ended is the minimum of min + 1 or cost to current
    https://app.codility.com/cert/view/certTW75G2-8Q2W2PZD9MG4FSGR/

    In more detail.
    "Next" array maintains the next occurence of each character (or -1 if this is the last
    instance of that character,
    "costTo"  is a 2D array.  The first dimension is the current block (2,1 or 0),  The
    second dimension is the position in the string.  The value stored is the cost of deletions
    such that this character is included in the relevant block.
    Note that when we have populated costTo for index i, we can also populate it for
    the next occurence of that character (using the next array) by deleting all the characters
    in between.
    min1, min2 and min3 are the minimum costs as we go through the array to being in the last
    block, the second last or the first block.

    For example we get to a character.  We populate min1 and min3 and costTo[0 and 2]  but to
    take an example of popliating min2 and costTo[1].
    costTo is the cost where this character is being used in the relevant block.  So be included
    in the second block, either we were already in the second block for this character (and
    the cost is stored in in costTo) or we were in the first block with min3 deletions.
    We take the minimum of min3 or the existing value of costTo[1].  We then use this value
    to calculate costTo for the next occurence of this character.
    min1 is then either costTo, or it is the previous version of min1 + 1 (we delete this
    character as well).  

     */

    fun solution(S: String): Int {
        val last = IntArray(26){-1}
        val next = IntArray(S.length){-1}
        // write your code in Kotlin 1.6.0 (Linux)
        for (i in S.indices) {
            val c = S[i] - 'a'
            if (last[c] != -1) {
                next[last[c]] = i
            }
            last[c] = i
        }
        var min3 = S.length
        var min2 = S.length
        var min1 = S.length
        val costTo = Array(3){IntArray(S.length){S.length}}
        for (i in S.indices) {
            val nextIndex = next[i]
            costTo[0][i] = minOf(costTo[0][i], min2)
            costTo[1][i] = minOf(costTo[1][i], min3)
            costTo[2][i] = minOf(costTo[2][i],i)
            min1 = minOf(min1 + 1, costTo[0][i])
            min2 = minOf(min2 + 1, costTo[1][i])
            min3 = minOf(min3 + 1, costTo[2][i])
            if (nextIndex != -1) {
                costTo[0][nextIndex] = costTo[0][i] + nextIndex - i - 1
                costTo[1][nextIndex] = costTo[1][i] + nextIndex - i - 1
                costTo[2][nextIndex] = costTo[2][i] + nextIndex - i - 1
            }
        }
        return S.length - minOf(minOf(min1,min2),min3)
    }
}