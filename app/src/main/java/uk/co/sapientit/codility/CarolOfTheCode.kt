package uk.co.sapientit.codility

class CarolOfTheCode {
    /*
    https://app.codility.com/cert/view/certX9TX28-62PC8M45RDCA2BVG/
    This seems a really easy challenge and yet the fastest time was 7 minutes.  It would have
    been a much harder challenge if repeating colours were allowed.
    Anyway, for completeness, the key to this challenge is that each colour appears
    only once.  So if the start tile is in a certain position then that means all
    the position of all the other tiles is fixed by that.
    So we just keep 4 totals - one for red on the left side of the first tile, one
    for green, etc.  Then just add the number of turns tile by tile to make that happen
     */
    fun solution(A: Array<String>): Int {
        // write your code in Kotlin 1.6.0 (Linux)
        var prev = mutableMapOf('R' to 0, 'G' to 0, 'W' to 0, 'B' to 0)
        var next = mutableMapOf<Char,Int>()
        for (current in A) {
            next[current[0]] = prev[current[2]]!! + 1
            next[current[1]] = prev[current[3]]!!
            next[current[2]] = prev[current[0]]!! + 1
            next[current[3]] = prev[current[1]]!! + 2
            prev = next
            next = mutableMapOf<Char,Int>()
        }
        return minOf(prev['R']!!,minOf(prev['G']!!,minOf(prev['B']!!, prev['W']!!)))
    }
}