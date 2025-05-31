package dungeon.engine;

import java.io.*;
import java.util.*;

public class GameEngine {
    private Map map;
    private Player player;
    private int playerX, playerY;
    private static final int MAX_STEPS = 100;
    private int difficulty;
    private int level = 1;
    private boolean gameOver = false;
    private boolean win = false;
    private int goldQuantity;
    private int trapQuantity;
    private int meleeQuantity;
    private int rangedQuantity;
    private int healthPotionQuantity;
    private int entryX, entryY;
    private int ladderX, ladderY;

    /**
     * Creates a square game board.
     *
     * @param size the width and height.
     */
    public GameEngine(int size, int difficulty) {
        this.difficulty = difficulty;
        this.goldQuantity = 5;
        this.trapQuantity = 5;
        this.meleeQuantity = 3;
        this.rangedQuantity = difficulty; //Only ranged mutants scale
        this.healthPotionQuantity = 2;
        map = new Map(size, 1); //Use level 1 layout
        player = new Player(10, 0); //Default HP 10, Score 0
        //Set entry at bottom left
        entryX = size - 1;
        entryY = 0;
        playerX = entryX;
        playerY = entryY;
        //Place random ladder (not at entry, not on wall)
        do {
            ladderX = (int) (Math.random() * map.getSize());
            ladderY = (int) (Math.random() * map.getSize());
        } while ((ladderX == entryX && ladderY == entryY) || map.getCell(ladderX, ladderY).getItem() instanceof Wall);
        //Place the player at the entry
        map.getCell(playerX, playerY).setItem(player);
        //Place all items, skipping entry and ladder cells
        placeRandomItems();
    }

    private void placeRandomItems() {
        placeRandom(Gold.class, goldQuantity);
        placeRandom(Trap.class, trapQuantity);
        placeRandom(Melee.class, meleeQuantity);
        placeRandom(Ranged.class, rangedQuantity);
        placeRandom(HealPot.class, healthPotionQuantity);
    }

