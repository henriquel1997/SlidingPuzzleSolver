import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SlidingPuzzle {

    private TreeNode root;
    private List<Integer> cantMove = new ArrayList<>();

    enum Direction {
        VERTICAL,
        HORIZONTAL
    }

    class TreeNode{

        TreeNode parent;
        int[][] state;
        int n;
        int zeroX;
        int zeroY;
        int totalDistance;
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

            calculateDistance();
        }

        void calculateDistance(){
            totalDistance = 0;

            for(int x = 0; x < n; x++){
                for(int y = 0; y < n; y++){
                    int number = state[y][x];
                    if(number > 0){
                        totalDistance += distanceToGoal(number, x, y);
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
                return new TreeNode(newState, this, newState[zeroY][zeroX], x, y, depth + 1);
            }

            return null;
        }

        private int distanceToGoal(int number, int x, int y){
            int goalX = goalXOf(number, n);
            int goalY = goalYOf(number, n);
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
            List<Integer> sequence = new ArrayList<>();

            if(root.totalDistance == 0) return sequence;

            if(root.n > 3){
                int[] seqRow = new int[root.n];
                for(int i = 0; i < root.n; i++){
                    seqRow[i] = i + 1;
                }

                int index = 0;
                int[] seqColumn = new int[root.n - 1];
                for(int i = root.n + 1; i - 1 <= root.n * (root.n - 1); i += root.n){
                    seqColumn[index++] = i;
                }

                solveSequence(seqRow);
                solveSequence(seqColumn);

                sequence.addAll(root.getSteps());
                root.parent = null;
                root.pieceMoved = -1;

                if(root.n > 4){
                    int[][] newBoard = new int[root.n - 1][root.n - 1];

                    for(int x = 1; x < root.state.length; x++) {
                        for (int y = 1; y < root.state.length; y++) {
                            int number = root.state[y][x];
                            if(number > 0){
                                newBoard[y - 1][x - 1] = number - root.n - goalYOf(number, root.n);
                            }
                        }
                    }

                    List<Integer> moves = new SlidingPuzzle(newBoard).solve();

                    if(moves != null){
                        for(int move: moves){
                            sequence.add(move + root.n + goalYOf(move, root.n - 1) + 1);
                        }
                        return sequence;
                    }

                    return null;
                }
            }

            root = idaStar();
            sequence.addAll(root.getSteps());

            return sequence;
        }
        return null;
    }

    private void solveSequence(int[] seq){
        for(int i = 0; i < seq.length - 2; i++) {
            int number = seq[i];
            moveNumberToGoal(number, goalXOf(number, root.n), goalYOf(number, root.n));
        }

        int last = seq[seq.length - 1];
        moveNumberToGoal(last, root.n - 1, root.n - 1);
        cantMove.remove(cantMove.size() - 1);

        int secondToLast = seq[seq.length - 2];
        if(secondToLast <= root.n){
            moveNumberToGoal(secondToLast, goalXOf(secondToLast, root.n) + 1, goalYOf(secondToLast, root.n));
            moveNumberToGoal(last, goalXOf(last, root.n), goalYOf(last, root.n) + 1);
        }else{
            moveNumberToGoal(secondToLast, goalXOf(secondToLast, root.n), goalYOf(secondToLast, root.n) + 1);
            moveNumberToGoal(last, goalXOf(last, root.n) + 1, goalYOf(last, root.n));
        }

        cantMove.remove(cantMove.size() - 1);
        cantMove.remove(cantMove.size() - 1);

        moveNumberToGoal(secondToLast, goalXOf(secondToLast, root.n), goalYOf(secondToLast, root.n));
        moveNumberToGoal(last, goalXOf(last, root.n), goalYOf(last, root.n));
    }

    private void moveNumberToGoal(int number, int goalX, int goalY){
        cantMove.add(number);

        if(root.state[goalY][goalX] == number){
            return;
        }

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

        int dirX = Integer.signum(goalX - numberX);
        int dirY = Integer.signum(goalY - numberY);

        Direction direction = Direction.VERTICAL;
        int inicioZeroX = numberX;
        int inicioZeroY = numberY + dirY;
        if((number <= root.n && numberX != goalX) || numberY == goalY){
            direction = Direction.HORIZONTAL;
            inicioZeroX = numberX + dirX;
            inicioZeroY = numberY;
        }

        root = getShortestPathFromZeroTo(inicioZeroX, inicioZeroY);

        moveNumberToGoal(direction, numberX, numberY, dirX, dirY, goalX, goalY);
    }

    private TreeNode getShortestPathFromZeroTo(int x, int y){
        List<TreeNode> list = new ArrayList<>();
        list.add(root);
        do{
            TreeNode current = list.remove(0);

            int dist = manhattanDistance(current.zeroX, current.zeroY, x, y);
            if(dist == 0){
                return current;
            }

            for(TreeNode node: current.generateNextNodes()){
                int pos = 0;
                int distNode = manhattanDistance(node.zeroX, node.zeroY, x, y);
                for(int j = 0; j < list.size() && distNode > manhattanDistance(list.get(j).zeroX, list.get(j).zeroY, x, y); j++){
                    pos++;
                }
                list.add(pos, node);
            }
        }while (list.size() > 0);

        return null;
    }

    private void moveNumberToGoal(Direction coord, int numberX, int numberY, int dirX, int dirY, int goalX, int goalY){
        while((coord == Direction.HORIZONTAL && numberX != goalX) || (coord == Direction.VERTICAL && numberY != goalY)){
            //Move the number in the direction  of its goal in the given coordinate
            if(coord == Direction.VERTICAL){
                root = root.movePiece(root.zeroX, root.zeroY - dirY, false);
                numberY += dirY;

                if(numberY == goalY){
                    break;
                }

                //Move the empty space around the number
                moveEmptySpaceAround(coord, dirY);
            }else{
                root = root.movePiece(root.zeroX - dirX, root.zeroY, false);
                numberX += dirX;

                if(numberX == goalX){
                    break;
                }

                //Move the empty space around the number
                moveEmptySpaceAround(coord, dirX);
            }
        }

        if(numberX == goalX && numberY == goalY){
            return;
        }

        if(coord == Direction.HORIZONTAL){
            coord = Direction.VERTICAL;
            root = getShortestPathFromZeroTo(numberX, numberY + dirY);
        }else{
            coord = Direction.HORIZONTAL;
            root = getShortestPathFromZeroTo(numberX + dirX, numberY);
        }

        moveNumberToGoal(coord, numberX, numberY, dirX, dirY, goalX, goalY);
    }

    private void moveEmptySpaceAround(Direction coord, int dir){
        int[] seqAround = new int[]{1, 0, 0, -1, 0, -1, -1, 0}; //Up, reverse to go left

        if(dir > 0){
            for(int i = 2; i < seqAround.length - 2; i++){
                seqAround[i] = -seqAround[i];
            }
        }

        if(coord == Direction.HORIZONTAL){
            for(int i = 0; i < seqAround.length/2; i++){
                int aux = seqAround[seqAround.length - 1 - i];
                seqAround[seqAround.length - 1 - i] = seqAround[i];
                seqAround[i] = aux;
            }
        }

        TreeNode current = root;
        for(int i = 0; i < seqAround.length; i += 2){
            int nextX = current.zeroX + seqAround[i];
            int nextY = current.zeroY + seqAround[i + 1];

            if(nextX >= 0 && nextX < current.n &&
               nextY >= 0 && nextY < current.n &&
               !cantMove.contains(current.state[nextY][nextX])){

                current = current.movePiece(nextX, nextY, false);
            }else{
                i = -2;
                current = root;
                seqAround[0] = - seqAround[0];
                seqAround[1] = - seqAround[1];
                seqAround[seqAround.length - 1] = - seqAround[seqAround.length - 1];
                seqAround[seqAround.length - 2] = - seqAround[seqAround.length - 2];
            }
        }

        root = current;
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

    private int goalXOf(int number, int n){
        return (number-1)%n;
    }

    private int goalYOf(int number, int n){
        return (number-1)/n;
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