public class Instructor extends User {
    public Instructor(int id, String name, String email) {
        super(id, name, email, "INSTRUCTOR");
    }

    @Override
    public void showMenu() {
        System.out.println("--- INSTRUCTOR DASHBOARD ---");
        System.out.println("1. Create Lesson");
        System.out.println("2. Provide Feedback");
        System.out.println("3. Track Learner Progress");
    }
}