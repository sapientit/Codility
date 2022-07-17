package uk.co.sapientit.codility

class Kappa {
    /*
    The permutations for A crew out of B is A! / {B! * (A-B)!)
    The total permutations are the permutations for each country multiplied together.

    The first thing to realise is that calculating factorial is expensive, and to get to A! we need to go through
    A-1, A-2... 1

    So we find the maximum value of T[i] and calculate and store factorials up to that value.

    The second thing to realise is that division in modular arithmetic is not easy. So therefore we multiply
    all the T[i]! together, and then multiply all the (D[i]! * (T[i]-D[i])!) elements together.

    Then we we have to do a single division operation.

    Modular division A/B mod C only works if B and C are co-prime (clearly that is the case in this case).  It relies
    on the fact that B**2, B**3, ... B**n (mod C) will cycle through every possible value 1..C - 1.  If that is the case
    then clearly B**(C-1) (mod C) must equal 1, (there are C - 1 possible values, so the C-1th of them must equal 1 so that
    the cycle can start again with B**C = C

    If (B**(C-1) = 1, it  means that B**(C-2) * B = 1 - in normal arithmetic this is the
    equivalent of 1/B * B = 1.  So dividing by B is the same in modular arithmetic as multiplying by B**(C-2)

    Raising B to the (C-2th power) is done by making use of the binary value of C-2.

    For example if the binary representation of C-2 is 100101 then B**(C-2)  = B**1 * B**4 * B**32.  We can therefore
    just keep squaring the value of B to go through B, B**2, B**4, B**8 etc and pick out the values we need.
     */
    val MOD = 1410000017L // Binary is 1010100000010101110010010010001
    lateinit var factorial: LongArray
    fun solution(T: IntArray, D: IntArray): Int {
        var max = 0
        T.forEach {
            max = maxOf(max, it)
        }
        factorial = LongArray(max + 1)
        factorial[1] = 1
        factorial[0] = 1
        for (i in 2..max) {
            factorial[i] = (factorial[i -1] * i) % MOD
        }
        var multiplier = 1L
        var divider = 1L
        for (i in T.indices) {
            multiplier = (multiplier * factorial[T[i]]) % MOD
            divider = (divider * factorial[D[i]]) % MOD
            divider = (divider * factorial[T[i] - D[i]]) % MOD
        }
        return ((multiplier * modularInverse(divider, MOD)) % MOD).toInt()

    }
    fun modularInverse(num: Long, mod: Long)  : Long{
        var rem = mod - 2
        var current = num
        var inverse = 1L
        while (rem > 0) {
            if (rem and 1L == 1L) {
                inverse = (inverse * current) % mod
            }
            rem = rem shr 1
            current = (current * current) % mod
        }
        return inverse
    }
}