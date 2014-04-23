import java.util.ArrayList;
import java.util.HashMap;

class Solution {

    public static void main(String[] args) {
        try{
            java.io.BufferedReader stdin = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
            String line = stdin.readLine();
            if ((line == null) || (! line.matches("\\d+"))) {
                System.out.println("Invalid input! Should input a number which indicates the count of boxes.");
                return;
            } else {
                int rows = Integer.parseInt(line);
                BoxGame game = new BoxGame();
                for (int i = 1; i <= rows; i++) {
                    line = stdin.readLine();
                    if (line == null) {
                        System.out.println("Expected " + rows + " lines input but reached the end of file in line " + i);
                        return;
                    } else if (line.matches("\\d+\\s\\d+\\s\\d+")) {
                        String []part = line.split("\\s");
                        game.addBox(Integer.parseInt(part[0]), Integer.parseInt(part[1]), Integer.parseInt(part[2]));
                    } else {
                        System.out.println("Syntax error in line " + i + ":" + line);
                        return;
                    }
                }
                System.out.println(game.getMaxHeight());
/*
                System.out.println("The path is:");
                ArrayList<ArrayList<PathNode>> paths = game.getMaxWeighPaths();
                for (ArrayList<PathNode> al : paths) {
                    for (PathNode node: al) {
                        System.out.println(node);
                    }
                    System.out.println();
                }
*/
            }
        }catch (java.io.IOException e) {
            System.out.println(e); 
        }
    }
}



class BoxGame {
    ArrayList<Box> boxes = new ArrayList<>();
    ArrayList<Integer> cubes = new ArrayList<>();
    HashMap<Box, Integer> boxCounter = new HashMap<>();
    int count = 0;
    boolean done = false;
    ArrayList<PathNode> path = new ArrayList<>();
    ArrayList<Box> boxStack = new ArrayList<>();
    int maxWeigh = 0;
    int sumWeigh = 0;
    ArrayList<ArrayList<PathNode>> maxWeighPaths = new ArrayList<>();
    
    void addBox(int l1, int l2, int l3) {
        // Check whether there are boxes which is a cube: l1 = l2 = l3
        if ((l1 == l2) && (l2 == l3)) {
            cubes.add(l1);
            return;
        }
        
        // Check whether any boxes in duplicated size
        for (Box b: boxes) {
            if (b.equals(l1, l2, l3)) {
                int x = boxCounter.get(b);
                boxCounter.put(b, x + 1);
                return;
            }
        }
        
        Box b = new Box(l1, l2, l3);
        boxes.add(b);
        boxCounter.put(b, 1);
    }
    
    // Do not count the cuplicated boxes and cubes.
    ArrayList<ArrayList<PathNode>> getMaxWeighPaths() {
        calculate();
        return maxWeighPaths;
    }
    
    int getMaxHeight() {
        calculate();
        int height = maxWeigh;
        // Count on the cubes
        for (Integer i : cubes)
            height += i;
            
        return height;
    }
    
    private void calculate() {
        // only allow once for each BoxGame instance
        if (done) return;
        
        for (Box b1: boxes)
            for (Box b2: boxes) {
                if (b1 != b2)
                    b1.link(b2); // Need not do it reversely.
            }

        for (Box b : boxes)
            for (int dim = 0; dim < 3; dim++)
                visit(b, dim, 1);
        done = true;
    }
    
    private void visit(Box b, int dim, int depth) {
        PathNode pathnode = new PathNode(b, dim);
//        System.out.println("Visit box: " + pathnode.toString() + " depth " + depth);
        path.add(pathnode);
        boxStack.add(b);
        // Calculate the total weigh
        sumWeigh += (b.weigh[dim] * boxCounter.get(b));
        if (sumWeigh > maxWeigh) {
            maxWeighPaths.clear(); // clear old saved paths.
            ArrayList<PathNode> list = new ArrayList<>(path);
            maxWeighPaths.add(list);
            maxWeigh = sumWeigh;
        } else if (sumWeigh == maxWeigh) {
            ArrayList<PathNode> list = new ArrayList<>(path);
            maxWeighPaths.add(list);
        }

        if (depth < boxes.size()) {
            // Visit the following Box nodes of this dimension
            LinkNode p = b.follows[dim].next;
            while (p != null) {
                if (! boxStack.contains(p.box)) { // this box not visited
                    visit(p.box, p.face, depth + 1);
                }
                p = p.next;
            }
        }
        path.remove(pathnode);
        boxStack.remove(b);
        sumWeigh -= (b.weigh[dim] * boxCounter.get(b));
    }
}

