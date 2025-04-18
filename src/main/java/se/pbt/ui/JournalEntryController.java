package se.pbt.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import se.pbt.model.HoldingSnapshot;
import se.pbt.model.JournalEntry;
import se.pbt.service.JournalEntryService;
import se.pbt.service.ServiceLocator;

import java.io.IOException;

/**
 * JavaFX controller for the main Journal Entry view.
 * <p>
 * Handles UI interactions for adding, displaying, and saving
 * {@link HoldingSnapshot} instances to a {@link JournalEntry}.
 * <\p>
 */
public class JournalEntryController {

    @FXML private TextField commentField;

    @FXML private TableView<HoldingSnapshot> snapshotTable; // TODO: Add more columns and other info
    @FXML private TableColumn<HoldingSnapshot, String> assetNameColumn;
    @FXML private TableColumn<HoldingSnapshot, String> assetTypeColumn;
    @FXML private TableColumn<HoldingSnapshot, Number> startValueColumn;

    private final ObservableList<HoldingSnapshot> snapshots = FXCollections.observableArrayList();

    private JournalEntryService journalEntryService;
    private JournalEntry journalEntry;

    /**
     * Initializes the controller after its FXML file has been loaded.
     * Binds table columns to snapshot properties and initializes a new journal entry.
     */
    @FXML
    public void initialize() {
        snapshotTable.setItems(snapshots);
        journalEntryService = ServiceLocator.getJournalEntryService();
        journalEntry = new JournalEntry();

        assetNameColumn.setCellValueFactory(new PropertyValueFactory<>("assetName"));
        assetTypeColumn.setCellValueFactory(new PropertyValueFactory<>("assetType"));
        startValueColumn.setCellValueFactory(new PropertyValueFactory<>("startValue"));
    }

    /**
     * Opens a dialog window for adding a new asset.
     * If confirmed, the resulting snapshot is added to both the journal entry and the table.
     */
    @FXML
    private void handleAddAsset() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se/pbt/ui/AddAssetDialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Asset");
            dialogStage.setScene(new Scene(loader.load()));
            dialogStage.showAndWait();

            AddAssetController controller = loader.getController();
            HoldingSnapshot snapshot = controller.getResult();

            if (snapshot != null) {
                journalEntry.addSnapshot(snapshot);
                snapshotTable.getItems().add(snapshot);
            }
        } catch (IOException e) {
            e.printStackTrace(); // TODO: Replace with better error handling
        }
    }

    @FXML
    private void handleSaveJournalEntry() {
        if (journalEntry != null && !journalEntry.getSnapshots().isEmpty()) {
            journalEntry.setComment(commentField.getText().trim());
            journalEntry.setDate(java.time.LocalDate.now());
            journalEntryService.save(journalEntry);
            System.out.println("JournalEntry saved with " + journalEntry.getSnapshots().size() + " snapshots.");
            // TODO: Add better save confirmation for user
        } else {
            System.out.println("No data to save.");
        }
    }

    @FXML
    public void onEditSnapshot() {
        // TODO: Expand or open editor for selected snapshot
    }

    @FXML
    public void onDeleteSnapshot() {
        // TODO: Remove selected snapshot from table and data
    }
}
