package com.she.sudoku.model;

import java.util.*;
import com.she.sudoku.util.Debug;


/*abstract*/ class Region {
    enum RegionType {
        Row,
        Col,
        Squ
    };
    
    // private int x, y; // start pos
    private Set<Cell> emptyCells = new HashSet<>(9);
    private Set<Integer> freeValues = new HashSet<>(9);
    private boolean done = false;
    
    Region () {
        for (int i = 1; i <= 9; i++)
            freeValues.add(i);
    }
    
    // Return the count of extra cells be filled
    void fillCell(Cell c, Map<Cell, Integer> wait) {
        emptyCells.remove(c);
        freeValues.remove(c.getValue());
        
        for (Cell c1:emptyCells) {
            c1.notAllow(c.getValue());
        }

        if (emptyCells.isEmpty()) {
            done = true;
        } else if ((emptyCells.size() == 1) && (freeValues.size() == 1)) { // Quick solve the last element
            Cell c1 = emptyCells.iterator().next();
            Integer v1 = freeValues.iterator().next();
            Debug.log("\t Add wait (" + c1.getX() + ", " + c1.getY() + ") = " + v1);
            wait.put(c1, v1);
        }
    }
    
    
    /**
     * Test whether it is 100% sure that value v should be filled into Cell c.
     * @param c The empty cell
     * @param v The value (1-9)
     * @return true if yes; else false.
     */
    boolean testFill(Cell c, Integer v) {
        boolean canFill = true;
        for (Cell c1: emptyCells) {
            if ((c1 != c) && c1.allows(v)) {
                canFill = false;
                break;
            }
        }
        
        return canFill;
    }
    
    boolean checkValue(int x) {
        return freeValues.contains(x);
    }
    
    boolean finished() {
        return done;
    }
    
    void addCell(Cell c, RegionType t) {
        if (t == RegionType.Col)
            c.setCol(this);
        else if (t == RegionType.Row)
            c.setRow(this);
        else
            c.setSqu(this);
        
        if (c.isEmpty()) {
            emptyCells.add(c);
        } else {
            freeValues.remove(c.getValue());
        }
    }
}
