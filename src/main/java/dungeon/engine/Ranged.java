package dungeon.engine;

import java.util.Random;

public class Ranged implements CellItem {
    private static final Random random = new Random();

    @Override
    public char getSymbol() {
        return 'R';
    }

    @Override
    public String onPlayerEnter(GameEngine engine, int x, int y) {
        Player player = engine.getPlayer();
        player.setScore(player.getScore() + 2);
        engine.getMap()[x][y].setItem(null); // Remove mutant from cell
        return "The battle with a ranged mutant was fierce, but you emerged victorious and gained 2 points.";
    }

    /**
     * Called by GameEngine to check if the player is in range and attack.
     */
    public String tryAttackPlayer(GameEngine engine, int mutantX, int mutantY, int playerX, int playerY) {
        if ((Math.abs(mutantX - playerX) == 2 && mutantY == playerY) ||
            (Math.abs(mutantY - playerY) == 2 && mutantX == playerX)) {
            if (random.nextBoolean()) {
                engine.getPlayer().setHp(engine.getPlayer().getHp() - 2);
                return "A ranged mutant attacked you and dealt 2 damage. You now have " + engine.getPlayer().getHp() + " HP left.";
            } else {
                return "A ranged mutant tried to attack you, but you dodged the attack!";
            }
        }
        return null;
    }
}
