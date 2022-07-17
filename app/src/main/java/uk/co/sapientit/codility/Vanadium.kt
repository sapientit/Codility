package uk.co.sapientit.templateapp

class Vanadium {
    /*
    The key to this is that there are only 10 possible values and we can therefore use an integer as a bitmap.
    If each of the first 10 bits represents 0-9 then if we xor each bit as we find the bulb then equal numbers of
    each colour will end up as zero.
    A palindrome is therefore a 10 bit number with either a single 1 or no 1s.
    Starting from the left and recording values we note that a slice that is a palindrome has 1 bit difference
    from the start (before we xor for the first bulb) to the end point.
    Rather than calculating each slice we just record the number of times each 10 bit string (1024 integer) occurs.
    Then if for example there are n occurences of 57 then we start with n + n-1 + n-2 + n-3 ... 1 slices that start
    on one occurence and end on another (so zero 1s in the difference)
    Then we would look at 57 xor 1, 2,4,8,16...512 (1 bit difference from the original.)
    Note that because 56 xor 1 = 57 and 57 xor 1 = 56 we would double count if we did all values, so we only
    count where the original bit was 0.
    In the above example, if there are N occurences of 57 and M of 56 this would generate N * M slices.
     */
    val powers = intArrayOf(1,2,4,8,16,32,64,128,256,512)
    val counts = IntArray(1024)
    val MODULO = 1000000007L

    fun solution(S: String): Int {
        var bitCode = 0
        val ascii0 = '0'.toInt()
        counts[0] = 1
        S.forEach {
            val digit =  powers[it.toInt() - ascii0]
            bitCode = bitCode xor digit
            counts[bitCode] += 1
        }
    //    var total = S.length.toLong() // All single elements are plaindromes
        var total = 0L
        for (i in 0..1023) {
            total += getValues(i)
        }


        return (total % MODULO).toInt()
    }
    fun getValues(from: Int) : Long {
        val count = counts[from].toLong()
        var total = (count * (count - 1) / 2).toLong()
        for ( bit in powers) {
            if (from and bit == 0) {
                val adjacent = from or bit
                total += counts[adjacent] * count
            }
        }
        return total
    }

}