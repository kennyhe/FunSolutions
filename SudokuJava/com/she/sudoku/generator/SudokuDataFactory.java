package com.she.sudoku.generator;



public class SudokuDataFactory {
    // prevent the construction
    private SudokuDataFactory() {}
    
    public static SudokuDataGenerator getGenerator() {
        return getGenerator("");
    }
    
    public static SudokuDataGenerator getGenerator(String param) {
        if (param == null)
            return RandomGenerator.getInstance();
        
        switch (param) {
            case "random":
                return RandomGenerator.getInstance();
            case "fixed":
                return FixedGenerator.getInstance();
            case "brute":
                return BruteForceGenerator.getInstance();
            default:
                return RandomGenerator.getInstance();
        }
    }
}
