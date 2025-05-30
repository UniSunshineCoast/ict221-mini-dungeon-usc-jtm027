package dungeon.engine;

public class Wall implements CellItem {
    @Override
    public char getSymbol() {
        return '#';
    }

    @Override
    public String onPlayerEnter(GameEngine engine, int x, int y) {
        return "You cannot pass through a wall.";
    }
}
