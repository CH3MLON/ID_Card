import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static DatabaseManager dbManager = new DatabaseManager();

    public static void main(String[] args) {
        File outputDir = new File("output");
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }

        boolean running = true;

        while (running) {
            displayMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    generateNewIDCard();
                    break;
                case "2":
                    viewAllStudents();
                    break;
                case "3":
                    searchStudent();
                    break;
                case "4":
                    updateStudent();
                    break;
                case "5":
                    deleteStudent();
                    break;
                case "6":
                    viewIssueLogs();
                    break;
                case "7":
                    System.out.println("\n[INFO] Exiting application. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("\n[ERROR] Invalid choice. Please enter 1-7.\n");
            }
        }

        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("================================");
        System.out.println("   DIGITAL ID CARD SYSTEM");
        System.out.println("================================");
        System.out.println("1. Generate New ID Card");
        System.out.println("2. View All Students");
        System.out.println("3. Search Student by ID");
        System.out.println("4. Update Student Information");
        System.out.println("5. Delete Student Record");
        System.out.println("6. View Issue Logs");
        System.out.println("7. Exit");
        System.out.println("================================");
        System.out.print("Enter your choice (1-7): ");
    }

    private static void generateNewIDCard() {
        System.out.println("\n=== Generate New ID Card ===");

        System.out.print("Enter student name: ");
        String name = scanner.nextLine();
        if (!Utils.validateNotEmpty(name)) {
            System.out.println("[ERROR] Name cannot be empty.\n");
            return;
        }

        System.out.print("Enter course: ");
        String course = scanner.nextLine();
        if (!Utils.validateNotEmpty(course)) {
            System.out.println("[ERROR] Course cannot be empty.\n");
            return;
        }

        System.out.print("Enter year: ");
        String year = scanner.nextLine();
        if (!Utils.validateNotEmpty(year)) {
            System.out.println("[ERROR] Year cannot be empty.\n");
            return;
        }

        System.out.print("Enter photo path: ");
        String photoPath = scanner.nextLine();
        if (!Utils.validateFile(photoPath)) {
            System.out.println("[WARNING] Photo file not found: " + photoPath);
            System.out.print("Continue with placeholder photo? (Y/N): ");
            String confirm = scanner.nextLine();
            if (!confirm.equalsIgnoreCase("Y")) {
                System.out.println("[INFO] Operation cancelled.\n");
                return;
            }
        }

        String studentId = Utils.generateStudentId();
        int attempts = 0;
        while (!Utils.checkStudentIdUnique(studentId, dbManager) && attempts < 10) {
            studentId = Utils.generateStudentId();
            attempts++;
        }

        if (attempts >= 10) {
            System.out.println("[ERROR] Failed to generate unique student ID. Please try again.\n");
            return;
        }

        Student student = new Student(studentId, name, course, year, photoPath);

        System.out.println("\nGenerating ID card...");
        if (!dbManager.insertStudent(student)) {
            System.out.println("[ERROR] Failed to save student to database.\n");
            return;
        }

        String outputPath = "output/" + studentId + ".png";
        if (!IDCardGenerator.generateCard(student, outputPath)) {
            System.out.println("[ERROR] Failed to generate ID card image (but student saved in database).\n");
            return;
        }

        dbManager.logIssuance(studentId);

        System.out.println("[SUCCESS] ID card generated successfully!");
        System.out.println("Student ID: " + studentId);
        System.out.println("Card saved to: " + outputPath + "\n");
    }

    private static void viewAllStudents() {
        System.out.println("\n=== All Students ===");

        List<Student> students = dbManager.getAllStudents();

        if (students.isEmpty()) {
            System.out.println("No students found in database.\n");
            return;
        }

        System.out.printf("%-12s | %-20s | %-20s | %-10s%n", "ID", "Name", "Course", "Year");
        System.out.println("-----------------------------------------------------------");

        for (Student student : students) {
            System.out.printf("%-12s | %-20s | %-20s | %-10s%n",
                student.getStudentId(),
                student.getName(),
                student.getCourse(),
                student.getYear());
        }

        System.out.println("\nTotal: " + students.size() + " students\n");
    }

    private static void searchStudent() {
        System.out.println("\n=== Search Student ===");
        System.out.print("Enter student ID to search: ");
        String studentId = scanner.nextLine();

        Student student = dbManager.getStudentById(studentId);

        if (student == null) {
            System.out.println("[ERROR] Student not found with ID: " + studentId + "\n");
            return;
        }

        System.out.println("\n=== Student Details ===");
        System.out.println("Student ID: " + student.getStudentId());
        System.out.println("Name: " + student.getName());
        System.out.println("Course: " + student.getCourse());
        System.out.println("Year: " + student.getYear());
        System.out.println("Photo Path: " + student.getPhotoPath());
        System.out.println("ID Card Location: output/" + student.getStudentId() + ".png\n");
    }

    private static void updateStudent() {
        System.out.println("\n=== Update Student Information ===");
        System.out.print("Enter student ID to update: ");
        String studentId = scanner.nextLine();

        Student student = dbManager.getStudentById(studentId);

        if (student == null) {
            System.out.println("[ERROR] Student not found with ID: " + studentId + "\n");
            return;
        }

        System.out.println("\n=== Current Information ===");
        System.out.println("Name: " + student.getName());
        System.out.println("Course: " + student.getCourse());
        System.out.println("Year: " + student.getYear());
        System.out.println("Photo Path: " + student.getPhotoPath());

        System.out.println("\n=== Enter New Information (press Enter to keep current) ===");

        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();
        if (Utils.validateNotEmpty(newName)) {
            student.setName(newName);
        }

        System.out.print("Enter new course: ");
        String newCourse = scanner.nextLine();
        if (Utils.validateNotEmpty(newCourse)) {
            student.setCourse(newCourse);
        }

        System.out.print("Enter new year: ");
        String newYear = scanner.nextLine();
        if (Utils.validateNotEmpty(newYear)) {
            student.setYear(newYear);
        }

        System.out.print("Enter new photo path: ");
        String newPhotoPath = scanner.nextLine();
        if (Utils.validateNotEmpty(newPhotoPath)) {
            if (!Utils.validateFile(newPhotoPath)) {
                System.out.println("[WARNING] Photo file not found: " + newPhotoPath);
                System.out.print("Continue with this path? (Y/N): ");
                String confirm = scanner.nextLine();
                if (!confirm.equalsIgnoreCase("Y")) {
                    System.out.println("[INFO] Photo path not updated.");
                } else {
                    student.setPhotoPath(newPhotoPath);
                }
            } else {
                student.setPhotoPath(newPhotoPath);
            }
        }

        if (!dbManager.updateStudent(student)) {
            System.out.println("[ERROR] Failed to update student in database.\n");
            return;
        }

        String outputPath = "output/" + studentId + ".png";
        if (!IDCardGenerator.generateCard(student, outputPath)) {
            System.out.println("[WARNING] Failed to regenerate ID card image.\n");
        }

        dbManager.logIssuance(studentId);

        System.out.println("[SUCCESS] Student information updated and ID card regenerated!\n");
    }

    private static void deleteStudent() {
        System.out.println("\n=== Delete Student Record ===");
        System.out.print("Enter student ID to delete: ");
        String studentId = scanner.nextLine();

        Student student = dbManager.getStudentById(studentId);

        if (student == null) {
            System.out.println("[ERROR] Student not found with ID: " + studentId + "\n");
            return;
        }

        System.out.println("\n=== Student Information ===");
        System.out.println("Student ID: " + student.getStudentId());
        System.out.println("Name: " + student.getName());
        System.out.println("Course: " + student.getCourse());
        System.out.println("Year: " + student.getYear());

        System.out.print("\nAre you sure you want to delete this student? (Y/N): ");
        String confirmation = scanner.nextLine();

        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("[INFO] Deletion cancelled.\n");
            return;
        }

        if (!dbManager.deleteStudent(studentId)) {
            System.out.println("[ERROR] Failed to delete student from database.\n");
            return;
        }

        File cardFile = new File("output/" + studentId + ".png");
        if (cardFile.exists()) {
            cardFile.delete();
        }

        System.out.println("[SUCCESS] Student record deleted successfully!\n");
    }

    private static void viewIssueLogs() {
        System.out.println("\n=== ID Card Issue Logs ===");

        List<String[]> logs = dbManager.getIssueLogs();

        if (logs.isEmpty()) {
            System.out.println("No issue logs found.\n");
            return;
        }

        System.out.printf("%-7s | %-12s | %-20s | %-20s%n", "Log ID", "Student ID", "Name", "Issued At");
        System.out.println("-------------------------------------------------------");

        for (String[] log : logs) {
            System.out.printf("%-7s | %-12s | %-20s | %-20s%n",
                log[0], log[1], log[2], log[3]);
        }

        System.out.println("\nTotal: " + logs.size() + " logs\n");
    }
}
