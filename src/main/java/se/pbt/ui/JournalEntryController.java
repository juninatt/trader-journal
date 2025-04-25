package se.pbt.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import se.pbt.model.JournalEntry;
import se.pbt.model.Trade;
import se.pbt.model.TradeSnapshot;
import se.pbt.service.JournalEntryService;
import se.pbt.service.ServiceLocator;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class JournalEntryController {

    @FXML private TextField commentField;
    @FXML private Label cashLabel;
    @FXML private Label investedLabel;

    @FXML private VBox previousTradesBox;
    @FXML private VBox currentTradesBox;

    private JournalEntry journalEntry;
    private JournalEntryService journalEntryService;

    @FXML
    public void initialize() {
        journalEntryService = ServiceLocator.getJournalEntryService();

        journalEntryService.getLatestEntry().ifPresentOrElse(entry -> {
            journalEntry = new JournalEntry();
            populateTradeCards(entry, previousTradesBox);

            cashLabel.setText(entry.getAvailableCash() != null ? entry.getAvailableCash().toString() : "");
            investedLabel.setText(entry.getInvestedCapital() != null ? entry.getInvestedCapital().toString() : "");
        }, () -> journalEntry = new JournalEntry());

    }

    private void populateTradeCards(JournalEntry entry, VBox container) {
        container.getChildren().clear();
        for (Trade trade : entry.getTrades()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/se/pbt/ui/TradeCard.fxml"));
                Node tradeCard = loader.load();

                TradeCardController controller = loader.getController();
                controller.setTrade(trade);

                container.getChildren().add(tradeCard);
            } catch (IOException e) {
                e.printStackTrace(); // TODO: Add dialog in future
            }
        }
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
                snapshot.setJournalEntry(journalEntry);

                Trade trade = snapshot.getTrade();
                trade.addSnapshot(snapshot);

                journalEntry.addTradeSnapshot(snapshot);

                // Update today's view
                populateTradeCards(journalEntry, currentTradesBox);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSaveJournalEntry() {
        if (journalEntry != null && !journalEntry.getTradeSnapshots().isEmpty()) {
            journalEntry.setEntryText(commentField.getText().trim());
            journalEntry.setDate(LocalDate.now());

            try {
                BigDecimal cash = new BigDecimal(cashLabel.getText().trim());
                BigDecimal invested = new BigDecimal(investedLabel.getText().trim());

                journalEntry.setAvailableCash(cash);
                journalEntry.setInvestedCapital(invested);
            } catch (NumberFormatException e) {
                System.err.println("Invalid number in cash/invested fields.");
                return;
            }

            journalEntryService.save(journalEntry);

            System.out.println("Saved JournalEntry with " + journalEntry.getTradeSnapshots().size() + " snapshots.");
        } else {
            System.out.println("No trades to save.");
        }
    }

    @FXML
    public void onEditSnapshot() {
        // TODO: To be implemented
    }

    @FXML
    public void onDeleteSnapshot() {
        // TODO: To be implemented
    }
}
