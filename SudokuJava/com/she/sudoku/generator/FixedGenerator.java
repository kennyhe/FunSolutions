package com.she.sudoku.generator;
import java.util.ArrayList;
import java.util.Map;
import java.util.Hashtable;


class FixedGenerator implements SudokuDataGenerator {
    private int current = 0;
    private ArrayList<int[][]> sudokuLib = new ArrayList<>();
    private Map<int[][], int[][]> sudokuAnswerLib = new Hashtable<>();


    // Singleton
    private static FixedGenerator instance = null;
    private FixedGenerator () {
        sudokuLib.add(sudoku0);
        sudokuAnswerLib.put(sudoku0, sudokuAnswer0);
        sudokuLib.add(sudoku1);
        sudokuAnswerLib.put(sudoku1, sudokuAnswer0);
        regenerate();
    }
    public static FixedGenerator getInstance() {
        if (instance == null) {
            instance = new FixedGenerator();
        }
        return instance;
    }
         
    
    @Override
    public int[][] getQuestion() {
        return sudokuLib.get(current);
    }
    
    @Override
    public int[][] getAnswer() {
        return sudokuAnswerLib.get(sudokuLib.get(current));
    }
    
    @Override
    public void regenerate() {
        current = 1;
        //current = (int)(Math.random() * COUNT);
    }
    
    
    final int COUNT = 1;

    final int[][] sudoku0 = {
         {8, 0, 0, 5, 2, 0, 0, 0, 0},
         {5, 1, 0, 3, 0, 4, 8, 0, 7},
         {6, 3, 0, 0, 0, 8, 0, 0, 0},
         {0, 0, 8, 2, 1, 0, 0, 0, 5},
         {0, 0, 6, 0, 7, 0, 1, 0, 0},
         {3, 0, 0, 0, 4, 6, 9, 0, 0},
         {0, 0, 0, 1, 0, 0, 0, 5, 4},
         {2, 0, 4, 6, 0, 9, 0, 1, 8},
         {0, 0, 0, 0, 3, 2, 0, 0, 6}
     };
    
    
    final int[][] sudokuAnswer0 = {
         {8, 4, 7, 5, 2, 1, 6, 3, 9},
         {5, 1, 9, 3, 6, 4, 8, 2, 7},
         {6, 3, 2, 7, 9, 8, 5, 4, 1},
         {7, 9, 8, 2, 1, 3, 4, 6, 5},
         {4, 2, 6, 9, 7, 5, 1, 8, 3},
         {3, 5, 1, 8, 4, 6, 9, 7, 2},
         {9, 6, 3, 1, 8, 7, 2, 5, 4},
         {2, 7, 4, 6, 5, 9, 3, 1, 8},
         {1, 8, 5, 4, 3, 2, 7, 9, 6}
     };
     

    final int[][] sudoku1 = {
        {0, 0, 0, 8, 0, 0, 0, 0, 0}, 
        {0, 0, 0, 4, 0, 9, 0, 0, 1}, 
        {0, 5, 6, 0, 0, 0, 0, 4, 2}, 
        {0, 7, 0, 0, 5, 0, 4, 2, 0}, 
        {6, 0, 0, 0, 0, 4, 0, 0, 7}, 
        {0, 4, 0, 7, 0, 0, 1, 0, 5}, 
        {0, 0, 5, 0, 0, 6, 9, 0, 0}, 
        {0, 6, 0, 0, 0, 0, 8, 1, 0}, 
        {0, 0, 0, 2, 0, 0, 0, 0, 0}
    };
}
