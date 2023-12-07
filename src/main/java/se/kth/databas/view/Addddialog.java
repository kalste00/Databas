package se.kth.databas.view;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ButtonType;
import javafx.util.Callback;
import se.kth.databas.model.Book;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class Addddialog {

    public static Optional<Book> showAddDialog() {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Add Book");
        dialog.setHeaderText("Enter book details:");

        // Set the button types
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Create and configure the title, isbn, publishDate, and rating fields.
        TextField titleField = new TextField();
        TextField isbnField = new TextField();
        DatePicker publishedDateField = new DatePicker();
        TextField ratingField = new TextField();

        GridPane grid = new GridPane();
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("ISBN:"), 0, 1);
        grid.add(isbnField, 1, 1);
        grid.add(new Label("Published Date:"), 0, 2);
        grid.add(publishedDateField, 1, 2);
        grid.add(new Label("Rating:"), 0, 3);
        grid.add(ratingField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a Book object when the add button is clicked
        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButton) {
                try {
                    String title = titleField.getText();
                    String isbn = isbnField.getText();
                    LocalDate publishedDate = publishedDateField.getValue();
                    int rating = Integer.parseInt(ratingField.getText());

                    // Create a Book object using the appropriate constructor
                    return new Book(rating, title, isbn, Date.valueOf(publishedDate));
                } catch (NumberFormatException e) {
                    showAlert("Invalid rating. Please enter a valid integer.", Alert.AlertType.ERROR);
                }
            }
            return null;
        });

        return dialog.showAndWait();
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
