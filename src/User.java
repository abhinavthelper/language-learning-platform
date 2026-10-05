public abstract class User {
    protected int id;
    protected String name;
    protected String email;
    protected String role;

    public User(int id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public abstract void showMenu();

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}