package com.she.sudoku.generator;

import com.she.sudoku.model.Model;

public class RandomGenerator implements SudokuDataGenerator {
    Model m = null;
    

    // Singleton
    private static RandomGenerator instance = null;
    private RandomGenerator() {
        m = Model.newSudokuGame();
    }
    public static RandomGenerator getInstance() {
        if (instance == null) {
            instance = new RandomGenerator();
        }
        return instance;
    }

    
    @Override
    public int[][] getQuestion() {
        return m.getQestion();
    }

    @Override
    public int[][] getAnswer() {
        return m.getSolution();
    }

    @Override
    public void regenerate() {
        m = Model.newSudokuGame();
    }

}
