package uk.co.sapientit.templateapp

import kotlin.random.Random

class Chromium {
    /*
    First I replace the random numbers with numbers that increment from 1 to H.size.  We don't care about the actual
    values of height, only the sequence of heights - this 9,1,3 can be replaced by 3,1,2 with the same result.
    Actually I calculate as 1 is the highest nest and H.size is the lowest nest.  That wasn't strictly necessary since I
    started from 1 instead of 0.
    Now consider the nth nest.  The numbers higher than it are now 1,2,3,...n-1.  Each of those numbers are either on the
    left or right.
    Rather than treating them individually I want the number of consecutive numbers that are left or right
    So 1(L) 2(L)  3(L) 4(R)  5(L) 6(R) 7(R) ... N
    We want to know 3 left, 1 right, 1 left, 2 right etc.
    If we call these L1, R1, L2, R2, ...  (L1 = 3, R1 = 1, L2 = 1, R2 = 2...)
    Starting with the first set on the left, there are L1 different ways we can end up with a left hand number and 0 on right
    (There is also +1 case of nothing)
    Lets call this Pl(L1) = L1, Pr(L1) = 0
    Now add the first set of right numbers.
    There are R1 ways we can have just a right hand number, + R1 * L1 of right/left. Pr(R1) = R1 * (Pl(L1) + 1)
    The number of cases where the last number is left is still Pl(L1)

    No add the second set of left numbers.  Similarly the number of ways we can start with a left number in L2 is
    L2 * (Pr(R1) + 1) but we then need to add the number of left numbers from L1
    Pl(L2) = L2 * (Pr(R1) + 1) + Pl(L1)
    This is the formula we need to use. Ignoring the difference between left and right, if we have C1 = the number of
    consecutive numbers on the same side starting from the highest number and C2 is the following set of consecutive
    numbers on the opposite side, and C3...
    Then P(Cn) = Cn * (P(Cn-1) + 1) + P(Cn-2)

    The actual number we want to add in any case is Pr + Pl + 1 (the +1 is the case where there are no nests.)

    We start from one side and then calculate each nest in turn.  Note that If we move nest N, then it only affects P for
    lower nests.  We do not need to recalculate the subtotal from the top each time.

    The tricky part is that subtotal can get so high that even Int64 is not enough!  We have to calculate the subtotal
    modulo as we go.  That confused me at first.

     */
    val MODULO = 1000000007
    fun solution(H: IntArray): Int {
        if (H.size == 1)
            return 1
        val comparator = Comparator<Int>() { a1,a2 ->
            H[a2] - H[a1]
        }
        val simpleASeq = IntArray(H.size){it}.sortedWith(comparator).toIntArray()
        val simpleA = IntArray(H.size)
        for (i in simpleASeq.indices) {
            simpleA[simpleASeq[i]] = i + 1
        }
        /*
        Instead of an array of large numbers we just have the cumbers 1 - H.size.  1 is the largest
        peak and H.size is the smallest.
         */
        val nextChange = IntArray(H.size + 2) {Int.MAX_VALUE}
        nextChange[0] = 1  // Initially we have all numbers on one side
        nextChange[1] = H.size + 1  // Initially we have all numbers on one side
        var total = 0L
        val subTotal = LongArray(H.size + 1){0}
        val startPoints = IntArray(H.size + 3)
        startPoints[2] = 1
        var startHWM = 2
        simpleA.forEach {
            var current: Int
            var next: Int
            if (it == 1) {
                total += 1
   //             println("s " + total)
                next = nextChange[1]
                if (next == 2) {
                    nextChange[1] = nextChange[2]
                } else {
                    nextChange[2] = nextChange[1]
                    nextChange[1] = 2
                }
                startHWM = 3
                startPoints[3] = nextChange[1]
                subTotal[2] = (nextChange[1] - 1).toLong()
            } else {
                if (it > startPoints[startHWM]) {
                    current = startPoints[startHWM]
                    next = nextChange[current]
                    while (next < it) {
                        //                if (startHWM == 1)
                        //                    subTotal[1] = next - current
                        //                else
                        subTotal[startHWM] = ((next - current) * (subTotal[startHWM - 1] + 1) + subTotal[startHWM - 2]) % MODULO
                        startHWM += 1
                        startPoints[startHWM] = next
                        current = next
                        next = nextChange[current]
                    }
                    total += (it - current) * (subTotal[startHWM - 1] + 1) + 1 + subTotal[startHWM - 1] + subTotal[startHWM -2]
     //               println("s " +total)

                } else {
                    var min = 2
                    var max = startHWM
                    while (nextChange[startPoints[min]] < it) {
                        val mid = (min + max + 1) / 2
                        if (startPoints[mid] < it)
                            min = mid
                        else
                            max = mid
                    }
                    startHWM = min
                    current = startPoints[min]
                    next = nextChange[current]
                    if (startHWM == 2)
                        total += it
                    else
                        total += (it - current) * (subTotal[startHWM - 1] + 1) + 1 + subTotal[startHWM - 1] + subTotal[startHWM -2]
    //                println("s " + total)


                }
                if (it == next) {
                    if (nextChange[next] == next + 1) {
                        nextChange[current] = nextChange[next + 1]
                    } else {
                        nextChange[current] = next + 1
                        nextChange[next + 1] = nextChange[next]
                    }
                } else {
                    if (nextChange[current] == it + 1) {
                        nextChange[it] = nextChange[it + 1]
                        nextChange[current] = it
                    } else {
                        if (it == current) {
                            nextChange[it + 1] = nextChange[it]
                            nextChange[it] = nextChange[it]
                        } else {
                            nextChange[it + 1] = nextChange[current]
                            nextChange[it] = it + 1
                            nextChange[current] = it
                        }
                    }
                }
            }
            total = total % MODULO
        }
        return total.toInt()

    }

    fun bruteForce(H: IntArray): Int {
        var total = 0
        for (i in 0 until H.size) {
            val solutions = HashSet<String>()
            getCount(H,H[i],i,true, solutions,"")
            getCount(H,H[i],i,false, solutions,"")
            total += solutions.count()
 //           println(total)
        }
        return total
    }
    fun getCount(H: IntArray, over: Int, middle: Int, high: Boolean, solutions: HashSet<String>, current: String ) {

        solutions.add(current)
        if (high) {
            if (middle < H.size - 1) {
                for (j in middle + 1 until H.size) {
                    if (H[j] > over) {
                        getCount(H, H[j], middle, !high, solutions, current + ","+H[j].toString())

                    }
                }
            }
        } else {
            if (middle > 0) {
                for (j in 0 until middle) {
                    if (H[j] > over) {
                        getCount(H, H[j], middle, !high, solutions, current + "," + H[j].toString())
                    }
                }

            }

        }
    }
    fun generateData(rand: Random, size: Int) : Boolean {
        val H = IntArray(size)
        for (i in 0 until H.size) {
            H[i] = rand.nextInt(1000000000)
        }
        val t1 = solution(H)
        val t2 = bruteForce((H))
        if (t1 != t2) {
            println ("Failed " + t1 + " " + t2)
            for (i in 0 until H.size) {
                print(H[i])
                print(" ")
            }
            println()
            return false
        }
        return true

    }
}