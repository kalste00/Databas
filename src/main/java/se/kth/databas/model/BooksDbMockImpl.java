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
        searchISBN = searchISBN.toLowerCase();
        for (Book book : books) {
            if (book.getIsbn().toLowerCase().contains(searchISBN)) {
                result.add(book);
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
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO books (isbn, title, published) VALUES (?, ?, ?)")) {
            statement.setString(1, book.getIsbn());
            statement.setString(2, book.getTitle());
            statement.setDate(3, book.getPublished());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error adding book: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateBook(Book book) throws BooksDbException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE books SET title = ?, isbn = ?, published = ? WHERE bookId = ?")) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getIsbn());
            statement.setDate(3, book.getPublished());
            statement.setInt(4, book.getBookId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error updating book: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteBook(Book book) throws BooksDbException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM books WHERE bookId = ?")) {
            statement.setInt(1, book.getBookId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error deleting book: " + e.getMessage(), e);
        }
    }


    private static final Book[] DATA = {
            new Book(1, "123456789", "Databases Illuminated", new Date(2018, 1, 1)),
            new Book(2, "234567891", "Dark Databases", new Date(1990, 1, 1)),
            new Book(3, "456789012", "The buried giant", new Date(2000, 1, 1)),
            new Book(4, "567890123", "Never let me go", new Date(2000, 1, 1)),
            new Book(5, "678901234", "The remains of the day", new Date(2000, 1, 1)),
            new Book(6, "234567890", "Alias Grace", new Date(2000, 1, 1)),
            new Book(7, "345678911", "The handmaids tale", new Date(2010, 1, 1)),
            new Book(8, "345678901", "Shuggie Bain", new Date(2020, 1, 1)),
            new Book(9, "345678912", "Microserfs", new Date(2000, 1, 1)),
    };
}
