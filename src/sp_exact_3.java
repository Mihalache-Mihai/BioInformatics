import java.util.*;

public class sp_exact_3 {

    private static Map<Character, Integer> charMapping = new HashMap() {{
        put('A', 0);
        put('C', 1);
        put('G', 2);
        put('T', 3);
    }};
    private String seq1, seq2, seq3;
    private static final int GAPCOST = 5;
    private int[][][] dMatrix;

    private static int[][] scoreMatrix;

    public static void main(String[] args) {
        String filename = args.length > 0 ? args[0] : "seq.fasta";
        scoreMatrix = Main2.readPhyLip("score.txt", new ArrayList<>());
        ArrayList<String> names = new ArrayList();
        ArrayList<String> seqs = Main2.readMultipleSeqFile(filename, names);
        sp_exact_3 spExactThree = new sp_exact_3(seqs.get(0), seqs.get(1), seqs.get(2));
        List<StringBuilder> sol = spExactThree.calculateSPAlignmentAndScore(scoreMatrix);
        Main2.writeSolutionToFile(sol, names, 0);
    }

    public sp_exact_3(String seq1, String seq2, String seq3) {
        this.seq1 = seq1;
        this.seq2 = seq2;
        this.seq3 = seq3;
        this.dMatrix = new int[seq1.length() + 1][seq2.length() + 1][seq3.length() + 1];
    }

    public List<StringBuilder> calculateSPAlignmentAndScore(int[][] scoreMatrix) {
        for (int i = 0; i <= seq1.length(); i++) {
            for (int j = 0; j <= seq2.length(); j++) {
                for (int k = 0; k <= seq3.length(); k++) {
                    int minimumValue = 999999999;
                    int v0, v1, v2, v3, v4, v5, v6, v7;

                    if (i == 0 && j == 0 && k == 0) {
                        v0 = 0;
                        minimumValue = v0;
                    }

                    if (i > 0 && j > 0 && k > 0) {
                        int sijk = scoreMatrix[charMapping.get(seq1.charAt(i - 1))][charMapping.get(seq2.charAt(j - 1))] +
                                scoreMatrix[charMapping.get(seq2.charAt(j - 1))][charMapping.get(seq3.charAt(k - 1))] +
                                scoreMatrix[charMapping.get(seq1.charAt(i - 1))][charMapping.get(seq3.charAt(k - 1))];
                        v1 = dMatrix[i - 1][j - 1][k - 1] + sijk;
                        if (v1 < minimumValue) {
                            minimumValue = v1;
                        }
                    }

                    if (i > 0 && j > 0 && k >= 0) {
                        int sij = scoreMatrix[charMapping.get(seq1.charAt(i - 1))][charMapping.get(seq2.charAt(j - 1))] + GAPCOST + GAPCOST;

                        v2 = dMatrix[i - 1][j - 1][k] + sij;
                        if (v2 < minimumValue) {
                            minimumValue = v2;
                        }
                    }
                    if (i > 0 && j >= 0 && k > 0) {
                        int sik = scoreMatrix[charMapping.get(seq1.charAt(i - 1))][charMapping.get(seq3.charAt(k - 1))] + GAPCOST + GAPCOST;
                        v3 = dMatrix[i - 1][j][k - 1] + sik;
                        if (v3 < minimumValue) {
                            minimumValue = v3;
                        }
                    }
                    if (i >= 0 && j > 0 && k > 0) {
                        int sjk = GAPCOST + GAPCOST + scoreMatrix[charMapping.get(seq2.charAt(j - 1))][charMapping.get(seq3.charAt(k - 1))];
                        v4 = dMatrix[i][j - 1][k - 1] + sjk;
                        if (v4 < minimumValue) {
                            minimumValue = v4;
                        }
                    }
                    if (i > 0 && j >= 0 && k >= 0) {
                        v5 = dMatrix[i - 1][j][k] + GAPCOST + GAPCOST;
                        if (v5 < minimumValue) {
                            minimumValue = v5;
                        }
                    }
                    if (i >= 0 && j > 0 && k >= 0) {
                        v6 = dMatrix[i][j - 1][k] + GAPCOST + GAPCOST;
                        if (v6 < minimumValue) {
                            minimumValue = v6;
                        }
                    }
                    if (i >= 0 && j >= 0 && k > 0) {
                        v7 = dMatrix[i][j][k - 1] + GAPCOST + GAPCOST;
                        if (v7 < minimumValue) {
                            minimumValue = v7;
                        }
                    }

                    dMatrix[i][j][k] = minimumValue;
                }
            }
        }

        ArrayList<Character> alignSeq1 = new ArrayList<>();
        ArrayList<Character> alignSeq2 = new ArrayList<>();
        ArrayList<Character> alignSeq3 = new ArrayList<>();

        backtracking(seq1.length(), seq2.length(), seq3.length(), alignSeq1, alignSeq2, alignSeq3, scoreMatrix);

        System.out.println("Score is : " + dMatrix[seq1.length()][seq2.length()][seq3.length()]);
        StringBuilder builder1 = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();
        StringBuilder builder3 = new StringBuilder();
        for (int i = alignSeq1.size() - 1; i >= 0; i--) {
            builder1.append(alignSeq1.get(i));
            builder2.append(alignSeq2.get(i));
            builder3.append(alignSeq3.get(i));
        }
        System.out.println("----------SP exact 3----------");
        for (int i = alignSeq1.size() - 1; i > -1; i--) {
            System.out.print(alignSeq1.get(i));
        }
        System.out.println();
        for (int i = alignSeq2.size() - 1; i > -1; i--) {
            System.out.print(alignSeq2.get(i));
        }
        System.out.println();
        for (int i = alignSeq3.size() - 1; i > -1; i--) {
            System.out.print(alignSeq3.get(i));
        }
        return Arrays.asList(builder1, builder2, builder3);
    }

