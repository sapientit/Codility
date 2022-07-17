package uk.co.sapientit.templateapp

class Titanium {
    /*
    We will calculate value by add 1 for ( and subtract for ).  A difference of zero means they are matching numbers.
    A -ve number means that we had too many ) before (, so some will need to turn
    The code centres around the minimum value.  Before the minimum the slice will either end at or before the minimum or be affected
    by it.
    After the minimum the start will always be >= all subsequent values.  That is because if there is a smaller
    value later then you will get further reach by starting at the preceding occurrence of that smaller value.
    For all values that reach past the minimum we must consider the final value and if that doesn't work then
    the last occurence of the maximum value that would work.
     */
    fun solution(S: String, K: Int): Int {
        val last = IntArray(2 * S.length + 2) {-1}
        val value = IntArray( S.length)
        val prev = IntArray( S.length)
        var maxLength = 0
        var current = S.length
        var orig = current
        val delta = 2 * K
        // If it goes below minTarget there are not enough rotations for this start point to go further
        var minTarget = current - delta
        var minValue = current
        var startPos = -1
        for (i in 0 until S.length) {
            if (S[i] == '(') {
                current += 1
                value[i] = current
            } else {
                current -= 1
                value[i] = current
                if (current < minTarget) {
                    val length = i - startPos - 1
                    maxLength = maxOf(maxLength,length)
                    startPos += 1
                    while (value[startPos] >= orig) {
                        startPos += 1
                    }
                    orig = value[startPos]
                    minTarget = orig - delta
                }
            }
            minValue = minOf(minValue,current)
            prev[i] = last[current]
            last[current] = i
        }
        /*
        At this stage we know that we have got a value that never has too many closing brackets after it.
        If maximumLength finiishes because of too many closing brackets then we have calculated that length.
        Next check if we can go all the way to the end
        End target depends on how many excess close brackets we have seen before we get to the end
         */
        var endTarget =  (K - (orig - minValue + 1) / 2) * 2 + minValue
        if (current <= endTarget) {
            maxLength = maxOf(maxLength, S.length - startPos - 1)
            return maxLength / 2 * 2 // In case we got an odd value drop 1 character
        }

        var endPoint = last[endTarget]
        maxLength = maxOf(maxLength, endPoint - startPos - 1)

        /*
        So now look for each value between orig and minValue and see where the last possible end is
         */
        while (orig > minValue) {
            startPos += 1
            if (value[startPos] < orig) {
                orig = value[startPos]
                endTarget = (K - (orig - minValue + 1) / 2) * 2 + minValue
                if (current <= endTarget) {
                    maxLength = maxOf(maxLength, S.length - startPos - 1)
                    return maxLength / 2 * 2 // In case we got an odd value drop 1 character
                }

                endPoint = last[endTarget]
                maxLength = maxOf(maxLength, endPoint - startPos)

            }

        }
        /*
        We have reached the minimum value for the entire array.
        On the way up we are only interested in minimums.  Once we are no longer minimum skip to next min
         */
        var lookUntil = S.length - maxLength - 1
        while (startPos < lookUntil) {
            startPos += 1
            if (value[startPos] > orig) {
                if (last[orig] > startPos) {
                    startPos = last[orig]
                } else {
                    orig += 1
                    endTarget = orig + 2 * K
                    if (current <= endTarget) {
                        maxLength = maxOf(maxLength, S.length - startPos - 1)
                        return maxLength / 2 * 2 // In case we got an odd value drop 1 character
                    }
                    endPoint = last[endTarget]
                    maxLength = maxOf(maxLength, endPoint - startPos)
                    lookUntil = S.length - maxLength - 1
                }
            }
        }
        return maxLength / 2 * 2
    }
}