package dungeon.engine;

public class Player implements CellItem {
    private int hp;
    private int score;
    int steps;

    public Player(int hp, int score) {
        this.hp = hp;
        this.score = score;
        this.steps = 0;
    }

    @Override
    public char getSymbol() {
        return 'P';
    }

    @Override
    public String onPlayerEnter(GameEngine engine, int x, int y) {
        // Player cannot enter itself; no effect
        return "You are already here.";
    }

    // Getters and setters for hp, score, steps
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getSteps() { return steps; }
    public void incrementSteps() { this.steps++; }
}
