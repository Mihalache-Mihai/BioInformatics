public class Main {

    public static void main(String[] args) {
        new Main();

    }

    public Main(){
        Task t=new Task(false,"acta","aggt",null,1);
        //table=new int[][]{{0,1,2,3,4},{1,0,1,2,3},{2,1,1,2,3},{3,2,2,2,2},{4,3,3,3,3}};
        paths=new Operation[][]{
                {Operation.SUB,Operation.DEL,Operation.DEL,Operation.DEL,Operation.DEL},
                {Operation.INS,Operation.SUB,Operation.DEL,Operation.DEL,Operation.DEL},
                {Operation.INS,Operation.INS,Operation.SUB,Operation.DEL,Operation.DEL},
                {Operation.INS,Operation.INS,Operation.DEL,Operation.SUB,Operation.DEL},
                {Operation.INS,Operation.DEL,Operation.INS,Operation.DEL,Operation.DEL}};
        String[] result=getAlignment(t);
        System.out.println(result[0]+'\n'+result[1]);
    }

    private enum Operation {
        SUB, INS, DEL
    }


    private class Task {
        public boolean maximizing;
        public String seq1;
        public String seq2;

        public Task(boolean maximizing, String seq1, String seq2, int[][] score, int gap) {
            this.maximizing = maximizing;
            this.seq1 = seq1;
            this.seq2 = seq2;
            this.score = score;
            this.gap = gap;
        }

        public int[][] score;
        public int gap;

    }

    private int[][] table;
    private Operation[][] paths;

    public void calculateTable() {
    }

    public int getValue() {
        return 0;
    }


    public String[] getAlignment(Task task) {
        int x = table.length - 1;
        int y = table[0].length - 1;
        StringBuilder result1 = new StringBuilder();
        StringBuilder result2 = new StringBuilder();
        while (x > 0 || y > 0) {
            if (paths[x][y] == Operation.SUB) {
                result1.insert(0, task.seq1.charAt(x-1));
                result2.insert(0, task.seq2.charAt(y-1));
                x--;
                y--;
            } else if (paths[x][y] == Operation.DEL) {
                result1.insert(0, '_');
                result2.insert(0, task.seq2.charAt(y-1));
                y--;
            } else if (paths[x][y] == Operation.INS) {
                result1.insert(0, task.seq1.charAt(x-1));
                result2.insert(0, '_');
                x--;
            }
        }
        return new String[]{result1.toString(), result2.toString()};
    }
}
