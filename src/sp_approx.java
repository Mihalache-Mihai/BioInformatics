import java.util.ArrayList;

public class sp_approx {

    private static final int GAPCOST = 5;
    private static final boolean MAXIMIZING = false;


    public enum Operation {
        SUB, INS, DEL
    }


    private int[][] scoreMatrix;
    private ArrayList<Character> alphabet = new ArrayList<>();

    private static Operation[][] paths;

    public static void main(String[] args) {
        new sp_approx(args.length > 0 ? args[0] : "seq.fasta");
    }

    public sp_approx(String filename) {
        scoreMatrix = Main2.readPhyLip("score.txt", alphabet);
        ArrayList<String> names = new ArrayList();
        ArrayList<String> seqs = Main2.readMultipleSeqFile(filename, names);
        doAll(seqs, names);
    }


    public void doAll(ArrayList<String> sequences, ArrayList<String> names) {
        int[][] scoreTable = new int[sequences.size()][sequences.size()];
        calculateAllPairAlignments(sequences, scoreTable);
        int minimum = determineMinimalMiddle(scoreTable);
        ArrayList<StringBuilder> multiple = new ArrayList<>();
        for (int i = 0; i < sequences.size(); i++) {
            if (i != minimum) {
                linear_table(MAXIMIZING, sequences.get(minimum), sequences.get(i), this.scoreMatrix, GAPCOST);
                String[] aligns = getAlignment(sequences.get(minimum), sequences.get(i));
                extendAlignment(multiple, aligns[0], aligns[1]);
            }
        }
        Main2.writeSolutionToFile(multiple, names, minimum);
    }

    public void extendAlignment(ArrayList<StringBuilder> multiple, String middle, String newOne) {
        System.out.println("L: " + middle.length() + ";" + newOne.length());
        if (multiple.size() == 0) {
            multiple.add(new StringBuilder(middle));
            multiple.add(new StringBuilder(newOne));
            return;
        }
        StringBuilder newBuilder = new StringBuilder();
        int mIndex = 0;
        for (int i = 0; i < middle.length(); i++) {
            if (middle.charAt(i) == GAPCHAR) {
                newBuilder.append(newOne.charAt(i));
                for (int j = 0; j < multiple.size(); j++) {
                    multiple.get(j).insert(mIndex, GAPCHAR);
                }
                mIndex++;
            } else if (multiple.get(0).charAt(mIndex) == GAPCHAR) {
                mIndex++;
                i--;
                newBuilder.append(GAPCHAR);
            } else {
                mIndex++;
                newBuilder.append(newOne.charAt(i));
            }
        }
        while (mIndex < multiple.get(0).length()) {
            newBuilder.append(GAPCHAR);
            mIndex++;
        }
        multiple.add(newBuilder);
        for (int i = 0; i < multiple.size(); i++) {
            System.out.print(multiple.get(i).length() + " ;");
        }
        System.out.println();
    }

    public int determineMinimalMiddle(int[][] scoreTable) {
        int minIndex = -1;
        int minVal = Integer.MAX_VALUE;
        for (int i = 0; i < scoreTable.length; i++) {
            int cur = 0;
            for (int j = 0; j < scoreTable.length; j++) {
                cur += scoreTable[i][j];
            }
            if (cur < minVal) {
                minIndex = i;
                minVal = cur;
            }
        }
        return minIndex;
    }

    public void calculateAllPairAlignments(ArrayList<String> seqs, int[][] scoreTable) {
        for (int i = 0; i < seqs.size(); i++) {
            for (int j = i + 1; j < seqs.size(); j++) {
                int cost = linear_alignment_cost(MAXIMIZING, seqs.get(i), seqs.get(j), this.scoreMatrix, GAPCOST);
                scoreTable[i][j] = cost;
                scoreTable[j][i] = cost;
            }
        }
    }

    private static final char GAPCHAR = '-';

    public String[] getAlignment(String seq1, String seq2) {
        int x = seq2.length();
        int y = seq1.length();
        StringBuilder result1 = new StringBuilder();
        StringBuilder result2 = new StringBuilder();
        while (x > 0 || y > 0) {
            if (paths[x][y] == Operation.SUB) {
                result1.insert(0, seq2.charAt(x - 1));
                result2.insert(0, seq1.charAt(y - 1));
                x--;
                y--;
            } else if (paths[x][y] == Operation.DEL) {
                result1.insert(0, GAPCHAR);
                result2.insert(0, seq1.charAt(y - 1));
                y--;
            } else if (paths[x][y] == Operation.INS) {
                result1.insert(0, seq2.charAt(x - 1));
                result2.insert(0, GAPCHAR);
                x--;
            }
        }
        return new String[]{result2.toString(), result1.toString()};
    }

    public int[][] linear_table(boolean maximizing, String seq1, String seq2, int[][] scoreMatrix, int gap) {
        int[][] resultedMatrix = new int[seq2.length() + 1][seq1.length() + 1];
        paths = new Operation[seq2.length() + 1][seq1.length() + 1];

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
                int subcost = scoreMatrix[alphabet.indexOf(seq2.charAt(row - 1))][alphabet
                        .indexOf(seq1.charAt(column - 1))];
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

        return resultedMatrix;

    }

    public int linear_alignment_cost(boolean maximizing, String seq1, String seq2, int[][] scoreMatrix, int gap) {
        int[][] resultedMatrix = new int[seq2.length() + 1][seq1.length() + 1];

        resultedMatrix[0][0] = 0;
        for (int i = 1; i < (seq1.length() > seq2.length() ? seq1.length() + 1 : seq2.length() + 1); i++) {
            if (i < seq1.length() + 1) {
                resultedMatrix[0][i] = resultedMatrix[0][i - 1] + gap;
            }
            if (i < seq2.length() + 1) {
                resultedMatrix[i][0] = resultedMatrix[i - 1][0] + gap;
            }
        }
        for (int row = 1; row < resultedMatrix.length; row++) {
            for (int column = 1; column < resultedMatrix[0].length; column++) {
                int subcost = scoreMatrix[alphabet.indexOf(seq2.charAt(row - 1))][alphabet
                        .indexOf(seq1.charAt(column - 1))];
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
            }
        }

        return resultedMatrix[resultedMatrix.length - 1][resultedMatrix[0].length - 1];

    }


}
