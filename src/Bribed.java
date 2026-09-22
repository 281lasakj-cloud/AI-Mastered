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
                int score = evaluateMove(grid, r, c, myID, oppID);

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

    private int evaluateMove(Grid grid, int r, int c, int myID, int oppID){
        
        int cell = grid.getCell(r, c);
        int neighbors = GridFunctions.getNeighbors(r, c, grid);
        int score = 0;

        if(cell == -1){
            if(neighbors == 3){
                score += 30;
            } else if (neighbors == 2){
                score += 15;
            } else if (neighbors == 1){
                score += 5;
            }
        } else if(cell == oppID){
            if(neighbors == 2 || neighbors == 3){
                score += 25;
            } else if(neighbors > 3){
                score += 5;
            } else {
                score += 1;
            }
        } else if(cell == myID){
            //Simple logic so don't think about this yet
            score -=5;
        }
        return score;
    }
}
