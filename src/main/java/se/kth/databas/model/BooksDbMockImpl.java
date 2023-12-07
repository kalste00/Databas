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
import java.time.LocalDate;

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
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(searchTitle)) {
                result.add(book);
            }
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
                        String isbn = rs.getString("ISBN");
                        String title = rs.getString("title");
                        LocalDate published = rs.getDate("published").toLocalDate(); // Convert java.sql.Date to LocalDate

                        // Fetch authors associated with the book
                        List<Author> bookAuthors = getAuthorsForBook(bookId);

                        Book book = new Book(isbn, title, published, bookId);
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

    public List<Author> searchAuthorsByName(String searchName) throws BooksDbException {
        List<Author> result = new ArrayList<>();
        searchName = searchName.toLowerCase();
        for (Author author : allAuthors) {
            if (author.getName().toLowerCase().contains(searchName)) {
                result.add(author);
            }
        }
        return result;
    }



    @Override
    public List<Book> searchBooksByAuthor(String searchAuthor) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        searchAuthor = searchAuthor.toLowerCase();
        for (Book book : books) {
            for (Author author : book.getAuthors()) {
                if (author.getName().toLowerCase().contains(searchAuthor)) {
                    result.add(book);
                    break;  // Once a match is found for an author, no need to check further
                }
            }
        }
        return result;
    }

    @Override
    public List<Book> getAllBooks() {
        return null;
    }

    @Override
    public void addBook(Book book) throws BooksDbException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Book (isbn, title, published) VALUES (?, ?, ?)")) {
            statement.setString(1, book.getIsbn());
            statement.setString(2, book.getTitle());

            // Convert LocalDate to java.sql.Date
            statement.setDate(3, Date.valueOf(book.getPublished()));

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error adding book: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateBook(Book book) throws BooksDbException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE Book SET title = ?, isbn = ?, published = ? WHERE bookId = ?")) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getIsbn());

            // Convert LocalDate to java.sql.Date
            statement.setDate(3, Date.valueOf(book.getPublished()));

            statement.setInt(4, book.getBookId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error updating book: " + e.getMessage(), e);
        }
    }


    @Override
    public void deleteBook(Book book) throws BooksDbException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM BOOK WHERE bookId = ?")) {
            statement.setInt(1, book.getBookId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error deleting book: " + e.getMessage(), e);
        }
    }


    private static final Book[] DATA = {
            new Book(1, "123456789", "Databases Illuminated", new Date(2018, 1, 1).toLocalDate(),2),
            new Book(2, "234567891", "Dark Databases", new Date(1990, 1, 1).toLocalDate(),1),
            new Book(3, "456789012", "The buried giant", new Date(2000, 1, 1).toLocalDate(),2),
            new Book(4, "567890123", "Never let me go", new Date(2000, 1, 1).toLocalDate(),3),
            new Book(5, "678901234", "The remains of the day", new Date(2000, 1, 1).toLocalDate(),2),
            new Book(6, "234567890", "Alias Grace", new Date(2000, 1, 1).toLocalDate(),3),
            new Book(7, "345678911", "The handmaids tale", new Date(2010, 1, 1).toLocalDate(),3),
            new Book(8, "345678901", "Shuggie Bain", new Date(2020, 1, 1).toLocalDate(),2),
            new Book(9, "345678912", "Microserfs", new Date(2000, 1, 1).toLocalDate(),4),
    };
}
