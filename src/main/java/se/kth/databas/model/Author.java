package se.kth.databas.model;

public class Author {
    private int authorId;
    private String firstName;
    private String lastName;

    public Author(int authorId, String firstName, String lastName) {
        this.authorId = authorId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Author(String firstName, String lastName){
        this(-1, firstName, lastName);
    }

    public int getAuthorId(){
        return authorId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}