    //Needleman–Wunsch algorithm
    public void backtracking(int i, int j, int k, ArrayList<Character> alignSeq1, ArrayList<Character> alignSeq2, ArrayList<Character> alignSeq3, int[][] scoreMatrix) {
        if (i < 0 && j < 0 && k < 0) return;

        if (i > 0 && j > 0 && k > 0 && (dMatrix[i][j][k] == dMatrix[i - 1][j - 1][k - 1] + scoreMatrix[charMapping.get(seq1.charAt(i - 1))][charMapping.get(seq2.charAt(j - 1))] +
                scoreMatrix[charMapping.get(seq2.charAt(j - 1))][charMapping.get(seq3.charAt(k - 1))] +
                scoreMatrix[charMapping.get(seq1.charAt(i - 1))][charMapping.get(seq3.charAt(k - 1))])) {
            alignSeq1.add(seq1.charAt(i - 1));
            alignSeq2.add(seq2.charAt(j - 1));
            alignSeq3.add(seq3.charAt(k - 1));
            backtracking(i - 1, j - 1, k - 1, alignSeq1, alignSeq2, alignSeq3, scoreMatrix);


        } else if (i >= 0 && j >= 0 && k > 0 && (dMatrix[i][j][k] == dMatrix[i][j][k - 1] + GAPCOST * 2)) {
            alignSeq1.add('-');
            alignSeq2.add('-');
            alignSeq3.add(seq3.charAt(k - 1));
            backtracking(i, j, k - 1, alignSeq1, alignSeq2, alignSeq3, scoreMatrix);
        } else if (i >= 0 && j > 0 && k >= 0 && (dMatrix[i][j][k] == dMatrix[i][j - 1][k] + GAPCOST * 2)) {
            alignSeq1.add('-');
            alignSeq2.add(seq2.charAt(j - 1));
            alignSeq3.add('-');
            backtracking(i, j - 1, k, alignSeq1, alignSeq2, alignSeq3, scoreMatrix);
        } else if (i > 0 && j >= 0 && k >= 0 && (dMatrix[i][j][k] == dMatrix[i - 1][j][k] + GAPCOST * 2)) {
            alignSeq1.add(seq1.charAt(i - 1));
            alignSeq2.add('-');
            alignSeq3.add('-');
            backtracking(i - 1, j, k, alignSeq1, alignSeq2, alignSeq3, scoreMatrix);
        } else if (i >= 0 && j > 0 && k > 0 && dMatrix[i][j][k] == dMatrix[i][j - 1][k - 1] + GAPCOST + GAPCOST + scoreMatrix[charMapping.get(seq2.charAt(j - 1))][charMapping.get(seq3.charAt(k - 1))]) {
            alignSeq1.add('-');
            alignSeq2.add(seq2.charAt(j - 1));
            alignSeq3.add(seq3.charAt(k - 1));
            backtracking(i, j - 1, k - 1, alignSeq1, alignSeq2, alignSeq3, scoreMatrix);


        } else if (i > 0 && j >= 0 && k > 0 && (dMatrix[i][j][k] == dMatrix[i - 1][j][k - 1] + GAPCOST + GAPCOST + scoreMatrix[charMapping.get(seq1.charAt(i - 1))][charMapping.get(seq3.charAt(k - 1))])) {
            alignSeq1.add(seq1.charAt(i - 1));
            alignSeq2.add('-');
            alignSeq3.add(seq3.charAt(k - 1));
            backtracking(i - 1, j, k - 1, alignSeq1, alignSeq2, alignSeq3, scoreMatrix);

        } else if (i > 0 && j > 0 && k >= 0 && (dMatrix[i][j][k] == dMatrix[i - 1][j - 1][k] + GAPCOST + GAPCOST + scoreMatrix[charMapping.get(seq1.charAt(i - 1))][charMapping.get(seq2.charAt(j - 1))])) {

            alignSeq1.add(seq1.charAt(i - 1));
            alignSeq2.add(seq2.charAt(j - 1));
            alignSeq3.add('-');
            backtracking(i - 1, j - 1, k, alignSeq1, alignSeq2, alignSeq3, scoreMatrix);


        }
    }
}
