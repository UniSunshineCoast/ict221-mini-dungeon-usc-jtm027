package dungeon.engine;

public class Ladder implements CellItem {
    @Override
    public char getSymbol() {
        return 'L';
    }

    @Override
    public String onPlayerEnter(GameEngine engine, int x, int y) {
        // Logic for advancing to next level or winning handled in GameEngine
        return "You reached the ladder.";
    }
}
