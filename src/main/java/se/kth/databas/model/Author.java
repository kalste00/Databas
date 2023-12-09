package se.kth.databas.model;

public class Author {
    private int authorId;
    private String name;

    public Author(int authorId, String name) {
        this.authorId = authorId;
        this.name = name;
    }

    public Author(String name){
        this(-1, name);
    }

    public int getAuthorId(){
        return authorId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name ;
    }
}
