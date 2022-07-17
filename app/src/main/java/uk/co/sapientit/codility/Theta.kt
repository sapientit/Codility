package uk.co.sapientit.templateapp

class Theta {
    fun solution(D: IntArray, P: IntArray, T: Int): Int {
        var total = 0L
        val refills = IntArray(D.size + 1)
        val prices = IntArray(D.size + 1)

        var highWater = 0
        for (i in D.size - 1 downTo 0) {
            var capacity = T - D[i]
            if (capacity < 0)
                return -1

            while (P[i] < prices[highWater]  && capacity > 0 ) {
                if (refills[highWater] < capacity) {
                    capacity -= refills[highWater]
                    highWater -= 1
                } else {
                    refills[highWater] -= capacity
                    capacity = 0
                }
            }
            highWater += 1
            prices[highWater] = P[i]
            refills[highWater] = T - capacity


        }
        for (i in 1..highWater)
            total += prices[i].toLong() * refills[i]
        if (total > 1000000000)
            return -2
        return total.toInt()
    }
}