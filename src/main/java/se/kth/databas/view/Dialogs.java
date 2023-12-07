package se.kth.databas.view;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import se.kth.databas.model.Book;
import se.kth.databas.model.Genre;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

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

    public static Optional<Book> showAddDialog() {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Add Book");
        dialog.setHeaderText("Enter book details:");

        // Set the button types
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Create and configure the title, isbn, publishDate, rating, and genre fields.
        TextField titleField = new TextField();
        TextField isbnField = new TextField();
        DatePicker publishedDateField = new DatePicker();
        TextField ratingField = new TextField();

        // Use ChoiceBox for genre
        ChoiceBox<Genre> genreChoiceBox = new ChoiceBox<>();
        genreChoiceBox.getItems().addAll(Genre.values());

        GridPane grid = new GridPane();
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("ISBN:"), 0, 1);
        grid.add(isbnField, 1, 1);
        grid.add(new Label("Published Date:"), 0, 2);
        grid.add(publishedDateField, 1, 2);
        grid.add(new Label("Rating:"), 0, 3);
        grid.add(ratingField, 1, 3);
        grid.add(new Label("Genre:"), 0, 4);
        grid.add(genreChoiceBox, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a Book object when the add button is clicked
        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButton) {
                try {
                    String title = titleField.getText();
                    String isbn = isbnField.getText();
                    LocalDate publishedDate = publishedDateField.getValue();
                    int rating = Integer.parseInt(ratingField.getText());
                    Genre selectedGenre = genreChoiceBox.getValue();

                    // Create a Book object using the appropriate constructor
                    return new Book(title, isbn, Date.valueOf(publishedDate), selectedGenre, rating);
                } catch (NumberFormatException e) {
                    showAlert("Invalid rating. Please enter a valid integer.", Alert.AlertType.ERROR);
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static Optional<Book> showUpdateDialog(Book book) {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Update Book");
        dialog.setHeaderText("Update book details:");

        // Set the button types
        ButtonType updateButton = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButton, ButtonType.CANCEL);

        // Create and configure the title, isbn, publishDate, rating, and genre fields.
        TextField titleField = new TextField(book.getTitle());
        TextField isbnField = new TextField(book.getIsbn());
        DatePicker publishedDateField = new DatePicker(book.getPublishDate().toLocalDate());
        TextField ratingField = new TextField(String.valueOf(book.getRating()));

        // Use ChoiceBox for genre
        ChoiceBox<Genre> genreChoiceBox = new ChoiceBox<>();
        genreChoiceBox.getItems().addAll(Genre.values());
        genreChoiceBox.setValue(book.getGenre());

        GridPane grid = new GridPane();
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("ISBN:"), 0, 1);
        grid.add(isbnField, 1, 1);
        grid.add(new Label("Published Date:"), 0, 2);
        grid.add(publishedDateField, 1, 2);
        grid.add(new Label("Rating:"), 0, 3);
        grid.add(ratingField, 1, 3);
        grid.add(new Label("Genre:"), 0, 4);
        grid.add(genreChoiceBox, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a Book object when the update button is clicked
        dialog.setResultConverter(buttonType -> {
            if (buttonType == updateButton) {
                try {
                    String title = titleField.getText();
                    String isbn = isbnField.getText();
                    LocalDate publishedDate = publishedDateField.getValue();
                    int rating = Integer.parseInt(ratingField.getText());
                    Genre selectedGenre = genreChoiceBox.getValue();

                    // Update the Book object with new values
                    book.setTitle(title);
                    book.setIsbn(isbn);
                    book.setPublishDate(Date.valueOf(publishedDate));
                    book.setRating(rating);
                    book.setGenre(selectedGenre);

                    return book;
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