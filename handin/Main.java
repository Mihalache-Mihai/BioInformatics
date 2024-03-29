import java.io.*;
import java.util.*;

public class Main {

    private static final boolean MAXIMIZING = false;

    public static void main(String[] args) throws IOException {
        new Main();

    }

    public Main() throws IOException {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Welcome. Please type in the name of a file with the score matrix and gapcost (e.g. score.txt)");

        String scoreFilename =console.readLine();
        readPhyLip(scoreFilename);

        System.out.println("Please specify the gap cost: One variable for linear gapcost, two (opening and continuing) values for affine gapcost (e.g. 1 or 2 -1 where 2 is the constant and -1 the linear coefficient");
        String nmbs[] = console.readLine().split(" ");
        if (nmbs.length == 1) {
            gapCost = k -> Integer.parseInt(nmbs[0]);
        } else {
            gapCost = k -> k == 'n' ? Integer.parseInt(nmbs[0]) + Integer.parseInt(nmbs[1]) : Integer.parseInt(nmbs[1]);
        }

        System.out.println("Please type in the name of the file with sequence 1 (fasta format)");
        String filename1 = console.readLine();
        StringBuilder seq1NameB = new StringBuilder();
        String seq1 = readFasta(filename1, seq1NameB);
        String seq1Name = seq1NameB.toString();

        System.out.println("Please type in the name of the file with sequence 2 (fasta format)");
        String filename2 =  console.readLine();
        StringBuilder seq2NameB = new StringBuilder();
        String seq2 = readFasta(filename2, seq2NameB);
        String seq2Name = seq2NameB.toString();

        int[][] table;
        String[] alignments;
        try {
            if (nmbs.length == 1) {
                table = linear_table(MAXIMIZING, seq1, seq2, score, gapCost);
                alignments = getAlignment(seq1, seq2, table);
            } else {
                table = affine_table(MAXIMIZING, seq1, seq2, score, gapCost);
                alignments = getAffineAlignment(seq1, seq2, table);
            }
            printMatrix(table);
            System.out.println("The cost of an optimal alignment is " + getLastValueFromMatrix(table));
            System.out.println(">" + seq1Name + "\n");
            System.out.println(alignments[0]);
            System.out.println(">" + seq2Name + "\n");
            System.out.println(alignments[1]);

            writeAlignment("solution.fastalignment", alignments[0], alignments[1], seq1Name, seq2Name);
            System.out.println("The alignment was written to solution.fastalignment");

        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("Error: You probably used a character in your sequence, that you didnt specified in the score matrix (case sensitive)");
            System.exit(1);
        }
 }

    private enum Operation {
        SUB, INS, DEL
    }


    private GapCost gapCost = null;
    private int[][] score = null;
    private ArrayList<Character> alphabet = new ArrayList<>();

    private static Operation[][] paths;

    public String[] getAffineAlignment(String seq1, String seq2, int[][] resultTable) {
        int nGap = gapCost.calc('n');//new block
        int cGap = gapCost.calc('c');//continue
        int i = seq2.length();
        int j = seq1.length();
        StringBuilder result1 = new StringBuilder();
        StringBuilder result2 = new StringBuilder();
        boolean mustD=false;
        boolean mustI=false;
        while (i > 0 || j > 0) {
            int cur = resultTable[i][j];
            if (!mustI&&(mustD||i > 0 && resultTable[i - 1][j] + nGap == cur)) {
                mustD=false;
                result1.insert(0, seq2.charAt(i - 1));
                result2.insert(0, '_');
                i--;
            }
            else if (mustI||j > 0 && resultTable[i][j - 1] + nGap == cur ) {
                mustI=false;
                result1.insert(0, '_');
                result2.insert(0, seq1.charAt(j - 1));
                j--;
            } else if(i > 1 && D[i - 1][j] + cGap == cur) {
                result1.insert(0, seq2.charAt(i - 1));
                result2.insert(0, '_');
                resultTable[i - 1][j] = D[i - 1][j];
                i--;
                mustD=true;
            }else if ( j > 1 && I[i][j - 1] + cGap == cur){
                result1.insert(0, '_');
                result2.insert(0, seq1.charAt(j - 1));
                resultTable[i][j - 1] = I[i][j - 1];
                j--;
                mustI=true;
            }
            else if (i > 0 && j > 0 && resultTable[i - 1][j - 1] + score[alphabet.indexOf(seq2.charAt(i - 1))][alphabet.indexOf(seq1.charAt(j - 1))] == cur) {
                result1.insert(0, seq2.charAt(i - 1));
                result2.insert(0, seq1.charAt(j - 1));
                i--;
                j--;
            }
            System.out.println(i+ "" +j);
        }
        return new String[]{result2.toString(), result1.toString()};
    }

    private int[][] D;
    private int[][] I;

