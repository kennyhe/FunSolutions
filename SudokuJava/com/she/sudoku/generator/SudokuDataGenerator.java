package com.she.sudoku.generator;

public interface SudokuDataGenerator {
    int[][] getQuestion();
    int[][] getAnswer();
    void regenerate();
}
