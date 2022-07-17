package uk.co.sapientit.templateapp

class CodingWeek {
    // https://app.codility.com/cert/view/certQH4EDE-9UXE6422M89HVS8V/
    /*
    The key to this is that there is never a reason NOT to translate aab to bba.  This never prevents another
    translation so we just have to do all possible translations.
    As always, we work on a CharArray rather than a string.  Not sure whether replacing a single character in a string
    is efficient or not, but using a CharArray loses little.
    One of the examples shows the tricky part.
    abbbbbb  does not just become baabbbb, since that creates a new abb that can be translated.
    ab(*2n) -> b(ab)*(n-1)aa.  abbbbbbbb (8bs) -> b ababab aa (3* ab followed by aa).
    So we start from the back and count the bs.  Once we find an a then we do the translation as above.
    Still don't understand how people can read and understand the question and type the code and submit in 6 minutes...
     */
    fun solution(S: String): String {
        // write your code in Kotlin 1.3.11 (Linux)
        val result = CharArray(S.length)
        var bCount = 0
        for (i in S.length - 1 downTo 0) {
            val char = S[i]
            if (char == 'b') {
                bCount += 1
            } else {

                if (bCount >= 2) {
                    if (bCount % 2 == 1) {
                        result[i + bCount] = 'b'
                        bCount -= 1
                    }
                    result[i+bCount] = 'a'
                    result[i+bCount - 1] = 'a'
                    bCount -= 2
                    while (bCount > 0) {
                        result[i + bCount] = 'b'
                        result[i + bCount - 1] = 'a'
                        bCount -= 2
                    }
                    bCount = 1
                } else {
                    while (bCount > 0) {
                        result[i + bCount] = 'b'
                        bCount -= 1
                    }
                    result[i] = 'a'
                }
            }
        }

        while (bCount > 0) {
            result[bCount - 1] = 'b'
            bCount -= 1
        }
        return String(result)
    }
}