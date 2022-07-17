package uk.co.sapientit.codility

class Helium {
    /*
    I tried a number of approaches here.  I thought that a suffix tree would work, but realised that a suffix tree
    with 1,000,000 a's ends up as O(N**2).

    But then I realised that I didn't need a full suffix tree, since I was only interested in matching against the
    "suffix" that starts with the first character.  Creating a suffix tree was too much of an overhead.

    But the principles behind a suffix tree still worked.  As I go through the characters I am only interested
    when I find a character than no longer matches with the prefix.  At that point I need to start again. But I
    don't want to start right from the beginning -
    Consider AAAAAAAAANAAAAAAAAAAAA
    I don't want to matcch all those A's every time I restart. What I need to do is remember how far along the prefix
    I am.
    The duplicate array does just that. For string ABABABAC the duplicate array looks like this:

    A   B   A   B   A   B   C
    -1  -1  0   1   2   3   -1

    The second A is the starting character of the string so  ABA also contains the prefix A (position 0)
    Similarly ABAB   contains the prefix ending at position 1 (AB)
    ABABA  contain ABA (which also contains A)
    ABABAB contains ABAB (which contains AB)
    ABABABC  doesn't contain any other prefixes.

    The failPoint array is similar, but it tells where to start matching again if thuis character doesn't match.
     For the string ABACA the array would look like
    A   B   A   C   A
    -1  0   -1  1   -1

    If wer encounter a substring later  ABAD  The D doesn't match  against ABAC so we have to now check in position 1.
    If it is a B then we now have a prefix AB  and we  are looking for another A next.

    This is almost identical to the build part of the KMP algorith (Knuth-Morris-Pratt).

    So why do we need both of these?

    Low maintains the position of the longest prefix the we have on the current ending substring.  If the next character
    doesn't match then we use the failPoint directory to work out where to start counting from again.

    The midpoint pointer never goe sabove half of the length of the current substring (if it is more than half then the
    prefix and the second occurence overlap.  If it does go over then we use the duplicate array to get the preceding
    prefix that also maycjes.

    Maintain a maxlength variable - this is the maximum length of prefix and string in the middle (we make sure that
    the middle string doesn't get so close to the end that there is no room for the suffix).

    Finally we will hopefully have a lowPoint somewhere in the string.  If htis is longer than the maxLength then
    we use the duplicates array again to get the next smallest suffix that matches the prefix.  And so on until we have
    a suffix <= maxLength

     */
    fun solution(S: String): Int {
        var maxLength = -1
        val duplicate = IntArray(S.length){-1}
        val failPoint = IntArray(S.length + 1)
        var low = -1
        var lenRem = S.length - 2
        var midPoint = -1
        failPoint[0] = -1
        for (i in 1 until S.length) {
            lenRem--
            low++
            if (S[low] != S[i]) {
                failPoint[i] = low
                while (low != -1 && S[low] != S[i]) {
                    low = failPoint[low]
                }
                midPoint = low
            } else {
                failPoint[i] = failPoint[low]
                midPoint++
                while (midPoint >= (i + 1) / 2) {  // I think if should work here as well.
                    midPoint = duplicate[midPoint]
                }
            }
            duplicate[i] = low
            /*
            Duplicate is the longest leangth earlier in the string that overlaps with the end of the
            current substring.  So ABCDABC - the pointer for ABC will point back to the preceding C (since
            the end of the current string (ABC) is also the start of the string.
            We maintain this because we need to ensure it doesn;t overlap
            For example ABABA We have a string ABA that overlaps, so our midpoint pointer would be pointing at the second A
            (ABA) but that is more than half way, so we have to skip back to the preceding duplicate (ABA -> the first A).
             */
            maxLength = maxOf(maxLength,minOf(lenRem,midPoint))
        }
        while (low > maxLength) {
            low = duplicate[low]
        }
        return low + 1

    }



}
