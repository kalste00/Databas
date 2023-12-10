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
    private final List<Author> allAuthors = Arrays.asList(AUTHOR_DATA);


    public BooksDbMockImpl() {
        books = Arrays.asList(DATA);
    }

    private static final Author[] AUTHOR_DATA = {
            new Author("Author1"),
            new Author("Author2"),
            // Add more authors as needed
    };

    @Override
    public boolean connect(String database) throws BooksDbException {
        try {
            String connectionString = "jdbc:mysql://localhost:3306/" + database + "?user=root" + "&password=Gaming123";
            connection = DriverManager.getConnection(connectionString);
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


                        // Fetch authors associated with the book
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
                    String authorName = rs.getString("name");
                    Author author = new Author(authorId, authorName);
                    authors.add(author);
                }
            }
        }

        return authors;
    }

    //Needed?
    public List<Author> searchAuthorsByName(String searchName) throws BooksDbException {
        List<Author> result = new ArrayList<>();
        searchName = searchName.toLowerCase();

        try {
            String sql = "SELECT * FROM Author WHERE name LIKE ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, "%" + searchName + "%");

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int authorId = rs.getInt("authorId");
                        String authorName = rs.getString("name");
                        Author author = new Author(authorId, authorName);
                        result.add(author);
                    }
                }
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error searching authors by name", e);
        }

        return result;
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

                    // Fetch authors associated with the book
                    List<Author> bookAuthors = getAuthorsForBook(bookId);

                    // Convert genre string to Genre enum
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
            connection.setAutoCommit(false); // Start a transaction
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Book (ISBN, title, publishDate, genre, rating) VALUES (?, ?, ?, ?, ?)")) {
                statement.setString(1, book.getIsbn());
                statement.setString(2, book.getTitle());
                statement.setDate(3, book.getPublishDate());
                statement.setString(4, book.getGenre().toString());
                statement.setInt(5, book.getRating());
                statement.executeUpdate();
            }
            connection.commit(); // Commit the transaction if successful
            connection.setAutoCommit(true); // Reset auto-commit to true
        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback the transaction if an error occurs
            } catch (SQLException rollbackException) {
                throw new BooksDbException("Error rolling back transaction: " + rollbackException.getMessage(), rollbackException);
            }
            throw new BooksDbException("Error adding book: " + e.getMessage(), e);
        }
    }


    @Override
    public void updateBook(Book book) throws BooksDbException {
        try {
            connection.setAutoCommit(false); // Start a transaction
            try (PreparedStatement statement = connection.prepareStatement("UPDATE Book SET title = ?, ISBN = ?, publishDate = ?, genre = ?, rating = ? WHERE bookId = ?")) {
                statement.setString(1, book.getTitle());
                statement.setString(2, book.getIsbn());
                statement.setDate(3, book.getPublishDate());
                statement.setString(4, book.getGenre().toString());
                statement.setInt(5, book.getRating());
                statement.setInt(6, book.getBookId());
                statement.executeUpdate();
            }
            connection.commit(); // Commit the transaction if successful
            connection.setAutoCommit(true); // Reset auto-commit to true
        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback the transaction if an error occurs
            } catch (SQLException rollbackException) {
                throw new BooksDbException("Error rolling back transaction: " + rollbackException.getMessage(), rollbackException);
            }
            throw new BooksDbException("Error updating book: " + e.getMessage(), e);
        }
    }


    @Override
    public void deleteBook(Book book) throws BooksDbException {
        try {
            connection.setAutoCommit(false); // Start a transaction
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Book WHERE bookId = ?")) {
                statement.setInt(1, book.getBookId());
                statement.executeUpdate();
            }
            connection.commit(); // Commit the transaction if successful
            connection.setAutoCommit(true); // Reset auto-commit to true
        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback the transaction if an error occurs
            } catch (SQLException rollbackException) {
                throw new BooksDbException("Error rolling back transaction: " + rollbackException.getMessage(), rollbackException);
            }
            throw new BooksDbException("Error deleting book: " + e.getMessage(), e);
        }
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
