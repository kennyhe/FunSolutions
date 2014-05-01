import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


public class FrequentValuePairs <VT> {

    private Object[][] matrix;
    private VT value2Comp;
    private int minFreq;
    private int cols;
    PFTreeNode pfRoot;
    IntPair[] colsByFreq; // Track the columns and corresponding frequency. Sorted by frequency in descending order 
    
    int[][] pairsCountMatrix;
    Stack<Integer> prevColIndice; // a stack keep track of visited parent nodes in DFS traversal
    
    
    /**
     * 
     * @param matrix
     * @param value
     * @param minFrequency
     * @return
     */
    public List<IntPair> getFrequentDigitPairs(Object[][] matrix, VT valueToCompare, int minFrequency) {
        if (matrix == null || matrix[0] == null)
            return null;
        
        this.value2Comp = valueToCompare;
        this.minFreq = minFrequency;
        this.cols = matrix[0].length;
        this.matrix = matrix;
        
        ArrayList<IntPair> result = new ArrayList<IntPair>();

        // first scan
        scanColumnFrequency();
        if (colsByFreq.length <= 1) {
            // 0 or 1 column satisfies the requirements, absolutely no result 
            return result;
        }
        
        // second scan and builds the pattern frequency tree
        buildFPTree();
        
        // Computes the frequency of pairs by traversal the PFTree
        pairsCountMatrix = new int[colsByFreq.length][colsByFreq.length];
        prevColIndice = new Stack<>();
        for (PFTreeNode node : pfRoot.children.values()) {
            visitPFTree(node);
        }
        
        for (int index1 = 0; index1 < colsByFreq.length; ++index1) {
            for (int index2 = 0; index2 < colsByFreq.length; ++index2) {
                if (pairsCountMatrix[index1][index2] > minFreq) {
                    result.add(new IntPair(colsByFreq[index1].getFirst(), 
                                           colsByFreq[index2].getFirst()));
                }
            }
        }
        
        return result;
    }
    
    
    private void scanColumnFrequency() {
        // Scan and calculate the frequency of each columns
        int[] colFreq = new int[cols];
        for (Object[] row: matrix) {
            for (int c = 0; c < cols; ++c) {
                if (row[c] == value2Comp) {
                    colFreq[c]++;
                }
            }
        }
        
        colsByFreq = new IntPair[cols];
        
        for (int c = 0; c < cols; ++c) {
            if (colFreq[c] >= minFreq) {
                colsByFreq[c] = new IntPair(c, colFreq[c]);
            }
        }
        
        if (colsByFreq.length <= 1)
            return;
        
        // Sorts the frequency in columns. (preferred descending order but also fine ascending)
        // Complexity: O(MlogM)
        Arrays.sort(colsByFreq, new Comparator<IntPair>() {

            @Override
            public int compare(IntPair o1, IntPair o2) {
                return o2.getSecond() - o1.getSecond();
            }
            
        });

    }
    
    
    private void buildFPTree() {
        this.pfRoot = new PFTreeNode(-1);
        int col;
        
        for (Object[] row: matrix) {
            PFTreeNode curNode = pfRoot;
            // checks the columns, from high frequency to low frequency
            for (IntPair ip : colsByFreq) {
                col = ip.getFirst();
                
                if (row[col] == value2Comp) {   // If find match
                    // Adds a new child (if children of current node does not contain this column), 
                    // or increases the frequency of that child (contained) in the tree.
                    // Then set that child as current child
                    curNode = curNode.addChild(col);  
                }
            }
        }
    }
    
    
    private void visitPFTree(PFTreeNode node) {
        if (! prevColIndice.isEmpty()) {
            for (Integer cols: prevColIndice) {
                pairsCountMatrix[cols][node.colIndex] += node.times;
            }
        }
        
        if (node.hasNoChildren())
            return;
        
        prevColIndice.push(node.colIndex);
        for (PFTreeNode child: node.children.values()) {
            visitPFTree(child);
        }
        prevColIndice.pop();
    }

    
    public static <VT> Object[][] generateTestingData(int rows, int cols, List<VT> miscValueList, VT value4Check, double probability) {
        // Make sure they value for check is not in the misc values list
        if (miscValueList == null || miscValueList.isEmpty())
            return null;
            
        miscValueList.remove(value4Check);
        
        Object[][] testData = (new Object[rows][cols]);
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                if (Math.random() < probability) {
                    testData[i][j] = value4Check;
                } else {
                    testData[i][j] = miscValueList.get((int) (miscValueList.size() * Math.random()));
                }
            }
        }
        return testData;
    }
    
    
    /**
     *  PatternFrequency Tree Node
     */
    private class PFTreeNode {
        int colIndex; // column index # in the colsByFreq array
        Map<Integer, PFTreeNode> children; // children set
        int times; // The time it appears
        
        public PFTreeNode(int c) {
            this.colIndex = c;
            this.children = null;
            this.times = 1;
        }
        
        public PFTreeNode addChild(int colIdx) {
            PFTreeNode childNode = null;
            if (children == null) {
                children = new HashMap<>();
                childNode = new PFTreeNode(colIdx);
                children.put(colIdx, childNode);
            } else {
                childNode = children.get(colIdx); // For HashSet, this step is O(1). If using List, then needs O(M)
                if (childNode == null) {
                    childNode = new PFTreeNode(colIdx);
                    children.put(colIdx, childNode);
                } else {
                    ++ childNode.times;
                }
            }
            return childNode;
        }
        
        public boolean hasNoChildren() {
            return (children == null || children.isEmpty());
        }
    }
    
    
    public static class IntPair {
        int f, s;
        
        IntPair(int first, int second) {
            f = first; s = second;
        }
        public int getFirst() {
            return f;
        }
        public int getSecond() {
            return s;
        }
    }
    
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        List<Integer> miscValues = new ArrayList<>();
        miscValues.add(0);
        Object[][] testData = FrequentValuePairs.generateTestingData(3000, 1000, miscValues, 1, 0.36);
        /*
        System.out.println("Test data:");
        for (Object[] row: testData) {
            for (Object i: row) {
                System.out.format("%d ", i);
            }
            System.out.println();
        }
        */
        FrequentValuePairs<Integer> test = new FrequentValuePairs<Integer>();
        List<IntPair> result = test.getFrequentDigitPairs(testData, 1, 460);
        
        System.out.println("Column pairs more than 50 times:");
        for (IntPair pair : result) {
            System.out.format("%d\t%d\n", pair.getFirst(), pair.getSecond());
        }
    }
    
    

}
