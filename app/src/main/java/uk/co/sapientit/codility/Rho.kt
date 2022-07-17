package uk.co.sapientit.codility


class Rho {
    /*
    Finally I have managed to get this to work within the time limit (but only just - I suspect
    that if I ran this again it would fail on one of the tests still :-().

    My first thought was that there was some logic in how to find the shortest array of numbers,
    other than the obvious addition of the powers of 2, but that failed on some obscure numbers.

    So I then tried the brute force approach, but quickly realised that in the worst case I am
    recursing 600 times, adding 1 each time.

    They first trick, as the published solution says, is to limit the depth of the search to the solution
    you can get by just using the powers of 2.  But that still times out.

    The second trick is to always include the largest number - the published solution worked out that the first
    time this fails is for a number >> 600.  It's nasty to include a trick that you can't prove to yourself, but
    that's the way it goes.

    So take the example of the numbers we have so far are 1,2,4,8.

    We start by adding 1 -> 1,2,4, 8, 9
    To this we add 1, 2, 4 or 9.  Say we add the 2 -? 1, 2, 4, 8, 9, 11

    When we finish this loop we try again with adding 2 -> 1, 2, 4, 8, 10.

    If we add 1 to this, we get to 11 - the same as we got to by adding 1 and then 2.  It is not quite the
    same since have a 9 in the first sequence and a 10 in the second.  For some numbers I am sure this would
    be important, but fortunately it doesn't make any difference for numbers 1-600.

    The final trick that shaved the last few milliseconds off was to not add the same number twice if we had
    previously doubled that number.

    So taking 1, 2, 4, 8 and we add 4 -> 1, 2, 4, 8, 12.  If we add 4 again then we get to 16.  We could have
    got to 16 on the previous number by justadding 8 rather than adding 4 twice.  Again, is that 12 that we could
    have go important?  Apparently not for numbers 1-600.

    So 3 tricks, none of which can I prove to be valid assumptions, finally got this to the necessary time.  I did try
    removing the recursion previously but it didn't make much difference and added to complexity.

    In my view - not a good problem :-(


     */

    var ans = IntArray(1)
    var res: IntArray? = null
    fun solution(A: Int) : IntArray {
        if (A == 1 )
            return intArrayOf(A)
        if (A == 2)
            return intArrayOf(1,2)
   //     doubled[0] = true
        val max = getMax(A)
        ans = IntArray(max - 1)
        ans[0] = 1
        ans[1] = 2
        getNext(A,1, 2,0)
        if (res != null) return res!!
        return getSimpleMax(A) // We found nothing better than the adding powers of 2 method.

    }
    fun getNext(target: Int,  hwm: Int, current: Int, minIndex: Int) : Boolean{
        if (current >= target) {
            if (ans[hwm] > target) return false
            res = ans
            ans = IntArray(res!!.size - 1){res!![it]}
            return true
        }
        if (hwm >= ans.lastIndex) return false
        for (i in minIndex until hwm) {
  //          doubled[hwm + 1] = false
            ans[hwm + 1] = current + ans[i]
            val new = ans[hwm + 1]
            if (new > target) return false
            if (ans[i + 1] == ans[i] * 2) {
                if (getNext(target, hwm + 1, new, i + 1)) return false
            } else {
                if (getNext(target, hwm + 1, new, i)) return false
            }
        }
        ans[hwm + 1] = 2 *  ans[hwm]
        val new = ans[hwm + 1]
  //      doubled[hwm] = true
        getNext(target, hwm + 1, new, 0)
        return false
    }
    fun getMax(A: Int) : Int {
        var target = A
        var length = 1
        while (target > 1) {
            length += 1
            if (target % 2 == 1)
                target -= 1
            else
                target = target / 2
        }
        return length
    }
    fun getSimpleMax(A: Int) : IntArray {
        var target = A
        val result = mutableListOf<Int>()
        val additional = mutableListOf<Int>()
        var current = 1
        while (target > 1) {
            result.add(current)
            if (target % 2 == 1)
                additional.add(0,current)
            target = target / 2
            current *= 2
        }
        result.add(current)
        additional.forEach{
            current += it
            result.add(it)
        }
        return result.toIntArray()
    }

}