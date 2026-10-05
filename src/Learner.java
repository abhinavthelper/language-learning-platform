public class Learner extends User {
    public Learner(int id, String name, String email) {
        super(id, name, email, "LEARNER");
    }

    @Override
    public void showMenu() {
        System.out.println("--- LEARNER DASHBOARD ---");
        System.out.println("1. Take Lesson");
        System.out.println("2. View Progress");
        System.out.println("3. Interact with Learners");
        System.out.println("4. Update Profile");
    }
}