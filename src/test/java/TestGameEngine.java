import dungeon.engine.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class TestGameEngine {
    @Test
    void testGetSize() {
        GameEngine ge = new GameEngine(10, 3);
        assertEquals(10, ge.getSize());
    }

    @Test
    void testPlayerStartsAtEntry() {
        GameEngine ge = new GameEngine(10, 3);
        int entryX = ge.getEntryX();
        int entryY = ge.getEntryY();
        assertEquals(ge.getPlayer(), ge.getMap()[entryX][entryY].getItem());
    }

    @Test
    void testMovePlayerIntoWall() {
        GameEngine ge = new GameEngine(10, 3);
        int x = ge.getEntryX();
        int y = ge.getEntryY();
        if (y + 1 < ge.getSize()) {
            ge.getMap()[x][y + 1].setItem(new Wall());
            String result = ge.movePlayer("RIGHT");
            assertTrue(result.contains("wall"));
        }
    }

    @Test
    void testGoldIncreasesScore() {
        GameEngine ge = new GameEngine(10, 3);
        int x = ge.getEntryX();
        int y = ge.getEntryY();
        if (x - 1 >= 0) {
            ge.getMap()[x - 1][y].setItem(new Gold());
            int oldScore = ge.getPlayer().getScore();
            ge.movePlayer("UP");
            assertTrue(ge.getPlayer().getScore() > oldScore);
        }
    }

    @Test
    void testTrapDecreasesHP() {
        GameEngine ge = new GameEngine(10, 3);
        int x = ge.getEntryX();
        int y = ge.getEntryY();
        if (x - 1 >= 0) {
            ge.getMap()[x - 1][y].setItem(new Trap());
            int oldHp = ge.getPlayer().getHp();
            ge.movePlayer("UP");
            assertTrue(ge.getPlayer().getHp() < oldHp);
        }
    }

    @Test
    void testHealthPotionRestoresHP() {
        GameEngine ge = new GameEngine(10, 3);
        int x = ge.getEntryX();
        int y = ge.getEntryY();
        ge.getPlayer().setHp(5);
        if (x - 1 >= 0) {
            ge.getMap()[x - 1][y].setItem(new HealPot());
            ge.movePlayer("UP");
            assertEquals(9, ge.getPlayer().getHp());
        }
    }

    @Test
    void testMeleeMutantDecreasesHPAndIncreasesScore() {
        GameEngine ge = new GameEngine(10, 3);
        int x = ge.getEntryX();
        int y = ge.getEntryY();
        int oldHp = ge.getPlayer().getHp();
        int oldScore = ge.getPlayer().getScore();
        if (x - 1 >= 0) {
            ge.getMap()[x - 1][y].setItem(new Melee());
            ge.movePlayer("UP");
            assertTrue(ge.getPlayer().getHp() < oldHp);
            assertTrue(ge.getPlayer().getScore() > oldScore);
        }
    }

    @Test
    void testRangedMutantDecreasesHPOrMisses() {
        GameEngine ge = new GameEngine(10, 3);
        int x = ge.getEntryX();
        int y = ge.getEntryY();
        if (x - 2 >= 0) {
            ge.getMap()[x - 2][y].setItem(new Ranged());
            int oldHp = ge.getPlayer().getHp();
            ge.movePlayer("UP");
            assertTrue(ge.getPlayer().getHp() <= oldHp);
        }
    }

    @Test
    void testGameOverByHP() {
        GameEngine ge = new GameEngine(10, 3);
        ge.getPlayer().setHp(1);
        int x = ge.getEntryX();
        int y = ge.getEntryY();
        if (x - 1 >= 0) {
            ge.getMap()[x - 1][y].setItem(new Trap());
            ge.movePlayer("UP");
            assertTrue(ge.isGameOver());
        }
    }

    @Test
    void testLadderAdvancesLevelOrWins() {
        GameEngine ge = new GameEngine(10, 3);
        int ladderX = ge.getLadderX();
        int ladderY = ge.getLadderY();
        ge.getMap()[ge.getEntryX()][ge.getEntryY()].setItem(null);
        ge.getMap()[ladderX][ladderY].setItem(ge.getPlayer());
        String result = ge.useLadder();
        assertTrue(result.contains("advanced") || result.contains("win"));
    }

    @Test
    void testLadderButtonRequiresPlayerOnLadder() {
        GameEngine ge = new GameEngine(10, 3);
        // Place player NOT on ladder
        int entryX = ge.getEntryX();
        int entryY = ge.getEntryY();
        int ladderX = ge.getLadderX();
        int ladderY = ge.getLadderY();
        // Ensure player is not on ladder
        assertFalse(entryX == ladderX && entryY == ladderY);
        // Simulate pressing the ladder button when not on ladder
        String result = ge.useLadder();
        assertEquals("You must be on the ladder to use it.", result);
        // Move player to ladder and try again
        ge.getMap()[entryX][entryY].setItem(null);
        ge.getMap()[ladderX][ladderY].setItem(ge.getPlayer());
        // Simulate pressing the ladder button when on ladder
        result = ge.useLadder();
        assertTrue(result.contains("advanced") || result.contains("win"));
    }
}
