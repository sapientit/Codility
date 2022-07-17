package uk.co.sapientit.templateapp

class Mu {
    fun solution(S: String) : Int {
        var total = 0L
        var size = S.length
        var zeroes = 0
        var first = true
        var prevDigit = 1
        for (i in 0.. S.length -1) {
            val digit = Character.getNumericValue(S[i])
            total = total * 10
            if (first)
                first = false
            else {
                total += prevDigit * size
                if (digit == 0) {
                    total -= 1
                    zeroes += 1
                }
            }

            total +=  zeroes * digit
            total = total % 1410000017
            prevDigit = digit
            size -= 1
            //          println("after digit " + total)
        }
        total +=  zeroes + 1410000018

        total = total % 1410000017
        return total.toInt()
    }

}