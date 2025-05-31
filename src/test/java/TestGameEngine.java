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
        // Move player to ladder position
        ge.getMap()[ge.getEntryX()][ge.getEntryY()].setItem(null);
        ge.getMap()[ladderX][ladderY].setItem(ge.getPlayer());
        // Set player coordinates to ladder
        // (simulate player standing on ladder)
        try {
            java.lang.reflect.Field px = GameEngine.class.getDeclaredField("playerX");
            java.lang.reflect.Field py = GameEngine.class.getDeclaredField("playerY");
            px.setAccessible(true);
            py.setAccessible(true);
            px.setInt(ge, ladderX);
            py.setInt(ge, ladderY);
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
        String result = ge.useLadder();
        assertTrue(result.contains("advanced") || result.contains("win"));
    }

    @Test
    void testLadderButtonRequiresPlayerOnLadder() {
        GameEngine ge = new GameEngine(10, 3);
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
        // Set player coordinates to ladder
        try {
            java.lang.reflect.Field px = GameEngine.class.getDeclaredField("playerX");
            java.lang.reflect.Field py = GameEngine.class.getDeclaredField("playerY");
            px.setAccessible(true);
            py.setAccessible(true);
            px.setInt(ge, ladderX);
            py.setInt(ge, ladderY);
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
        result = ge.useLadder();
        assertTrue(result.contains("advanced") || result.contains("win"));
    }

    // --- Additional unit tests for dungeon.engine classes ---
    @Test
    void testCellSetAndGetItem() {
        Cell cell = new Cell();
        Gold gold = new Gold();
        cell.setItem(gold);
        assertEquals(gold, cell.getItem());
    }

    @Test
    void testEntryMethods() {
        Entry entry = new Entry();
        assertEquals('E', entry.getSymbol());
        GameEngine ge = new GameEngine(10, 3);
        assertTrue(entry.onPlayerEnter(ge, 0, 0).contains("entry"));
    }

    @Test
    void testGoldMethods() {
        Gold gold = new Gold();
        assertEquals('G', gold.getSymbol());
        GameEngine ge = new GameEngine(10, 3);
        int x = ge.getEntryX();
        int y = ge.getEntryY();
        ge.getMap()[x][y].setItem(gold);
        String msg = gold.onPlayerEnter(ge, x, y);
        assertTrue(msg.contains("gold"));
    }

    @Test
    void testHealPotMethods() {
        HealPot pot = new HealPot();
        assertEquals('H', pot.getSymbol());
        GameEngine ge = new GameEngine(10, 3);
        int x = ge.getEntryX();
        int y = ge.getEntryY();
        ge.getPlayer().setHp(5);
        ge.getMap()[x][y].setItem(pot);
        String msg = pot.onPlayerEnter(ge, x, y);
        assertTrue(msg.contains("restored"));
    }

    @Test
    void testLadderMethods() {
        Ladder ladder = new Ladder();
        assertEquals('L', ladder.getSymbol());
        GameEngine ge = new GameEngine(10, 3);
        assertTrue(ladder.onPlayerEnter(ge, 0, 0).contains("ladder"));
    }

    @Test
    void testMeleeMethods() {
        Melee melee = new Melee();
        assertEquals('M', melee.getSymbol());
        GameEngine ge = new GameEngine(10, 3);
        int x = ge.getEntryX();
        int y = ge.getEntryY();
        ge.getMap()[x][y].setItem(melee);
        int oldHp = ge.getPlayer().getHp();
        int oldScore = ge.getPlayer().getScore();
        String msg = melee.onPlayerEnter(ge, x, y);
        assertTrue(msg.contains("mutant"));
        assertTrue(ge.getPlayer().getHp() < oldHp);
        assertTrue(ge.getPlayer().getScore() > oldScore);
    }

    @Test
    void testPlayerMethods() {
        Player player = new Player(10, 0);
        assertEquals('P', player.getSymbol());
        GameEngine ge = new GameEngine(10, 3);
        String msg = player.onPlayerEnter(ge, 0, 0);
        assertTrue(msg.contains("already here"));
        int steps = player.getSteps();
        player.incrementSteps();
        assertEquals(steps + 1, player.getSteps());
    }

    @Test
    void testRangedMethods() {
        Ranged ranged = new Ranged();
        assertEquals('R', ranged.getSymbol());
        GameEngine ge = new GameEngine(10, 3);
        int x = ge.getEntryX();
        int y = ge.getEntryY();
        ge.getMap()[x][y].setItem(ranged);
        String msg = ranged.onPlayerEnter(ge, x, y);
        assertTrue(msg.contains("mutant"));
        // tryAttackPlayer: test both hit and miss
        String attackMsg = ranged.tryAttackPlayer(ge, x, y, x + 2, y);
        assertNotNull(attackMsg);
    }

    @Test
    void testTrapMethods() {
        Trap trap = new Trap();
        assertEquals('T', trap.getSymbol());
        GameEngine ge = new GameEngine(10, 3);
        int x = ge.getEntryX();
        int y = ge.getEntryY();
        ge.getMap()[x][y].setItem(trap);
        int oldHp = ge.getPlayer().getHp();
        String msg = trap.onPlayerEnter(ge, x, y);
        assertTrue(msg.contains("trap"));
        assertTrue(ge.getPlayer().getHp() < oldHp);
    }

    @Test
    void testMapIsPathAndGetCell() {
        Map map = new Map(5, 1);
        assertTrue(map.isPath(0, 0, 0, 0));
        assertNotNull(map.getCell(0, 0));
        assertNull(map.getCell(-1, -1));
    }
}
