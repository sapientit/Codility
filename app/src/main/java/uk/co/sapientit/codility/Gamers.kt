package uk.co.sapientit.codility

import kotlin.random.Random


class Gamers {
    fun solution(S: String): Int {
        //https://app.codility.com/cert/view/certF4QZEN-CMRTRXWVBVW6XNSV/
        // write your code in Kotlin 1.3.11 (Linux)
        /*
        This is harder than I thought :-)
        I coded it without too much thought catering for a single ? and discovered to my
        cost that ?? is more difficult to cater for than ?.
        A ?? causes problems when the correct result is either 1 or 2.  So a simple
        solution would be to check for an answer and if the answer is 1 then check that it really is
        possible to keep it to 1.  I suspect that would fail with timeout though.
        Therefore I had to work out the code to cater for it properly.
        a(even number of ?)b >= 2
        a(odd number of ?)b >=2
        The tricky part is making sure that you then set startQ and count correctly.
        startQ says that the count includes a starting ? that was necessary to keep the current
        max as low as possible.  We should remove that ? if the new max is > than the current max.
        I think there must be a better way of doing this - I might revisit later.
         */
        var max = 1
        var index = 0
        while (index < S.length && S[index] == '?') {
            index += 1
        }
        if (index == S.length) {
            return 1
        }
        var startQ = false
        var prevQ = false
        var count = 0
        var currentA: Boolean = S[index] == 'a'
        while (index < S.length) {
            when (S[index]) {
                'a' -> {
                    prevQ = false
                    if (currentA) {
                        count += 1
                    } else {
                        if (count > max) {
                            if (startQ) {
                                max = maxOf(count - 1, max + 1)
                            } else {
                                max = count
                            }
                        }
                        count = 1
                        startQ = false
                        currentA = true
                    }
                }
                'b' -> {
                    prevQ = false
                    if (!currentA) {
                        count += 1
                    } else {
                        if (count > max) {
                            if (startQ) {
                                max = maxOf(count - 1, max + 1)
                            } else {
                                max = count
                            }
                        }
                        count = 1
                        startQ = false
                        currentA = false
                    }
                }
                '?' -> {
                    if (prevQ) {
                        var countQ = 1
                        while (index < S.length && S[index] == '?') {
                            index += 1
                            countQ += 1
                        }
                        if (index < S.length) {
                            val newA = S[index] == 'a'
                            if (countQ % 2 == 0) {
                                if (newA != currentA) {
                                    max = maxOf(2, max)
                                }
                            } else {
                                if (newA == currentA) {
                                    max = maxOf(2, max)
                                }
                            }
                            currentA = newA
                        }
                        startQ = false
                        count = 1
                    } else {
                        if (count > max) {
                            if (startQ) {
                                if (count > max + 1) {
                                    max = count - 1
                                    startQ = true
                                    count = 1
                                } else {
                                    max = max + 1
                                    startQ = false
                                    count = 0
                                }
                            } else {
                                max = count
                                count = 1
                                startQ = true
                            }
                        } else {
                            if (count >= max) {
                                count = 1
                                startQ = true
                            } else {
                                count = 0
                                startQ = false
                            }
                        }
                        currentA = !currentA
                        prevQ = true


                    }
                }
            }
            index += 1
        }
        if (count > max) {
            if (startQ) {
                max = maxOf(count - 1, max + 1)
            } else {
                max = count
            }
        }
        return maxOf(max, 1)
    }
    fun generateData(rand: Random) : Boolean {
        val array = CharArray(10) {
            when (rand.nextInt(3)) {
                0 -> 'a'
                1 -> 'b'
                else -> '?'
            }
        }
        val string = String(array)
        val res = solution(string)
        if (testResult(string, res) && !testResult(string, res - 1)) {
            return true
        }
        return false
    }
    fun testResult(S: String, r: Int) : Boolean {
        var index = 0
        var count = 0
        while (index < S.length && S[index] == '?') {
            index += 1
        }
        var prev = ' '
        var prevQ = Int.MIN_VALUE
        while (index < S.length) {
            if (S[index] == '?') {
                if (count == r) {
                    if (prevQ == index - 1) {
                        count = 0
                    } else {
                        count = 1
                        if (prev == 'a') {
                            prev = 'b'
                        } else {
                            prev = 'a'
                        }
                    }
                } else {
                    prevQ = index
                    count += 1
                }
            } else {
                if (S[index] == prev) {
                    if (index - prevQ == 1) {
                        count = 1
                    } else {
                        count += 1
                    }
                } else {
                    count = 1
                    prev = S[index]
                }
            }
            if (count > r) {
                return false
            }
            index += 1

        }
        return true
    }
}