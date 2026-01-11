
import java.util.List;
import java.util.Scanner;

public class StudentService {
    private Scanner scanner;
    private Student student;

    public StudentService(Scanner scanner, Student student) {
        this.scanner = scanner;
        this.student = student;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\nStudent Menu:");
            System.out.println("1. View My Allocation");
            System.out.println("2. File a Complaint");
            System.out.println("3. View My Complaints");
            System.out.println("4. Logout");
            System.out.print("Enter choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        viewMyAllocation();
                        break;
                    case 2:
                        fileComplaint();
                        break;
                    case 3:
                        viewMyComplaints();
                        break;
                    case 4:
                        return; // Logout
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private void viewMyAllocation() {
        AllocationDAO allocationDAO = new AllocationDAO();
        String allocation = allocationDAO.getAllocationByStudentId(student.getId());
        System.out.println("\n--- My Allocation ---");
        if (allocation != null) {
            System.out.println(allocation);
        } else {
            System.out.println("No allocation found.");
        }
        System.out.println("---------------------");
    }

    private void fileComplaint() {
        System.out.print("Enter complaint description: ");
        String description = scanner.nextLine();
        ComplaintDAO complaintDAO = new ComplaintDAO();
        Complaint complaint = complaintDAO.fileComplaint(student.getId(), description);
        if (complaint != null) {
            System.out.println("Complaint filed successfully.");
        } else {
            System.out.println("Failed to file complaint.");
        }
    }

    private void viewMyComplaints() {
        ComplaintDAO complaintDAO = new ComplaintDAO();
        List<String> complaints = complaintDAO.getComplaintsByStudentId(student.getId());
        System.out.println("\n--- My Complaints ---");
        if (complaints.isEmpty()) {
            System.out.println("No complaints found.");
        } else {
            complaints.forEach(System.out::println);
        }
        System.out.println("---------------------");
    }
}