class Box {
    int l, w, h;
    int[] weigh;
    LinkNode[] follows; // an empty forenode.

    Box(int l1, int l2, int l3) {
        l = l1;
        w = l2;
        h = l3;
        weigh = new int[3];
        weigh[0] = l3;
        weigh[1] = l2;
        weigh[2] = l1;
        follows = new LinkNode[3];
        follows[0]  = new LinkNode();
        follows[1]  = new LinkNode();
        follows[2]  = new LinkNode();
    }
   
    boolean equals(int l1, int l2, int l3) {
        return (((l == l1) && (w == l2) && (h == l3)) ||
            ((l == l1) && (w == l3) && (h == l2)) ||
            ((l == l2) && (w == l1) && (h == l3)) ||
            ((l == l2) && (w == l3) && (h == l1)) ||
            ((l == l3) && (w == l1) && (h == l2)) ||
            ((l == l3) && (w == l2) && (h == l1)));
    }
    
    int getLongest() {
        int max = (l > w) ? l : w;
        return ((max > h) ? max : h);
    }

    void link(Box box2) {
        LinkNode p;
        
        // Box 0 vs Box2 0,1,2
        p = follows[0];
        while (p.next != null)
            p = p.next;
            
        if (((l >= box2.l) && (w >= box2.w)) ||
            ((l >= box2.w) && (w >= box2.l))) {
            LinkNode node = new LinkNode(box2, 0);
            p.next = node;
            p = node;
        }
            
        if (((l >= box2.l) && (w >= box2.h)) ||
            ((l >= box2.h) && (w >= box2.l))) {
            LinkNode node = new LinkNode(box2, 1);
            p.next = node;
            p = node;
        }
            
        if (((l >= box2.w) && (w >= box2.h)) ||
            ((l >= box2.h) && (w >= box2.w))) {
            LinkNode node = new LinkNode(box2, 2);
            p.next = node;
        }
        
        // Box 1 vs Box2 0,1,2
        p = follows[1];
        while (p.next != null)
            p = p.next;
            
        if (((l >= box2.l) && (h >= box2.w)) ||
            ((l >= box2.w) && (h >= box2.l))) {
            LinkNode node = new LinkNode(box2, 0);
            p.next = node;
            p = node;
        }
            
        if (((l >= box2.l) && (h >= box2.h)) ||
            ((l >= box2.h) && (h >= box2.l))) {
            LinkNode node = new LinkNode(box2, 1);
            p.next = node;
            p = node;
        }
            
        if (((l >= box2.w) && (h >= box2.h)) ||
            ((l >= box2.h) && (h >= box2.w))) {
            LinkNode node = new LinkNode(box2, 2);
            p.next = node;
        }
        
        // Box 1 vs Box2 0,1,2
        p = follows[2];
        while (p.next != null)
            p = p.next;
            
        if (((w >= box2.l) && (h >= box2.w)) ||
            ((w >= box2.w) && (h >= box2.l))) {
            LinkNode node = new LinkNode(box2, 0);
            p.next = node;
            p = node;
        }
            
        if (((w >= box2.l) && (h >= box2.h)) ||
            ((w >= box2.h) && (h >= box2.l))) {
            LinkNode node = new LinkNode(box2, 1);
            p.next = node;
            p = node;
        }
            
        if (((w >= box2.w) && (h >= box2.h)) ||
            ((w >= box2.h) && (h >= box2.w))) {
            LinkNode node = new LinkNode(box2, 2);
            p.next = node;
        }
    }
}

class LinkNode {
    Box box;
    int face; // 0, 1, 2
    LinkNode next;
    
    LinkNode() { // For empty head
        box = null;
        face = -1;
        next = null;
    }
        
    LinkNode(Box b, int f) {
        box = b;
        face = f;
        next = null;
    }
}

class PathNode {
    Box box;
    int face; // 0, 1, 2
    
    PathNode(Box b, int f) {
        box = b;
        face = f;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        switch (face) {
            case 0:
                sb.append("(").append(box.l).append(", ").append(box.w).append(") h:").append(box.h);
                break;
            case 1:
                sb.append("(").append(box.l).append(", ").append(box.h).append(") h:").append(box.w);
                break;
            case 2:
                sb.append("(").append(box.w).append(", ").append(box.h).append(") h:").append(box.l);
                break;
        }
        return sb.toString();
    }
}
