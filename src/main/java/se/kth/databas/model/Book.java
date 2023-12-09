package se.kth.databas.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a book.
 *
 * @author anderslm@kth.se
 */
public class Book {

    private int bookId;
    private String isbn;
    private String title;
    private Date publishDate;
    private Genre genre;
    private int rating;
    private String storyLine = "";
    private List<Author> authors = new ArrayList<>();

    /**
     * Constructs a new Book instance with the specified attributes.
     *
     */
    public Book(int bookId, String title, String isbn, Date publishDate, Genre genre, int rating) {
        this.bookId = bookId;
        this.title = title;
        this.isbn = isbn;
        this.publishDate = publishDate;
        this.genre = genre;
        this.rating = rating;
    }

    /**
     * extra constructor.
     * @param title
     * @param isbn
     * @param publishDate
     * @param genre
     * @param rating
     */
    public Book(String title, String isbn, Date publishDate, Genre genre, int rating) {
        this(-1, title,isbn, publishDate, genre, rating);
    }

    /**
     * returns the bookid
     * @return
     */
    public int getBookId() { return bookId; }

    /**
     * return the isbn
     * @return
     */
    public String getIsbn() { return isbn; }
    /**
     * Gets the title of the book.
     *
     * @return The title of the book.
     */
    public String getTitle() { return title; }
    public Date getPublishDate() { return publishDate; }
    public String getStoryLine() { return storyLine; }

    public void setStoryLine(String storyLine) {
        this.storyLine = storyLine;
    }

    /**
     * returns the list of authors.
     * @return
     */
    public List<Author> getAuthors() {
        return authors;
    }

    /**
     * adds authour.
     * @param author
     */
    public void addAuthor(Author author) {
        authors.add(author);
    }

    /**
     * Gets the rating of the book.
     *
     * @return The rating of the book.
     */
    public int getRating() {
        return rating;
    }
    /**
     * Sets the rating of the book.
     *
     * @param rating The rating to set.
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * returns genre.
     * @return
     */
    public Genre getGenre() {
        return genre;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    /**
     * Sets the title of the book.
     *
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * sets the published date.
     * @param publishDate
     */
    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    /**
     * sets the genre.
     * @param genre
     */
    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    /**
     * returns tostring with the values.
     * @return
     */
    @Override
    public String toString() {
        return title + ", " + isbn + ", " + publishDate.toString() + ", " + authors + ", " + genre + ", " + rating;
    }
}