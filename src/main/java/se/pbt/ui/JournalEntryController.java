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
import se.pbt.model.JournalEntry;
import se.pbt.model.Trade;
import se.pbt.model.TradeSnapshot;
import se.pbt.service.JournalEntryService;
import se.pbt.service.ServiceLocator;

import java.io.IOException;
import java.time.LocalTime;

/**
 * JavaFX controller for the main Journal Entry view.
 * <p>
 * Handles UI interactions for adding, displaying, and saving
 * {@link TradeSnapshot} instances via {@link Trade} containers to a {@link JournalEntry}.
 * </p>
 */
public class JournalEntryController {

    @FXML private TextField commentField;

    @FXML private TableView<TradeSnapshot> snapshotTable;
    @FXML private TableColumn<TradeSnapshot, String> assetNameColumn;
    @FXML private TableColumn<TradeSnapshot, String> assetTypeColumn;
    @FXML private TableColumn<TradeSnapshot, Number> startValueColumn;
    @FXML private TableColumn<TradeSnapshot, Number> endValueColumn;
    @FXML private TableColumn<TradeSnapshot, Number> quantityColumn;
    @FXML private TableColumn<TradeSnapshot, Number> buyFeeColumn;
    @FXML private TableColumn<TradeSnapshot, Number> sellFeeColumn;
    @FXML private TableColumn<TradeSnapshot, LocalTime> buyTimeColumn;
    @FXML private TableColumn<TradeSnapshot, LocalTime> sellTimeColumn;
    @FXML private TableColumn<TradeSnapshot, String> notesColumn;

    private final ObservableList<TradeSnapshot> snapshots = FXCollections.observableArrayList();

    private JournalEntryService journalEntryService;
    private JournalEntry journalEntry;

    @FXML
    public void initialize() {
        snapshotTable.setItems(snapshots);
        snapshotTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        journalEntryService = ServiceLocator.getJournalEntryService();
        journalEntry = new JournalEntry();

        journalEntryService.getLatestEntry().ifPresentOrElse(entry -> {
            journalEntry = entry;
            journalEntry.getTrades().forEach(trade -> snapshots.addAll(trade.getSnapshots()));
        }, () -> journalEntry = new JournalEntry());

        assetNameColumn.setCellValueFactory(new PropertyValueFactory<>("assetName"));
        assetTypeColumn.setCellValueFactory(new PropertyValueFactory<>("assetType"));
        startValueColumn.setCellValueFactory(new PropertyValueFactory<>("startValue"));
        endValueColumn.setCellValueFactory(new PropertyValueFactory<>("endValue"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        buyFeeColumn.setCellValueFactory(new PropertyValueFactory<>("buyFee"));
        sellFeeColumn.setCellValueFactory(new PropertyValueFactory<>("sellFee"));
        buyTimeColumn.setCellValueFactory(new PropertyValueFactory<>("buyTime"));
        sellTimeColumn.setCellValueFactory(new PropertyValueFactory<>("sellTime"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
    }

    @FXML
    private void handleAddAsset() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se/pbt/ui/AddAssetDialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Asset");
            dialogStage.setScene(new Scene(loader.load()));
            dialogStage.showAndWait();

            AddAssetController controller = loader.getController();
            TradeSnapshot snapshot = controller.getResult();

            if (snapshot != null) {
                Trade trade = Trade.builder().label("New Trade").build();
                trade.addSnapshot(snapshot);

                journalEntry.addTrade(trade);

                snapshots.add(snapshot);
            }

        } catch (IOException e) {
            e.printStackTrace(); // TODO: Better error dialog
        }
    }


    @FXML
    private void handleSaveJournalEntry() {
        if (journalEntry != null && !journalEntry.getTrades().isEmpty()) {
            journalEntry.setNotes(commentField.getText().trim());
            journalEntry.setDate(java.time.LocalDate.now());
            journalEntryService.save(journalEntry);
            System.out.println("Saved JournalEntry with " + snapshots.size() + " snapshots.");
        } else {
            System.out.println("No trades to save.");
        }
    }

    @FXML
    public void onEditSnapshot() {
        // TODO
    }

    @FXML
    public void onDeleteSnapshot() {
        // TODO
    }
}
