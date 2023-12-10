/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.databas.model;


import java.sql.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A mock implementation of the BooksDBInterface interface to demonstrate how to
 * use it together with the user interface.
 * <p>
 * Your implementation must access a real database.
 *
 * @author anderslm@kth.se
 */
public class BooksDbImpl implements BooksDbInterface {

    private Connection connection;

    @Override
    public boolean connect(String database) throws BooksDbException {
        try {
            String connectionString = "jdbc:mysql://localhost:3306/" + database + "?user=root" + "&password=Gaming123";
            connection = DriverManager.getConnection(connectionString);
            connection.setAutoCommit(false);
            System.out.println("Connected to the database");
            return true;
        } catch (SQLException e) {
            throw new BooksDbException("Failed to connect to the database", e);
        }
    }
    @Override
    public void disconnect() throws BooksDbException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from the database");
            }
        } catch (SQLException e) {
            throw new BooksDbException("Failed to disconnect from the database", e);
        }
    }

    @Override
    public List<Book> searchBooksByTitle(String searchTitle) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        searchTitle = searchTitle.toLowerCase();
        try {
            String sql = "SELECT * FROM Book WHERE title LIKE ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, "%" + searchTitle + "%");
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int bookId = rs.getInt("bookId");
                        String title = rs.getString("title");
                        String isbn = rs.getString("ISBN");
                        Date publishDate = rs.getDate("publishDate");
                        Genre genre = Genre.valueOf(rs.getString("genre"));
                        int rating = rs.getInt("rating");

                        List<Author> bookAuthors = getAuthorsForBook(bookId);

                        Book book = new Book(bookId, title, isbn, publishDate, genre, rating);
                        book.getAuthors().addAll(bookAuthors);

                        result.add(book);
                    }
                }
            }
        }catch (SQLException e) {
            throw new BooksDbException("Error searching books by Title", e);
        }
        return result;
    }
    @Override
    public List<Book> searchBooksByISBN(String searchISBN) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        try {
            String sql = "SELECT * FROM Book WHERE ISBN LIKE ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, "%" + searchISBN + "%");
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int bookId = rs.getInt("bookId");
                        String title = rs.getString("title");
                        String isbn = rs.getString("ISBN");
                        Date publishDate = rs.getDate("publishDate");
                        Genre genre = Genre.valueOf(rs.getString("genre"));
                        int rating = rs.getInt("rating");

                        // Fetch authors associated with the book
                        List<Author> bookAuthors = getAuthorsForBook(bookId);

                        Book book = new Book(bookId, title, isbn, publishDate, genre, rating);
                        book.getAuthors().addAll(bookAuthors);

                        result.add(book);
                    }
                }
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error searching books by ISBN", e);
        }

        return result;
    }


    // A method to retrieve authors for a given book
    private List<Author> getAuthorsForBook(int bookId) throws SQLException {
        String sql = "SELECT a.* FROM Author a JOIN Book_Author ba ON a.authorId = ba.authorId WHERE ba.bookId = ?";
        List<Author> authors = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int authorId = rs.getInt("authorId");
                    String authorName = rs.getString("authorName");
                    Author author = new Author(authorId, authorName);
                    authors.add(author);
                }
            }
        }

        return authors;
    }

    @Override
    public List<Book> searchBooksByAuthor(String searchAuthor) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        searchAuthor = searchAuthor.toLowerCase();

        try {
            String sql = "SELECT b.* FROM Book b JOIN Book_Author ba ON b.bookId = ba.bookId "
                    + "JOIN Author a ON a.authorId = ba.authorId WHERE a.authorName LIKE ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, "%" + searchAuthor + "%");

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int bookId = rs.getInt("bookId");
                        String title = rs.getString("title");
                        String isbn = rs.getString("ISBN");
                        Date publishDate = rs.getDate("publishDate");
                        Genre genre = Genre.valueOf(rs.getString("genre"));
                        int rating = rs.getInt("rating");

                        // Fetch authors associated with the book
                        List<Author> bookAuthors = getAuthorsForBook(bookId);

                        Book book = new Book(bookId, title, isbn, publishDate, genre, rating);
                        book.getAuthors().addAll(bookAuthors);

                        result.add(book);
                    }
                }
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error searching books by author", e);
        }

        return result;
    }

    @Override
    public List<Book> getAllBooks() throws BooksDbException {
        List<Book> result = new ArrayList<>();
        try {
            String sql = "SELECT * FROM Book";
            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    int bookId = rs.getInt("bookId");
                    String title = rs.getString("title");
                    String isbn = rs.getString("ISBN");
                    Date publishDate = rs.getDate("publishDate");
                    String genreStr = rs.getString("genre");
                    int rating = rs.getInt("rating");

                    List<Author> bookAuthors = getAuthorsForBook(bookId);

                    Genre genre = Genre.valueOf(genreStr);

                    Book book = new Book(bookId, title, isbn, publishDate, genre, rating);
                    book.getAuthors().addAll(bookAuthors);

                    result.add(book);
                }
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error getting all books", e);
        }
        return result;
    }

    @Override
    public void addBook(Book book) throws BooksDbException {
        try {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Book (ISBN, title, publishDate, genre, rating) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, book.getIsbn());
                statement.setString(2, book.getTitle());
                statement.setDate(3, book.getPublishDate());
                statement.setString(4, book.getGenre().toString());
                statement.setInt(5, book.getRating());
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int bookId = generatedKeys.getInt(1);
                        System.out.println("Generated Book ID: " + bookId);

                        clearBookAuthorConnections(bookId);

                        addAuthorsAndConnections(bookId, book.getAuthors());

                        connection.commit();
                    } else {
                        throw new SQLException("Failed to get generated book ID");
                    }
                }
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new BooksDbException("Error rolling back transaction", rollbackException);
            }
            throw new BooksDbException("Error adding book: " + e.getMessage(), e);
        }
    }

    private void addAuthorsAndConnections(int bookId, List<Author> authors) throws SQLException {
        for (Author author : authors) {
            int authorId;
            if (authorExists(author.getName())) {
                authorId = getAuthorId(author.getName());
            } else {
                authorId = addAuthorAndGetId(author);
            }
            try (PreparedStatement innerStatement = connection.prepareStatement("INSERT INTO Book_Author (bookId, authorId) VALUES (?, ?)")) {
                innerStatement.setInt(1, bookId);
                innerStatement.setInt(2, authorId);
                innerStatement.executeUpdate();

                System.out.println("Added author " + authorId + " for book " + bookId);
            }
        }
    }


    private void clearBookAuthorConnections(int bookId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Book_Author WHERE bookId = ?")) {
            statement.setInt(1, bookId);
            statement.executeUpdate();
            System.out.println("Cleared existing author connections for book with ID " + bookId);
        }
    }
    private boolean authorExists(String authorName) throws SQLException {
        String sql = "SELECT * FROM Author WHERE authorName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, authorName);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private int getAuthorId(String authorName) throws SQLException {
        String sql = "SELECT authorId FROM Author WHERE authorName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, authorName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("authorId");
                } else {
                    throw new SQLException("Author not found with name: " + authorName);
                }
            }
        }
    }

    private int addAuthorAndGetId(Author author) throws SQLException {
        try (PreparedStatement authorStatement = connection.prepareStatement("INSERT INTO Author (authorName) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            authorStatement.setString(1, author.getName());
            authorStatement.executeUpdate();
            try (ResultSet generatedKeys = authorStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Failed to get generated author ID");
                }
            }
        }
    }
