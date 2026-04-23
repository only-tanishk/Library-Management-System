package model;

import java.sql.Date;

/**
 * Transaction model — records a book issue/return event.
 * Fine rule: ₹50 per day after 15 days.
 */
public class Transaction {

    private static final int    FREE_DAYS   = 15;   // days before fine kicks in
    private static final double FINE_PER_DAY = 50.0; // ₹50 per overdue day

    private int    id;
    private int    bookId;
    private int    userId;
    private Date   issueDate;
    private Date   returnDate;
    private double finePaid;    // amount collected at return time

    // For display (from JOIN queries)
    private String bookTitle;
    private String bookCode;
    private String userName;

    public Transaction() {}

    public Transaction(int bookId, int userId, Date issueDate) {
        this.bookId    = bookId;
        this.userId    = userId;
        this.issueDate = issueDate;
    }

    // ─── Fine calculation ──────────────────────────────────────────────────────

    /**
     * Calculates the PENDING fine for an un-returned book based on today's date.
     * Returns 0 if the book was returned or within the free period.
     *
     * @param issueDate The date the book was issued.
     * @return Fine amount in ₹.
     */
    public static double calculatePendingFine(Date issueDate) {
        if (issueDate == null) return 0;
        long today   = System.currentTimeMillis();
        long issued  = issueDate.getTime();
        long days    = (today - issued) / (1000L * 60 * 60 * 24);
        return days > FREE_DAYS ? (days - FREE_DAYS) * FINE_PER_DAY : 0;
    }

    /** Returns days elapsed since issue date. */
    public static long daysElapsed(Date issueDate) {
        if (issueDate == null) return 0;
        return (System.currentTimeMillis() - issueDate.getTime()) / (1000L * 60 * 60 * 24);
    }

    // ─── Getters ───────────────────────────────────────────────────────────────
    public int    getId()        { return id; }
    public int    getBookId()    { return bookId; }
    public int    getUserId()    { return userId; }
    public Date   getIssueDate() { return issueDate; }
    public Date   getReturnDate(){ return returnDate; }
    public double getFinePaid()  { return finePaid; }
    public String getBookTitle() { return bookTitle; }
    public String getBookCode()  { return bookCode; }
    public String getUserName()  { return userName; }

    // ─── Setters ───────────────────────────────────────────────────────────────
    public void setId(int id)                  { this.id = id; }
    public void setBookId(int bookId)          { this.bookId = bookId; }
    public void setUserId(int userId)          { this.userId = userId; }
    public void setIssueDate(Date d)           { this.issueDate = d; }
    public void setReturnDate(Date d)          { this.returnDate = d; }
    public void setFinePaid(double finePaid)   { this.finePaid = finePaid; }
    public void setBookTitle(String t)         { this.bookTitle = t; }
    public void setBookCode(String c)          { this.bookCode = c; }
    public void setUserName(String n)          { this.userName = n; }
}
