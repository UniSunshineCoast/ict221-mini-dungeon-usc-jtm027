package dungeon.engine;

public class Melee implements CellItem {
    @Override
    public char getSymbol() {
        return 'M';
    }

    @Override
    public String onPlayerEnter(GameEngine engine, int x, int y) {
        Player player = engine.getPlayer();
        player.setHp(player.getHp() - 2);
        player.setScore(player.getScore() + 2);
        engine.getMap()[x][y].setItem(null); // Remove mutant from cell
        return "A melee mutant attacked you! Lost 2 HP but gained 2 Score.";
    }
}
