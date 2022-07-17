package uk.co.sapientit.templateapp

class OLX {
    /*
    This would be soooo simple if only the the source jars and the target jar were guaranteed to be different.
    Sort the jars into capacity sequence and also sort into juice volume sequence and then just add juice into the
    biggest capacity until you can't fit any more.
    But...
    The jar with the most capacity might also have a small amount of juice.  In which case there is still more
    capacity in the jar.
    So...
    We do the above and if we are including the juice from the jar we are using then we carry on filling until
    we run out of capacity.  This gives us a maximum including the jar we are using.  This might not be optimal though
    we now have to search through until we find a jar that we do not include the juice from (maximum excluding).
    One of these two will be the answer.
     */
    fun solution(juice: IntArray, capacity: IntArray): Int {
        val comp1 = Comparator<Int> {j1,j2 -> juice[j1] - juice[j2]}
        val comp2 = Comparator<Int> {j1,j2 -> (capacity[j2] - juice[j2]) - (capacity[j1] - juice[j1])}
        val j = IntArray(juice.size){it}.sortedWith(comp1).toIntArray()
        val c = IntArray(juice.size){it}.sortedWith(comp2).toIntArray()
        val total = LongArray(juice.size)
        var running = 0L
        var maxC = capacity[c[0]] - juice[c[0]]
        var index = 0
        while (index < j.size && running <= maxC) {
            running += juice[j[index]]
            total[index] = running
            index += 1
        }
        // Does this include the juice from the same jar?
        if (index < j.size && juice[c[0]] > juice[j[index]]) {
            // no it doesn't - this is therefore the answer
            return index
        }
        maxC = capacity[c[0]]
        while (index < j.size && running <= maxC) {
            running += juice[j[index]]
            total[index] = running
            index += 1
        }

        if (running <= maxC)
            return j.size
        val maxInc = index - 1 // Maximum that includes the juice in the jar

        // Now find a jar that has too much juice to be included in the count

        var maxExc = 0
        var tIndex = index - 1
        var current : Int
        var cap : Int
        var jui : Int
        index = 1
        while (maxExc == 0 && index < c.size) {
            current = c[index]
            cap = capacity[current]
            jui = juice[current]
            val spare = cap - jui
            //  Go down the total until we get the number of juices that will fir in the spare capacity
            while (tIndex >= 0 && spare < total[tIndex]) {
                tIndex -= 1
            }
            if (tIndex < 0 ) {
                maxExc = 1 // There are none - so no juices can be combined in this jar
            } else {
                if (jui < juice[j[tIndex + 1]]) { // Are we still including the juice from the jar?
                    index += 1 // yes - move on to the next jar
                } else { // no - we have an answer
                    maxExc = tIndex + 2 // + 1 because zero based, + 1 for the juice already in jar
                }
            }
        }
        return maxOf(maxExc, maxInc)
    }

}