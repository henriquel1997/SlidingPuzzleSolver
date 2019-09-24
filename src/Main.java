import java.util.List;

public class Main {

    public static void main(String[] args) {
//        int[][] board = {{1, 2, 3, 4},
//                         {5, 0, 6, 8},
//                         {9,10, 7,11},
//                         {13,14,15,12}};

//        int[][] board = {{ 4,  1,  3 },
//                         { 2,  8,  0 },
//                         { 7,  6,  5 }};

//        int[][] board = {{ 0,  6,  2 },
//                         { 7,  1,  3 },
//                         { 8,  5,  4 }};

//        int[][] board = {{ 5,  2,  3 },
//                         { 8,  0,  6 },
//                         { 4,  7,  1 }};

//        int[][] board = {{ 2,  8,  1 },
//                         { 6,  7,  4 },
//                         { 3,  0,  5 }};
//
//        int[][] board = {{11,  6,  4, 12}, //28 Segundos, Muito lento
//                         {14,  5,  0,  1},
//                         {13, 10,  3,  8},
//                         {15,  2,  9,  7}};

//        int[][] board = {{ 8,  7,  6 },
//                         { 4,  2,  5 },
//                         { 1,  3,  0 }};

//        int[][] board = {{14,  2, 15,  0},  //180 segundos, Muito lento
//                         { 4,  3,  1, 12},
//                         {10,  7,  6,  9},
//                         {13, 11,  5,  8}};
//
        int[][] board = {{ 6, 14,  4,  9}, //5 segundos
                         {10,  2,  3,  0},
                         {12,  7,  8,  5},
                         {15,  1, 13, 11}};

//        int[][] board = {{ 9,  0, 14,  2}, //61 Segundos
//                         {10,  5,  3,  1},
//                         {13, 11,  7, 12},
//                         { 6, 15,  8,  4}};

        long inicio = System.currentTimeMillis();
        List<Integer> steps = new SlidingPuzzle(board).solve();
        long fim = System.currentTimeMillis();

        if(steps != null){
            for (Integer move: steps) {
                System.out.println(move);
            }
            System.out.println("Número de passos: "+ steps.size());
        }else{
            System.out.println("Null");
        }

        System.out.println("Tempo: " + (fim - inicio) + " milisegundos");

    }
}
