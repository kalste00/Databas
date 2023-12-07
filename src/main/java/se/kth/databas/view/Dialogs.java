package se.kth.databas.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class Dialogs {

    public static void showRatingDialog(String bookTitle, Callback<Integer, Void> callback) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rate book");
        dialog.setHeaderText("Rate the book: " + bookTitle);
        dialog.setContentText("Input rate (1-5):");

        TextField ratingField = dialog.getEditor();

        ButtonType rateButtonType = new ButtonType("Rate", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().setAll(rateButtonType, ButtonType.CANCEL);

        dialog.getDialogPane().lookupButton(rateButtonType).addEventFilter(javafx.event.ActionEvent.ACTION, event -> {

            if (!isValidRating(ratingField.getText())) {
                event.consume();
                showAlert("Incorrect input. Rate between 1 and 5", Alert.AlertType.ERROR);
            }
        });

        dialog.showAndWait().ifPresent(result -> {
            if (isValidRating(result)) {
                callback.call(Integer.parseInt(result));
            }
        });
    }

    private static boolean isValidRating(String rating) {
        try {
            int value = Integer.parseInt(rating);
            return value >= 1 && value <= 5;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
