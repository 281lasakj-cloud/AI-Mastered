/**
 * STUDENT FILE
 *
 * Name: ______________________________
 * AI Code Name: ______________________
 *
 * Strategy Description:
 * Replace this comment with a short explanation of the strategy your AI uses.
 * Your final strategy must be fundamentally different from the sample AIs.
 */
import java.util.ArrayList;
public class MyAI extends CellAI {

    private static final int MAX_Candidates = 12; //Allows to limit searches
    private static final int Depth = 2;

    @Override
    public String getAIName() {
        return "Bribed";
    }

    @Override
    public Location select(Grid grid) {
        /*
         * Replace this starter strategy.
         *
         * Helpful information:
         *   getID()                     -> your cell ID
         *   grid.getRows()              -> number of rows
         *   grid.getCols()              -> number of columns
         *   grid.getCell(r, c)          -> -1 if dead, otherwise an AI ID
         *   GridFunctions.getNeighbors  -> number of living neighbors
         *   GridFunctions.mostCommonNeighbor -> most common neighboring AI
         *   randomInt(bound)            -> reproducible random integer
         */
        int myID = getID();
        int rows = grid.getRows();
        int cols = grid.getCols();

        // finding enemy structure
        boolean[][] enemy = new boolean[rows][cols];
        java.util.ArrayList<java.util.ArrayList<Location>> enemyClusters = new java.util.ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int cell = grid.getCell(r, c);
                if (!enemy[r][c] && cell != -1 && cell != myID) {
                    java.util.ArrayList<Location> cluster = new java.util.ArrayList<>();
                    flood(grid, r, c, cell, enemy, cluster);
                    if (!cluster.isEmpty()) {
                        enemyClusters.add(cluster);
                    }
                }
            }
        }

        Location[] candidate = new Location[MAX_Candidates];
        double[] scores = new double[MAX_Candidates];
        int candidateCount = 0;

        for (java.util.ArrayList<Location> cluster : enemyClusters) {
            for (Location l : cluster) {
                int r = l.getRow();
                int c = l.getCol();
                int n = GridFunctions.getNeighbors(r, c, grid);

                double score = 0.0;
                if (n == 2 || n == 3) 
                    score += 50.0 + cluster.size() * 3.0;
                else if (n == 1 || n == 4) 
                    score += 18.0 + cluster.size();
                else 
                    score += 4.0;

                score += countAround(grid, r, c, myID) * 5.0; 

                add(candidate, scores, candidateCount, l, score);
                if (candidateCount < MAX_Candidates) candidateCount++;
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid.getCell(r, c) != -1) continue;
                int n = GridFunctions.getNeighbors(r, c, grid);
                if (n == 0) continue;

                int common = GridFunctions.mostCommonNeighbor(r, c, grid);
                int mine = countAround(grid, r, c, myID);
                int enemys = n - mine;

                double score = 0.0;
                if (common != -1 && common != myID) score += 35.0;
                if (mine == 2) 
                    score += 22.0;
                else if (mine == 1) 
                    score += 7.0;
                else if (mine >= 3) 
                    score -= 12.0;

                score += enemys * 4.0;
                if (enemys >= 2) 
                    score += 8.0;

                add(candidate, scores, candidateCount, new Location(r, c), score);
                if (candidateCount < MAX_Candidates) candidateCount++;
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid.getCell(r, c) != -1) continue;
                int mine = countAround(grid, r, c, myID);
                if (mine == 2) {   // perfect support for a stable cell
                    double score = 15.0 + countAround(grid, r, c, myID) * 2.0;
                    add(candidate, scores, candidateCount, new Location(r, c), score);
                    if (candidateCount < MAX_Candidates) candidateCount++;
                }
            }
        }

        Location best = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < candidateCount; i++) {
            if (candidate[i] == null) continue;

            double value = scores[i] * 0.25 + search(grid, candidate[i], myID);
            if (value > bestValue) {
                bestValue = value;
                best = candidate[i];
            }
        }
        //worst case
        if (best == null) {
            return new Location(randomInt(rows), randomInt(cols));
        }
        return best;
    }

    private double search(Grid grid, Location move, int me) {
        int[][] state = copy(grid);
        apply(state, move, me);

        double total = 0.0;
        double weight = 1.0;

        for (int d = 0; d < Depth; d++) {
            int[][] next = evolution(state);
            total += weight * structureScore(next, me);
            state = next;
            weight *= 0.40;
        }
        return total;
    }
    //Strong evaluation (hopefully compared to others)
    private double structureScore(int[][] board, int myID) {
        int rows = board.length;
        int cols = board[0].length;

        int myCells = 0;
        int enemyCells = 0;
        int myStable = 0;
        int enemyStable = 0;
        int mySafety = 0;
        int enemySafety = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int v = board[r][c];
                if (v == -1) 
                    continue;

                int n = liveNeighbors(board, r, c);
                boolean stable = (n == 2 || n == 3);

                if (v == myID) {
                    myCells++;
                    if (stable) 
                        myStable++;
                    if (n >= 2) 
                        mySafety++;
                } else {
                    enemyCells++;
                    if (stable) 
                        enemyStable++;
                    if (n >= 2) 
                        enemySafety++;
                }
            }
        }

        double score = (myCells - enemyCells) * 12.0 + (myStable - enemyStable) * 9.0 + (mySafety - enemySafety) * 3.5;
        score -= enemyCells * 2.8;

        if (myCells == 0) 
            score -= 9000;
        if (enemyCells == 0 && myCells > 0) score += 7000;
        
        return score;
    }

    private int[][] evolution(int[][] board) {
        int rows = board.length, cols = board[0].length;
        int[][] next = new int[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int live = liveNeighbors(board, r, c);
                boolean lives = (board[r][c] != -1) ? (live == 2 || live == 3) : (live == 3);

                if (lives) {
                    next[r][c] = majorityCheck(board, r, c);
                } else {
                    next[r][c] = -1;
                }
            }
        }
        return next;
    }

    private int majorityCheck(int[][] board, int r, int c) {
        int[] id = new int[8];
        int[] p = new int[8];
        int used = 0;

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = r + dr, nc = c + dc;
                if (nr < 0 || nc < 0 || nr >= board.length || nc >= board[0].length) continue;
                int v = board[nr][nc];
                if (v == -1) continue;

                int i = 0;
                while (i < used && id[i] != v) i++;
                if (i == used) {
                    id[used] = v;
                    p[used] = 0;
                    used++;
                }
                p[i]++;
            }
        }

        if (used == 0) return board[r][c];

        int best = id[0], bestCnt = p[0];
        boolean tie = false;
        for (int i = 1; i < used; i++) {
            if (p[i] > bestCnt) {
                best = id[i];
                bestCnt = p[i];
                tie = false;
            } else if (p[i] == bestCnt) {
                tie = true;
            }
        }

        if (tie) {
            if (board[r][c] != -1) return board[r][c];
            int low = id[0];
            for (int i = 1; i < used; i++) if (id[i] < low) low = id[i];
            return low;
        }
        return best;
    }

    private void flood(Grid grid, int r, int c, int id, boolean[][] visited, java.util.ArrayList<Location> cluster) {
        if (r < 0 || c < 0 || r >= grid.getRows() || c >= grid.getCols()) 
            return;
        if (visited[r][c] || grid.getCell(r, c) != id)
            return;

        visited[r][c] = true;
        cluster.add(new Location(r, c));

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                flood(grid, r + dr, c + dc, id, visited, cluster);
            }
        }
    }

    private void apply(int[][] board, Location m, int myID) {
        int r = m.getRow();
        int c = m.getCol();
        if (board[r][c] == -1) 
            board[r][c] = myID;
        else 
            board[r][c] = -1;
    }

    private int[][] copy(Grid grid) {
        int rows = grid.getRows();
        int cols = grid.getCols();
        int[][] board = new int[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                board[r][c] = grid.getCell(r, c);
        return board;
    }

    private int liveNeighbors(int[][] board, int r, int c) {
        int n = 0;
        for (int dr = -1; dr <= 1; dr++)
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) 
                    continue;
                int nr = r + dr, nc = c + dc;
                if (nr >= 0 && nc >= 0 && nr < board.length && nc < board[0].length && board[nr][nc] != -1)
                    n++;
            }
        return n;
    }

    private int countAround(Grid grid, int r, int c, int id) {
        int n = 0;
        for (int dr = -1; dr <= 1; dr++)
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) 
                    continue;
                int nr = r + dr, nc = c + dc;
                if (nr >= 0 && nc >= 0 && nr < grid.getRows() && nc < grid.getCols() && grid.getCell(nr, nc) == id) 
                    n++;
            }
        return n;
    }

    private void add(Location[] locations, double[] scores, int size, Location loc, double s) {
        if (size < locations.length) {
            locations[size] = loc;
            scores[size] = s;
            int i = size;
            while (i > 0 && scores[i] > scores[i-1]) {
                double t = scores[i]; scores[i] = scores[i-1]; scores[i-1] = t;
                Location l = locations[i]; locations[i] = locations[i-1]; locations[i-1] = l;
                i--;
            }
        } else if (s > scores[size-1]) {
            locations[size-1] = loc;
            scores[size-1] = s;
            int i = size-1;
            while (i > 0 && scores[i] > scores[i-1]) {
                double t = scores[i]; scores[i] = scores[i-1]; scores[i-1] = t;
                Location l = locations[i]; locations[i] = locations[i-1]; locations[i-1] = l;
                i--;
            }
        }
    }
}