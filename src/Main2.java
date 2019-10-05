import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main2 {

 



    public static ArrayList<String> readMultipleSeqFile(String filename, ArrayList<String>names) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
            ArrayList<String> results = new ArrayList<>();
            StringBuilder cur = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.startsWith(">")) {
                    names.add(line);
                    String curr = eliminateWhiteSpace(cur.toString());
                    if (cur.length() > 0) {
                        results.add(curr);
                        cur = new StringBuilder();
                    }
                } else if(!line.startsWith(";")) {
                    cur.append(line);
                }

                line = reader.readLine();
            }
            String curr = eliminateWhiteSpace(cur.toString());
            if (cur.length() > 0) {
                results.add(curr);
            }
            reader.close();
            return results;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeSolutionToFile(List<StringBuilder> multiple, ArrayList<String> names, int minimum) {
        try {
            PrintWriter writer = new PrintWriter(new File("solution.fasta"));
            writer.println(names.get(minimum) + " (center sequence)");
            writer.println(multiple.get(0).toString());
            for (int i = 1; i < multiple.size(); i++) {
                int j = i < minimum ? i - 1 : i;
                System.out.println(multiple.get(i).toString());
                writer.println(names.get(j));
                writer.println(multiple.get(i).toString());
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
    }


    /**
     * FILE READING
     */

    private static String eliminateWhiteSpace(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
    }


    /**
     * Reads in a file with a gapcost and a scorematrix, defines the alphabet (see project-information (2))
     *
     * @param filename which file
     */
    public static int[][] readPhyLip(String filename,ArrayList<Character> alphabet) {
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
            int [][]scoreMatrix = new int[matrixLines.size()][matrixLines.size()];
            for (int i = 0; i < matrixLines.size(); i++) {
                alphabet.add(matrixLines.get(i).charAt(0));
                line = matrixLines.get(i).substring(2);
                String[] nmbs = line.split(" ");
                int l = 0;
                for (int j = 0; j < nmbs.length; j++) {
                    if (nmbs[j].equals("")) {
                        l++;
                        continue;
                    }
                    scoreMatrix[i][j - l] = Integer.parseInt(nmbs[j]);
                }
            }
            reader.close();
            return scoreMatrix;
        } catch (FileNotFoundException e) {
            System.out.println("File " + filename + " not found!");
        } catch (Exception e) {
            System.out.println("Invalid format!");
        }
        return null;
    }
}
