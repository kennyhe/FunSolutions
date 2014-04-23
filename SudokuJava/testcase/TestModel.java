package testcase;

//import com.she.sudoku.generator.SudokuDataFactory;
import com.she.sudoku.model.Model;
import com.she.sudoku.util.MatrixUtils;


public class TestModel {
    
    public static void main(String[] args) {
//        int[][] q = SudokuDataFactory.getGenerator("brute").getQuestion();
        Model m = Model.newSudokuGame();
//        for (int i=0; i<9999; i++) m = Model.newSudokuGame();
        int[][] q = m.getQestion();
        
        System.out.println("Question:");
        MatrixUtils.printMatrix(q, 9, 9);
        int[][] a = null;
        try {
            a = m.getSolution();
            if (a == null) {
                System.out.println("No solution for this problem!");
                return;
            }
        } catch (Exception e) {
            System.out.println("The question is wrong! Please check");
            return;
        }
        System.out.println("\nQuestion in a string:");
        System.out.println(MatrixUtils.matrixToString(q, 9, 9));
        System.out.println("\nAnswer:");
        MatrixUtils.printMatrix(a, 9, 9);
    }
}
