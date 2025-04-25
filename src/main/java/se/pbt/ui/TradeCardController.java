package se.pbt.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import se.pbt.model.Trade;

import java.math.BigDecimal;

public class TradeCardController {

    @FXML private VBox expandedInfo;
    @FXML private Button expandButton;

    @FXML private Label exchangeLabel;

    @FXML private Label assetNameLabel;
    @FXML private Label valueChangeLabel;
    @FXML private Label valueChangePctLabel;
    @FXML private Label totalValueLabel;

    @FXML private Label assetClassLabel;
    @FXML private Label quantityLabel;
    @FXML private Label snapshotCountLabel;

    private boolean expanded = false;

    public void setTrade(Trade trade) {
        assetNameLabel.setText(trade.getAsset().getName());

        BigDecimal valueChange = trade.calculateNetGain();
        BigDecimal changePct = trade.calculateNetGainPercentage();

        valueChangeLabel.setText("Change: " + valueChange + " SEK");
        valueChangePctLabel.setText("(" + changePct + " %)");
        totalValueLabel.setText("Current value: " + trade.calculateCurrentValue() + " SEK");

        // Color coding
        String color = valueChange.signum() > 0 ? "green" :
                valueChange.signum() < 0 ? "red" : "gray";
        valueChangeLabel.setStyle("-fx-text-fill: " + color);
        valueChangePctLabel.setStyle("-fx-text-fill: " + color);

        // Expanded info
        assetClassLabel.setText("Type: " + trade.getAsset().getAssetClass());
        quantityLabel.setText("Remaining: " + trade.getRemainingQuantity());
        snapshotCountLabel.setText("Day " + trade.getTradeSnapshots().size());
        exchangeLabel.setText("Exchange: " + trade.getAsset().getExchange().name());
    }


    @FXML
    private void toggleExpand() {
        expanded = !expanded;
        expandedInfo.setVisible(expanded);
        expandedInfo.setManaged(expanded);
        expandButton.setText(expanded ? "▲" : "▼");
    }
}
