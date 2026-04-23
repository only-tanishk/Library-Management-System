package model;

/**
 * Book model — includes a unique book_code for student self-issue.
 */
public class Book {

    private int    id;
    private String bookCode;   // unique short code e.g. BK001
    private String title;
    private String author;
    private int    quantity;

    public Book() {}

    public Book(String bookCode, String title, String author, int quantity) {
        this.bookCode = bookCode;
        this.title    = title;
        this.author   = author;
        this.quantity = quantity;
    }

    public Book(int id, String bookCode, String title, String author, int quantity) {
        this.id       = id;
        this.bookCode = bookCode;
        this.title    = title;
        this.author   = author;
        this.quantity = quantity;
    }

    // Getters
    public int    getId()       { return id; }
    public String getBookCode() { return bookCode; }
    public String getTitle()    { return title; }
    public String getAuthor()   { return author; }
    public int    getQuantity() { return quantity; }

    // Setters
    public void setId(int id)              { this.id = id; }
    public void setBookCode(String c)      { this.bookCode = c; }
    public void setTitle(String title)     { this.title = title; }
    public void setAuthor(String author)   { this.author = author; }
    public void setQuantity(int quantity)  { this.quantity = quantity; }

    @Override
    public String toString() {
        return "[" + bookCode + "] " + title + " (qty: " + quantity + ")";
    }
}
