import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SlidingPuzzle {

    private TreeNode root;

    class TreeNode{

        TreeNode parent;
        int[][] state;
        int n;
        int zeroX;
        int zeroY;
        int totalDistance = 0;
        int pieceMoved;
        int depth;

        TreeNode(int[][] puzzle, TreeNode parent, int pieceMoved, int zeroX, int zeroY, int depth){
            this.parent = parent;
            state = puzzle;
            n = puzzle.length;
            this.pieceMoved = pieceMoved;
            this.zeroX = zeroX;
            this.zeroY = zeroY;
            this.depth = depth;

            for(int x = 0; x < n; x++){
                for(int y = 0; y < n; y++){
                    if(puzzle[y][x] > 0){
                        totalDistance += distanceToGoal(puzzle[y][x], x, y);
                    }
                }
            }
        }

        List<TreeNode> generateNextNodes(){
            List<TreeNode> nextNodes = new ArrayList<>();

            if(zeroX - 1 >= 0 && state[zeroY][zeroX - 1] != pieceMoved){
                TreeNode node = movePiece(zeroX - 1, zeroY);
                if(node != null){
                    insertionSort(nextNodes, node);
                }
            }

            if(zeroX + 1 < n && state[zeroY][zeroX + 1] != pieceMoved){
                TreeNode node = movePiece(zeroX + 1, zeroY);
                if(node != null){
                    insertionSort(nextNodes, node);
                }
            }

            if(zeroY - 1 >= 0 && state[zeroY - 1][zeroX] != pieceMoved){
                TreeNode node = movePiece(zeroX, zeroY - 1);
                if(node != null){
                    insertionSort(nextNodes, node);
                }
            }

            if(zeroY + 1 < n && state[zeroY + 1][zeroX] != pieceMoved){
                TreeNode node = movePiece(zeroX, zeroY + 1);
                if(node != null){
                    insertionSort(nextNodes, node);
                }
            }

            return nextNodes;
        }

        private TreeNode movePiece(int x, int y){
            int[][] newState = new int[n][n];
            for(int i = 0; i < n; i++){
                for(int j = 0; j < n; j++){
                    newState[i][j] = state[i][j];
                }
            }

            newState[zeroY][zeroX] = newState[y][x];
            newState[y][x] = 0;

            //Check if state was already seen
            boolean wasStateSeen = false;
            for (TreeNode node = parent; node != null; node = node.parent) {
                int[][] state = node.state;

                int equalPieces = 0;
                for(int i = 0; i < n; i++){
                    for(int j = 0; j < n; j++){
                        if(state[i][j] == newState[i][j]){
                            equalPieces++;
                        }
                    }
                }

                if(equalPieces == (n*n)){
                    wasStateSeen = true;
                    break;
                }
            }

            if(!wasStateSeen){
                return new TreeNode(newState, this, newState[zeroY][zeroX], x, y, depth + 1);
            }

            return null;
        }

        private int distanceToGoal(int number, int x, int y){
            int goalX = (number-1)%n;
            int goalY = (number-1)/n;
            return  Math.abs(goalX - x) + Math.abs(goalY - y);
        }

        List<Integer> getSteps(){
            TreeNode current = this;
            List<Integer> steps = new ArrayList<>();
            while(current.pieceMoved != -1){
                steps.add(current.pieceMoved);
                current = current.parent;
            }

            Collections.reverse(steps);

            return steps;
        }

        int getCost(){
            return totalDistance + depth;
        }
    }

    public SlidingPuzzle(int[][] puzzle) {
        for(int x = 0; x < puzzle.length; x++){
            for(int y = 0; y < puzzle.length; y++){
                if(puzzle[y][x] == 0){
                    root = new TreeNode(puzzle, null, -1, x, y, 0);
                    break;
                }
            }
        }
    }

    public List<Integer> solve() {
        if(isSolvable()) {
            return idaStar();
        }

        return null;
    }

    private List<Integer> idaStar(){
        int bound = root.totalDistance;
        while(true){
            System.out.println("Bound: " + bound);
            TreeNode result = searchIdaStar(root, bound);
            if(result.totalDistance == 0) return result.getSteps();
            bound = result.getCost();
        }
    }

    private TreeNode searchIdaStar(TreeNode node, int bound){
        if(node.getCost() > bound || node.totalDistance == 0) return node;

        TreeNode minCostNode = null;
        for(TreeNode child : node.generateNextNodes()){
            TreeNode result = searchIdaStar(child, bound);
            if(result != null){
                if(result.totalDistance == 0) return result;
                if(minCostNode == null || child.getCost() < minCostNode.getCost()) {
                    minCostNode = result;
                }
            }
        }

        return minCostNode;
    }

    private boolean isSolvable(){
        //Even Grid with Zero in Even Row
        if(root.n % 2 == 0){
            if(root.zeroY % 2 == 0){
                return getNumberOfInversions() % 2 != 0;
            }
        }

        //Odd Grid or Even with Zero in Odd Row
        return getNumberOfInversions() % 2 == 0;
    }

    private int getNumberOfInversions(){
        int[] array = new int[root.n * root.n];
        int pos = 0;
        for(int x = 0; x < root.n; x++) {
            for (int y = 0; y < root.n; y++) {
                array[pos++] = root.state[x][y];
            }
        }

        int inversions = 0;
        for(int i = 0; i < array.length - 1; i++){
            if(array[i] > 0){
                for(int j = i+1; j < array.length; j++){
                    if(array[j] > 0 && array[i] > array[j]){
                        inversions++;
                    }
                }
            }
        }

        return inversions;
    }

    //** Assumes that the list is sorted **//
    private static void insertionSort(List<TreeNode> list, TreeNode node){
        int pos = 0;
        for(int i = 0; i < list.size() && node.getCost() >= list.get(i).getCost(); i++){
            pos++;
        }
        list.add(pos, node);
    }
}