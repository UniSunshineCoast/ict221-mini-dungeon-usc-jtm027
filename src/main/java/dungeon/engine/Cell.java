package dungeon.engine;

import javafx.scene.layout.StackPane;

public class Cell extends StackPane {
    private CellItem item;

    public CellItem getItem() {
        return item;
    }

    public void setItem(CellItem item) {
        this.item = item;
    }
    public Cell() {
        setPrefSize(50,50);
        setStyle("-fx-border-color: black; -fx-background-color: white;");
    }
}
