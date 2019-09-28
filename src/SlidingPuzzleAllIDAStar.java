import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SlidingPuzzleAllIDAStar {

    private TreeNode root;
    private List<Integer> cantMove = new ArrayList<>();
    private int goalXColumnLast = 1;
    private int goalYRowLast = 1;

    class TreeNode{

        TreeNode parent;
        int[][] state;
        int n;
        int zeroX;
        int zeroY;
        int totalDistance;
        int pieceMoved;
        int depth;
        int distanceType;
        boolean regularDistance;

        final static int TOTAL_DISTANCE = 0;

        TreeNode(int[][] puzzle, TreeNode parent, int pieceMoved, int zeroX, int zeroY, int depth, int distanceType, boolean regularDistance){
            this.parent = parent;
            state = puzzle;
            n = puzzle.length;
            this.pieceMoved = pieceMoved;
            this.zeroX = zeroX;
            this.zeroY = zeroY;
            this.depth = depth;
            this.distanceType = distanceType;
            this.regularDistance = regularDistance;

            calculateDistance();
        }

        void calculateDistance(){
            totalDistance = 0;

            if(distanceType == TOTAL_DISTANCE){
                for(int x = 0; x < n; x++){
                    for(int y = 0; y < n; y++){
                        int number = state[y][x];
                        if(number > 0){
                            totalDistance += distanceToGoal(number, x, y);
                        }
                    }
                }
            }else{
                for(int x = 0; x < n; x++){
                    for(int y = 0; y < n; y++){
                        int number = state[y][x];
                        if(number == distanceType){
                            totalDistance += distanceToGoal(number, x, y);
                        }
                    }
                }
            }
        }

        List<TreeNode> generateNextNodes(){
            List<TreeNode> nextNodes = new ArrayList<>();

            if(zeroX - 1 >= 0 && state[zeroY][zeroX - 1] != pieceMoved && !cantMove.contains(state[zeroY][zeroX - 1])){
                TreeNode node = movePiece(zeroX - 1, zeroY, true);
                if (node != null) {
                    insertionSort(nextNodes, node);
                }
            }

            if(zeroX + 1 < n && state[zeroY][zeroX + 1] != pieceMoved && !cantMove.contains(state[zeroY][zeroX + 1])){
                TreeNode node = movePiece(zeroX + 1, zeroY, true);
                if(node != null){
                    insertionSort(nextNodes, node);
                }
            }

            if(zeroY - 1 >= 0 && state[zeroY - 1][zeroX] != pieceMoved && !cantMove.contains(state[zeroY - 1][zeroX])){
                TreeNode node = movePiece(zeroX, zeroY - 1, true);
                if (node != null) {
                    insertionSort(nextNodes, node);
                }
            }

            if(zeroY + 1 < n && state[zeroY + 1][zeroX] != pieceMoved && !cantMove.contains(state[zeroY + 1][zeroX])){
                TreeNode node = movePiece(zeroX, zeroY + 1, true);
                if (node != null) {
                    insertionSort(nextNodes, node);
                }
            }

            return nextNodes;
        }

        TreeNode movePiece(int x, int y, boolean checkStates){
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

            if(checkStates){
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
            }

            if(!wasStateSeen){
                return new TreeNode(newState, this, newState[zeroY][zeroX], x, y, depth + 1, distanceType, regularDistance);
            }

            return null;
        }

        int goalXOf(int number){
            return (number-1)%n;
        }

        int goalYOf(int number){
            return (number-1)/n;
        }

        private int distanceToGoal(int number, int x, int y){

            int goalX = goalXOf(number);
            int goalY = goalYOf(number);

            if(!regularDistance && distanceType > 0 && number == distanceType){
                if(number == n){
                    //Last from row
                    goalY += goalYRowLast;
                }else if(number == n - 1){
                    //Before last from row
                    goalX++;
                }else if((number - 1) % n == 0){
                    if(number - 1 == (n*(n - 1))){
                        //Last from column
                        goalX += goalXColumnLast;
                    }else if(number - 1 == (n*(n - 2))){
                        //Before last from column
                        goalY++;
                    }
                }
            }

            return  manhattanDistance(x, y, goalX, goalY);
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

        void showState(){
            for(int x = 0; x < n; x++){
                for(int y = 0; y < n; y++){
                    if(y > 0){
                        System.out.print(",");
                    }

                    if(state[x][y] > 10){
                        System.out.print(state[x][y]);
                    }else{
                        System.out.print(" "+state[x][y]);
                    }
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public SlidingPuzzleAllIDAStar(int[][] puzzle) {
        for(int x = 0; x < puzzle.length; x++){
            for(int y = 0; y < puzzle.length; y++){
                if(puzzle[y][x] == 0){
                    root = new TreeNode(puzzle, null, -1, x, y, 0, TreeNode.TOTAL_DISTANCE, false);
                    break;
                }
            }
        }
    }

    public List<Integer> solve() {
        if(isSolvable()) {
            List<Integer> sequence = new ArrayList<>();

            if(root.totalDistance == 0) return sequence;

            if(root.n > 3){
                int index = 0;
                int[] seq = new int[2 * root.n - 1];
                for(int i = 1; i <= root.n; i++){
                    seq[index++] = i;
                }

                for(int i = root.n + 1; i - 1 <= root.n * (root.n - 1); i += root.n){
                    seq[index++] = i;
                }

                boolean areTheLastTwoFromRowSetUp = false;
                boolean areTheLastTwoFromColumnSetUp = false;

                for(int i = 0; i < seq.length; i++){
                    int number = seq[i];

                    goalXColumnLast = 1;
                    goalYRowLast = 1;

                    boolean shouldAddToCantMove = true;
                    if(!areTheLastTwoFromRowSetUp && number == root.n - 1){
                        boolean isLastFromRowFarAway = root.state[root.n -1][root.n -1] == root.n;
                        if(!isLastFromRowFarAway){
                            goalYRowLast = root.n - 1;
                            number = seq[i+1];
                            shouldAddToCantMove = false;
                            i--;
                        }
                    }else if(!areTheLastTwoFromColumnSetUp && number -1 == root.n * (root.n - 2)){
                        boolean isLastFromColumnFarAway = root.state[root.n -1][root.n -1] - 1 == (root.n * (root.n - 1));
                        if(!isLastFromColumnFarAway){
                            goalXColumnLast = root.n - 1;
                            number = seq[i+1];
                            shouldAddToCantMove = false;
                            i--;
                        }
                    }

//                    if(root.state[root.goalYOf(number)][root.goalXOf(number)] == number && shouldAddToCantMove){
//                        cantMove.add(number);
//                        continue;
//                    }
//
//                    sequence.addAll(moveZeroCloseToNumber(number));

                    root.distanceType = number;
                    root.calculateDistance();
                    root = idaStar();
                    root.showState();
                    sequence.addAll(root.getSteps());
                    root.parent = null;
                    root.pieceMoved = -1;

                    if(shouldAddToCantMove){
                        cantMove.add(number);

                        boolean isLastFromRow = number == root.n;
                        boolean isLastFromColumn = (number - 1) == (root.n * (root.n-1));
                        if(isLastFromRow || isLastFromColumn){
                            if(!root.regularDistance){
                                i -= 2;
                                cantMove.remove(cantMove.size() - 2);
                                if(isLastFromRow){
                                    areTheLastTwoFromRowSetUp = true;
                                }else{
                                    areTheLastTwoFromColumnSetUp = true;
                                }
                            }

                            root.regularDistance = !root.regularDistance;
                        }else if(root.regularDistance){
                            cantMove.remove(cantMove.size() - 2);
                        }
                    }
                }

                cantMove.clear();

                if(root.n > 4){
                    //TODO: Consertar essa conversão
                    int[][] reducedBoard = new int[root.n - 1][root.n - 1];
                    for(int x = 1; x < root.n; x++){
                        for(int y = 1; y < root.n; y++){
                            int number =  root.state[y][x];
                            if(number > 0){
                                reducedBoard[y - 1][x - 1] = root.state[y][x] - (root.n + 1 + (number/root.n));
                            }
                        }
                    }

                    for(Integer move: new SlidingPuzzleAllIDAStar(reducedBoard).solve()){
                        sequence.add(move + (root.n + 1 + (move/root.n)));
                    }

                    return sequence;
                }

                root.distanceType = TreeNode.TOTAL_DISTANCE;
                root.calculateDistance();
            }

            root = idaStar();
            sequence.addAll(root.getSteps());

            root.showState();

            return sequence;
        }
        return null;
    }

    private List<Integer> moveZeroCloseToNumber(int number){
        int closestX = Integer.MAX_VALUE;
        int closestY = Integer.MIN_VALUE;
        int closestDistance = Integer.MAX_VALUE;
        int numberX = -1;
        int numberY = -1;

        for(int x = 0; x < root.n; x++){
            for(int y = 0; y < root.n; y++){
                if(number == root.state[y][x]){
                    numberX = x;
                    numberY = y;
                    break;
                }
            }
        }

        if(numberX + 1 < root.n && !cantMove.contains(root.state[numberY][numberX + 1])) {
            int dis = manhattanDistance(root.zeroX, root.zeroY, numberX + 1, numberY);
            if(dis < closestDistance) {
                closestDistance = dis;
                closestX = numberX + 1;
                closestY = numberY;
            }
        }

        if(numberX - 1 >= 0 && !cantMove.contains(root.state[numberY][numberX - 1])) {
            int dis = manhattanDistance(root.zeroX, root.zeroY, numberX - 1, numberY);
            if(dis < closestDistance) {
                closestDistance = dis;
                closestX = numberX - 1;
                closestY = numberY;
            }
        }

        if(numberY + 1 < root.n && !cantMove.contains(root.state[numberY + 1][numberX])) {
            int dis = manhattanDistance(root.zeroX, root.zeroY, numberX, numberY + 1);
            if(dis < closestDistance) {
                closestDistance = dis;
                closestX = numberX;
                closestY = numberY + 1;
            }
        }

        if(numberY - 1 >= 0 && !cantMove.contains(root.state[numberY - 1][numberX])) {
            int dis = manhattanDistance(root.zeroX, root.zeroY, numberX, numberY - 1);
            if(dis < closestDistance) {
                closestDistance = dis;
                closestX = numberX;
                closestY = numberY - 1;
            }
        }

        int dir = Integer.signum(closestX - root.zeroX);
        while(root.zeroX != closestX){
            root = root.movePiece(root.zeroX + dir, root.zeroY, false);
        }

        dir = Integer.signum(closestY - root.zeroY);
        while(root.zeroY != closestY){
            root = root.movePiece(root.zeroX, root.zeroY + dir, false);
        }

        List<Integer> moves = root.getSteps();
        root.parent = null;
        root.pieceMoved = -1;

        return moves;
    }

    private TreeNode idaStar(){
        int bound = root.totalDistance;
        while(true){
            TreeNode result = searchIdaStar(root, bound);
            if(result.totalDistance == 0) return result;
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

    private static int manhattanDistance(int xFrom, int yFrom, int xTo, int yTo){
        return Math.abs(xTo - xFrom) + Math.abs(yTo - yFrom);
    }
}