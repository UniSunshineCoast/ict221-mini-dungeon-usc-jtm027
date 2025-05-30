package dungeon.engine;

public interface CellItem {
    /**
     * Returns the symbol representing this item on the map (e.g., 'E', 'P', 'G', 'T', etc.).
     */
    char getSymbol();

    /**
     * Called when the player steps on this item. Returns a string describing the result.
     */
    String onPlayerEnter(GameEngine engine, int x, int y);
}

