package uk.co.sapientit.templateapp

import kotlin.random.Random

class GreatCodeOff2021 {
    /*
    My first thought for this was to sort into layer order and then create maps for each layer and merge them.
    That failed on performance, as the sort was too expensive.
    We therefore need to handle everything without a sort.
    Since N is <= 100000 we can afford to build a list of which operations start and end at each cake form.
    The hard part is working out whether the cake is edible.  Tasting would be my preferred option but maybe
    not with 100,000 of them... :-)
    There are two parts to this - we can only have 1 each of the K layers, but the K layers must also be in the
    right order.  Since we are processing in the sequence 1..cakes, we are processing the operations in random order.
    We therefore need to store the operation number that generates each layer and then a valid cake has operation
    numbers for the layers 1..K in ascending sequence.

    In order to do that, we need to store a list of active operations for each layer.  I chose to do this by using the first
    K entries in an array to store the pointers and then maintained a two way chain of the operations.  That makes it easy to
    add and remove entries, at the cost of a adding K + 1 (base) to all the indices.

    For example, with K = 2, and N = 10
    Entry 0 is not used in the nextActive array.  Entry 1 is a pointer to the first active operation for layer 1, entry 2
    is first entry for layer 2 and entry 3 is the next entry after operation 0 for the same layer.

    validLayer maintains an entry for layers 1-K that is either int.min_value or an entry (+K + 1) that is currently active
    for that layer.  Entry 0 is set to K (to avoid accidentally treating the summy 1-K entries as valid) and entry K + 1
    is set to Int.MAX_VALUe so it is always > entry K.

    Whenever validLayer changes check whether operation for K is > operation for K -1, and do the same for K+1 - K.

    Keep track of how many valid entres we have and add 1 when when everything is vald.

     */
    fun solution(N: Int, K: Int, A: IntArray, B: IntArray, C: IntArray): Int {
        val base = K + 1
        val lastStart = IntArray(N + 1){Int.MIN_VALUE}
        val lastEnd = IntArray(N + 1){Int.MIN_VALUE}
        val nextStart = IntArray(A.size + base){Int.MIN_VALUE}
        val nextEnd = IntArray(B.size + base){Int.MIN_VALUE}
        for (i in A.indices) {
            val start = A[i]
            nextStart[i + base] = lastStart[start]
            lastStart[start] = i + base

            val end = B[i]
            nextEnd[i + base] = lastEnd[end]
            lastEnd[end] = i + base
        }
        val prevActive = IntArray(A.size + base){Int.MIN_VALUE}
        val nextActive = IntArray(A.size + base){Int.MIN_VALUE}
        val lastActive = IntArray(base){it}
        var activeCount = 0
        var validCount = 0
        val validIndex = IntArray(base + 1){Int.MIN_VALUE}
        validIndex[0] = K
        validIndex[base] = Int.MAX_VALUE
        val invalidLayers = BooleanArray(K+2){true}
        invalidLayers[K + 1] = false
        for (i in 1 until base) {
            invalidLayers[i] = true
        }
        var cakesFound = 0
        for (i in 1 until lastStart.size) {
            var starting = lastStart[i]
            while (starting != Int.MIN_VALUE) {
                val layer = C[starting - base]
                val last = lastActive[layer]
                nextActive[last] = starting
                prevActive[starting] = last
                lastActive[layer] = starting
                if (validIndex[layer] == Int.MIN_VALUE) {
                    validIndex[layer] = starting
                    validCount += checkValid(layer,validIndex,invalidLayers)
                    validCount += checkValid(layer  + 1,validIndex,invalidLayers)
                }
                activeCount += 1
                starting = nextStart[starting]
            }
            if (activeCount == K && validCount == K) {
                    cakesFound += 1
            }
            var ending = lastEnd[i]
            while (ending != Int.MIN_VALUE) {
                val layer = C[ending - base]
                val prev = prevActive[ending]
                val next = nextActive[ending]
                nextActive[prev] = next
                if (next > Int.MIN_VALUE) {
                    prevActive[next] = prev
                } else {
                    lastActive[layer] = prev
                }
                if (validIndex[layer] == ending) {
                    validIndex[layer] = nextActive[layer]
                    validCount += checkValid(layer,validIndex,invalidLayers)
                    validCount += checkValid(layer  + 1,validIndex,invalidLayers)
                }
                activeCount -= 1
                ending = nextEnd[ending]
            }
        }
        return cakesFound
    }
    fun checkValid(layer: Int, validLayers: IntArray, invalidLayers: BooleanArray) : Int {
        val prevInValid = invalidLayers[layer]
        val newInValid: Boolean
        if (validLayers[layer] > validLayers[layer - 1]){
            newInValid = false
        } else {
            newInValid = true
        }
        invalidLayers[layer] = newInValid
        if (newInValid == prevInValid)
            return 0
        if (newInValid) return -1
        return +1

    }
    var index = 0
    fun solutionFailed(N: Int, K: Int, A: IntArray, B: IntArray, C: IntArray): Int {
        val comparator = Comparator<Int>{ l1,l2 ->
            if (C[l1] == C[l2]) {
                if (A[l1] == A[l2]) {
                    B[l1] - B[l2]
                } else {
                    A[l1] - A[l2]
                }
            } else {
                C[l1] - C[l2]
            }
        }
        val sorted = IntArray(A.size){it}.sortedWith(comparator).toIntArray()
        var validStart = IntArray(N + 1)
        var validEnd = IntArray(N + 1)
        var validTime = IntArray(N + 1)
        val newStart = IntArray(N + 1)
        val newEnd = IntArray(N + 1)
        val newTime = IntArray(N + 1)
        var resStart = IntArray(N + 1)
        var resEnd = IntArray(N + 1)
        var resTime = IntArray(N + 1)
        var validCount = buildValid(1,validStart,validEnd,validTime,sorted,A,B,C)
        var k = 1
        while (k < K ) {
            k += 1
            val newCount = buildValid(k,newStart,newEnd,newTime,sorted,A,B,C)
            val resCount = mergeRanges(validStart,validEnd,validTime,validCount,
                newStart,newEnd,newTime,newCount,resStart,resEnd,resTime)
            val tempStart = validStart
            val tempEnd = validEnd
            val tempTime = validTime
            validStart = resStart
            validEnd = resEnd
            validTime = resTime
            validCount = resCount
            resStart = tempStart
            resEnd = tempEnd
            resTime = tempTime
        }
        var index = 0
        var count = 0
        while (index < validCount) {
            count += validEnd[index] - validStart[index] + 1
            index += 1
        }
        return count

    }
    fun buildValid(layer: Int, start: IntArray, end: IntArray, seq: IntArray, sorted: IntArray, A: IntArray, B: IntArray, C: IntArray) : Int {
        if (index >= sorted.size)
            return 0
        var current = sorted[index]
        if (C[current] != layer)
            return 0
        var currentEnd = B[current]
        var currentStart = A[current]
        var currentIndex = current
        var count = 0
        index += 1
        var haveRange = true
        while (index < sorted.size) {
            current = sorted[index]
            if (C[current] != layer)
                break
            val newStart = maxOf(currentStart,A[current])
            val newEnd = B[current]
            if (newStart <= newEnd) {
                if (newStart > currentEnd) {
                    if (haveRange) {
                        start[count] = currentStart
                        end[count] = currentEnd
                        seq[count] = currentIndex
                        count += 1
                    }
                    currentEnd = newEnd
                    currentStart = newStart
                    currentIndex = current
                    haveRange = true
                } else {
                    if (newStart > currentStart) {
                        if (haveRange) {
                            start[count] = currentStart
                            end[count] = newStart - 1
                            seq[count] = currentIndex
                            count += 1
                        }
                        if (newEnd > currentEnd) {
                            currentStart = currentEnd + 1
                            currentEnd = newEnd
                            currentIndex = current
                            haveRange = true
                        } else {
                            if (newEnd < currentEnd) {
                                currentStart = newEnd + 1
                            } else {
                                currentStart = currentEnd + 1
                                haveRange = false
                            }
                        }
                    } else {
                        if (newEnd > currentEnd) {
                            currentStart = currentEnd + 1
                            currentEnd = newEnd
                            currentIndex = current
                            haveRange = true
                        } else {
                            if (newEnd == currentEnd) {
                                currentStart = currentEnd + 1
                                haveRange = false
                            } else {
                                currentStart = maxOf(currentStart, newEnd + 1)
                                haveRange = true
                            }
                        }
                    }
                }
            }
            index += 1
        }
        if (haveRange) {
            start[count] = currentStart
            end[count] = currentEnd
            seq[count] = currentIndex
            count += 1
        }
        return count
    }
    fun mergeRanges(validStart: IntArray, validEnd: IntArray, validTime: IntArray, validCount: Int,
                    newStart: IntArray, newEnd: IntArray, newTime: IntArray, newCount: Int,
                    resStart: IntArray, resEnd: IntArray, resTime: IntArray) : Int     {
        var indexValid = 0
        var indexNew = 0
        var indexRes = 0
        var vS = validStart[0]
        var vE = validEnd[0]
        var vT = validTime[0]
        var nS = newStart[0]
        var nE = newEnd[0]
        var nT = newTime[0]
        var prevEnd = Int.MIN_VALUE
        var prevTime = Int.MIN_VALUE
        while (indexValid < validCount && indexNew < newCount) {
            if (nS >= vS) {
                if (vE < nS) {
                    indexValid += 1
                    vS = validStart[indexValid]
                    vE = validEnd[indexValid]
                    vT = validTime[indexValid]
                } else {
                    if (nE > vE) {
                        if (nT > vT) {
                            if (nT == prevTime && nS == prevEnd + 1) {
                                resEnd[indexRes] = vE
                                prevEnd = vE
                            } else {
                                resStart[indexRes] = nS
                                resEnd[indexRes] = vE
                                resTime[indexRes] = nT
                                indexRes += 1
                                prevEnd = vE
                                prevTime = nT
                            }
                        }
                        indexValid += 1
                        vS = validStart[indexValid]
                        vE = validEnd[indexValid]
                        vT = validTime[indexValid]
                    } else {
                        if (nT > vT) {
                            resStart[indexRes] = nS
                            resEnd[indexRes] = nE
                            resTime[indexRes] = nT
                            indexRes += 1
                            prevEnd = nE
                            prevTime = nT
                        }
                        indexNew += 1
                        nS = newStart[indexNew]
                        nE = newEnd[indexNew]
                        nT = newTime[indexNew]
                    }
                }
            } else { // nS < vS
                if (nE >= vS) {
                    if (nE <= vE) {
                        if (nT > vT) {
                            resStart[indexRes] = vS
                            resEnd[indexRes] = nE
                            resTime[indexRes] = nT
                            indexRes += 1
                            prevEnd = nE
                            prevTime = nT
                        }
                        indexNew += 1
                        nS = newStart[indexNew]
                        nE = newEnd[indexNew]
                        nT = newTime[indexNew]
                    } else {
                        if (nT > vT) {
                            if (nT == prevTime && nS == prevEnd + 1) {
                                resEnd[indexRes] = vE
                                prevEnd = vE
                            } else {
                                resStart[indexRes] = vS
                                resEnd[indexRes] = vE
                                resTime[indexRes] = nT
                                indexRes += 1
                                prevEnd = vE
                                prevTime = nT
                            }
                        }
                        indexValid += 1
                        vS = validStart[indexValid]
                        vE = validEnd[indexValid]
                        vT = validTime[indexValid]

                    }
                } else {
                    indexNew += 1
                    nS = newStart[indexNew]
                    nE = newEnd[indexNew]
                    nT = newTime[indexNew]
                }
            }
        }
        return indexRes
    }
    fun bruteForce(N: Int, K: Int, A: IntArray, B: IntArray, C: IntArray): Int {
        val cakes = IntArray(N + 1)
        for (i in 0 until A.size) {
            val layer = C[i]
            for (j in A[i]..B[i]) {
                if (cakes[j] == layer - 1)
                    cakes[j] = layer
                else
                    cakes[j] = -1
            }
        }
        return cakes.filter { it == K }.count()
    }
    fun generateData(rand: Random, size: Int, K: Int) : Boolean {
        val A = IntArray(size)
        val B = IntArray(size)
        val C = IntArray(size)
        for (i in 0 until size) {
            A[i] = rand.nextInt(14) + 1
            B[i] = A[i] + rand.nextInt(5)
            val newRand = rand.nextInt(10)
            if (newRand < K)
                C[i] = newRand + 1
            else {
                C[i] = i * K / size + 1
            }
            C[i]
        }
        val t1 = solution(20,K,A,B,C)
        val t2 = bruteForce(20,K,A,B,C)
        if (t1 != t2) {
            println("sols " + t1 + " " + t2)
            return false
        }
        return true
    }
}