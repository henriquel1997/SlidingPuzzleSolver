import java.util.List;

public class Main {

    public static void main(String[] args) {
//        int[][] board = {{1, 2, 3, 4},
//                         {5, 0, 6, 8},
//                         {9,10, 7,11},
//                         {13,14,15,12}};
//
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

//        int[][] board = {{ 8,  7,  6 }, //132 milisegundos
//                         { 4,  2,  5 },
//                         { 1,  3,  0 }};

//        int[][] board = {{ 6, 14,  4,  9}, //5 segundos
//                         {10,  2,  3,  0},
//                         {12,  7,  8,  5},
//                         {15,  1, 13, 11}};

//        int[][] board = {{ 9,  0, 14,  2}, //61 Segundos
//                         {10,  5,  3,  1},
//                         {13, 11,  7, 12},
//                         { 6, 15,  8,  4}};

//        int[][] board = {{  5,  4,  8, },
//                         {  2,  0,  1, },
//                         {  7,  6,  3, }};

//        int[][] board = {{  2,  1,  7, },
//                         {  3,  5,  8, },
//                         {  4,  6,  0, }};

//        int[][] board = {{ 2,  7,  5, 15},
//                         { 0, 14,  3,  1},
//                         { 9,  4, 11, 10},
//                         {12, 13,  8,  6}};

//        int[][] board = {{11, 12, 15,  3},
//                         {14, 13,  9,  5},
//                         { 0,  7, 10,  2},
//                         { 4,  8,  6, 1}};
//
//        int[][] board = {{11, 12, 15,  3},
//                         {14, 13,  9,  5},
//                         { 0,  7, 10,  2},
//                         { 4,  8,  6, 1}}

//        int[][] board = {{14,  2, 15,  0},  //180 segundos, Muito lento
//                         { 4,  3,  1, 12},
//                         {10,  7,  6,  9},
//                         {13, 11,  5,  8}};

//        int[][] board = {{1,   2,  3,  4},
//                         {14,  7,  0, 15},
//                         {10,  6,  9, 12},
//                         {13, 11,  5,  8}};

//        int[][] board = {{ 1,  2,  3,  4},
//                         { 5,  6,  7,  8},
//                         {14, 11,  0, 10},
//                         {13,  9, 15, 12}};

//        int[][] board = {{14, 11,  2,  9},
//                         { 6, 15, 13, 12},
//                         { 5,  8,  3,  0},
//                         { 1,  4,  7, 10}};

//        int[][] board = {{1, 2,  3,  4},
//                         { 5, 6, 0, 15},
//                         { 9,  8,  11,  14},
//                         { 13,  7,  10, 12}};

//        int[][] board = {{14, 11,  2,  9},
//                         { 6, 15, 13, 12},
//                         { 5,  8,  3,  0},
//                         { 1,  4,  7, 10}};

//        int[][] board = {{ 7,  9,  6,  3},
//                         { 2,  4, 15, 11},
//                         { 1, 14, 12,  0},
//                         { 5, 13, 10,  8}};

//        int[][] board = {{16,  4, 15, 22,  5},
//                         { 1, 23,  3, 12, 10},
//                         { 6, 11,  9,  2,  7},
//                         {24, 17, 13, 20,  0},
//                         {21,  8, 14, 19, 18}};

                int[][] board = {{14,  6, 11,  0}, //TODO: Fica preso na hora de mover o 9
                                 {12,  1,  8,  3},
                                 { 5,  2,  9, 10},
                                 { 4, 15, 13,  7}};

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
