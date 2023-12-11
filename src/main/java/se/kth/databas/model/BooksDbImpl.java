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
                        System.out.println("book ID: " + bookId);

                        Book book = new Book(title, isbn, publishDate, genre, rating);
                        book.setBookId(bookId);
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
                        System.out.println("book ID: " + bookId);

                        // Fetch authors associated with the book
                        List<Author> bookAuthors = getAuthorsForBook(bookId);

                        Book book = new Book(title, isbn, publishDate, genre, rating);
                        book.setBookId(bookId);
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
                        System.out.println("book ID: " + bookId);

                        // Fetch authors associated with the book
                        List<Author> bookAuthors = getAuthorsForBook(bookId);

                        Book book = new Book(title, isbn, publishDate, genre, rating);
                        book.setBookId(bookId);
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
                    System.out.println("book ID: " + bookId);

                    List<Author> bookAuthors = getAuthorsForBook(bookId);

                    Genre genre = Genre.valueOf(genreStr);

                    Book book = new Book(title, isbn, publishDate, genre, rating);
                    book.setBookId(bookId);
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
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Book (title, isbn, publishDate, genre, rating) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, book.getTitle());
                statement.setString(2, book.getIsbn());
                statement.setDate(3, book.getPublishDate());
                statement.setString(4, book.getGenre().toString());
                statement.setInt(5, book.getRating());
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int bookId = generatedKeys.getInt(1);
                        System.out.println("Generated Book ID: " + bookId);
                        System.out.println("Generated author ID: " + book.getAuthors());

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

    private void updateBookAuthors(int bookId, List<Author> updatedAuthors) throws SQLException {
        List<Author> existingAuthors = getAuthorsForBook(bookId);
        List<Author> newAuthors = new ArrayList<>();
        List<Author> removedAuthors = new ArrayList<>();

        // Find new authors and removed authors (similar to the previous method)

        // Iterate through authors to find new and removed ones
        for (Author updatedAuthor : updatedAuthors) {
            if (!existingAuthors.contains(updatedAuthor)) {
                newAuthors.add(updatedAuthor);
            }
        }

        for (Author existingAuthor : existingAuthors) {
            if (!updatedAuthors.contains(existingAuthor)) {
                removedAuthors.add(existingAuthor);
            }
        }
        // Step 1: Clear removed authors
        if (!removedAuthors.isEmpty()) {
            clearBookAuthorConnections(bookId);
        }

        // Step 2: Add new authors
        if (!newAuthors.isEmpty()) {
            addAuthorsAndConnections(bookId, newAuthors);
        }
    }
    @Override
    public void updateBook(Book book) throws BooksDbException {
        try {
            connection.setAutoCommit(false);

            // Step 1: Get the correct book ID using the old ISBN
            int correctBookId = searchBooksByISBN(book.getIsbn()).get(0).getBookId();

            // Step 2: Update book information
            PreparedStatement updateStatement = connection.prepareStatement(
                    "UPDATE Book SET title = ?, ISBN = ?, publishDate = ?, genre = ?, rating = ? WHERE bookId = ?");
            updateStatement.setString(1, book.getTitle());
            updateStatement.setString(2, book.getIsbn());
            updateStatement.setDate(3, book.getPublishDate());
            updateStatement.setString(4, book.getGenre().toString());
            updateStatement.setInt(5, book.getRating());
            updateStatement.setInt(6, correctBookId);
            updateStatement.executeUpdate();

            // Step 3: Update book authors
            updateBookAuthors(correctBookId, book.getAuthors());

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