    private void placeRandom(Class<? extends CellItem> clazz, int count) {
        int placed = 0;
        while (placed < count) {
            int x = (int) (Math.random() * map.getSize());
            int y = (int) (Math.random() * map.getSize());
            // Never place on entry or ladder
            if ((x == entryX && y == entryY) || (x == ladderX && y == ladderY)) continue;
            Cell cell = map.getCell(x, y);
            if (cell.getItem() == null && map.isPath(x, y, x, y)) {
                try {
                    cell.setItem(clazz.getDeclaredConstructor().newInstance());
                    placed++;
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void setDifficulty(int d) {
        this.difficulty = d;
        this.rangedQuantity = d;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getLevel() {
        return level;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isWin() {
        return win;
    }

    /**
     * The size of the current game.
     *
     * @return this is both the width and the height.
     */
    public int getSize() {
        return map.getSize();
    }

    /**
     * The map of the current game.
     *
     * @return the map, which is a 2d array.
     */
    public Cell[][] getMap() {
        return map.getCells();
    }

    /**
     * Returns the player of the current game.
     *
     * @return the player.
     */
    public Player getPlayer() {
        return player;
    }

    public int getEntryX() {
        return entryX;
    }

    public int getEntryY() {
        return entryY;
    }

    public int getLadderX() {
        return ladderX;
    }

    public int getLadderY() {
        return ladderY;
    }

    /**
     * Plays a text-based game
     */
    public static void main(String[] args) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        System.out.println("Welcome to MiniDungeon (Text Mode)!");
        System.out.print("Enter difficulty (0-10): ");
        int difficulty;
        try {
            difficulty = Integer.parseInt(scanner.nextLine().trim());
            if (difficulty < 0) difficulty = 0;
            if (difficulty > 10) difficulty = 10;
        } catch (Exception e) {
            System.out.println("Input error, defaulting to 3.");
            difficulty = 3;
        }
        GameEngine engine = new GameEngine(10, difficulty);
        System.out.println("New Game. Commands; up, down, left, right, help, quit");
        while (!engine.isGameOver()) {
            printMap(engine);
            System.out.print("Moved: ");
            String cmd = scanner.nextLine().trim().toLowerCase();
            switch (cmd) {
                case "quit" -> {
                    System.out.println(" Exiting game. Goodbye!");
                    break;
                }
                case "help" -> System.out.println("Move with up/down/left/right. 'use' to use ladder if on it. 'quit' to exit.");
                case "use" -> System.out.println(engine.useLadder());
                case "up", "down", "left", "right" -> System.out.println(engine.movePlayer(cmd));
                default -> System.out.println("Display options using 'help'. Invalid command");
            }
            if (cmd.equals("quit")) break;
        }
        if (engine.isGameOver()) {
            if (engine.isWin()) {
                System.out.println("Woo Congratulations! You won the game!");
            } else {
                System.out.println("Game over!");
            }
            engine.saveTopScore();
            System.out.println("Top Scores:");
            int rank = 1;
            for (String score : engine.getTopScores()) {
                System.out.println("#" + rank + ": " + score);
                rank++;
            }
        }
        scanner.close();
    }

    private static void printMap(GameEngine engine) {
        for (int i = 0; i < engine.getSize(); i++) {
            for (int j = 0; j < engine.getSize(); j++) {
                boolean isPlayer = (engine.getPlayer() != null && engine.getPlayer().equals(engine.getMap()[i][j].getItem()));
                boolean isEntry = (i == engine.getEntryX() && j == engine.getEntryY());
                boolean isLadder = (i == engine.getLadderX() && j == engine.getLadderY());
                if (isPlayer && isLadder) {
                    System.out.print("PL ");
                } else if (isPlayer) {
                    System.out.print("P ");
                } else if (isLadder) {
                    System.out.print("L ");
                } else if (isEntry) {
                    System.out.print("E ");
                } else {
                    var item = engine.getMap()[i][j].getItem();
                    if (item == null) System.out.print(". ");
                    else if (item instanceof Trap) System.out.print("T ");
                    else if (item instanceof Gold) System.out.print("G ");
                    else if (item instanceof Wall) System.out.print("# ");
                    else if (item instanceof HealPot) System.out.print("H ");
                    else if (item instanceof Melee) System.out.print("M ");
                    else if (item instanceof Ranged) System.out.print("R ");
                    else System.out.print("? ");
                }
            }
            System.out.println();
        }
        System.out.printf("Level: %d  Difficulty: %d  HP: %d  Score: %d  Steps: %d\n",
                engine.getLevel(), engine.getDifficulty(), engine.getPlayer().getHp(), engine.getPlayer().getScore(), engine.getPlayer().getSteps());
    }

    public String movePlayer(String direction) {
        if (gameOver) return win ? "You already won!" : "Game over! You can't move.";
        int newX = playerX;
        int newY = playerY;
        direction = direction.toUpperCase();
        switch (direction) {
            case "UP" -> newX--;
            case "DOWN" -> newX++;
            case "LEFT" -> newY--;
            case "RIGHT" -> newY++;
            default -> { return "Direction invalid."; }
        }
        if (newX < 0 || newX >= getSize() || newY < 0 || newY >= getSize()) {
            return "That move is out of bounds!";
        }
        if (!map.isPath(playerX, playerY, newX, newY)) {
            return " You can't move there, there's a wall!";
        }
        //Interact with item before moving
        Cell nextCell = map.getCell(newX, newY);
        CellItem item = nextCell.getItem();
        StringBuilder result = new StringBuilder();
        if (item != null && !(item instanceof Player)) {
            result.append(item.onPlayerEnter(this, newX, newY));
        }
        //Player moves to new position
        Cell currentCell = map.getCell(playerX, playerY);
        if (!(currentCell.getItem() instanceof Trap)) {
            currentCell.setItem(null);
        }
        playerX = newX;
        playerY = newY;
        Cell destCell = map.getCell(playerX, playerY);
        if (!(destCell.getItem() instanceof Trap)) {
            destCell.setItem(player);
        }
        player.incrementSteps();
        //Ranged mutant attacks (only if line of sight)
        for (int i = 0; i < map.getSize(); i++) {
            for (int j = 0; j < map.getSize(); j++) {
                CellItem ci = map.getCell(i, j).getItem();
                if (ci instanceof Ranged) {
                    if (hasLineOfSight(i, j, playerX, playerY)) {
                        String attackMsg = ((Ranged) ci).tryAttackPlayer(this, i, j, playerX, playerY);
                        if (attackMsg != null) result.append("\n").append(attackMsg);
                    }
                }
            }
        }
        if (player.getHp() <= 0) {
            gameOver = true;
            win = false;
            return "You lost all your HP! Game over!";
        }
        if (player.getSteps() >= MAX_STEPS) {
            gameOver = true;
            win = false;
            return "You ran out of steps! Game over!";
        }
        if (result.length() == 0) {
            return "You moved " + direction.toLowerCase() + ".";
        }
        return result.toString();
    }

    //Helper: true if no wall between (x1,y1) and (x2,y2) in straight line
    private boolean hasLineOfSight(int x1, int y1, int x2, int y2) {
        if (x1 == x2) {
            int min = Math.min(y1, y2), max = Math.max(y1, y2);
            for (int y = min + 1; y < max; y++) {
                if (map.getCell(x1, y).getItem() instanceof Wall) return false;
            }
            return true;
        } else if (y1 == y2) {
            int min = Math.min(x1, x2), max = Math.max(x1, x2);
            for (int x = min + 1; x < max; x++) {
                if (map.getCell(x, y1).getItem() instanceof Wall) return false;
            }
            return true;
        }
        return false;
    }

    public void saveGame() {
        try (PrintWriter out = new PrintWriter(new FileWriter("savegame.txt"))) {
            out.println(playerX + "," + playerY);
            out.println(player.getHp() + "," + player.getScore() + "," + player.getSteps());
            out.println(level + "," + difficulty);
            out.println(entryX + "," + entryY);
            out.println(ladderX + "," + ladderY);
            //Save map items (type and position)
            for (int i = 0; i < map.getSize(); i++) {
                for (int j = 0; j < map.getSize(); j++) {
                    CellItem item = map.getCell(i, j).getItem();
                    if (item != null && !(item instanceof Player)) {
                        out.println(i + "," + j + "," + item.getClass().getSimpleName());
                    }
                }
            }
            out.println("END");
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }

    public void loadGame() {
        try (BufferedReader in = new BufferedReader(new FileReader("savegame.txt"))) {
            String[] pos = in.readLine().split(",");
            playerX = Integer.parseInt(pos[0]);
            playerY = Integer.parseInt(pos[1]);
            String[] stats = in.readLine().split(",");
            player.setHp(Integer.parseInt(stats[0]));
            player.setScore(Integer.parseInt(stats[1]));
            player.steps = Integer.parseInt(stats[2]);
            String[] lvl = in.readLine().split(",");
            level = Integer.parseInt(lvl[0]);
            difficulty = Integer.parseInt(lvl[1]);
            String[] entry = in.readLine().split(",");
            entryX = Integer.parseInt(entry[0]);
            entryY = Integer.parseInt(entry[1]);
            String[] ladder = in.readLine().split(",");
            ladderX = Integer.parseInt(ladder[0]);
            ladderY = Integer.parseInt(ladder[1]);
            map = new Map(getSize(), level);
            for (int i = 0; i < map.getSize(); i++)
                for (int j = 0; j < map.getSize(); j++)
                    map.getCell(i, j).setItem(null);
            map.getCell(playerX, playerY).setItem(player);
            String line;
            while (!(line = in.readLine()).equals("END")) {
                String[] parts = line.split(",");
                int i = Integer.parseInt(parts[0]);
                int j = Integer.parseInt(parts[1]);
                String type = parts[2];
                CellItem item = switch (type) {
                    case "Gold" -> new Gold();
                    case "Trap" -> new Trap();
                    case "Melee" -> new Melee();
                    case "Ranged" -> new Ranged();
                    case "HealPot" -> new HealPot();
                    case "Wall" -> new Wall();
                    default -> null;
                };
                if (item != null) map.getCell(i, j).setItem(item);
            }
        } catch (IOException e) {
            System.err.println("Error loading game: " + e.getMessage());
        }
    }

    public String useLadder() {
        if (playerX == ladderX && playerY == ladderY) {
            if (level == 2) {
                win = true;
                gameOver = true;
                saveTopScore();
                return "You reached the ladder! You win!";
            } else {
                level = 2;
                this.difficulty += 2;
                this.goldQuantity = 5;
                this.trapQuantity = 5;
                this.meleeQuantity = 3;
                this.rangedQuantity = difficulty;
                this.healthPotionQuantity = 2;
                map = new Map(getSize(), 2);
                entryX = ladderX; entryY = ladderY;
                do {
                    ladderX = (int)(Math.random()*map.getSize());
                    ladderY = (int)(Math.random()*map.getSize());
                } while ((ladderX == entryX && ladderY == entryY) || map.getCell(ladderX, ladderY).getItem() instanceof Wall);
                playerX = entryX; playerY = entryY;
                map.getCell(playerX, playerY).setItem(player);
                placeRandomItems();
                return "You advanced to Level 2!";
            }
        } else {
            return "You must be on the ladder to use it.";
        }
    }

    public void saveTopScore() {
        try (PrintWriter out = new PrintWriter(new FileWriter("topscores.txt", true))) {
            out.println(player.getScore() + "," + new Date());
        } catch (IOException e) {
            System.err.println("Cannot save score." + e.getMessage());
        }
    }

    public List<String> getTopScores() {
        List<String> scores = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new FileReader("topscores.txt"))) {
            String line;
            while ((line = in.readLine()) != null) {
                scores.add(line);
            }
        } catch (IOException e) {

        }
        scores.sort((a, b) -> Integer.compare(
            Integer.parseInt(b.split(",")[0]),
            Integer.parseInt(a.split(",")[0])
        ));
        return scores.size() > 10 ? scores.subList(0, 10) : scores;
    }

    public String getCellIcon(int i, int j) {
        //Show player if present
        if (playerX == i && playerY == j) return "/player.png";
        //Show entry/ladder icons based on coordinates
        if (i == entryX && j == entryY) {
            if (level == 1) {
                return "/entry.png";
            } else {
                return "/trapdoor.png";
            }
        }
        if (i == ladderX && j == ladderY) return "/ladder.png";
        //Otherwise, show item icon
        CellItem item = map.getCell(i, j).getItem();
        if (item instanceof Gold) return "/gold.png";
        if (item instanceof Trap) return "/trap.png";
        if (item instanceof Wall) return "/wall.png";
        if (item instanceof HealPot) return "/health.png";
        if (item instanceof Melee) return "/melee.png";
        if (item instanceof Ranged) return "/ranged.png";
        return "";
    }
}
