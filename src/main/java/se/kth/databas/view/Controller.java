package se.kth.databas.view;

import javafx.application.Platform;
import se.kth.databas.model.Book;
import se.kth.databas.model.BooksDbInterface;
import se.kth.databas.model.Genre;
import se.kth.databas.model.SearchMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import static javafx.scene.control.Alert.AlertType.*;

/**
 * The controller is responsible for handling user requests and updating the view
 * (and in some cases the model).
 *
 * @author anderslm@kth.se
 */
public class Controller {

    private final BooksPane booksView; // view
    private final BooksDbInterface booksDb; // model
    private final Semaphore updateSemaphore = new Semaphore(1);


    public Controller(BooksDbInterface booksDb, BooksPane booksView) {
        this.booksDb = booksDb;
        this.booksView = booksView;
    }

    protected void onSearchSelected(String searchFor, SearchMode mode) {
        try {
            List<Book> result;
            switch (mode) {
                case Title:
                    result = booksDb.searchBooksByTitle(searchFor);
                    break;
                case ISBN:
                    result = booksDb.searchBooksByISBN(searchFor);
                    break;
                case Author:
                    result = booksDb.searchBooksByAuthor(searchFor);
                    break;
                case AllBooks:
                    result = booksDb.getAllBooks();
                    break;
                default:
                    result = new ArrayList<>();
            }
            if (result == null || result.isEmpty()) {
                booksView.showAlertAndWait("No results found.", INFORMATION);
            } else {
                booksView.displayBooks(result);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Add proper logging or print statements for debugging
            booksView.showAlertAndWait("Database error.", ERROR);
        }
    }

    public void connectToDatabase() {
        try {
            if (booksDb.connect("kcdb2")) {
                booksView.showAlertAndWait("Connected to the database.", INFORMATION);
            } else {
                booksView.showAlertAndWait("Failed to connect to the database.", WARNING);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Error connecting to the database: " + e.getMessage(), ERROR);
        }
    }

    public void disconnectFromDatabase() {
        try {
            booksDb.disconnect();
            booksView.showAlertAndWait("Disconnected from the database.", INFORMATION);
        } catch (Exception e) {
            booksView.showAlertAndWait("Error disconnecting from the database: " + e.getMessage(), ERROR);
        }
    }

    public void rateBook(Book book) {
        Dialogs.showRatingDialog(book.getTitle(), rating -> {
            book.setRating(rating);
            updateItem(book);
            return null;
        });
    }

    public void deleteBooks(Book bookToDelete) {
        new Thread(() -> {
            try {
                if (booksDb.isConnected()) {
                    if (bookToDelete != null) {
                        booksDb.deleteBook(bookToDelete);
                        List<Book> updatedBooks = booksDb.getAllBooks();
                        Platform.runLater(() -> booksView.displayBooks(updatedBooks));
                    } else {
                        Platform.runLater(() -> booksView.showAlertAndWait("Select a book to remove.", WARNING));
                    }
                } else {
                    Platform.runLater(() -> booksView.showAlertAndWait("Not connected", ERROR));
                }
            } catch (Exception e) {
                Platform.runLater(() -> booksView.showAlertAndWait("Error removing item: " + e.getMessage(), ERROR));
            }
        }).start();
    }

    public void addItem(Book newItem) {
        new Thread(() -> {
            try {
                if (booksDb.isConnected()) {
                    if (newItem != null && newItem.getGenre() != null) {
                        booksDb.addBook(newItem);
                        List<Book> updatedBooks = booksDb.getAllBooks();
                        Platform.runLater(() -> booksView.displayBooks(updatedBooks));
                        if (newItem.getGenre() == null) {
                            newItem.setGenre(Genre.None);
                        }
                    } else {
                        Platform.runLater(() -> booksView.showAlertAndWait("Enter book details to add.", WARNING));
                    }
                } else {
                    Platform.runLater(() -> booksView.showAlertAndWait("Not connected", ERROR));
                }
            } catch (Exception e) {
                Platform.runLater(() -> booksView.showAlertAndWait("Error adding item: " + e.getMessage(), ERROR));
            }
        }).start();
    }

    public void updateItem(Book updatedItem) {
        new Thread(() -> {
            try {
                updateSemaphore.acquire(); // Acquire the semaphore to ensure synchronization
                if (booksDb.isConnected()) {
                    if (updatedItem != null) {
                        booksDb.updateBook(updatedItem);
                        List<Book> updatedBooks = booksDb.getAllBooks();
                        Platform.runLater(() -> booksView.displayBooks(updatedBooks));
                    } else {
                        Platform.runLater(() -> booksView.showAlertAndWait("Select a book to update.", WARNING));
                    }
                } else {
                    Platform.runLater(() -> booksView.showAlertAndWait("Not connected", ERROR));
                }
            } catch (Exception e) {
                Platform.runLater(() -> booksView.showAlertAndWait("Error updating item: " + e.getMessage(), ERROR));
            } finally {
                updateSemaphore.release(); // Release the semaphore
            }
        }).start();
    }
    // TODO:
    // Add methods for all types of user interaction (e.g., via menus).
}
