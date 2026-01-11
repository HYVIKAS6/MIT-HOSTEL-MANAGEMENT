
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DataStorage.ensureDataFilesExist();
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            System.out.println("========================================");
            System.out.println("     MIT HOSTEL MANAGEMENT SYSTEM");
            System.out.println("========================================");
            System.out.println("1. Admin Login");
            System.out.println("2. Student Login");
            System.out.println("3. Exit");
            System.out.println("========================================");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        adminLogin(scanner);
                        break;
                    case 2:
                        studentLoginOrRegister(scanner);
                        break;
                    case 3:
                        System.out.println("Exiting...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private static void adminLogin(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        AdminDAO adminDAO = new AdminDAO();
        Admin admin = adminDAO.login(username, password);

        if (admin != null) {
            System.out.println("Admin login successful!");
            AdminService adminService = new AdminService(scanner);
            adminService.showMenu();
        } else {
            System.out.println("Invalid admin credentials.");
        }
    }

    private static void studentLoginOrRegister(Scanner scanner) {
        System.out.println("\n1. Login");
        System.out.println("2. Register");
        System.out.print("Enter choice: ");
        int choice;
        try{
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }


        if (choice == 1) {
            studentLogin(scanner);
        } else if (choice == 2) {
            studentRegister(scanner);
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static void studentLogin(Scanner scanner){
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.login(username, password);

        if (student != null) {
            System.out.println("Student login successful!");
            StudentService studentService = new StudentService(scanner, student);
            studentService.showMenu();
        } else {
            System.out.println("Invalid student credentials.");
        }
    }

    private static void studentRegister(Scanner scanner) {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter contact: ");
        String contact = scanner.nextLine();

        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.createStudent(name, username, password, contact);

        if (student != null) {
            System.out.println("Student registration successful!");
            StudentService studentService = new StudentService(scanner, student);
            studentService.showMenu();
        } else {
            System.out.println("Student registration failed.");
        }
    }
}
