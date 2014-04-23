package com.she.sudoku.util;

public class Debug {
    public static boolean Debug = false;//true;
    public static boolean ExInfo = true;
    
    public static void log(String s) {
        if (Debug) {
            System.out.print(s);
        }
    }
    
    public static void logln(String s) {
        if (Debug) {
            System.out.println(s);
        }
    }

    public static void plog(String s) {
        System.out.print(s);
    }

    public static void plogln(String s) {
        System.out.println(s);
    }


}
