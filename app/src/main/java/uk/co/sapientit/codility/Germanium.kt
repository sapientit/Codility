package uk.co.sapientit.templateapp

import kotlin.random.Random

class Germanium {
    /*
    First find the index of all the cards of each number (<= N (A.size) since we can never go over that value with N
    cards.
    Then form groups of cards.  The first group is all cards with a 1 on it. Thare are M1 member cards in this group and
    we need to use 1 of them.
    Now form a group of all the cards with a 2 on them.  If any of them are already in a group then that group is merged
    with this group.  We add the number of members of that group and add the number of used cards in tha group to our new
    group.
    If there are less members in the group than cards we want to use, then this is impossible and we have our answer.
    Otherwise move on.
    Why does this work?  We are only including cards in a group if they are potentially useful for the numbers in
    the group, and with only 2 sides it is always possible to use the cards efficiently.  If we have 3 cards 1,2 / 1,2 / 1,2
    then one of the cards is wasted but that is ok because numbers 1 and 2 are accounted for.  If 3 is part of this
    group then there must be a card 1,3 or 2,3 as well.  That card accounts for 3 and so on.

     */
    fun solution(A: IntArray, B: IntArray): Int {
        val first = IntArray(A.size + 1) {Int.MIN_VALUE}
        val next = IntArray(A.size * 2) {Int.MIN_VALUE}
        val max = A.size
        for (i in A.indices) {
            var num = A[i]
            if (num <= max) {
                next[i] = first[num]
                first[num] = i
            }
            num = B[i]
            if (num <= max) {
                next[i + max] = first[num]
                first[num] = i + max
            }
        }
        val groups = IntArray(max){Int.MIN_VALUE}
        val members = IntArray(max + 1)
        val used = IntArray(max + 1)
        val includedIn = IntArray(max + 1){Int.MIN_VALUE}
        for (i in 1..max) {
            used[i] = 1
            var nextNum = first[i]
            while (nextNum != Int.MIN_VALUE) {
                var cardNum : Int
                if (nextNum >= max)
                    cardNum = nextNum - max
                else
                    cardNum = nextNum
                if (groups[cardNum] == Int.MIN_VALUE) {
                    groups[cardNum] = i
                    members[i] += 1
                } else {
                    var oldGroup = groups[cardNum]
                    while (includedIn[oldGroup] != Int.MIN_VALUE) {
                        val prevOld = oldGroup
                        oldGroup = includedIn[oldGroup]
                        includedIn[prevOld] = i
                    }
                    if (oldGroup != i) {
                        members[i] += members[oldGroup]
                        used[i] += used[oldGroup]
                        includedIn[oldGroup] = i
                        groups[cardNum] = i
                    }
                }
                nextNum = next[nextNum]
            }

            if (used[i] > members[i])
                return i
        }
        return max + 1
    }
    fun bruteForce(A: IntArray, B: IntArray): Int {
        val used = BooleanArray(A.size){false}
        return tryNum(A,B,1, used)

    }
    fun tryNum(A: IntArray, B: IntArray, target: Int, used: BooleanArray) : Int {
        var max = target
        for (i in A.indices) {
            if ((A[i] == target || B[i] == target) && !used[i]) {
                used[i] = true
                max = maxOf(max, tryNum(A,B,target + 1, used))
                used[i] = false
            }
        }
        return max
    }
    fun generateData(rand: Random, size: Int) : Boolean {
        val A = IntArray(size){rand.nextInt(1,size)}
        val B = IntArray(size){rand.nextInt(1,size)}
        val t1 = solution(A,B)
        val t2 = bruteForce(A,B)
        if (t1 != t2) {
            for (i in A.indices) {
                print(A[i])
                print(" ")
            }
            println()
            for (i in B.indices) {
                print(B[i])
                print(" ")
            }
            println()
            return false
        }
        return true
    }
}