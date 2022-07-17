package uk.co.sapientit.templateapp

class CuttingComplexity {
    /*
        In principle this is simple.  Look for the most M's we get in K an swap the L's to M's.
        If contiguous M's are longer than K than just change one to M every K + 1.
        The challenge is all the special cases.  I have a feeling there is a better way to do this.
     */
    fun solution(S: String, K: Int): Int {
        val value = IntArray(S.length)
        var runningtotal = 0
        var index = 0
        while (index < K) {
            if (S[index] == 'M')
                runningtotal += 1

            value[index] = runningtotal
            index += 1
        }
        var max = runningtotal
        if (S.length > K && max < K) {
            if (S[K] == 'M')
                max -= 1
        }
        while (max < K && index < S.length) {

            if (S[index] == 'M')
                runningtotal += 1

            value[index] = runningtotal
            val tryMax = runningtotal - value[index - K]
            if ( tryMax > max) {
                if (tryMax == K)
                    max = K
                else {
                    var newMax = tryMax - 1
                    if (index >= K) {
                        if (S[index - K + 1] == 'L' && S[index - K] == 'L')
                            newMax = tryMax
                    }
                    if (index < S.length - 1) {
                        if (S[index] == 'L' && S[index + 1] == 'L')
                            newMax = tryMax

                    }
                    if (S[index] == 'M' && S[index - K + 1] == 'M') {
                        if (index == S.length - 1 ||  S[index + 1] == 'L')
                            if (index == K || S[index - K] == 'L')
                                newMax = tryMax
                    }

                    max = newMax
                }
            }

    //        max = maxOf (max, runningtotal - value[index - K])
            index += 1
        }
        if (max < K) {
            return K - max
        }
        var count = K
        var result = 0
        while (index < S.length) {

            if (S[index] == 'M') {
                count += 1
                if (count > K) {
                    result += 1
                    count = 0
                }
            } else {
                count = 0
            }
            index += 1
        }

        return result
    }
}