/*
//UPDATE SOL1
    @Override
    public void updateBook(Book book) throws BooksDbException {
        try {
            connection.setAutoCommit(false);

            if (book.getTitle() != null && !book.getTitle().isEmpty()) {
                try (PreparedStatement stmt = connection.prepareStatement("UPDATE Book SET title = ? WHERE bookId = ?")) {
                    stmt.setString(1, book.getTitle());
                    stmt.setInt(2, book.getBookId());
                    stmt.executeUpdate();
                }
            }

            if (book.getIsbn() != null && !book.getIsbn().isEmpty()) {
                try (PreparedStatement stmt = connection.prepareStatement("UPDATE Book SET ISBN = ? WHERE bookId = ?")) {
                    stmt.setString(1, book.getIsbn());
                    stmt.setInt(2, book.getBookId());
                    stmt.executeUpdate();
                }
            }

            if (book.getPublishDate() != null) {
                try (PreparedStatement stmt = connection.prepareStatement("UPDATE Book SET publishDate = ? WHERE bookId = ?")) {
                    stmt.setDate(1, Date.valueOf(String.valueOf(book.getPublishDate())));
                    stmt.setInt(2, book.getBookId());
                    stmt.executeUpdate();
                }
            }

            // Uppdatera andra fält på liknande sätt

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new BooksDbException("Error rolling back transaction", rollbackException);
            }
            throw new BooksDbException("Error updating book: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new BooksDbException("Error setting auto-commit to true", e);
            }
        }
    }*/
