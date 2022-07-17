package uk.co.sapientit.codility
//https://app.codility.com/cert/view/certQM77SX-AYTEGKYB4UHSCCTE/
class YearOfTheTiger {
    /*
    Since the string is only 3 characters long it may be more efficient to store it as
    an integer than a string, but probably marginal.
    The only way I can see to do this efficiently is to actually keep a running count
    of the possible values.  So for xyz add 1 to the count of xyz, xzy am yxz.
    Note that xxy we only  add 1 to xxy and xyx.  Do not count xxy twice.

     */


    fun solution(T: Array<String>): Int {
        val count = mutableMapOf<Int,Int>()
        T.forEach {
            val a = it[0] - 'a'
            val b = it[1] - 'a'
            val c = it[2] - 'a'
            addPerms (count, (a) + (b shl 5) + (c shl 10))
            if (a != b) addPerms (count, (b) + (a shl 5) + (c shl 10))
            if (b != c) addPerms (count, (a) + (c shl 5) + (b shl 10))
        }
        var max = Int.MIN_VALUE
        for (v in count.values) {
            max = maxOf(max,  v)

        }
        return max

    }
    fun addPerms(count: MutableMap<Int,Int>, chars: Int)  {
        val current = count.getOrElse(chars){0} + 1
        count[chars] = current
    }
}