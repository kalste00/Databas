package se.kth.databas.view;

import se.kth.databas.model.Book;
import se.kth.databas.model.BooksDbInterface;
import se.kth.databas.model.SearchMode;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.control.Alert.AlertType.*;

/**
 * The controller is responsible for handling user requests and update the view
 * (and in some cases the model).
 *
 * @author anderslm@kth.se
 */
public class Controller {

    private final BooksPane booksView; // view
    private final BooksDbInterface booksDb; // model

    public Controller(BooksDbInterface booksDb, BooksPane booksView) {
        this.booksDb = booksDb;
        this.booksView = booksView;
    }

    protected void onSearchSelected(String searchFor, SearchMode mode) {
        try {
            if (searchFor != null && searchFor.length() > 1) {
                List<Book> result = null;
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
                    default:
                        result = new ArrayList<>();
                }
                if (result == null || result.isEmpty()) {
                    booksView.showAlertAndWait("No results found.", INFORMATION);
                } else {
                    booksView.displayBooks(result);
                }
            } else {
                booksView.showAlertAndWait("Enter a search string!", WARNING);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Add proper logging or print statements for debugging
            booksView.showAlertAndWait("Database error.", ERROR);
        }
    }


    public void deleteItem(Book itemToDelete) {
        try {
            if (itemToDelete != null) {
                booksDb.deleteBook(itemToDelete); // Assuming you have a deleteBook method in your BooksDbInterface
                List<Book> updatedBooks = booksDb.getAllBooks(); // Replace this with the actual method to get all books
                booksView.displayBooks(updatedBooks); // Refresh the display
            } else {
                booksView.showAlertAndWait("Select a book to delete.", WARNING);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Error deleting item: " + e.getMessage(), ERROR);
        }
    }

    public void addItem(Book newItem) {
        try {
            if (newItem != null) {
                booksDb.addBook(newItem); // Assuming you have an addBook method in your BooksDbInterface
                List<Book> updatedBooks = booksDb.getAllBooks(); // Replace this with the actual method to get all books
                booksView.displayBooks(updatedBooks); // Refresh the display
            } else {
                booksView.showAlertAndWait("Enter book details to add.", WARNING);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Error adding item: " + e.getMessage(), ERROR);
        }
    }

    public void updateItem(Book updatedItem) {
        try {
            if (updatedItem != null) {
                booksDb.updateBook(updatedItem); // Assuming you have an updateBook method in your BooksDbInterface
                List<Book> updatedBooks = booksDb.getAllBooks(); // Replace this with the actual method to get all books
                booksView.displayBooks(updatedBooks); // Refresh the display
            } else {
                booksView.showAlertAndWait("Select a book to update.", WARNING);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Error updating item: " + e.getMessage(), ERROR);
        }
    }

    // TODO:
    // Add methods for all types of user interaction (e.g. via  menus).
}
