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
        return "MyAI - Change Name";
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

        return new Location(randomInt(grid.getRows()), randomInt(grid.getCols()));
    }
}
