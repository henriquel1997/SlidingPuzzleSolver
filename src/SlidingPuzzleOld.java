import java.util.ArrayList;
import java.util.List;

public class SlidingPuzzleOld {

    private TreeNode root;

    class TreeNode{

        final static int maxDepth = 30;

        TreeNode parent;
        int[][] state;
        int n;
        int zeroX;
        int zeroY;
        int totalDistance = 0;
        int pieceMoved;
        int depth;

        List<int[][]> seenStates;
        List<TreeNode> nextNodes = new ArrayList<>();

        TreeNode(int[][] puzzle, TreeNode parent, int pieceMoved, int zeroX, int zeroY, List<int[][]> seenStates, int depth){
            this.parent = parent;
            state = puzzle;
            n = puzzle.length;
            this.pieceMoved = pieceMoved;
            this.zeroX = zeroX;
            this.zeroY = zeroY;
            this.depth = depth;
            this.seenStates = seenStates;
            seenStates.add(state);

            for(int x = 0; x < n; x++){
                for(int y = 0; y < n; y++){
                    if(puzzle[y][x] > 0){
                        totalDistance += distanceToGoal(puzzle[y][x], x, y);
                    }
                }
            }
        }

        List<Integer> generateAndSearchTree(){

            if(totalDistance == 0){
                List<Integer> list = new ArrayList<>();
                list.add(pieceMoved);
                return list;
            }

            if(depth + 1 > maxDepth){
                return null;
            }

            if(zeroX - 1 >= 0){
                swapWithZeroAndCreateNode(zeroX - 1, zeroY);
            }

            if(zeroX + 1 < n){
                swapWithZeroAndCreateNode(zeroX + 1, zeroY);
            }

            if(zeroY - 1 >= 0){
                swapWithZeroAndCreateNode(zeroX, zeroY - 1);
            }

            if(zeroY + 1 < n){
                swapWithZeroAndCreateNode(zeroX, zeroY + 1);
            }

            for (TreeNode node: nextNodes) {
                List<Integer> list = node.generateAndSearchTree();
                if(list != null){
                    list.add(0, pieceMoved);
                    return list;
                }
            }

            return null;
        }

        private void swapWithZeroAndCreateNode(int x, int y){
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
            for (int[][] state: seenStates) {
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
                TreeNode newNode = new TreeNode(newState, this, newState[zeroY][zeroX], x, y, seenStates, depth + 1);

                //Insertion sort
                int pos = 0;
                while(pos < nextNodes.size() && nextNodes.get(pos).totalDistance < newNode.totalDistance){
                    pos++;
                }
                nextNodes.add(pos, newNode);
            }
        }

        private int distanceToGoal(int number, int x, int y){
            int goalX = (number-1)%n;
            int goalY = (number-1)/n;
            return  Math.abs(goalX - x) + Math.abs(goalY - y);
        }
    }

    public SlidingPuzzleOld(int[][] puzzle) {
        for(int x = 0; x < puzzle.length; x++){
            for(int y = 0; y < puzzle.length; y++){
                if(puzzle[y][x] == 0){
                    root = new TreeNode(puzzle, null, -1, x, y, new ArrayList<>(), 0);
                    break;
                }
            }
        }
    }

    public List<Integer> solve() {
        if(isSolvable()){
            List<Integer> list = root.generateAndSearchTree();
            if(list != null) {
                list.remove(0);
            }
            return list;
        }
        return null;
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
}