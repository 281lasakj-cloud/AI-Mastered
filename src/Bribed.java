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

        int myCells = 0;
        int oppCells = 0;
        int deadCells = 0;

        for(int r = 0; r < grid.getRows(); r++) {
            for(int c = 0; c < grid.getCols(); c++) {
                int cell = grid.getCell(r, c);

                if(cell == -1)
                    deadCells++;
                else if(cell == myID)
                    myCells++;
                else if(cell == oppID)
                    oppCells++;
            }
        }
        System.out.println("My Cells: " + myCells + " Opp Cells: " + oppCells + " Dead Cells: " + deadCells);   
        

        return new Location(0,0);
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
}
