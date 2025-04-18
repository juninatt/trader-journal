package se.pbt.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;
import se.pbt.model.HoldingSnapshot;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Controller for the Add Asset dialog.
 * <p>
 * Responsible for capturing user input, validating it, and creating
 * a {@link HoldingSnapshot} based on the form fields.
 * </p>
 */
public class AddAssetController {

    @FXML private TextField assetNameField;
    @FXML private TextField assetTypeField;
    @FXML private TextField buyTimeField;
    @FXML private TextField sellTimeField;
    @FXML private TextField startValueField;
    @FXML private TextField endValueField;
    @FXML private TextField quantityField;
    @FXML private TextField buyFeeField;
    @FXML private TextField sellFeeField;
    @FXML private TextField notesField;

    /**
     * The result of the dialog once the user presses Save.
     * Will be {@code null} if the dialog is closed without saving.
     */
    @Getter
    private HoldingSnapshot result;

    /**
     * Handles the Save button click.
     * <p>
     * Attempts to construct a {@link HoldingSnapshot} from input fields.
     * If successful, sets the result and closes the dialog.
     * </p>
     */
    @FXML
    private void onSave() {
        try {
            result = HoldingSnapshot.builder()
                    .assetName(assetNameField.getText())
                    .assetType(assetTypeField.getText())
                    .buyTime(LocalTime.parse(buyTimeField.getText()))
                    .sellTime(parseNullableTime(sellTimeField.getText()))
                    .startValue(new BigDecimal(startValueField.getText()))
                    .endValue(parseNullableBigDecimal(endValueField.getText()))
                    .quantity(Integer.parseInt(quantityField.getText()))
                    .buyFee(Double.parseDouble(buyFeeField.getText()))
                    .sellFee(Double.parseDouble(sellFeeField.getText()))
                    .notes(notesField.getText())
                    .build();

            close();
        } catch (Exception e) {
            e.printStackTrace(); // TODO: Replace with validation feedback
        }
    }

    /**
     * Handles the "Cancel" button click.
     * <p>
     * Resets the result and closes the dialog window without saving.
     * </p>
     */
    @FXML
    private void onCancel() {
        result = null;
        close();
    }

    /**
     * Closes the current dialog window.
     */
    private void close() {
        Stage stage = (Stage) assetNameField.getScene().getWindow();
        stage.close();
    }

    /**
     * Tries to parse the input string into a LocalDateTime.
     * Returns null if the input is empty or not a valid date-time.
     */
    private LocalTime parseNullableTime(String input) {
        if (input == null || input.isBlank()) return null;
        try {
            return LocalTime.parse(input.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Tries to parse the input string into a BigDecimal.
     * Returns null if the input is empty.
     */
    private BigDecimal parseNullableBigDecimal(String input) {
        if (input == null || input.isBlank()) return null;
        return new BigDecimal(input.trim());
    }
}
