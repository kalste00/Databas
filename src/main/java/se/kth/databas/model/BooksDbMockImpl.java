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
import java.util.stream.Collectors;

/**
 * A mock implementation of the BooksDBInterface interface to demonstrate how to
 * use it together with the user interface.
 * <p>
 * Your implementation must access a real database.
 *
 * @author anderslm@kth.se
 */
public class BooksDbMockImpl implements BooksDbInterface {

    private final List<Book> books;
    private Connection connection;

    public BooksDbMockImpl() {
        books = Arrays.asList(DATA);
    }

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
                    + "JOIN Author a ON a.authorId = ba.authorId WHERE a.name LIKE ?";
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

    @Override
    public void updateBook(Book book) throws BooksDbException {
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement("UPDATE Book SET title = ?, ISBN = ?, publishDate = ?, genre = ?, rating = ? WHERE bookId = ?")) {
                statement.setString(1, book.getTitle());
                statement.setString(2, book.getIsbn());
                statement.setDate(3, book.getPublishDate());
                statement.setString(4, book.getGenre().toString());
                statement.setInt(5, book.getRating());
                statement.setInt(6, book.getBookId());
                statement.executeUpdate();
            }

            clearBookAuthorConnections(book.getBookId());
            addAuthorsAndConnections(book.getBookId(), book.getAuthors());

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

            updateBookIdsAfterDelete(deletedBookId);

            resetBookIdSequence();

            List<Integer> authorIds = getAuthorsForBook(deletedBookId).stream().map(Author::getAuthorId).collect(Collectors.toList());

            deleteAuthorsIfNeeded(authorIds);

            resetAuthorIdSequence();
            resetBookIdSequence();
            connection.commit();
        } catch (SQLException e) {
            throw new BooksDbException("Error deleting book: " + e.getMessage(), e);
        }
    }

    private void resetAuthorIdSequence() throws SQLException {
        int maxAuthorId = getMaxAuthorIdConnectedToBooks();
        String sql = "ALTER TABLE Author AUTO_INCREMENT = ?";

        try (PreparedStatement resetSequence = connection.prepareStatement(sql)) {
            resetSequence.setInt(1, maxAuthorId);
            resetSequence.executeUpdate();
        }
    }

    // Add this method to get the maximum author ID associated with any author connected to a book
    private int getMaxAuthorIdConnectedToBooks() throws SQLException {
        String sql = "SELECT MAX(a.authorId) FROM Author a JOIN Book_Author ba ON a.authorId = ba.authorId";

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getInt(1) + 1;
            } else {
                return 1;
            }
        }
    }

    private void deleteAuthorsIfNeeded(List<Integer> authorIds) throws SQLException {
        for (Integer authorId : authorIds) {
            if (!isAuthorConnectedToOtherBooks(authorId)) {
                // Ta bort författaren om den inte är kopplad till andra böcker
                deleteAuthorFromDatabase(authorId);
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

    private static final Book[] DATA = {
            new Book(1, "123456789", "Databases Illuminated", new Date(2018, 1, 1),Genre.Literature,5),
            new Book(2, "234567891", "Dark Databases", new Date(1990, 1, 1),Genre.Literature,5),
            new Book(3, "456789012", "The buried giant", new Date(2000, 1, 1),Genre.Romance,3),
            new Book(4, "567890123", "Never let me go", new Date(2000, 1, 1),Genre.ScienceFiction,4),
            new Book(5, "678901234", "The remains of the day", new Date(2000, 1, 1),Genre.ScienceFiction,3),
            new Book(6, "234567890", "Alias Grace", new Date(2000, 1, 1),Genre.Action,2),
            new Book(7, "345678911", "The handmaids tale", new Date(2010, 1, 1),Genre.Adventure,4),
            new Book(8, "345678901", "Shuggie Bain", new Date(2020, 1, 1),Genre.Fantasy,3),
            new Book(9, "345678912", "Microserfs", new Date(2000, 1, 1),Genre.Drama,3),
    };
}
