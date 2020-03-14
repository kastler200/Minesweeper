import java.util.Random;


public class GameBoard
{
    public static boolean[][] getMineField(final int[] mineList, final int[][] cellNums) {
        final boolean[][] mineField = new boolean[cellNums.length][cellNums[0].length];
        for (int g = 0; g < mineList.length; ++g) {
            for (int i = 0; i < cellNums.length; ++i) {
                for (int j = 0; j < cellNums[i].length; ++j) {
                    if (mineList[g] == cellNums[i][j]) {
                        mineField[i][j] = true;
                    }
                }
            }
        }
        return mineField;
    }
    
    public static int[][] getAdjacentMineVals(final int[][] cellNums, final boolean[][] mineField) {
        final int[][] adjacentMineVal = new int[cellNums.length][cellNums[0].length];
        for (int i = 0; i < cellNums.length; ++i) {
            for (int j = 0; j < cellNums[i].length; ++j) {
                if (!mineField[i][j]) {
                    int adjacentMines = 0;
                    for (int dy = -1; dy <= 1; ++dy) {
                        for (int dx = -1; dx <= 1; ++dx) {
                            if ((dx != 0 || dy != 0) && j + dx >= 0 && j + dx < cellNums[i].length && i + dy >= 0 && i + dy < cellNums.length && mineField[i + dy][j + dx]) {
                                ++adjacentMines;
                            }
                        }
                    }
                    adjacentMineVal[i][j] = adjacentMines;
                }
            }
        }
        return adjacentMineVal;
    }
    
    public static int[][] setCellNum(final int numRows, final int numCols) {
        int x = 0;
        final int[][] cells = new int[numRows][numCols];
        for (int i = 0; i < numRows; ++i) {
            for (int j = 0; j < numCols; ++j) {
                cells[i][j] = x;
                ++x;
            }
        }
        return cells;
    }
    
    public static int[] setMines(final int numMines, final int numRows, final int numCol) {
        final Random gen = new Random();
        final int[] mineCells = new int[numMines];
        for (int i = 0; i < mineCells.length; ++i) {
            boolean cellsClear = false;
            int x = 0;
            if (i == 0) {
                x = gen.nextInt(100);
                mineCells[i] = x;
            }
            else {
                while (!cellsClear) {
                    int testCells = 0;
                    x = gen.nextInt(numRows * numCol);
                    for (int j = 0; j < i && x != mineCells[j]; ++j) {
                        ++testCells;
                    }
                    if (testCells == i) {
                        cellsClear = true;
                    }
                }
                mineCells[i] = x;
            }
        }
        return mineCells;
    }
}
