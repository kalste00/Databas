package se.kth.databas.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a book.
 * 
 * @author anderslm@kth.se
 */
public class    Book {
    
    private int bookId;
    private String isbn; // should check format
    private String title;
    private Date published;
    private int grade;

    private String storyLine = "";
    private ArrayList<Author> authors = new ArrayList<>();

    // TODO:
    // Add authors, as a separate class(!), and corresponding methods, to your implementation
    // as well, i.e. "private ArrayList<Author> authors;"
    public Book(int bookId, String isbn, String title, Date published, int grade) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.published = published;
        this.grade = grade;
    }


    // Additional constructor without the grade field...
    public Book(String isbn, String title, Date published) {
        this(-1, isbn, title, published, 0); // Default grade is 0
    }
    public Book(String isbn, String title, Date published, int grade) {
        this(-1, isbn, title, published, grade);
    }
    public int getBookId() { return bookId; }
    public String getIsbn() { return isbn; }
    public int getGrade() {
        return grade;
    }
    public void setGrade(int grade) {
        // You can add validation logic here if needed
        this.grade = grade;
    }
    public String getTitle() { return title; }
    public Date getPublished() { return published; }
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
    @Override
    public String toString() {
        return title + ", " + isbn + ", " + published.toString() + ", Grade: " + grade;
    }
}
