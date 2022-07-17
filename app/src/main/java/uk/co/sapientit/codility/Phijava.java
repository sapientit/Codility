package uk.co.sapientit.codility;

import java.util.LinkedList;
import java.util.List;

public class Phijava {
    /*
    I found the description posted as a solution quite confusing, so I am not sure whetehr this uses the same
    approach or not.  I think it is - but it doesn't really match the description.

    To explain this approach, we will take an example of M = 4, but the same works for all sizes (even M > 7 but
    it gets slower quite quickly).

    If we look at the last row placed (X is a 2*2 square and O is a 1*! square), there are only a certain number
    of possible views we can have

    A - OOOO   - no 2*2 square on the last line
    B - XXOO   - 2*2 square on left
    C - OXXO   - 2*2 in the middle
    D - OOXX   - 2*2 on the right
    E - XXXX   - 2*2 on left and 2*2 on right.

  >>  Code implementation: generatePossibilities generates a list of all the possible views we can have by recursion.
  >>  It uses 11 to indicate a 2*2 square and O for 1*1.  For each position it looks at all the generated
  >>  possibilities.  If the previous possibility had a 0 in the last position then we add a new position with
  >>  a 2*2 square covering this position and the last position.

    The new row that we are placing must also be one of these 5 patterns.  But, since the 2*2 square would extend
    back onto the previous square we cannot go between two pattersn that have an X in the same position.

    So from A we can go to any pattern.
    From B we can go to A or D
    from C we can only go to A
    from D we can go to A or B
    from E we can only go to A.

    If we use integers for these patterns, with 1's for the 2*2 squares, then "and" the two patterns together
    must result in 0 for the transition to be valid.

    This is actually a matrix X
    ( 1 1 1 1 1 )
    ( 1 0 0 1 0 )
    ( 1 0 0 0 0 )
    ( 1 1 0 0 0 )
    ( 1 0 0 0 0 )

    We start with row 1 must be entirely empty (position A)
    If we use a matrix S
    { 1 )
    ( 0 )
    ( 0 )
    ( 0 )
    ( 0 )
    to define this position.

    Then X*S *matrix multipliation) tells us all the ways we can get to patterns A-E
    ( 1 )
    ( 1 )
    ( 1 )
    ( 1 )
    ( 1 )

   We can see that this is correct - after 2 rows there are only 5 possible positions we can have (each of the
   patterns A-E).

   After 3 rows X * X * S
   ( 5 )
   ( 2 )
   ( 1 )
   ( 2 )
   ( 1 )

   Pattern A can occur with any of the patterns A-E followed by a blank row
   B is only possible after pattern A or pattern D on the previous row
   C is only possible after 2 blank rows
   D is possible only after row 2 is A or B
   E is only possible after a blank row.

   the total patterns is now the sum of all the patterns ending on this row - 5 + 2 +1 + 2 + 1 = 11

   After N rows the sum is X**(N - 1)* S

   For integers A**B can be calculated in logB time as follows:

   Take the binary representation of B - eg 37 =
   100101 = 32 + 4 + 1
   A**37 == A**32 * A**4 * A**1

   Set power to A
   Set running total to 1
   So we look at the last bit of B.  If it is a 1 we multiply our running total by the current power.
   Then shift B right by 1 and square "power"  (**2, **4, **8, **16, ...**(2**N))

   Repeat until B is 0

   The same can be done with matrices (the identity matrix with 1s on the diagonals is the matrix equivalent to 1).

   So to summarise:
   - find all possible patterns for the given M
   - build a matrix of all valid transations (X)
   - raise X to power (N-1)
   - mutiply by S the starting position
   - Count the total number of combinations.
   - Don't forget to MOD all over the place!

   There are some optimisations that are possible (but not necessary).

   1. The matrix is symmetrical so we actually only need to calculate half of it
   2. The power function does an expensive matrix multiplication at the end which is unnecessary
   3. The final multiply by S is not necessary - at that point we can just add up the first column of the matrix.

     */
    long MOD = 10000007;
    public List<Long> generatePossibilities (int n) {
        LinkedList<Long> poss = new LinkedList<>();
        poss.add(0L);
        checkPoss(3L,1, n, poss);
        return poss;
    }

    void checkPoss (long mask, int index, int size, List<Long> poss) {
        if (index == size) return;
        for (int i = poss.size() - 1; i >= 0; i--) {
            if ((poss.get(i) & mask) == 0L) {
                poss.add( poss.get(i) | mask);
            }
        }
        checkPoss(mask << 1, index + 1, size, poss);
    }
    public long[][] generateMatrix(int size) {
        List<Long> poss = generatePossibilities(size);
        long[][] matrix = new long[poss.size()][poss.size()];
        for (int i = 0 ; i < matrix.length; i++) {
            for (int j = 0 ; j < matrix.length; j++) {
                if ((poss.get(i) & poss.get(j)) == 0L) {
                    matrix[i][j] = 1L;
                } else {
                    matrix[i][j] = 0L;
                }
            }
        }
        return matrix;
    }
    public long[][] multiplyMatrix(long[][] matrix1, long[][] matrix2) {
        long[][] result = new long[matrix1.length][matrix2[0].length];
        for (int i = 0 ;i < result.length ;i++) {
            for (int j = 0 ; j <  result[0].length;j++) {
                long sum = 0L;
                for (int k = 0 ; k <  matrix1[0].length;k++) {
                    sum += matrix1[i][k] * matrix2[k][j];
                }
                result[i][j] = sum % MOD;
            }
        }
        return result;
    }
    public long[][] matrixPower(long[][] matrix, int power) {
        long[][] result = new long[matrix.length][matrix.length];
        for (int i = 0; i< matrix.length;i++) {
            result[i][i] = 1;
        }
        long[][] p2 = matrix;
        int rem = power;
        while (rem > 0) {
            if ((rem & 1) == 1) {
                result = multiplyMatrix(p2,result);
            }
            p2 = multiplyMatrix(p2,p2);
            rem = rem / 2;
        }
        return result;
    }
    public int solution(int N, int M) {
        long[][] matrix = generateMatrix(M);
        long[][] exp = matrixPower(matrix, N -1);
        long[][] startingCondition = new long[matrix.length][1];
        startingCondition[0][0] = 1L;
        long[][] resM = multiplyMatrix(exp, startingCondition);
        long count = 0L;
        for (int i = 0; i< resM.length; i++) {
            count = count + resM[i][0];
        }
        return (int) (count % MOD);

    }
}
