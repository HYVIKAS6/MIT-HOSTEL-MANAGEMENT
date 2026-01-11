import java.util.List;
import java.util.Scanner;

public class AdminService {
    private Scanner scanner;
    private RoomDAO roomDAO;
    private AllocationDAO allocationDAO;
    private ComplaintDAO complaintDAO;

    public AdminService(Scanner scanner) {
        this.scanner = scanner;
        this.roomDAO = new RoomDAO();
        this.allocationDAO = new AllocationDAO();
        this.complaintDAO = new ComplaintDAO();
    }

    public void showMenu() {
        while (true) {
            System.out.println("\nAdmin Menu:");
            System.out.println("1. Add Room");
            System.out.println("2. Allocate Room to Student");
            System.out.println("3. View All Allocations");
            System.out.println("4. View Complaints");
            System.out.println("5. Update Complaint Status");
            System.out.println("6. Generate Reports");
            System.out.println("7. Delete Student");
            System.out.println("8. Delete Room");
            System.out.println("9. View Room Details");
            System.out.println("10. Deallocate Student");
            System.out.println("11. List All Students");
            System.out.println("12. Logout");
            System.out.print("Enter choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        addRoom();
                        break;
                    case 2:
                        allocateRoom();
                        break;
                    case 3:
                        viewAllAllocations();
                        break;
                    case 4:
                        viewComplaints();
                        break;
                    case 5:
                        updateComplaintStatus();
                        break;
                    case 6:
                        generateReports();
                        break;
                    case 7:
                        deleteStudent();
                        break;
                    case 8:
                        deleteRoom();
                        break;
                    case 9:
                        viewRoomDetails();
                        break;
                    case 10:
                        deallocateStudent();
                        break;
                    case 11:
                        listAllStudents();
                        break;
                    case 12:
                        return; // Logout
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private void listAllStudents() {
        StudentDAO studentDAO = new StudentDAO();
        System.out.println("\n--- All Registered Students ---");
        List<String> studentList = studentDAO.getAllStudentsWithAllocationDetails();
        if (studentList.isEmpty()) {
            System.out.println("No students found.");
        } else {
            studentList.forEach(System.out::println);
        }
        System.out.println("-------------------------------");
    }

    private void addRoom() {
        System.out.print("Enter room number: ");
        String roomNumber = scanner.nextLine();
        System.out.print("Enter capacity: ");
        try {
            int capacity = Integer.parseInt(scanner.nextLine());
            Room room = roomDAO.addRoom(roomNumber, capacity);
            if (room != null) {
                System.out.println("Room added successfully.");
            } else {
                System.out.println("Failed to add room.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid capacity.");
        }
    }

    private void allocateRoom() {
        System.out.print("Enter student ID: ");
        int studentId = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter room ID: ");
        int roomId = Integer.parseInt(scanner.nextLine());

        Allocation allocation = allocationDAO.allocateRoom(studentId, roomId);
        if (allocation != null) {
            System.out.println("Room allocated successfully.");
        } else {
            System.out.println("Failed to allocate room.");
        }
    }

    private void viewAllAllocations() {
        List<String> allocations = allocationDAO.getAllAllocationsWithDetails();
        System.out.println("\n--- All Allocations ---");
        if (allocations.isEmpty()) {
            System.out.println("No allocations found.");
        } else {
            allocations.forEach(System.out::println);
        }
        System.out.println("----------------------");
    }

    private void viewComplaints() {
        List<String> complaints = complaintDAO.getAllComplaintsWithDetails();
        System.out.println("\n--- All Complaints ---");
        if (complaints.isEmpty()) {
            System.out.println("No complaints found.");
        } else {
            complaints.forEach(System.out::println);
        }
        System.out.println("--------------------");
    }

    private void updateComplaintStatus() {
        viewComplaints(); // Show complaints first for context
        System.out.print("\nEnter the ID of the complaint to update: ");
        try {
            int complaintId = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter the new status (e.g., Resolved, In Progress): ");
            String newStatus = scanner.nextLine();

            boolean success = complaintDAO.updateComplaintStatus(complaintId, newStatus);

            if (success) {
                System.out.println("Complaint status updated successfully.");
            } else {
                System.out.println("Failed to update complaint status. Complaint ID not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid complaint ID. Please enter a number.");
        }
    }

    private void generateReports() {
        // Simple report of room occupancy
        List<Room> rooms = roomDAO.getAllRooms();
        System.out.println("\n--- Room Occupancy Report ---");
        for (Room room : rooms) {
            System.out.println("ID: " + room.getId() + " | Room " + room.getRoomNumber() + ": " + 
                               room.getCurrentOccupancy() + "/" + room.getCapacity());
        }
        System.out.println("-----------------------------");
    }

    private void deleteStudent() {
        StudentDAO studentDAO = new StudentDAO();

        System.out.println("\n--- Available Students ---");
        List<String> studentList = studentDAO.getAllStudentsWithAllocationDetails();
        if (studentList.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        studentList.forEach(System.out::println);
        System.out.println("--------------------------");

        System.out.print("Enter student ID to delete: ");
        try {
            int studentId = Integer.parseInt(scanner.nextLine());
            
            AllocationDAO allocationDAO = new AllocationDAO();
            RoomDAO roomDAO = new RoomDAO();

            // First, delete the allocation and get the room ID
            int roomId = allocationDAO.deleteAllocationByStudentId(studentId);

            // If the student had an allocation, update the room occupancy
            if (roomId != -1) {
                roomDAO.updateRoomOccupancy(roomId, -1);
            }

            // Finally, delete the student
            boolean deleted = studentDAO.deleteStudentById(studentId);

            if (deleted) {
                System.out.println("Student deleted successfully.");
                if (roomId != -1) {
                    System.out.println("Student's room allocation has been removed.");
                }
            } else {
                System.out.println("Student not found or error during deletion.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid student ID.");
        }
    }

    private void deleteRoom() {
        System.out.print("Enter room ID to delete: ");
        try {
            int roomId = Integer.parseInt(scanner.nextLine());
            RoomDAO roomDAO = new RoomDAO();
            int result = roomDAO.deleteRoomById(roomId);

            switch (result) {
                case 0:
                    System.out.println("Room deleted successfully.");
                    break;
                case 1:
                    System.out.println("Error: Room not found.");
                    break;
                case 2:
                    System.out.println("Error: Cannot delete room because it is occupied.");
                    break;
                default:
                    System.out.println("An unexpected error occurred during room deletion.");
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid room ID.");
        }
    }

    private void deallocateStudent() {
        StudentDAO studentDAO = new StudentDAO();

        System.out.println("\n--- All Students with Allocations ---");
        List<String> studentList = studentDAO.getAllStudentsWithAllocationDetails();
        if (studentList.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        studentList.forEach(System.out::println);
        System.out.println("-------------------------------------");
        
        System.out.print("Enter student ID to deallocate: ");
        try {
            int studentId = Integer.parseInt(scanner.nextLine());

            int roomId = allocationDAO.deleteAllocationByStudentId(studentId);

            if (roomId != -1) {
                roomDAO.updateRoomOccupancy(roomId, -1);
                System.out.println("Student deallocated successfully.");
            } else {
                System.out.println("Student not found or not allocated.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid student ID.");
        }
    }

    private void viewRoomDetails() {
        RoomDAO roomDAO = new RoomDAO();
        System.out.println("\n--- Room Allocation Details ---");
        List<String> roomDetails = roomDAO.getRoomAllocationDetails();
        if (roomDetails.isEmpty()) {
            System.out.println("No rooms found.");
        } else {
            for (String detail : roomDetails) {
                System.out.println(detail);
            }
        }
        System.out.println("-----------------------------");
    }
}
