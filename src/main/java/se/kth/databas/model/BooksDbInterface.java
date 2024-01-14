package se.kth.databas.model;

import java.util.List;

/**
 * This interface declares methods for querying a Books database.
 * Different implementations of this interface handle the connection and
 * queries to a specific DBMS and database, for example a MySQL or a MongoDB
 * database.
 *
 * NB! The methods in the implementation must catch the SQL/MongoDBExceptions thrown
 * by the underlying driver, wrap them in a BooksDbException, and then re-throw the latter
 * exception. This way, the interface is the same for both implementations, because the
 * exception type in the method signatures is the same. More info in BooksDbException.java.
 *
 * @author anderslm@kth.se
 */
public interface BooksDbInterface {

    /**
     * Connect to the database.
     * @param database The name of the database to connect to.
     * @return true on successful connection.
     * @throws BooksDbException If an error occurs during the connection.
     */
    public boolean connect(String database) throws BooksDbException;

    /**
     * Disconnects from the database.
     * @throws BooksDbException If an error occurs during disconnection.
     */
    public void disconnect() throws BooksDbException;

    /**
     * Searches for books by title in the database.
     * @param title The title to search for.
     * @return A list of books matching the title.
     * @throws BooksDbException If an error occurs during the database query.
     */
    public List<Book> searchBooksByTitle(String title) throws BooksDbException;

    /**
     * Searches for books by ISBN in the database.
     * @param isbn The ISBN to search for.
     * @return A list of books matching the ISBN.
     * @throws BooksDbException If an error occurs during the database query.
     */
    List<Book> searchBooksByISBN(String isbn) throws BooksDbException;

    /**
     * Searches for books by author in the database.
     * @param author The author's name to search for.
     * @return A list of books written by the specified author.
     * @throws BooksDbException If an error occurs during the database query.
     */
    List<Book> searchBooksByAuthor(String author) throws BooksDbException;

    /**
     * Retrieves all books from the database.
     * @return A list of all books in the database.
     * @throws BooksDbException If an error occurs during the database query.
     */
    List<Book> getAllBooks() throws BooksDbException;

    /**
     * Retrieves the authors associated with a specific book.
     * @param bookId The ID of the book.
     * @return A list of authors for the specified book.
     * @throws BooksDbException If an error occurs during the database query.
     */
    List<Author> getAuthorsForBook(int bookId) throws BooksDbException;

    /**
     * Adds a new book to the database.
     * @param newItem The book to add.
     * @throws BooksDbException If an error occurs during the database operation.
     */
    void addBook(Book newItem) throws BooksDbException;

    /**
     * Clears author connections for a specific book.
     * @param bookId The ID of the book.
     * @throws BooksDbException If an error occurs during the database operation.
     */
    void clearBookAuthorConnections(int bookId) throws BooksDbException;

    /**
     * Checks if an author with a given name already exists in the database.
     * @param authorName The name of the author.
     * @return true if the author exists, false otherwise.
     * @throws BooksDbException If an error occurs during the database query.
     */
    boolean authorExists(String authorName) throws BooksDbException;

    /**
     * Retrieves the ID of an author with a given name.
     * @param authorName The name of the author.
     * @return The ID of the author.
     * @throws BooksDbException If the author is not found or an error occurs during the database query.
     */
    int getAuthorId(String authorName) throws BooksDbException;

    /**
     * Adds a new author to the database.
     * @param author The author to add.
     * @throws BooksDbException If an error occurs during the database operation.
     */
    void addAuthor(Author author) throws BooksDbException;

    /**
     * Updates information for an existing book in the database.
     * @param updatedItem The updated book information.
     * @throws BooksDbException If an error occurs during the database operation.
     */
    void updateBook(Book updatedItem) throws BooksDbException;

    /**
     * Adds authors and their connections to a specific book in the database.
     * @param book The book to which authors are added.
     * @param bookId The ID of the book.
     * @param authors The list of authors to add.
     * @throws BooksDbException If an error occurs during the database operation.
     */
    void addAuthorsAndConnections(Book book, int bookId, List<Author> authors) throws BooksDbException;

    /**
     * Retrieves the ID of a book with a given title.
     * @param bookTitle The title of the book.
     * @return The ID of the book.
     * @throws BooksDbException If the book is not found or an error occurs during the database query.
     */
    int getBookId(String bookTitle) throws BooksDbException;

    /**
     * Deletes a book from the database.
     * @param itemToDelete The book to delete.
     * @throws BooksDbException If an error occurs during the database operation.
     */
    void deleteBook(Book itemToDelete) throws BooksDbException;

    /**
     * Clears orphan authors from the database.
     * @throws BooksDbException If an error occurs during the database operation.
     */
    void clearOrphanAuthors() throws BooksDbException;

    /**
     * Retrieves the author IDs associated with a specific book.
     * @param bookId The ID of the book.
     * @return A list of author IDs for the specified book.
     * @throws BooksDbException If an error occurs during the database query.
     */
    List<Integer> getAuthorIdsForBook(int bookId) throws BooksDbException;

    /**
     * Updates author IDs after deleting a specific author.
     * @param deletedAuthorId The ID of the deleted author.
     * @throws BooksDbException If an error occurs during the database operation.
     */
    void updateAuthorIdsAfterDelete(int deletedAuthorId) throws BooksDbException;

    /**
     * Deletes a book from the database.
     * @param bookId The ID of the book to delete.
     * @throws BooksDbException If an error occurs during the database operation.
     */
    void deleteBookFromDatabase(int bookId) throws BooksDbException;

    /**
     * Updates book IDs after deleting a specific book.
     * @param deletedBookId The ID of the deleted book.
     * @throws BooksDbException If an error occurs during the database operation.
     */
    void updateBookIdsAfterDelete(int deletedBookId) throws BooksDbException;

    /**
     * Deletes an author from the database.
     * @param authorId The ID of the author to delete.
     * @throws BooksDbException If an error occurs during the database operation.
     */
    void deleteAuthorFromDatabase(int authorId) throws BooksDbException;

    /**
     * Checks if an author is connected to other books in the database.
     * @param authorId The ID of the author.
     * @return true if the author is connected to other books, false otherwise.
     * @throws BooksDbException If an error occurs during the database query.
     */
    boolean isAuthorConnectedToOtherBooks(int authorId) throws BooksDbException;

    /**
     * Checks if there is an active connection to the database.
     * @return true if there is an active connection, false otherwise.
     */
    boolean isConnected();

    // TODO: Add abstract methods for all inserts, deletes, and queries
    // mentioned in the instructions for the assignment.
}
