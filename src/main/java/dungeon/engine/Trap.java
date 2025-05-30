package dungeon.engine;

public class Trap implements CellItem {
    @Override
    public char getSymbol() {
        return 'T';
    }

    @Override
    public String onPlayerEnter(GameEngine engine, int x, int y) {
        Player player = engine.getPlayer();
        player.setHp(player.getHp() - 2);
        return "You stepped on a trap and lost 2 HP. Current HP: " + player.getHp();
    }
}
