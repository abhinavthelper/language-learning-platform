public class Admin extends User {
    public Admin(int id, String name, String email) {
        super(id, name, email, "ADMIN");
    }

    @Override
    public void showMenu() {
        System.out.println("--- ADMIN DASHBOARD ---");
        System.out.println("1. Manage Users");
        System.out.println("2. Approve Lessons");
        System.out.println("3. System Settings");
        System.out.println("4. Activity Log");
    }
}