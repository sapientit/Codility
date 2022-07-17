package uk.co.sapientit.codility

class JurassicCode {
    /*
    https://app.codility.com/cert/view/cert3VAHUB-BDW5ZRBNZQWDW6MV/
    These challenges seems to be getting easier.  The key to this challenge is obviously to sort into decreasing distance
    order (you could also do it increasing, but that means you always need to process all the data).
    Having done that, go from the end of the array until you find a distance that leaves you an equal number.

    Note that it is possible for two dots to be equidistant, so only stop looking if the current distance is Not equal
    to the previous distance.
     */
    fun solution(X: IntArray, Y: IntArray, colors: String): Int {
        // write your code in Kotlin 1.3.11 (Linux)
        val distance = IntArray(X.size){X[it] * X[it] + Y[it] * Y[it]}
        var red = 0
        for (colour in colors) {
            red += if (colour == 'R') 1 else -1
        }
        val sorted = IntArray(colors.length){it}.sortedWith (Comparator<Int>(){d1,d2 -> distance[d2] - distance[d1]})
        var prev = Int.MAX_VALUE
        for (i in sorted.indices) {
            val current = sorted[i]
            if (red == 0 && distance[current] != prev) {
                return colors.length - i
            }
            prev = distance[current]
            red -= if (colors[current] == 'R') 1 else -1
        }
        return 0
    }

}