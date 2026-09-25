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
public class MyAI extends CellAI {

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

        Location bestKill = null;
        int bestKillScore = Integer.MIN_VALUE;

        Location bestDefense = null;
        int bestDefenseScore = Integer.MIN_VALUE;

        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < cols; c++) {
                int cell = grid.getCell(r, c);
                int neighbors = GridFunctions.getNeighbors(r, c, grid);

                if(cell != myID && cell != -1) {
                    int score = 0;
                    
                    if(neighbors == 2 || neighbors == 3){
                        score += 100;
                    } else if(neighbors == 1 || neighbors == 4) {
                        score += 40;
                    } else {
                        score += 10;
                    }

                    int nearby = countNeighbors(grid, r, c, myID);
                    score += nearby * 8;

                    if(score > bestKillScore) {
                        bestKillScore = score;
                        bestKill = new Location(r, c);
                    }
                }

                if(cell == -1){
                    int score = 0;
                    int common = GridFunctions.mostCommonNeighbor(r, c, grid);

                    if(common != -1 && common != myID){
                        score += 80;
                    }

                    int nearby = countNeighbors(grid, r, c, myID);
                    if(nearby == 2){
                        score += 50;
                    } else if(nearby == 1){
                        score += 20;
                    } else if(nearby >= 3) {
                        score -= 30;
                    }

                    int closeEnemy = neighbors - nearby;
                    score += closeEnemy * 6;
                    
                    if(score > bestDefenseScore) {
                        bestDefenseScore = score;
                        bestDefense = new Location(r, c);
                    }
                }
            }
        }
        if(bestKill != null && bestKillScore >= 50){
            return bestKill;
        }
        if(bestKill == null){
            return bestDefense;
        }
        return new Location(randomInt(grid.getRows()), randomInt(grid.getCols()));
    }

    private int countNeighbors(Grid grid, int r, int c, int id) {
        int count = 0;
        for(int l = -1; l <= 1; l++) {
            for(int k = -1; k <= 1; k++) {
                if(l == 0 && k == 0) 
                    continue;
                int n = r + l;
                int m = c + k;
                if(n >= 0 && n < grid.getRows() && m >= 0 && m < grid.getCols()) {
                    if(grid.getCell(n, m) == id) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
