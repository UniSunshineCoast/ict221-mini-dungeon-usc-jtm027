package dungeon.gui;

import dungeon.engine.Cell;
import dungeon.engine.GameEngine;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Controller {
    @FXML private GridPane gridPane;
    @FXML private Label hpLabel;
    @FXML private Label scoreLabel;
    @FXML private Label stepsLabel;
    @FXML private Button upButton;
    @FXML private Button downButton;
    @FXML private Button leftButton;
    @FXML private Button rightButton;
    @FXML private Button helpButton;
    @FXML private Button saveButton;
    @FXML private Button loadButton;
    @FXML private Button useLadderButton;
    @FXML private TextArea statusArea;
    @FXML private ListView<String> topScoresList;
    @FXML private TextField difficultyField;
    @FXML private Button runButton;

    private GameEngine engine;
    private ObservableList<String> topScores = FXCollections.observableArrayList();
    private int difficulty = 3;

    @FXML
    public void initialize() {
        setGameControlsDisabled(true);
        setupButtonHandlers();
        loadAndDisplayTopScores();
    }

    private void setupButtonHandlers() {
        upButton.setOnAction(e -> movePlayer("UP"));
        downButton.setOnAction(e -> movePlayer("DOWN"));
        leftButton.setOnAction(e -> movePlayer("LEFT"));
        rightButton.setOnAction(e -> movePlayer("RIGHT"));
        helpButton.setOnAction(e -> showHelp());
        saveButton.setOnAction(e -> saveGame());
        loadButton.setOnAction(e -> loadGame());
        useLadderButton.setOnAction(e -> useLadder());
        runButton.setOnAction(e -> startNewGame());
    }

    private void setGameControlsDisabled(boolean disabled) {
        upButton.setDisable(disabled);
        downButton.setDisable(disabled);
        leftButton.setDisable(disabled);
        rightButton.setDisable(disabled);
        helpButton.setDisable(disabled);
        saveButton.setDisable(disabled);
        loadButton.setDisable(disabled);
        useLadderButton.setDisable(true); // Always start disabled
    }

    private void movePlayer(String direction) {
        String status = engine.movePlayer(direction);
        if (!engine.isGameOver()) {
            statusArea.appendText(status + "\n");
        }
        updateGui();
    }

    private void showHelp() {
        if (!engine.isGameOver()) {
            statusArea.appendText("Use the arrow buttons to move. Collect gold, avoid traps, and reach the ladder!\n");
        }
    }

    private void saveGame() {
        engine.saveGame();
        if (!engine.isGameOver()) {
            statusArea.appendText("Game saved.\n");
        }
    }

    private void loadGame() {
        engine.loadGame();
        if (!engine.isGameOver()) {
            statusArea.appendText("Game loaded.\n");
        }
        updateGui();
    }

    private void useLadder() {
        // Only allow if player is on ladder
        if (engine.getPlayer().equals(engine.getMap()[engine.getLadderX()][engine.getLadderY()].getItem())) {
            String result = engine.useLadder();
            if (!engine.isGameOver()) {
                statusArea.appendText(result + "\n");
            }
            updateGui();
        } else {
            if (!engine.isGameOver()) {
                statusArea.appendText("You must be on the ladder to use it.\n");
            }
        }
    }

    private void startNewGame() {
        try {
            int d = Integer.parseInt(difficultyField.getText().trim());
            if (d < 0) d = 0;
            if (d > 10) d = 10;
            difficulty = d;
        } catch (Exception ex) {
            difficulty = 3;
            difficultyField.setText("3");
        }
        engine = new GameEngine(10, difficulty);
        statusArea.clear();
        setGameControlsDisabled(false);
        updateGui();
        loadAndDisplayTopScores();
    }

    private void updateGui() {
        // Update stats
        hpLabel.setText(String.valueOf(engine.getPlayer().getHp()));
        scoreLabel.setText(String.valueOf(engine.getPlayer().getScore()));
        stepsLabel.setText(String.valueOf(engine.getPlayer().getSteps()));
        // Update grid
        gridPane.getChildren().clear();
        boolean onLadder = (engine.getPlayer().equals(engine.getMap()[engine.getLadderX()][engine.getLadderY()].getItem()));
        useLadderButton.setDisable(!onLadder);
        if (onLadder && !engine.isGameOver()) {
            statusArea.appendText("You are on the ladder. Press the 'Use Ladder' button to advance.\n");
        }
        // Show win/game over message if game is over
        if (engine.isGameOver()) {
            if (engine.isWin()) {
                statusArea.appendText("Congratulations! You win!\n");
            } else {
                statusArea.appendText("Game over!\n");
            }
        }
        for (int i = 0; i < engine.getSize(); i++) {
            for (int j = 0; j < engine.getSize(); j++) {
                String iconPath = engine.getCellIcon(i, j);
                // If player is on the ladder cell, show player icon
                if (engine.getPlayer().equals(engine.getMap()[i][j].getItem()) && i == engine.getLadderX() && j == engine.getLadderY()) {
                    iconPath = "/player.png";
                }
                // Try both with and without leading slash for image loading
                Image imgObj = null;
                if (iconPath != null && !iconPath.isEmpty()) {
                    java.io.InputStream imgStream = getClass().getResourceAsStream(iconPath);
                    if (imgStream == null && iconPath.startsWith("/")) {
                        imgStream = getClass().getResourceAsStream(iconPath.substring(1));
                    }
                    if (imgStream == null && !engine.isGameOver()) {
                        statusArea.appendText("Image not found: " + iconPath + "\n");
                    } else if (imgStream != null) {
                        imgObj = new Image(imgStream);
                    }
                }
                if (imgObj != null) {
                    ImageView img = new ImageView(imgObj);
                    img.setFitWidth(48);
                    img.setFitHeight(48);
                    StackPane cellPane = new StackPane();
                    cellPane.setPrefSize(56, 56);
                    cellPane.setStyle("-fx-border-color: black; -fx-background-color: white;");
                    cellPane.getChildren().add(img);
                    gridPane.add(cellPane, j, i);
                } else {
                    StackPane cellPane = new StackPane();
                    cellPane.setPrefSize(56, 56);
                    cellPane.setStyle("-fx-border-color: black; -fx-background-color: white;");
                    gridPane.add(cellPane, j, i);
                }
            }
        }
        loadAndDisplayTopScores();
    }

    private void loadAndDisplayTopScores() {
        if (engine == null) return;
        topScores.clear();
        for (String score : engine.getTopScores()) {
            topScores.add(score);
        }
        topScoresList.setItems(topScores);
    }
}
