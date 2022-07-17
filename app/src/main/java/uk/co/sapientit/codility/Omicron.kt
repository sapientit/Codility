package uk.co.sapientit.codility

class Omicron {
    /*
    To be honest, this just uses the solution that is published.  The key point is that the Fibonacci series
    cycles when you take the modulus of it.  In one sense it is obvious that it must, but it is not so
    obvious that the cycle is going to be a reasonable number.  Maybe this is common knowledge for some
    people, but not me.

    Having discovered that it does cycle, the code times out if you work out that cycle manually (which is the only
    way to do it).  But since the mod is fixed, you can just plug the value into the code after you have calculated
    it once.  After that it is just a matter of raising the Fibonacci matrix to the nth power.

    All fairly simple, seems more maths than coding...
     */
    val MOD = 10000103L
    fun solution(N: Int, M: Int): Int {
      //  val fibCycle = findFibCycle(MOD)
        val fibCycle = 20000208L // result from above.
        val power = findPower(N, M, fibCycle)
        if (power == 0) return 0
        if (power == 1) return 1
        return fibMatrix(power - 1).toInt()
    }
    fun fibMatrix ( count: Int) : Long {
        val fibM = arrayOf(longArrayOf(1,1), longArrayOf(1,0))
        // current at top previous is bottom value
        val exp = matrixPower(fibM,count )
        return exp[0][0]
    }
    fun multiplyMatrix(matrix1: Array<LongArray>, matrix2: Array<LongArray>): Array<LongArray> {
        val result = Array(matrix1.size){LongArray(matrix2[0].size)}
        for (i in 0 until result.size) {
            for (j in 0 until result[0].size) {
                var sum = 0L
                for (k in 0 until matrix1[0].size) {
                    sum += matrix1[i][k] * matrix2[k][j]
                }
                result[i][j] = sum % MOD
            }
        }
        return result
    }
    fun matrixPower(matrix: Array<LongArray>, power: Int) : Array<LongArray>{
        var result = Array(matrix.size){row -> LongArray(matrix.size){ col -> if (col == row) 1 else 0} }
        var p2 = matrix
        var rem = power
        while (rem > 0) {
            if (rem and 1 == 1) {
                result = multiplyMatrix(p2,result)
            }
            rem = rem shr 1
            if (rem > 0)
                p2 = multiplyMatrix(p2,p2)
        }
        return result
    }
    fun findFibCycle(mod: Long) : Long {
        var prev = 1L
        var next = 1L
        var count = 1
        while (prev != 0L || next != 1L) {
            val new =(prev + next) % mod
            prev = next
            next = new
            count++
        }
        return count.toLong()
    }
    fun findPower(A: Int, B: Int, mod: Long) : Int {
        var current = A.toLong()
        if (B == 0) return 1
        var rem = B
        var total = 1L
        while (true) {
            if (rem and 1 == 1) {
                total = (total * current) % mod
            }
            rem = rem shr 1
            if (rem == 0) {
                return total.toInt()
            }
            current = (current * current ) % mod
        }
    }

}