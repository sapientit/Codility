package uk.co.sapientit.templateapp

class Phosphorous {
    /*
    When my kids were doing exams, I always gave them the advice someone once gave me - RTFQ (read the flipping question)!
    I spent ages trying to work out how to do this, and eventually re-read the question.  There is a key sentence that
    I had missed:
    There are N-1 corridors connecting N intersections.
    There is therefore only 1 way through the maze between any two points.  That makes the problem sooooooooo much
    simpler!
    Starting from the leaf nodes, they are connected to the exit.  Eventually we will hit a prisoner or we will hit
    a junction.
    If we hit a prisoner (that sounds a bit brutal but you know what I mean) then we need a guard before that prisoner.
    We then start tracking that we have a route to a prisoner from this branch, but we no longer have a route to an exit.
    If we hit a junction then (as long as there is not a prisoner there) then we propagate whether this is a route to an
    exit or a route to a prisoner.
    If we start to process a node and find that we have both a route to prisoner and exit then we need a guard at that point.
    Process all the nodes in this manner and we have the number of guards required.
    -1 is the correct answer where a prisoner is on a leaf node.
     */
   fun solution(A: IntArray, B: IntArray, C: IntArray): Int {
       val N = A.size + 1
       val counts = IntArray(N)
       val prisoner = BooleanArray(N){false}
       C.forEach { prisoner[it]  = true}
       val firstChild = IntArray(N){Int.MIN_VALUE}
       val nextChild = IntArray(N * 2){Int.MIN_VALUE}
       for (i in A.indices) {
           val a = A[i]
           val b = B[i]
           counts[a] += 1
           counts[b] += 1
           nextChild[i] = firstChild[a]
           firstChild[a] = i
           nextChild[i + N] = firstChild[b]
           firstChild[b] = i + N
       }
       val toProcess = IntArray(N)
  //     val parent = IntArray(N)
       var highWater = 0
       val leadsToExit = BooleanArray(N){false}
       val leadsToPrisoner = BooleanArray(N){false}
       val processed = BooleanArray(N){false}
       for (i in counts.indices) {
           if (counts[i] <= 1) {
               toProcess[highWater] = i
               highWater += 1
               counts[i] = 0
               leadsToExit[i] = true
               if (prisoner[i])  // prisoner is at exit
                   return -1
           }
       }
       var index = 0
       var guards = 0
       while (index < highWater) {
           val current = toProcess[index]
           processed[current] = true
           if (leadsToExit[current] && leadsToPrisoner[current]) {
               guards += 1
               leadsToExit[current] = false
               leadsToPrisoner[current] = false
           }
           var child = firstChild[current]
           while (child != Int.MIN_VALUE) {
               val other: Int
               other = if (child >= N) A[child - N] else B[child]
               if (!processed[other]) {
                   counts[other] -= 1
    //               parent[current] = other
                   if (leadsToExit[current]) {
                       if (prisoner[other]) {
                           guards += 1
                           leadsToPrisoner[other] = true
                       } else {
                           leadsToExit[other] = true
                       }
                   } else {
                       leadsToPrisoner[other] = leadsToPrisoner[other] || leadsToPrisoner[current] || prisoner[other]
                   }
                   if (counts[other] == 1) {
                       toProcess[highWater] = other
                       highWater += 1
                   }
                   break
               }
               child = nextChild[child]
           }
           index += 1
       }
    return guards
    }
}