import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class SPExactThree {

    private static Map<Character, Integer> charMapping = new HashMap() {{
        put('A', 0);
        put('C', 1);
        put('G', 2);
        put('T', 3);
    }};
    private String seq1, seq2, seq3;
    private Integer gapCost;
    private int[][][] dMatrix;

    public SPExactThree(String seq1, String seq2, String seq3, Integer gapCost) {
        this.seq1 = seq1;
        this.seq2 = seq2;
        this.seq3 = seq3;
        this.gapCost = gapCost;
        this.dMatrix = new int[seq1.length()][seq2.length()][seq3.length()];
    }

    public List<StringBuilder> calculateSPAlignmentAndScore(int[][] scoreMatrix) {
        for (int i = 0; i < seq1.length(); i++) {
            for (int j = 0; j < seq2.length(); j++) {
                for (int k = 0; k < seq3.length(); k++) {
                    int minimumValue = 999999999;
                    int v0, v1, v2, v3, v4, v5, v6, v7;
                    int sijk = scoreMatrix[charMapping.get(seq1.charAt(i))][charMapping.get(seq2.charAt(j))] +
                            scoreMatrix[charMapping.get(seq2.charAt(j))][charMapping.get(seq3.charAt(k))] +
                            scoreMatrix[charMapping.get(seq1.charAt(i))][charMapping.get(seq3.charAt(k))],
                            sij = scoreMatrix[charMapping.get(seq1.charAt(i))][charMapping.get(seq2.charAt(j))] + gapCost + gapCost,
                            sik = gapCost + scoreMatrix[charMapping.get(seq1.charAt(i))][charMapping.get(seq3.charAt(k))] + gapCost,
                            sjk = gapCost + gapCost + scoreMatrix[charMapping.get(seq2.charAt(j))][charMapping.get(seq3.charAt(k))],
                            si = gapCost + gapCost,
                            sj = gapCost + gapCost,
                            sk = gapCost + gapCost;

                    if (i == 0 && j == 0 && k == 0) {
                        v0 = 0;
                        minimumValue = v0;
                    }

                    if (i > 0 && j > 0 && k > 0) {
                        v1 = dMatrix[i - 1][j - 1][k - 1] + sijk;
                        if (v1 < minimumValue) {
                            minimumValue = v1;
                        }
                    }

                    if (i > 0 && j > 0 && k >= 0) {
                        v2 = dMatrix[i - 1][j - 1][k] + sij;
                        if (v2 < minimumValue) {
                            minimumValue = v2;
                        }
                    }
                    if (i > 0 && j >= 0 && k > 0) {
                        v3 = dMatrix[i - 1][j][k - 1] + sik;
                        if (v3 < minimumValue) {
                            minimumValue = v3;
                        }
                    }
                    if (i >= 0 && j > 0 && k > 0) {
                        v4 = dMatrix[i][j - 1][k - 1] + sjk;
                        if (v4 < minimumValue) {
                            minimumValue = v4;
                        }
                    }
                    if (i > 0 && j >= 0 && k >= 0) {
                        v5 = dMatrix[i - 1][j][k] + si;
                        if (v5 < minimumValue) {
                            minimumValue = v5;
                        }
                    }
                    if (i >= 0 && j > 0 && k >= 0) {
                        v6 = dMatrix[i][j - 1][k] + sj;
                        if (v6 < minimumValue) {
                            minimumValue = v6;
                        }
                    }
                    if (i >= 0 && j >= 0 && k > 0) {
                        v7 = dMatrix[i][j][k - 1] + sk;
                        if (v7 < minimumValue) {
                            minimumValue = v7;
                        }
                    }

                    dMatrix[i][j][k] = minimumValue;
                }
            }
        }
//        print3DMatrix(dMatrix);

        ArrayList<Character> alignSeq1 = new ArrayList<>();
        ArrayList<Character> alignSeq2 = new ArrayList<>();
        ArrayList<Character> alignSeq3 = new ArrayList<>();

        backtracking(seq1.length() - 1, seq2.length() - 1, seq3.length() - 1, alignSeq1, alignSeq2, alignSeq3, scoreMatrix);


	StringBuilder builder1=new StringBuilder();
	StringBuilder builder2=new StringBuilder();
	StringBuilder builder3=new StringBuilder();
        for(int i=alignSeq1.size()-1;i>=0;i--){
            builder1.append(alignSeq1.get(i));
            builder2.append(alignSeq2.get(i));
            builder3.append(alignSeq3.get(i));
	}
        /*System.out.println("MSA 3 seq ----------");
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
        System.out.println("\nMSA 3 seq ----------");*/
        return List.of(builder1,builder2,builder3);
    }

    //Needleman–Wunsch algorithm
    public void backtracking(int i, int j, int k, ArrayList<Character> alignSeq1, ArrayList<Character> alignSeq2, ArrayList<Character> alignSeq3, int[][] scoreMatrix) {
       
        int sijk = scoreMatrix[charMapping.get(seq1.charAt(i))][charMapping.get(seq2.charAt(j))] +
                scoreMatrix[charMapping.get(seq2.charAt(j))][charMapping.get(seq3.charAt(k))] +
                scoreMatrix[charMapping.get(seq1.charAt(i))][charMapping.get(seq3.charAt(k))],
                sij = scoreMatrix[charMapping.get(seq1.charAt(i))][charMapping.get(seq2.charAt(j))] + gapCost + gapCost,
                sik = gapCost + scoreMatrix[charMapping.get(seq1.charAt(i))][charMapping.get(seq3.charAt(k))] + gapCost,
                sjk = gapCost + gapCost + scoreMatrix[charMapping.get(seq2.charAt(j))][charMapping.get(seq3.charAt(k))],
                si = gapCost + gapCost,
                sj = gapCost + gapCost,
                sk = gapCost + gapCost;

        if (i == 0 && j == 0 && k == 0) {
            alignSeq1.add(seq1.charAt(i));
            alignSeq2.add(seq2.charAt(j));
            alignSeq3.add(seq3.charAt(k));
        }

        if (i > 0 && j > 0 && k > 0 && (dMatrix[i][j][k] == dMatrix[i - 1][j - 1][k - 1] + sijk)) {
            alignSeq1.add(seq1.charAt(i));
            alignSeq2.add(seq2.charAt(j));
            alignSeq3.add(seq3.charAt(k));
            backtracking(i - 1, j - 1, k - 1, alignSeq1, alignSeq2, alignSeq3, scoreMatrix);
        }
        else if (i >= 0 && j >= 0 && k > 0 && (dMatrix[i][j][k] == dMatrix[i][j][k - 1] + sk)) {
            alignSeq1.add('-');
            alignSeq2.add('-');
            alignSeq3.add(seq3.charAt(k));
            backtracking(i, j, k - 1, alignSeq1, alignSeq2, alignSeq3, scoreMatrix);
        }
        else if (i >= 0 && j > 0 && k >= 0 && (dMatrix[i][j][k] == dMatrix[i][j - 1][k] + sj)) {
            alignSeq1.add('-');
            alignSeq2.add(seq2.charAt(j));
            alignSeq3.add('-');
            backtracking(i, j - 1, k, alignSeq1, alignSeq2, alignSeq3, scoreMatrix);
        }
        else if (i > 0 && j >= 0 && k >= 0 && (dMatrix[i][j][k] == dMatrix[i - 1][j][k] + si)) {
            alignSeq1.add(seq1.charAt(i));
            alignSeq2.add('-');
            alignSeq3.add('-');
            backtracking(i - 1, j, k, alignSeq1, alignSeq2, alignSeq3, scoreMatrix);
        }
        else if (i >= 0 && j > 0 && k > 0 && (dMatrix[i][j][k] == dMatrix[i][j - 1][k - 1] + sjk)) {
            alignSeq1.add('-');
            alignSeq2.add(seq2.charAt(j));
            alignSeq3.add(seq3.charAt(k));
            backtracking(i, j - 1, k - 1, alignSeq1, alignSeq2, alignSeq3, scoreMatrix);
        }
       else  if (i > 0 && j >= 0 && k > 0 && (dMatrix[i][j][k] == dMatrix[i - 1][j][k - 1] + sik)) {
            alignSeq1.add(seq1.charAt(i));
            alignSeq2.add('-');
            alignSeq3.add(seq3.charAt(k));
            backtracking(i - 1, j, k - 1, alignSeq1, alignSeq2, alignSeq3, scoreMatrix);
        }
       else  if (i > 0 && j > 0 && k >= 0 && (dMatrix[i][j][k] == dMatrix[i - 1][j - 1][k] + sij)) {
            alignSeq1.add(seq1.charAt(i));
            alignSeq2.add(seq2.charAt(j));
            alignSeq3.add('-');
            backtracking(i - 1, j - 1, k, alignSeq1, alignSeq2, alignSeq3, scoreMatrix);
        }
    }

    public void print3DMatrix(int[][][] dMatrix) {
        for (int[][] array2D : dMatrix) {
            for (int[] array1D : array2D) {
                for (int item : array1D) {
                    System.out.println(item);
                }
            }
        }
    }
}
