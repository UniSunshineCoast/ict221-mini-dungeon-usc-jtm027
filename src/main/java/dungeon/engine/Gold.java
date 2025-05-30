package dungeon.engine;

public class Gold implements CellItem {
    @Override
    public char getSymbol() {
        return 'G';
    }

    @Override
    public String onPlayerEnter(GameEngine engine, int x, int y) {
        engine.getPlayer().setScore(engine.getPlayer().getScore() + 2);
        engine.getMap()[x][y].setItem(null); // Remove gold from cell
        return "You picked up a gold. Current score: " + engine.getPlayer().getScore();
    }
}
