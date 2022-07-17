package uk.co.sapientit.codility

class PairACoder {
    //https://app.codility.com/cert/view/certMJWG3H-EWDHKZCD6ZXR3PFD/
    /*
    The key here is that for each character a-z we store what is the minimum number of
    characters we can have remaining before it.
    For example with 'abcd' the minimum characters before 'd' is 3, but for "abad"  the minimum
    characters remaining before the 'd' is 0.  We store these values in minToStart.
    Now when we reach a character the minimum to that point is the minimum to the point before + 1
    or the minimum to this character that we have stored in minToStart
    So to take the example of 'abad'
    The first character 'a' is new.  We store minToStart[a] as 0 (there is nothing before it.
    The next character is 'b' - also new.  minToStart[b] is 1 more than previous = 1
    The 3rd character is 'a'.  Adding 1 to previous gives 2, but that is more than the previously stored
    minToStart[a] (0) so the minimum to this location is 0.  Note that minToStart will stay as 0
    And finally 'd'.  That is a new character so just 1 more than the minimum to previous character (0+1 = 1)
    And we have our answer.
     */
    fun solution(S: String): Int {
        val minToStart = mutableMapOf<Char,Int>()
        var lastBreak = 0
        var lastMin = 0
        for (i in 0 until S.length) {
            val c = S[i]
            val min2Start = minToStart[c] ?: Int.MAX_VALUE
            val thisMin = i - lastBreak + lastMin
            if (thisMin >= min2Start) {
                lastBreak = i + 1
                lastMin = min2Start
            }
            minToStart[c] = minOf(thisMin,min2Start)

        }
        return S.length - lastBreak + lastMin
    }
}