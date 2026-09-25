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
public class Bribed extends CellAI {

    @Override
    public String getAIName() {
        return "Bribe";
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
        int oppID = findOpp(grid);
        ArrayList<Location> candidate = limitMoves(grid, findGoodSearch(grid), myID, oppID, 6);   
        
        if(candidate.isEmpty()){
            return new Location(0,0);
        }
        int bestScore = Integer.MIN_VALUE;
        Location bestMove = candidate.get(0);

        for(Location a : candidate){ {

            Grid next = applicationForPlayer(grid, a.getRow(), a.getCol(), myID);
            int score = bestMove(next, myID, oppID, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            
            if(score > bestScore) {
                bestScore = score;
                bestMove = a;
            }
        } 

        }

        return bestMove;
    }

    private int findOpp(Grid g){
        int myID = getID();

        for(int r = 0; r < g.getRows(); r++){
            for(int c = 0; c < g.getCols(); c++){
                int cell = g.getCell(r,c);

                if(cell >= 0 && cell != myID){
                    return cell;
                }
            }
        }
        return -1;
    }

    private Grid applicationForOpp(Grid grid, int r, int c, int oppID){
        int[][] board = copyGrid(grid);

        if(board[r][c] == -1){
            board[r][c] = oppID;
        } else {
            board[r][c] = -1;
        }

        return nextGen(new Grid(board));
    }

    private ArrayList<Location> findOpponentMoves(Grid grid, int myID, int oppID) {

    ArrayList<Location> move = new ArrayList<Location>();

    boolean[][] nearMine = new boolean[grid.getRows()][grid.getCols()];

        for(int r = 0; r < grid.getRows(); r++) {
            for(int c = 0; c < grid.getCols(); c++) {

                if(grid.getCell(r, c) != myID) {
                    continue;
                }

                for(int l = -1; l <= 1; l++) {
                    for(int ri = -1; ri <= 1; ri++) {

                        int n = r + l;
                        int m = c + ri;

                        if(n >= 0 && n < grid.getRows() && m >= 0 && m < grid.getCols()) {
                            nearMine[n][m] = true;
                        }
                    }
                }
            }
        }

        for(int r = 0; r < grid.getRows(); r++) {
            for(int c = 0; c < grid.getCols(); c++) {
                if(nearMine[r][c]) {
                    move.add(new Location(r, c));
                }
            }
        }

        return move;
    }

    private int evaluateFuture(Grid grid, int myID, int oppID){
        int myCells = 0; 
        int oppCells = 0;

        int myS = 0;
        int oppS = 0;

        int myP = 0;
        int oppP = 0;

        for(int r = 0; r < grid.getRows(); r++){
            for(int c = 0; c < grid.getCols(); c++){
                int cell = grid.getCell(r, c);
                int neighbors = GridFunctions.getNeighbors(r, c, grid);
                if(cell == myID){
                    myCells++;
                    if(neighbors == 2 || neighbors == 3){
                        myS++;
                    }
                } else if(cell == oppID){
                    oppCells++;
                    if(neighbors == 2 || neighbors == 3){
                        oppS++;
                    }
                }
                if(cell == -1 && neighbors == 3){
                    int o = GridFunctions.mostCommonNeighbor(r, c, grid);
                    if(o == myID){
                        myP++;
                    } else if(o == oppID){
                        oppP++;
                    }
                }
            }
        }
        int cellScore = (myCells - oppCells) * 10;
        int survival = (myS - oppS) * 5;
        int potential = (myP - oppP) * 3;
        return cellScore + survival + potential;
    }

    private int bestMove(Grid grid, int myID, int oppID, int depth, int alpha, int beta, boolean maximizing) {
    if(depth == 0) {
        return evaluateFuture(grid, myID, oppID);
    }
    int playerID;
    if(maximizing){
        playerID = myID;
    } else {
        playerID = oppID;
    }

    ArrayList<Location> moves = limitMoves(grid, findGoodSearch(grid), playerID, oppID, 6);

    if(moves.isEmpty()){
        return evaluateFuture(grid, myID, oppID);
    }

    if(maximizing) {
        int best = Integer.MIN_VALUE;

        for(Location m : moves) {
            Grid next = applicationForPlayer(grid, m.getRow(), m.getCol(), myID);

            int score = bestMove(next, myID, oppID, depth - 1, alpha, beta, false);

            best = Math.max(best, score);
            alpha = Math.max(alpha, best);

        
            if(beta <= alpha) {
                break;
            }
        }

        return best;
    } else {
        int best = Integer.MAX_VALUE;

        for(Location m : moves) {
            Grid next = applicationForOpp(grid, m.getRow(), m.getCol(), oppID);

            int score = bestMove(next, myID, oppID, depth - 1, alpha, beta, true);

                best = Math.min(best, score);
                beta = Math.min(beta, best);

                if(beta <= alpha) {
                    break;
                }
        }

            return best;
    }
}

    private ArrayList<Location> findGoodSearch(Grid grid){
        ArrayList<Location> candidate = new ArrayList<Location>();

        boolean[][] near = new boolean[grid.getRows()][grid.getCols()];
        
        for(int r = 0; r < grid.getRows(); r++){
            for(int c = 0; c < grid.getCols(); c++){
                
                if(grid.getCell(r, c) == -1){
                    continue;
                }    
                    
                    for(int l = -1; l <= 1; l++){
                        for(int ri = -1; ri <= 1; ri++){
                            
                            if(l == 0 && ri == 0){
                                continue;
                            }
                            int n = r + l;
                            int m = c + ri;
                            
                            if(n >= 0 && n < grid.getRows() && m >= 0 && m < grid.getCols()){
                                near[n][m] = true;
                            }
                        }
                    }
            }
        }
        
        for(int r =0; r < grid.getRows(); r++){
            for(int c = 0; c < grid.getCols(); c++){
                if(near[r][c]){
                    candidate.add(new Location(r, c));
                }
            }
        }
        return candidate;
    }

    private Grid application(Grid grid, int r, int c){
        int[][] board = copyGrid(grid);

        if(board[r][c] == -1){
            board[r][c] = getID();
        } else {
            board[r][c] = -1;
        }

        return nextGen(new Grid(board));
    }

    private Grid applicationForPlayer(Grid grid, int r, int c, int myID){
        int[][] board = copyGrid(grid);

        if(board[r][c] == -1){  
            board[r][c] = myID;
        } else {
            board[r][c] = -1;
        }

        return nextGen(new Grid(board));
    }

    private ArrayList<Location> limitMoves(Grid grid, ArrayList<Location> moves, int myID, int oppID,int limit){
        ArrayList<Location> result = new ArrayList<Location>();
        ArrayList<Integer> scores = new ArrayList<Integer>();

        for(Location move : moves){
            int r = move.getRow();
            int c = move.getCol();
            int score = 0;
            
            for(int l = -1; l <= 1; l++){
                for(int ri = -1; ri <= 1; ri++){
                    if(l == 0 && ri == 0){
                        continue;
                    }
                    int n = r + l;
                    int m = c + ri;

                    if(n < 0 || n >= grid.getRows() || m < 0 || m >= grid.getCols()){
                        continue;
                    }
                    int cell = grid.getCell(n, m);
                    
                    if(cell == myID){
                        score += 4;
                    } else if(cell == oppID){
                        score += 3;
                    }
                }
            }

            if(grid.getCell(r, c) == -1){
                score+= 2;
            }

            int neighbors = GridFunctions.getNeighbors(r, c, grid);

            if(neighbors == 2 || neighbors == 3){
                score += 5;
            }
            int index = 0;
            while(index < scores.size() && scores.get(index) >= score){
                index++;
            }
            scores.add(index, score);
            result.add(index, move);
        }
        if(result.size() > limit){
            ArrayList<Location> limitedResult = new ArrayList<Location>();
            for(int i = 0; i < limit; i++){
                limitedResult.add(result.get(i));
            }
            return limitedResult;
        }
        return result;
    }

    private ArrayList<Location> orderOfMoves(Grid grid, ArrayList<Location> moves, int myID){
        ArrayList<Location> orderedMoves = new ArrayList<Location>();
        ArrayList<Integer> scores = new ArrayList<Integer>();

        int oppID = findOpp(grid);

        for(Location move : moves){
            int r = move.getRow();
            int c = move.getCol();
            int score = 0;

            for(int l = -1; l <= 1; l++){
                for(int ri = -1; ri <= 1; ri++){
                    if(l == 0 && ri == 0){
                        continue;
                    }
                    int n = r + l;
                    int m = c + ri;

                    if(n < 0 || n >= grid.getRows() || m < 0 || m >= grid.getCols()){
                        continue;
                    }
                    int cell = grid.getCell(n, m);
                    
                    if(cell == myID){
                        score += 5;
                    } else if(cell == oppID){
                        score += 4;
                    }
                }
            }
            int neighbors = GridFunctions.getNeighbors(r, c, grid);

            if(neighbors == 3){
                score += 8;
            } else if (neighbors == 2){
                score += 5;
            }

            if(grid.getCell(r, c) == -1){
                score += 2;
            }
            int index = 0;
            while(index < scores.size() && score < scores.get(index)){
                index++;
            }
            scores.add(index, score);
            orderedMoves.add(index, move);
        }
        return orderedMoves;
    }
    
    private int[][] copyGrid(Grid grid){
        int [][] board = new int[grid.getRows()][grid.getCols()];

        for(int r = 0; r < grid.getRows(); r++){
            for(int c = 0; c < grid.getCols(); c++){
                board[r][c] = grid.getCell(r, c);
            }
        }
        return board;
    }

    private Grid nextGen(Grid grid){
        int row = grid.getRows();
        int col = grid.getCols();

        int[][] next = new int[row][col];

        for(int r = 0; r < row; r++){
            for(int c = 0; c < col; c++){
                int currentCell = grid.getCell(r, c);
                int neighbors = GridFunctions.getNeighbors(r, c, grid);
                if(currentCell != -1){
                    if(neighbors < 2 || neighbors > 3){
                        next[r][c] = -1;
                    } else {
                        next[r][c] = GridFunctions.mostCommonNeighbor(r, c, grid);
                    }
                } else {
                    if(neighbors == 3){
                        next[r][c] = GridFunctions.mostCommonNeighbor(r, c, grid);
                    } else {
                        next[r][c] = -1;
                    }
                }
            }
        }
            return new Grid(next);  
    }
}
