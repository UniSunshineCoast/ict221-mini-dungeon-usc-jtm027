package dungeon.engine;

public class HealPot implements CellItem {
    @Override
    public char getSymbol() {
        return 'H';
    }

    @Override
    public String onPlayerEnter(GameEngine engine, int x, int y) {
        Player player = engine.getPlayer();
        int newHp = Math.min(player.getHp() + 4, 10);
        player.setHp(newHp);
        engine.getMap()[x][y].setItem(null); // Remove potion from cell
        return "You drank a health potion and restored 4 HP.";
    }
}
