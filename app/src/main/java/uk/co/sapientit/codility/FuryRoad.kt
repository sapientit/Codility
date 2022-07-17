package uk.co.sapientit.codility

class FuryRoad {
    // https://app.codility.com/cert/view/certNP49U7-XGQ76AW9C9U9D779/
    /*
    Obviously you can do this other ways, dynamic programming, prefix sums, etc, but all of that is overkill.
    Starting from the back, calculate for each step how much faster it would be to walk from that place (which is
    the same as previous spot +10 or -15 depending on the surface.
    We just record the maximum saving we make through the array.  Meanwhile work out how long it would take by scooter,
    and then we add the maximum saving (or subtract depending on whether you kept it as a +ve or -ve number)

    Then switch it all around because you realise that you misread it as walk first and then scooter..  Still couldn't have
    type it all in 2 minutes though (especially not in Kotlin where Codility inists that you have to switch from Java to Kotlin before you can start
    coding... :-))
     */
    fun solution(R: String): Int {
        // write your code in Kotlin 1.3.11 (Linux)
        var maxReduct = 0
        var current = 0
        var total = 0
        for (i in R.length - 1 downTo 0) {
            if (R[i] == 'A') {
                current -= 15
                total += 5
            } else {
                current += 10
                total += 40
                if (current > maxReduct) {
                    maxReduct = current
                }
            }
        }
        return total - maxReduct
    }
}