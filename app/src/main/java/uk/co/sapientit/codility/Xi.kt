package uk.co.sapientit.templateapp

class Xi {
    fun solution(S: String, T: String, K: Int): Int {
        val span = K + 1
        val maxLength =  T.length + span
        val sparse = IntArray(maxLength + 1){1 }
        var total = 1L
        for (i in span + 1..maxLength ) {
            total += sparse[i - span]
            total = total % 1000000007
            sparse[i] = total.toInt()
        }

        val sparseA = calcSparse(S, sparse, K, true)
        val sparseB = calcSparse(T, sparse, K, false)
        return ((sparseB - sparseA + 1000000007) % 1000000007).toInt()



    }
    fun calcSparse(number: String, sparse: IntArray, K: Int, excluded: Boolean): Long {
        val len = number.length
        var result = sparse[len + K + 1].toLong() - 1
        var Kcount = 0
        var doAdd = false
        for (i in 1..len - 1) {
            if (Kcount < K) {
                Kcount += 1
                if (number[i] == '1')
                    return result
            } else {
                if (doAdd) {
                    if (number[i] == '1') {
//                        result += sparse[len-i]
                        result = (result + sparse[len-i + K + 1] - 1) % 1000000007
                        //                       println ("For number " + number + " K "+ K +" " + result)

                        Kcount = 0
                        doAdd = false
                    }
                } else {
                    if (number[i] == '0') {
                        result = (result - sparse[len - i + K + 1] + 1) % 1000000007
                        doAdd = true
                    } else {
                        Kcount = 0
                    }
                }
            }
        }

        if (excluded )
            return result -1 // If we get here then we have a sparse number
        return result

    }

}