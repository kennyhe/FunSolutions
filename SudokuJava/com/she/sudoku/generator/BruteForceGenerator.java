package com.she.sudoku.generator;

/**
 * This BruteForce solution use Depth First in first 16 steps and Width First in the rest steps.
 * 
 * The solution is very time consuming. No longer use.
 * Theoretically correct but running time is o((9!)^9), a huge number = 362880^9.
 * And the result is not the optimized result. Many values can be removed.
 * There are more than 40 cells have been filled.
 */


//import com.she.sudoku.solver.OldBruteForceSolver;
import com.she.sudoku.model.Model;
import com.she.sudoku.util.MatrixUtils;
import com.she.sudoku.util.Debug;


class BruteForceGenerator implements SudokuDataGenerator {
    int[][] q = new int[9][9];
    int[][] a;
    int[] r = new int[9*9];
    int[] c = new int[9*9];

    // Singleton
    private static BruteForceGenerator instance = null;
    private BruteForceGenerator() {
        regenerate();
    }
    public static BruteForceGenerator getInstance() {
        if (instance == null) {
            instance = new BruteForceGenerator();
        }
        return instance;
    }
    

    @Override
    public int[][] getQuestion() {
        return q;
    }

    @Override
    public int[][] getAnswer() {
        return a;
    }

    @Override
    public void regenerate() {
        int x, y, count, i;
        // Generate a series of coordination (x, y) to r[] and c[].
        MatrixUtils.emptyMatrix(q, 9, 9);
        count = 0;
        do {
            // Randomly get an empty position
            do {
                i = MatrixUtils.getRandPos(81);
                x = i / 9; y = i % 9;
            } while (q[x][y] > 0);
            r[count] = x;
            c[count] = y;
            q[x][y] = 1;
            count++;
        } while (count < 81);
        
        do {
            MatrixUtils.emptyMatrix(q, 9, 9);
        } while (! fillWFS(0));
        
        Debug.logln("Brute force genreated question:");
        MatrixUtils.printMatrix(q, 9, 9);
        Debug.logln("\nBrute force generated answer:");
        MatrixUtils.printMatrix(a, 9, 9);
    }
    
    
    // Recursively try to get a solution, depth first
    private boolean fillDFS(int round) {
        // After the 15 round, try WFS.
        if (round > 15)
            return fillWFS(round);
        
        Debug.logln("Round " + round);
        int x = r[round], y = c[round];
        // Randomly get a value, and try from it.
        int v = MatrixUtils.getRandValue(9);
        int i = v;
        
        // Try all numbers in this round to solve the problem.
        loop:
        do {
            q[x][y] = i;
            // Find a value without conflict to existing values.
            while (MatrixUtils.checkConflicts(q, x, y)) {
                i++;
                if (i > 9) i = 1;
                if (i == v) break loop;
                q[x][y] = i;
            }
            
            Debug.logln("Try (" + x + ", " + y + ") = " + i);
            MatrixUtils.printMatrix(q, 9, 9);
            
            try {
                a = new Model(q).getSolution();
            } catch (Exception e) {
                a = null;
            }
//          a = OldBruteForceSolver.solve(q);
            if (a != null) {
                return true; // Get the solution
            }
            
            // Deep searching.
            if (fillDFS(round + 1)) {
                return true; // Get the solution in the next rounds
            }
            
            Debug.logln("Withdraw (" + x + ", " + y + ") = " + i);

            i++;
            if (i > 9) i = 1;
        } while (i != v); // Try Brute Force all possible values in this round.

        q[x][y] = 0;
        return false; // The value got in this round is not good. Go back to last round.
    }

    
    
    // Recursively try to get a solution, width first
    private boolean fillWFS(int round) {
        // In the first 15 round, try DFS.
        if (round <= 15)
            return fillDFS(round);

        Debug.logln("Round " + round);
        int x = r[round], y = c[round];
        // Randomly get a value, and try from it.
        int v = MatrixUtils.getRandValue(9);
        int i = v;
        
        // First go width.
        loop:
        do {
            q[x][y] = i;
            // Find a value without conflict to existing values.
            while (MatrixUtils.checkConflicts(q, x, y)) {
                i++;
                if (i > 9) i = 1;
                if (i == v) break loop;
                q[x][y] = i;
            }
            
            Debug.logln("Try (" + x + ", " + y + ") = " + i);
            MatrixUtils.printMatrix(q, 9, 9);
            
            try {
                a = new Model(q).getSolution();
            } catch (Exception e) {
                a = null;
            }
//          a = OldBruteForceSolver.solve(q);
            if (a != null) {
                return true; // Get the solution
            }
            
            Debug.logln("Withdraw (" + x + ", " + y + ") = " + i);

            i++;
            if (i > 9) i = 1;
        } while (i != v); // Try Brute Force all possible values in this round.
        
        // Then go depth.
        i = v;
        loop2:
        do {
            q[x][y] = i;
            // Find a value without conflict to existing values.
            while (MatrixUtils.checkConflicts(q, x, y)) {
                i++;
                if (i > 9) i = 1;
                if (i == v) break loop2;
                q[x][y] = i;
            }
            
            Debug.logln("2nd trying (" + x + ", " + y + ") = " + i);
            
            if (fillWFS(round + 1)) {
                return true; // Get the solution in the next rounds
            }
            
            Debug.logln("Withdraw (" + x + ", " + y + ") = " + i);
            
            // Failed in next round, then need to check the next value
            i++;
            if (i > 9) i = 1;
        } while (i != v); // Try Brute Force all possible values in this round with next rounds.

        q[x][y] = 0;
        return false; // The value got in this round is not good. Go back to last round.
    }

}
