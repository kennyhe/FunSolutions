package com.she.sudoku.util;

public class MatrixUtils {
    public static void printMatrix(int[][] m, int row, int col) {
        for (int i=0; i<row; i++) {
            for (int j=0; j<col; j++) {
                Debug.plog(m[i][j] + "\t");
            }
            Debug.plogln("");
        }
    }


    public static String matrixToString(int[][] m, int row, int col) {
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<row; i++) {
            for (int j=0; j<col; j++) {
                if (m[i][j] == 0)
                    sb.append(".");
                else
                    sb.append(m[i][j]);
            }
        }
        return sb.toString();
    }


    public static void emptyMatrix(int[][] m, int row, int col) {
        for (int i=0; i<row; i++) {
            for (int j=0; j<col; j++) {
                m[i][j] = 0;
            }
        }
    }
    
    
    /**
     * Check whether the element at (x, y) is identical to the value in the row, column, or small square.
     * @param m The 9x9 matrix
     * @param x, y the coordination of the element
     * @return true if there are duplicates, else false.
     */
    public static boolean checkConflicts(int[][] m, int x, int y) {
        for (int i = 0; i < 9; i++) {
            if ((i != x) && (m[i][y] == m[x][y])) {
                return true;
            }
            if ((i != y) && (m[x][i] == m[x][y])) {
                return true;
            }
        }
        
        for (int i = (x/3)*3; i <= (x/3)*3 + 2; i++)
            for (int j = (y/3)*3; j <= (y/3)*3 + 2; j++)
                if ((i != x) && (j != y) && (m[i][j] == m[x][y])) {
                    return true;
                }
        
        return false;
    }
    

    /**
     * Generate a random integer number between 1 and x (inclusive)
     * @param x The range.
     * @return The random number
     */
    public static int getRandValue(int x) {
        return 1 + getRandPos(x);
    }
    

    /**
     * Generate a random integer number between 0 and x - 1 (inclusive)
     * @param x The range.
     * @return The random number
     */
    public static int getRandPos(int x) {
        int t = (int)(Math.random() * x);
        
        if (t == x) t = x - 1;
        
        return t;
    }
}
