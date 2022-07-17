package uk.co.sapientit.templateapp

class Palladium {
    /*
    Clearly one of the banners must be of the same height as the maximum height of the buildings.
    The question therefore is how wide, and how big is the other banner.
    As we read through the heights we only need to deal with buildings that are higher than any that have come before.
    If, for example, we have buildings of height 3,2,6, then the height "2" must be irrelevant because either it must be
    covered by a banner of height 3 or 6.
    So to take a bigger example:
    Buildings of height:

    4 2 6 3 5 8 7 2 3

    One banner is 8.  The maximums from the left are 4,6,8, and from the right 3, 7, 8.
    The options to consider are therefore:

    a banner of height 4 covering up to the the building before the 6
    A banner of 6 covering up to the building before 8
    and from the right
    a building of 3 covering up to just after 7 (ie. covering 3 and 2)
    a building of 7 covering 3,2,7

    A bad example since the minimum of 62 can be obtained in 2 ways, but you get the idea...

     */
    fun solution(H: IntArray): Int {
        val maxLeft = IntArray(H.size)
        var highWater = 0
        maxLeft[0] = 0
        var max = H[0]
        for (i in H.indices)  {
            if (H[i] > max) {
                max = H[i]
                highWater += 1
                maxLeft[highWater] = i
            }
        }
        var maxR = H[H.size - 1]
        var min = max * (H.size - 1) + H[H.size - 1]
        var index = H.size - 2
        while (maxR < max) {
            if (H[index] > maxR) {
                min = minOf(min, max * (index + 1) + (maxR * (H.size - index - 1)))
                maxR = H[index]
            }
            index -= 1
        }
        if (highWater > 0) {
            for (i in 1..highWater) {
                index = maxLeft[i]
                val prev = maxLeft[i - 1]
                val prevMax = H[prev]
                min = minOf(min, prevMax * (index) + max * (H.size - index))
            }
        }
        return min
    }
}