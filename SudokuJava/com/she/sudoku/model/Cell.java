package com.she.sudoku.model;

import java.util.*;
import com.she.sudoku.util.Debug;

public class Cell {
    private int x, y, data;
    private Region row, col, squ;
    private Set<Integer> allow = new HashSet<>(9); 
    
    Cell(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.data = value;
    }
    
    public boolean isEmpty() {
        return data == 0;
    }
    
    
    /**
     * Need to keep both this function and the "allow" Set.
     * If this value is allowed for this cell, then return true. Else false.
     * @param v The value to be checked.
     * @return true if allow, else false.
     */
    public boolean checkValue(int v) {
        return row.checkValue(v) &&
                col.checkValue(v) &&
                squ.checkValue(v);
    }
    

    public int getValue() {
        return data;
    }

    public int fill(int v){
        int count = 1;
        this.data = v;
        Hashtable<Cell, Integer> wait = new Hashtable<>();
        
        Debug.log(" Filled (" + x + ", " + y + ") = " + v);
        
        row.fillCell(this, wait);
        col.fillCell(this, wait);
        squ.fillCell(this, wait);
        
        Enumeration<Cell> cells = wait.keys();
        while (cells.hasMoreElements()) {
            Cell cell = cells.nextElement();
            Integer value = wait.get(cell);
            Debug.log("\tlast item(row,col,or squ)->");
            count += cell.fill(value);
        }
        return count;
    }
    
    // Find the value 100% sure can be filled.
    public Integer findFill(){
        if (data > 0) return null;
        
        if (allow.size() == 1) { // The only value allowed
            Debug.log("\tonly allowed value->");
            return allow.iterator().next();
        } else {
            for (Integer v: allow) {
                if (Debug.Debug) {
                    if (row.testFill(this, v)) {
                        Debug.log("\tonly in row->");
                    }
                    
                    if (col.testFill(this, v)) {
                        Debug.log("\tonly in col->");
                    }
                    if (squ.testFill(this, v)) {
                        Debug.log("\tonly in squ->");
                    }
                }
                
                if (row.testFill(this, v) || col.testFill(this, v) || squ.testFill(this, v)) {
                    return v;
                }
            }
        }
        return null;
    }
    
    public void notAllow(Integer v) {
        allow.remove(v);
    }
    
    boolean allows(int v) {
        return allow.contains(v);
    }
    
    void updateAllows() {
        for (int i = 1; i <= 9; i++) {
            if (checkValue(i))
                allow.add(i);
        }
    }
    
    void allowAll() {
        for (int i = 1; i <= 9; i++) {
            allow.add(i);
        }
    }
    
    
    
    // Getters and Setters

    int getX() {
        return x;
    }

    void setX(int x) {
        this.x = x;
    }

    int getY() {
        return y;
    }

    void setY(int y) {
        this.y = y;
    }

    Region getRow() {
        return row;
    }

    void setRow(Region row) {
        this.row = row;
    }

    Region getCol() {
        return col;
    }

    void setCol(Region col) {
        this.col = col;
    }

    Region getSqu() {
        return squ;
    }

    void setSqu(Region squ) {
        this.squ = squ;
    }
    

}
