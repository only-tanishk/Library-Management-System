package model;

/**
 * User model — supports both admin and student roles with login credentials.
 */
public class User {

    private int    id;
    private String name;
    private String username;
    private String password;
    private String role;     // "admin" or "student"

    public User() {}

    // For signup (student)
    public User(String name, String username, String password) {
        this.name     = name;
        this.username = username;
        this.password = password;
        this.role     = "student";
    }

    // Full constructor (from DB)
    public User(int id, String name, String username, String password, String role) {
        this.id       = id;
        this.name     = name;
        this.username = username;
        this.password = password;
        this.role     = role;
    }

    // Getters
    public int    getId()       { return id; }
    public String getName()     { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole()     { return role; }

    // Setters
    public void setId(int id)              { this.id = id; }
    public void setName(String name)       { this.name = name; }
    public void setUsername(String u)      { this.username = u; }
    public void setPassword(String p)      { this.password = p; }
    public void setRole(String role)       { this.role = role; }

    public boolean isAdmin()   { return "admin".equals(role); }
    public boolean isStudent() { return "student".equals(role); }

    @Override
    public String toString() { return name; }
}
