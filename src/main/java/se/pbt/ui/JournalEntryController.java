package se.pbt.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import se.pbt.model.HoldingSnapshot;

public class JournalEntryController {

    @FXML private TableView<HoldingSnapshot> snapshotTable;
    @FXML private TableColumn<HoldingSnapshot, String> assetNameColumn;
    @FXML private TableColumn<HoldingSnapshot, String> assetTypeColumn;
    @FXML private TableColumn<HoldingSnapshot, Number> startValueColumn;

    private final ObservableList<HoldingSnapshot> snapshots = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        snapshotTable.setItems(snapshots);
        // TODO: bind columns to HoldingSnapshot fields using PropertyValueFactory
    }

    @FXML
    public void onAddSnapshot() {
        // TODO: Open form to add new snapshot
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
