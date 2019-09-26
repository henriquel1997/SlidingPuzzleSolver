import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SlidingPuzzle {

    private TreeNode root;
    private List<Integer> cantMove = new ArrayList<>();

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
                TreeNode node = new TreeNode(newState, this, newState[zeroY][zeroX], x, y, depth + 1, distanceType, regularDistance);
                return node;
            }

            return null;
        }

        private int distanceToGoal(int number, int x, int y){

            int goalX = (number-1)%n;
            int goalY = (number-1)/n;

            if(!regularDistance && distanceType > 0 && number == distanceType){
                if(number == n){
                    //Ultimo numero na posicao do antepenultimo
                    goalX--;
                }else if(number == n - 1){
                    //Antepenultimo número embaixo do último
                    goalY++;
                }else if((number - 1) % n == 0){
                    //Mesma coisa mas para a coluna
                    if(number - 1 == (n*(n - 1))){
                        goalY--;
                    }else if(number - 1 == (n*(n - 2))){
                        goalX++;
                    }
                }
            }

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
                    root = new TreeNode(puzzle, null, -1, x, y, 0, TreeNode.TOTAL_DISTANCE, false);
                    break;
                }
            }
        }
    }

    public List<Integer> solve() {
        if(isSolvable()) {
            List<Integer> sequence = new ArrayList<>();

            if(root.n > 3){
                //int[] seq = new int[]{ 1, 2, 4, 3, 5, 13, 9 };
                int index = 0;
                int[] seq = new int[2 * root.n - 1];
                for(int i = 1; i <= root.n; i++){
                    seq[index++] = i;
                }

                int aux = seq[index - 1];
                seq[index - 1] = seq[index - 2];
                seq[index - 2] = aux;

                for(int i = root.n + 1; i - 1 <= root.n * (root.n - 1); i += root.n){
                    seq[index++] = i;
                }

                aux = seq[index - 1];
                seq[index - 1] = seq[index - 2];
                seq[index - 2] = aux;

                for(int i = 0; i < seq.length; i++){
                    root.distanceType = seq[i];
                    root.calculateDistance();
                    root = idaStar();
                    root.showState();
                    sequence.addAll(root.getSteps());
                    root.parent = null;
                    root.pieceMoved = -1;
                    cantMove.add(seq[i]);

                    if(seq[i] == root.n - 1 || seq[i] - 1 == (root.n * (root.n-2))){
                        if(!root.regularDistance){
                            i -= 2;
                            cantMove.remove(cantMove.size() - 2);
                        }

                        root.regularDistance = !root.regularDistance;
                    }else if(root.regularDistance){
                        cantMove.remove(cantMove.size() - 2);
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

                    for(Integer move: new SlidingPuzzle(reducedBoard).solve()){
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
}