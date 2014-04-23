package com.she.sudoku.model;

import com.she.sudoku.model.Region.RegionType;
import com.she.sudoku.util.Debug;
import com.she.sudoku.util.MatrixUtils;

public class Model {
    private Cell[][] s = new Cell[9][9];
    private Region[] row = new Region[9];
    private Region[] col = new Region[9];
    private Region[] squ = new Region[9];
    
    private int[][] result = null;
    private boolean done = false;
    
    private Model() {
        init();
    }
    
    public static Model newSudokuGame() {
        return new Model();
    }
    
    public Model(int[][] data) {
        q = data;
        init(data);
    }
    
    public boolean hasFinished() {
        if (done)
            return true;
        
        for (int i = 0; i < 9; i++) {
            if (! row[i].finished()) {
                return false;
            }
        }
        
        done = true;
        return true;
    }
    
    
    public Cell getCell(int i, int j) {
        return s[i][j];
    }
    

    public int[][] getQestion() {
        return q;
    }

    
    public int[][] getSolution() {
        if (result != null)
            return result;
        
        
        int ret = fillMost();
        if (ret == -1)
            return null;
        else if (ret == 0)
            return null;
        
        result = new int[9][9];
        for (int i=0; i<9; i++) {
            for (int j=0; j<9; j++) {
                result[i][j] = s[i][j].getValue();
            }
        }
        return result;
    }

    private int fillMost() {
        // Brute force!
        boolean unfinished;
        int count = 0;
        do {
            count = 0;
            for (int i=0; i<9; i++) {
                for (int j=0; j<9; j++) {
                    Cell c = s[i][j];
                    if (c.isEmpty()) {
                        Integer v = c.findFill();
                        if (v != null) {
                            if (! c.checkValue(v))  // Wrong data found, question needs back.
                                return -1;

                            count += c.fill(v);
                        }
                    }
                }
            }
            unfinished = ! hasFinished();
            
            if ((count == 0) && unfinished) {
                return 0;
            }
        } while (unfinished);

        return 1;
    }
    
    
    // Init the a Sudoku with automatically generated data.
    private void init() {
        result = null;
        // Init cells
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                s[i][j] = new Cell(i, j, 0);
            }
        }
        
        updateRegions();
        
        for (Cell[] r: s) {
            for (Cell c: r) {
                c.allowAll();
            }
        }
        generateQuestion();
    }
    
    // Init the contents of the Sudoku
    private void init(int[][] data) {
        result = null;
        // Init cells
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                s[i][j] = new Cell(i, j, data[i][j]);
            }
        }
        
        updateRegions();
        
        for (Cell[] r: s) {
            for (Cell c: r) {
                c.updateAllows();
            }
        }
    }
        
    private void updateRegions() {
        for (int i = 0; i < 9; i++) {
            row[i] = new Region();
            col[i] = new Region();
            squ[i] = new Region();
        }
        
        // Initialize regions
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                row[i].addCell(s[i][j], RegionType.Row);
                col[j].addCell(s[i][j], RegionType.Col);
                squ[(i/3) * 3 + j/3].addCell(s[i][j], RegionType.Squ);
            }
        }
    }
    
    
    // Below are for question auto generation
    private int[][] q = null; // Generated question
    // Below are stacks tracking the dynamic progress.
    private int[] vst = new int[81]; // the value in that position

    private void generateQuestion() {
    //    round = 0;
        q = new int[9][9];
        fillDFS(0);
        
        result = new int[9][9];
        for (int i=0; i<9; i++) {
            for (int j=0; j<9; j++) {
                result[i][j] = s[i][j].getValue();
            }
        }
    }
    
    private int fillDFS(int n) { // n: round
        Debug.logln("Round " + n);
        int x = 0, y = 0, i = 0, v = 0;
        int ret = 1;
        // Randomly select position
        do {
            i = MatrixUtils.getRandPos(81);
            x = i / 9; y = i % 9;
        } while (! s[x][y].isEmpty());

        Cell c = s[x][y];

        // Randomly select value
        v = MatrixUtils.getRandValue(9);
        // Update the stack.
//        pos[n] = i;
        vst[n] = v;
        
loop:
        do {
            if (ret == -1) { // The last round returned -1
                // clean and reset
                q[x][y] = 0;
                init(q);
            }
            
            while (! c.checkValue(v)) { // check whether v is proper
                v++;
                if (v > 9) v = 1;
                if (vst[n] == v) break loop;
                q[x][y] = i;
            }
            // Update the latest question.
            q[x][y] = v;
            
            c.fill(v);
            ret = 0;
            // If n > 10, try to fill in some spaces based on known numbers
            // to avoid too much wrong numbers randomly placed
            if (n > 10) {
                ret = fillMost();
                if (ret == 1)
                    return 1;
            }
            
            if (ret == 0) { // Current v not bad, go forward to next round.
                ret = fillDFS(n + 1);
                if (ret == 1)
                    return 1;
            }
            
            v++;
            if (v > 9) i = 1;
        } while (v != vst[n]); // Try all the values.
        return -1;
    }
}
