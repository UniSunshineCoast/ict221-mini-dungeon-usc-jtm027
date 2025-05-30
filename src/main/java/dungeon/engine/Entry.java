package dungeon.engine;

public class Entry implements CellItem {
    @Override
    public char getSymbol() {
        return 'E';
    }

    @Override
    public String onPlayerEnter(GameEngine engine, int x, int y) {
        return "You are at the dungeon entry.";
    }
}

