package dungeon.engine;

import java.util.ArrayList;
import java.util.List;

public class Map {
    private Cell[][] cells;
    private int size;

    public Map(int size, int level) {
        this.size = size;
        this.cells = new Cell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = new Cell();
            }
        }
        generateFixedMaze(level);
    }

    private void generateFixedMaze(int level) {
        // 0 = path, 1 = wall
        int[][] layout1 = {
            {0,0,0,0,0,0,0,0,0,0},
            {0,1,0,1,1,0,1,0,1,1},
            {0,1,0,0,0,0,1,0,0,0},
            {0,0,1,0,1,0,0,1,1,0},
            {1,0,1,0,1,1,0,0,1,0},
            {0,0,0,0,0,0,1,0,0,0},
            {0,1,1,0,1,0,0,0,1,1},
            {0,0,0,0,1,0,1,0,0,0},
            {1,1,1,0,1,0,1,0,1,0},
            {0,0,0,0,0,0,0,0,1,0}
        };
        int[][] layout2 = {
            {0,0,0,0,1,0,0,0,1,0},
            {0,1,1,0,1,0,1,0,1,0},
            {0,0,1,0,1,0,1,0,1,0},
            {1,0,1,0,0,0,1,0,0,0},
            {0,0,0,1,1,1,1,1,1,1},
            {0,1,0,0,0,0,0,0,0,0},
            {0,1,1,1,1,1,1,1,1,0},
            {0,0,0,0,0,0,1,0,0,0},
            {1,1,1,1,1,0,1,0,1,1},
            {0,0,0,0,0,0,1,0,0,0}
        };
        int[][] layout = (level == 2) ? layout2 : layout1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (layout[i][j] == 1) {
                    cells[i][j].setItem(new Wall());
                } else {
                    cells[i][j].setItem(null);
                }
            }
        }
    }

    // Helper to check if two cells are connected (no wall between them)
    public boolean isPath(int x1, int y1, int x2, int y2) {
        if (x2 < 0 || x2 >= size || y2 < 0 || y2 >= size) return false;
        return !(cells[x2][y2].getItem() instanceof Wall);
    }

    public int getSize() {
        return size;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public Cell getCell(int x, int y) {
        if (x >= 0 && x < size && y >= 0 && y < size) {
            return cells[x][y];
        }
        return null;
    }
}

