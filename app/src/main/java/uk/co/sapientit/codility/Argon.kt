package uk.co.sapientit.templateapp

class Argon {
    /*
    If we replace the 0s with -1, then the problem becomes easier.
    counting the sum from left to right we are looking for a sequence of days where the sum overall goes up followed by
    a sequence where it goes down.
    The best way is to find the maximum sum.
    Note that we only include it as a maximum if it is followed by at least 1 day of trekking weather (-1). That is
    because a maximum that occurs on the last day is no use - we can't ever go trekking.
    While going through the sequence record the last day with a sum less than the maximum.
    If the maximum is less than or equal to zero we must have started with a (possibly long) series of days of trekking weather.
    If there were any sunny days included the maximum would have been higher, so we know that we get to a lower value
    in max + 1 days.  This then is the first day.
     */
    fun solution(A: IntArray): Int {
        val sum = IntArray(A.size)
        var currentSum = 0
        var maxValue = Int.MIN_VALUE
        var pendingMaxValue = Int.MIN_VALUE
        var lastDay = -1
        for (i in A.indices) {
            if (A[i] == 0) {
                currentSum += 1
                pendingMaxValue = maxOf(currentSum,pendingMaxValue)
                if (currentSum < maxValue) {
                    lastDay = i
                }
            } else {
                currentSum -= 1
                maxValue = pendingMaxValue
                lastDay = i
            }
            sum[i] = currentSum

        }
        if (maxValue == Int.MIN_VALUE)
            return 0
        val firstDay: Int
        if (maxValue <= 0) {
            firstDay = 1 - maxValue
        } else
            firstDay = 0
        return lastDay - firstDay + 1
    }
}