    /**
     * The version with affine cost function
     *
     * @param maximizing  will always minimize
     * @param seq1
     * @param seq2
     * @param scoreMatrix
     * @param gapCost
     * @return the S-table
     */
    public int[][] affine_table(boolean maximizing, String seq1, String seq2, int[][] scoreMatrix, GapCost gapCost) {
        int nGap = gapCost.calc('n');//new block
        int cGap = gapCost.calc('c');//continue
        int[][] S = new int[seq2.length() + 1][seq1.length() + 1];
        D = new int[seq2.length() + 1][seq1.length() + 1];
        I = new int[seq2.length() + 1][seq1.length() + 1];
        paths = new Operation[seq2.length() + 1][seq1.length() + 1];
        try {
            for (int row = 0; row < S.length; row++) {
                for (int col = 0; col < S[0].length; col++) {
                    if (row == 0 && col == 0) {
                        S[0][0] = 0;
                    } else {
                        if (row > 0) {
                            D[row][col] = S[row - 1][col] + nGap;
                            if (row > 1) {
                                D[row][col] = Math.min(D[row][col], D[row - 1][col] + cGap);
                            }
                        }
                        if (col > 0) {
                            I[row][col] = S[row][col - 1] + nGap;
                            if (col > 1) {
                                I[row][col] = Math.min(I[row][col], I[row][col - 1] + cGap);
                            }
                        }
                        if (row == 0) {
                            S[row][col] = I[row][col];
                            paths[row][col] = Operation.DEL;
                        } else if (col == 0) {
                            S[row][col] = D[row][col];
                            paths[row][col] = Operation.INS;
                        } else {
                            int s = scoreMatrix[alphabet.indexOf(seq2.charAt(row - 1))][alphabet.indexOf(seq1.charAt(col - 1))] + S[row - 1][col - 1];
                            if (s < D[row][col] && s < I[row][col]) {
                                paths[row][col] = Operation.SUB;
                                S[row][col] = s;
                            } else if (D[row][col] < I[row][col]) {
                                paths[row][col] = Operation.INS;
                                S[row][col] = D[row][col];
                            } else {
                                paths[row][col] = Operation.DEL;
                                S[row][col] = I[row][col];
                            }
                        }
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid character, be aware that the score matrix has to contain the characters and is case sensitive");
            System.exit(1);
        }
        return S;
    }

    public int[][] linear_table(boolean maximizing, String seq1, String seq2, int[][] scoreMatrix, GapCost gapCost) {
        int gap = gapCost.calc(1);
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
                int subcost = scoreMatrix[alphabet.indexOf(seq2.charAt(row - 1))][alphabet.indexOf(seq1.charAt(column - 1))];
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

    private static void printMatrix(int[][] resultedMatrix) {
        for (int i = 0; i < resultedMatrix.length; i++) {
            for (int j = 0; j < resultedMatrix[i].length; j++) {
                System.out.print(resultedMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }


    public static int getLastValueFromMatrix(int[][] resultTable) {
        return resultTable.length > 0 ? resultTable[resultTable.length - 1][resultTable[0].length - 1] : 0;
    }


    public String[] getAlignment(String seq1, String seq2, int[][] resultTable) {
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
        return new String[]{result2.toString(), result1.toString()};
    }


    public interface GapCost {
        int calc(int length);
    }

    /**
     * FILE READING
     */

    private String eliminateWhiteSpace(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
    }

    /**
     * Reads a file in fastaFormat
     *
     * @param filename which file
     * @param seqName  in the end this builder will contain the name of the seq, spec
     * @return the sequence
     */
    private String readFasta(String filename, StringBuilder seqName) {
        StringBuilder seq = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
            String line = eliminateWhiteSpace(reader.readLine());
            while (line != null) {
                if (line.startsWith(">")) {
                    seqName.append(line.substring(1));
                } else if (line.startsWith(";")) {
                    continue;
                } else {
                    seq.append(line);
                }
                line = eliminateWhiteSpace(reader.readLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("File " + filename + " not found!");
        } catch (Exception e) {
            System.out.println("Invalid format!");
        }
        return seq.toString();
    }

    /**
     * Reads in a file with a gapcost and a scorematrix, defines the alphabet (see project-information (2))
     *
     * @param filename which file
     */
    private void readPhyLip(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));

            ArrayList<String> matrixLines = new ArrayList();
            String line = reader.readLine();
            while (line != null) {
                if (line.length() > 1) {
                    matrixLines.add(line);
                }
                line = reader.readLine();
            }
            this.score = new int[matrixLines.size()][matrixLines.size()];
            for (int i = 0; i < matrixLines.size(); i++) {
                this.alphabet.add(matrixLines.get(i).charAt(0));
                line = matrixLines.get(i).substring(2);
                String[] nmbs = line.split(" ");
                int l = 0;
                for (int j = 0; j < nmbs.length; j++) {
                    if (nmbs[j].equals("")) {
                        l++;
                        continue;
                    }
                    this.score[i][j - l] = Integer.parseInt(nmbs[j]);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("File " + filename + " not found!");
        } catch (Exception e) {
            System.out.println("Invalid format!");
        }

    }

    private void writeAlignment(String filename, String seq1, String seq2, String name1, String name2) {
        try {
            PrintWriter writer = new PrintWriter(new File(filename));
            writer.println(">" + name1);
            writer.println(seq1);
            writer.println();
            writer.println(">" + name2);
            writer.println(seq2);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}


