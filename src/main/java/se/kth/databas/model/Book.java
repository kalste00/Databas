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
    private String isbn; // should check format
    private String title;
    private Date publishDate;
    private int rating;
    private Genre genre;
    private String storyLine = "";
    private ArrayList<Author> authors = new ArrayList<>();

    // TODO:
    // Add authors, as a separate class(!), and corresponding methods, to your implementation
    // as well, i.e. "private ArrayList<Author> authors;"
    public Book(int bookId, String isbn, String title, Date publishDate) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.publishDate = publishDate;
    }
    
    public Book(String isbn, String title, Date publishDate) {
        this(-1, isbn, title, publishDate);
    }
    
    public int getBookId() { return bookId; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public Date getPublished() { return publishDate; }
    public String getStoryLine() { return storyLine; }
    
    public void setStoryLine(String storyLine) {
        this.storyLine = storyLine;
    }
    public List<Author> getAuthors() {
        return authors;
    }
    public void addAuthor(Author author) {
        authors.add(author);
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return title + ", " + isbn + ", " + publishDate.toString() + ", " + authors + ", " + genre + ", " + rating;
    }
}
