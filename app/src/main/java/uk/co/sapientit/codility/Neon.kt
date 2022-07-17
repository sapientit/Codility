package uk.co.sapientit.templateapp

class Neon {
    fun solution(R: IntArray, X: Int, M: Int): Int {
        if (M < 2 * X * R.size)
            return -1
        var length = 0
        var spare = 0
        var last = 0
        R.forEach {
            // Ideal placement of next bottom edge is it - X - length
            val ideal = it - X - length
            if (ideal > last) {
                spare += (ideal - last)
                last = ideal + 2 * X
            } else {
                var place = last + X // This is the centre
                val ropeExtension = place - it - length // If place is below bollard we have enough rope
                if (ropeExtension > 0) {
                    val moveDown = minOf( spare,(ropeExtension + 1) / 2)
                    spare -= moveDown
                    place -= moveDown
                    length += maxOf(moveDown, ropeExtension - moveDown)
                }
                last = place + X
            }
        }
        if (last > M)
            length += last - M
        return length
    }
}