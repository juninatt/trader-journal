package se.pbt.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX application entry point for launching the Trader Journal graphical interface.
 * <p>
 * Loads the {@code JournalEntryView.fxml} layout and sets the primary stage.
 * This class is responsible for initializing and displaying the user interface.
 */
public class TraderJournalGui extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/se/pbt/ui/JournalEntryView.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Trader Journal");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Launches the JavaFX application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}

