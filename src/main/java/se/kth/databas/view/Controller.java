package se.kth.databas.view;

import se.kth.databas.model.Book;
import se.kth.databas.model.BooksDbException;
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
                        result= new ArrayList<>();
                }
                if (result == null || result.isEmpty()) {
                    booksView.showAlertAndWait(
                            "No results found.", INFORMATION);
                } else {
                    booksView.displayBooks(result);
                }
            } else {
                booksView.showAlertAndWait(
                        "Enter a search string!", WARNING);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Database error.",ERROR);
        }
    }

    public void addItem() {
        try {
            // Assuming you have a method in your model to add a new book, for example:
            // Book newItem = ...; // create a new Book instance
            // booksDb.addBook(newItem); // Add the new book to the database
            // Now you may want to refresh the display with the updated book list:
            List<Book> updatedBooks = booksDb.getAllBooks(); // Replace this with the actual method to get all books
            booksView.displayBooks(updatedBooks); // Refresh the display
        } catch (Exception e) {
            // Catch the most general exception (or specific exceptions if needed)
            booksView.showAlertAndWait("Error adding item: " + e.getMessage(), ERROR);
        }
    }

    public void removeItem() {
        try {
            // Implement the logic for removing an item (book) from the database
            // You might show a dialog or prompt the user for confirmation
            // After removing the item, refresh the display or update the view
            // Example:
            // Book itemToRemove = ...; // get the book to remove
            // booksDb.removeItem(itemToRemove); // assuming you have a removeItem method in your BooksDbInterface
            // booksView.displayBooks(booksDb.getAllBooks()); // refresh the display
        } catch (Exception e) {
            booksView.showAlertAndWait("Error removing item.", ERROR);
        }
    }

    public void updateItem() {
        try {
            // Implement the logic for updating an item (book) in the database
            // You might show a dialog or prompt the user for input
            // After updating the item, refresh the display or update the view
            // Example:
            // Book itemToUpdate = ...; // get the book to update
            // booksDb.updateItem(itemToUpdate); // assuming you have an updateItem method in your BooksDbInterface
            // booksView.displayBooks(booksDb.getAllBooks()); // refresh the display
        } catch (Exception e) {
            booksView.showAlertAndWait("Error updating item.", ERROR);
        }
    }



    // TODO:
    // Add methods for all types of user interaction (e.g. via  menus).
}