/*

    public Book getBookById(int bookId) throws BooksDbException {
        try {
            String sql = "SELECT * FROM Book WHERE bookId = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, bookId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String title = rs.getString("title");
                        String isbn = rs.getString("isbn");
                        Date publishDate = rs.getDate("publishDate");
                        Genre genre = Genre.valueOf(rs.getString("genre"));
                        int rating = rs.getInt("rating");

                        // Fetch authors associated with the book
                        List<Author> bookAuthors = getAuthorsForBook(bookId);

                        Book book = new Book(bookId, title, isbn, publishDate, genre, rating);
                        book.getAuthors().addAll(bookAuthors);

                        return book;
                    } else {
                        // If the book ID is not found, return null instead of throwing an exception
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error getting book by ID: " + e.getMessage(), e);
        }
    }
    @Override
    //UPDATE SOL2
    public void updateBook(Book book) throws BooksDbException {
        try {
            // Check if the book has a valid ID
            if (book.getBookId() <= 0) {
                throw new BooksDbException("Invalid book ID: " + book.getBookId());
            }

            connection.setAutoCommit(false);

            // Retrieve the existing book from the database
            Book existingBook = getBookById(book.getBookId());
            if (existingBook == null) {
                throw new BooksDbException("Book not found with ID: " + book.getBookId());
            }

            try (PreparedStatement statement = connection.prepareStatement("UPDATE Book SET title = ?, ISBN = ?, publishDate = ?, genre = ?, rating = ? WHERE bookId = ?")) {
                statement.setString(1, book.getTitle());
                statement.setString(2, book.getIsbn());
                statement.setDate(3, book.getPublishDate());
                statement.setString(4, book.getGenre().toString());
                statement.setInt(5, book.getRating());
                statement.setInt(6, book.getBookId());
                statement.executeUpdate();
            }

            // Clear existing author connections and add new ones
            clearBookAuthorConnections(book.getBookId());
            addAuthorsAndConnections(book.getBookId(), book.getAuthors());

            // Commit the changes within the same try block
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new BooksDbException("Error rolling back transaction", rollbackException);
            }
            throw new BooksDbException("Error updating book: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new BooksDbException("Error setting auto-commit to true", e);
            }
        }
    }

 */

    //UPDATE SOL3
    @Override
    public void updateBook(Book book) throws BooksDbException {
        try {
            connection.setAutoCommit(false);

            // Hämta befintliga författare innan ändringar
            List<Author> existingAuthors = getAuthorsForBook(book.getBookId());

            // Hämta uppdaterade författare
            List<Author> updatedAuthors = getAuthorsForBook(book.getBookId());

            // Hitta nya författare (de som finns i updatedAuthors men inte i existingAuthors)
            List<Author> newAuthors = new ArrayList<>();
            for (Author updatedAuthor : updatedAuthors) {
                if (!existingAuthors.contains(updatedAuthor)) {
                    newAuthors.add(updatedAuthor);
                }
            }

            // Hitta borttagna författare (de som finns i existingAuthors men inte i updatedAuthors)
            List<Author> removedAuthors = new ArrayList<>();
            for (Author existingAuthor : existingAuthors) {
                if (!updatedAuthors.contains(existingAuthor)) {
                    removedAuthors.add(existingAuthor);
                }
            }
            // Ta bort borttagna författare
            for (Author removedAuthor : removedAuthors) {
                deleteAuthorIfNeeded(removedAuthor);
            }

            // Lägg till nya författare
            for (Author newAuthor : newAuthors) {
                addAuthorIfNeeded(newAuthor);
                addAuthorsAndConnections(book.getBookId(), newAuthors);
            }

            // Lägg till/ta bort författare-kopplingar
            updateBookAuthors(book.getBookId(), updatedAuthors);

            // Uppdatera boken
            try (PreparedStatement statement = connection.prepareStatement("UPDATE Book SET title = ?, ISBN = ?, publishDate = ?, genre = ?, rating = ? WHERE bookId = ?")) {
                statement.setString(1, book.getTitle());
                statement.setString(2, book.getIsbn());
                statement.setDate(3, book.getPublishDate());
                statement.setString(4, book.getGenre().toString());
                statement.setInt(5, book.getRating());
                statement.setInt(6, book.getBookId());
                statement.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new BooksDbException("Error rolling back transaction", rollbackException);
            }
            throw new BooksDbException("Error updating book: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new BooksDbException("Error setting auto-commit to true", e);
            }
        }
    }

    private void deleteAuthorIfNeeded(Author author) throws SQLException {
        if (!isAuthorConnectedToOtherBooks(author.getAuthorId())) {
            deleteAuthorFromDatabase(author.getAuthorId());
        }
    }

    private void addAuthorIfNeeded(Author author) throws SQLException {
        if (!authorExists(author.getName())) {
            addAuthorAndGetId(author);
        }
    }

    private void updateBookAuthors(int bookId, List<Author> updatedAuthors) throws SQLException {
        List<Author> existingAuthors = getAuthorsForBook(bookId);

        // Hitta nya författare (de som finns i updatedAuthors men inte i existingAuthors)
        List<Author> newAuthors = new ArrayList<>();
        for (Author updatedAuthor : updatedAuthors) {
            if (!existingAuthors.contains(updatedAuthor)) {
                newAuthors.add(updatedAuthor);
            }
        }

        // Hitta borttagna författare (de som finns i existingAuthors men inte i updatedAuthors)
        List<Author> removedAuthors = new ArrayList<>();
        for (Author existingAuthor : existingAuthors) {
            if (!updatedAuthors.contains(existingAuthor)) {
                removedAuthors.add(existingAuthor);
            }
        }

        // Ta bort borttagna författare
        if (!removedAuthors.isEmpty()) {
            clearBookAuthorConnections(bookId);
        }

        // Lägg till nya författare
        if (!newAuthors.isEmpty()) {
            addAuthorsAndConnections(bookId, newAuthors);
        }
    }

    public void deleteBook(Book book) throws BooksDbException {
        try {
            int deletedBookId = book.getBookId();

            clearBookAuthorConnections(deletedBookId);

            deleteBookFromDatabase(deletedBookId);

            clearOrphanAuthors();

            updateBookIdsAfterDelete(deletedBookId);

            resetBookIdSequence();

            List<Integer> authorIds = getAuthorIdsForBook(deletedBookId);

            deleteAuthorsIfNeeded(authorIds);

            resetAuthorIdSequence();

            connection.commit();
        } catch (SQLException e) {
            throw new BooksDbException("Error deleting book: " + e.getMessage(), e);
        }
    }

    public void clearOrphanAuthors() throws SQLException {
        String sql = "DELETE FROM Author WHERE authorId NOT IN (SELECT DISTINCT authorId FROM Book_Author)";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }
    private List<Integer> getAuthorIdsForBook(int bookId) throws SQLException {
        List<Integer> authorIds = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement("SELECT authorId FROM Book_Author WHERE bookId = ?")) {
            statement.setInt(1, bookId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int authorId = resultSet.getInt("authorId");
                    authorIds.add(authorId);
                }
            }
        }

        return authorIds;
    }

    private void resetAuthorIdSequence() throws SQLException {
        int maxAuthorId = getMaxAuthorIdInAuthorsTable();
        String sql = "ALTER TABLE Author AUTO_INCREMENT = ?";
        try (PreparedStatement resetSequence = connection.prepareStatement(sql)) {
            resetSequence.setInt(1, maxAuthorId);
            resetSequence.executeUpdate();
        }
    }

    // Add this method to get the maximum author ID associated with any author connected to a book
    private int getMaxAuthorIdInAuthorsTable() throws SQLException {
        String sql = "SELECT MAX(authorId) FROM Author";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getInt(1) + 1;
            } else {
                return 1;
            }
        }
    }
    private void updateAuthorIdsAfterDelete(int deletedAuthorId) throws SQLException {
        try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE Author SET authorId = authorId - 1 WHERE authorId > ?")) {
            updateStatement.setInt(1, deletedAuthorId);
            updateStatement.executeUpdate();
        }
    }

    private void deleteAuthorsIfNeeded(List<Integer> authorIds) throws SQLException {
        for (Integer authorId : authorIds) {
            if (!isAuthorConnectedToOtherBooks(authorId)) {
                // Ta bort författaren om den inte är kopplad till andra böcker
                deleteAuthorFromDatabase(authorId);
                updateAuthorIdsAfterDelete(authorId);
            } else {
                System.out.println("Author with ID " + authorId + " is still connected to other books.");
            }
        }
    }

    private void deleteBookFromDatabase(int bookId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Book WHERE bookId = ?")) {
            statement.setInt(1, bookId);
            statement.executeUpdate();
        }
    }

    private void updateBookIdsAfterDelete(int deletedBookId) throws SQLException {
        try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE Book SET bookId = bookId - 1 WHERE bookId > ?")) {
            updateStatement.setInt(1, deletedBookId);
            updateStatement.executeUpdate();
        }
    }

    private void resetBookIdSequence() throws SQLException {
        try (PreparedStatement resetSequence = connection.prepareStatement("ALTER TABLE Book AUTO_INCREMENT = ?")) {
            resetSequence.setInt(1, getMaxBookId());
            resetSequence.executeUpdate();
        }
    }

    private void deleteAuthorFromDatabase(int authorId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Author WHERE authorId = ?")) {
            statement.setInt(1, authorId);
            statement.executeUpdate();
        }
    }

    private int getMaxBookId() throws SQLException {
        String sql = "SELECT MAX(bookId) FROM Book";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getInt(1) + 1;
            } else {
                return 1;
            }
        }
    }

    private boolean isAuthorConnectedToOtherBooks(int authorId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Book_Author WHERE authorId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, authorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}