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
public class Bribed extends CellAI {

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

        int oppID = findOpp(grid);
        int bestScore = Integer.MIN_VALUE;
        Location bestMove = new Location(0, 0);

        for(int r = 0; r < grid.getRows(); r++) {
            for(int c = 0; c < grid.getCols(); c++) {
                Grid next = application(grid, r, c);
                int score = evaluateFuture(next, myID);

                if(score > bestScore) {
                    bestScore = score;
                    bestMove = new Location(r, c);
                }
            }
        }
        
        System.out.println("I chose (" + bestMove.getRow() + ", " + bestMove.getCol() + ") with a score of " + bestScore);   
        

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

    private int evaluateFuture(Grid grid, int myID){
        int myCells = 0; 
        int oppCells = 0;

        for(int r = 0; r < grid.getRows(); r++){
            for(int c = 0; c < grid.getCols(); c++){
                int cell = grid.getCell(r, c);
                if(cell == myID){
                    myCells++;
                } else if(cell >= 0){
                    oppCells++;
                }
            }
        }
        return myCells*10 - oppCells;
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
