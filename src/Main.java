import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Online Language Learning Platform ===");
        System.out.println("1. Admin");
        System.out.println("2. Instructor");
        System.out.println("3. Learner");
        System.out.print("Choose role: ");
        int choice = sc.nextInt();

        User user;
        if (choice == 1) {
            user = new Admin(1, "Admin One", "admin@mail.com");
        } else if (choice == 2) {
            user = new Instructor(2, "Instructor One", "inst@mail.com");
        } else {
            user = new Learner(3, "Learner One", "learner@mail.com");
        }

        user.showMenu();
    }
}