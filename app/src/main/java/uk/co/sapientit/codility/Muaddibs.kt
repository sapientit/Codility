package uk.co.sapientit.codility

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*
/*
https://app.codility.com/cert/view/certSVQ54Z-PKBPJ25HYY8FPVHJ/

Each event gives 3 possible costs (1 day, 7 day and 30 day tickets).
We add the minimum cost generated up to now to these costs and add the 7 and 30 day
totals to a priority queue (sorted by last day of validity of the ticket).

Then read the priority queue until we get to >= next event date.  For each ticket take
the minimum of the ticket or the calculated cost.

So for example, events on days 1,2, 3, 4 and 7.
After day 1, there are entries for day 6 and 29 in the queue (cost 7/25).  The minimum cost is now 2
(for a 1 day ticket).
Day 2 adds 2 more entries to the queue - day 7 and 30, cost is 9/27 (adding the 2 calculated above)
Total cost is 4 going forward
Same for days 3 and 4
We now have 8 entries in the queue. 6,7,8,9 (costs 7,9,11,13)  and 29,30,31,32 (costs 25,27,29,31)
The cost is 4 1 day tickets - 8.

Day 7 is after the first entry in the queue, so we read it in.  The cost was 7 for this ticket, less
than the 8 we have calculated.  We set the cost to 7 therefore.
After procesing day 7 we have a cost of 9.  We read through everything in the queue, but nothing has a cost
less than 9, so 9 is the answer.
 */

class Muaddibs {
    data class Ticket(val endDay: Int, val cost: Int)
    @RequiresApi(Build.VERSION_CODES.N)
    fun solution(A: IntArray): Int {
        // write your code in Kotlin 1.3.11 (Linux)
        var cost = 0
        val queue = PriorityQueue<Ticket>(){ t1, t2 -> t1.endDay - t2.endDay}
        for (i in A.indices) {
            val day = A[i]
            while (queue.isNotEmpty() && queue.peek()!!.endDay < day) {
                val ticket = queue.poll()!!
                cost = minOf(cost, ticket.cost)
            }
            queue.add(Ticket(day + 6, cost + 7))
            queue.add(Ticket(day + 29, cost + 25))
            cost += 2
        }
        while (queue.isNotEmpty()) {
            val ticket = queue.poll()!!
            cost = minOf(cost, ticket.cost)
        }
        return cost
    }
}