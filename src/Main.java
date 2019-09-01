import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        new Main(); // wtf ?

    }

    public Main() throws FileNotFoundException {
        //TODO - do this for all sequences from seq1.fasta & seq2.fasta
        readSequenceFromFile(filename1, sequences1);
        readSequenceFromFile(filename2, sequences2);
        String seq1=removeLetters(String.join("",sequences1));
        String seq2=removeLetters(String.join("",sequences2));
        calculateTable(true,seq1,seq2,scoreMatrix,gapCost);
        String[] result = getAlignment(seq1,seq2);
        System.out.println(result[0] + '\n' + result[1]);


        calculateTable(true, "AATAAT", "AAGG", scoreMatrix, gapCost);

        //table=new int[][]{{0,1,2,3,4},{1,0,1,2,3},{2,1,1,2,3},{3,2,2,2,2},{4,3,3,3,3}};
        result = getAlignment("AATAAT", "AAGG");
        System.out.println(result[0] + '\n' + result[1]);


    }

    private enum Operation {
        SUB, INS, DEL
    }

    private static Map<Character, Integer> charMapping = new HashMap() {{
        put('A', 0);
        put('C', 1);
        put('G', 2);
        put('T', 3);
    }};
    private static int[][] scoreMatrix = new int[][]{
            {10, 2, 5, 2},
            {2, 10, 2, 5},
            {5, 2, 10, 2},
            {2, 5, 2, 10},

    };

    private static int gapCost = -5;

    private static int[][] resultTable;
    private static Operation[][] paths;
    private static ArrayList<String> sequences1 = new ArrayList<>();
    private static ArrayList<String> sequences2 = new ArrayList<>();

    private static String filename1 = "/home/n/Downloads/seq1.fasta";
    private static String filename2 = "/home/n/Downloads/seq2.fasta";

    public static String removeLetters(String seq){
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<seq.length();i++){
            if(charMapping.containsKey(seq.charAt(i))){
                stringBuilder.append(seq.charAt(i));
            }
        }
        return stringBuilder.toString();
    }

    public static void calculateTable(boolean maximizing, String seq1, String seq2, int[][] scoreMatrix, int gap) {
        int[][] resultedMatrix = new int[seq2.length() + 1][seq1.length() + 1];
        paths = new Operation[seq2.length()+1][seq1.length()+1];

        resultedMatrix[0][0] = 0;
        for (int i = 1; i < (seq1.length() > seq2.length() ? seq1.length() + 1 : seq2.length() + 1); i++) {
            if (i < seq1.length() + 1) {
                paths[0][i] = Operation.DEL;
                resultedMatrix[0][i] = resultedMatrix[0][i - 1] + gap;
            }
            if (i < seq2.length() + 1) {
                paths[i][0] = Operation.INS;
                resultedMatrix[i][0] = resultedMatrix[i - 1][0] + gap;
            }
        }
        for (int row = 1; row < resultedMatrix.length; row++) {
            for (int column = 1; column < resultedMatrix[0].length; column++) {
                int subcost = scoreMatrix[charMapping.get(seq2.charAt(row - 1))][charMapping.get(seq1.charAt(column - 1))];
                int expectedValue = subcost + resultedMatrix[row - 1][column - 1];
                Operation op = Operation.SUB;
                if (maximizing) {
                    if (expectedValue < (resultedMatrix[row - 1][column] + gap)) {
                        expectedValue = resultedMatrix[row - 1][column] + gap;
                        op = Operation.INS;
                    }
                    if (expectedValue < (resultedMatrix[row][column - 1] + gap)) {
                        op = Operation.DEL;
                        expectedValue = resultedMatrix[row][column - 1] + gap;
                    }
                } else {
                    if (expectedValue > (resultedMatrix[row - 1][column] + gap)) {
                        op = Operation.INS;
                        expectedValue = resultedMatrix[row - 1][column] + gap;
                    }
                    if (expectedValue > (resultedMatrix[row][column - 1] + gap)) {
                        op = Operation.DEL;
                        expectedValue = resultedMatrix[row][column - 1] + gap;
                    }
                }
                resultedMatrix[row][column] = expectedValue;
                paths[row][column] = op;
            }
        }

        resultTable = resultedMatrix;
        printMatrix(resultTable);

//        ArrayList<Operation> asd = new ArrayList<>();
//        backtracking(resultTable.length - 1, resultTable[0].length - 1, seq1, seq2, asd);
//
//
//        for (int i = 0; i < asd.size(); i++) {
//            paths[0][i] = asd.get(i);
//            System.out.println(paths[0][i]);
//        }

    }

    private static void printMatrix(int[][] resultedMatrix) {
        for (int i = 0; i < resultedMatrix.length; i++) {
            for (int j = 0; j < resultedMatrix[i].length; j++) {
                System.out.print(resultedMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }


    public static int getLastValueFromMatrix() {
        return resultTable.length > 0 ? resultTable[resultTable.length - 1][resultTable[0].length - 1] : 0;
    }

    public static void readSequenceFromFile(String filename, ArrayList<String> sequence) throws FileNotFoundException {
        File file = new File(filename);
        Scanner sc = new Scanner(file);
        sc.useDelimiter("\n");
        while (sc.hasNext()) {
            sequence.addAll(Arrays.asList(sc.next().split(" ")));
        }
    }
//Needleman–Wunsch algorithm
//    public static void backtracking(int i, int j, String seq1, String seq2, ArrayList<Operation> currentSol) {
//        if (i < 0 || j < 0) return;
//        if (i > 0 && j > 0 && resultTable[i][j] == resultTable[i - 1][j - 1] + scoreMatrix[charMapping.get(seq2.charAt(i - 1))][charMapping.get(seq1.charAt(j - 1))]) {
//            currentSol.add(Operation.SUB);
//            backtracking(i - 1, j - 1, seq1, seq2, currentSol);
//        } else if (i > 0 && resultTable[i][j] == resultTable[i - 1][j] + gapCost) {
//            currentSol.add(Operation.INS);
//            backtracking(i - 1, j, seq1, seq2, currentSol);
//        } else if (j > 0 && resultTable[i][j] == resultTable[i][j - 1] + gapCost) {
//            currentSol.add(Operation.DEL);
//            backtracking(i, j - 1, seq1, seq2, currentSol);
//        }
//    }


    public String[] getAlignment(String seq1,String seq2) {
        int x = resultTable.length - 1;
        int y = resultTable[0].length - 1;
        StringBuilder result1 = new StringBuilder();
        StringBuilder result2 = new StringBuilder();
        while (x > 0 || y > 0) {
            if (paths[x][y] == Operation.SUB) {
                result1.insert(0, seq2.charAt(x - 1));
                result2.insert(0, seq1.charAt(y - 1));
                x--;
                y--;
            } else if (paths[x][y] == Operation.DEL) {
                result1.insert(0, '_');
                result2.insert(0, seq1.charAt(y - 1));
                y--;
            } else if (paths[x][y] == Operation.INS) {
                result1.insert(0, seq2.charAt(x - 1));
                result2.insert(0, '_');
                x--;
            }
        }
        return new String[]{result1.toString(), result2.toString()};
    }
}


