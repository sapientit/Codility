package uk.co.sapientit.templateapp

class Sulphur {
    fun solution(A: IntArray, B: IntArray, C: IntArray): Int {
        // A = durability
        // B = weight
        // C = connected to
        val brokenRopes = BooleanArray(C.size)  {false}
        var brokenCount = 0
        val weights = IntArray(C.size)
        for (i in C.size - 1 downTo 0) {
            weights[i] +=  B[i]
            if (weights[i] > A[i]) {
                brokenRopes[i] = true
                brokenCount += 1
                weights[i] -= A[i]
                if (C[i] != -1) {
                    weights[C[i]] += A[i]
                }
            } else {
                if (C[i] != -1)
                    weights[C[i]] += weights[i]
            }
        }
        if (brokenCount == 0)
            return C.size
        /*
        We now have all the ropes that will break in broken ropes.  Weights[i] shows how much weight must be removed
        (after all downstream broken ropes have been repaired) in order to repair this rope.
        Next step is to work out which ropes are connected to which broken ropes
         */
        val mapTo = IntArray(C.size){-1}
        for (i in C.indices) {
            val target = C[i]
            if (target != -1) {
                if (brokenRopes[target])
                    mapTo[i] = target
                else
                    mapTo[i] = mapTo[target]
            }
        }
        /*
        mapTo contains next highest broken rope.  The exception is the broken rope itself which points to the rope above
        Now remove ropes 1 at a time
         */
        for (i in C.size - 1 downTo 0) {
            var target: Int
            if (brokenRopes[i])
                target = i
            else {
                var current = i
                var thisTarget = mapTo[current]
                while (thisTarget != -1 && !brokenRopes[thisTarget]) {
                    val nextTarget = mapTo[thisTarget]
                    mapTo[current] = nextTarget
                    current = thisTarget
                    thisTarget = nextTarget
                }
                target = thisTarget
            }
            var removeWeight = B[i]
            while (target != -1) {
                weights[target] -= removeWeight
                if (weights[target] <= 0) {
                    brokenRopes[target] = false
                    brokenCount -= 1
                    if (brokenCount == 0) {
                        return i
                    }
                }
                if (weights[target] < 0) {
                    removeWeight = -weights[target]  //  Remove weight from parent broken rope
                    var current = target
                    var thisTarget = mapTo[current]
                    while (thisTarget != -1 && !brokenRopes[thisTarget]) {
                        val nextTarget = mapTo[thisTarget]
                        mapTo[current] = nextTarget
                        current = thisTarget
                        thisTarget = nextTarget
                    }
                    target = thisTarget
                } else
                    target = -1  //
            }


        }
        return -1 //  Should never get here
    }

}