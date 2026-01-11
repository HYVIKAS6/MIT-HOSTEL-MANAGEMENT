
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public Student login(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.STUDENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    if (parts[2].equals(username) && parts[3].equals(password)) {
                        return new Student(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3], parts[4]);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Login failed: " + e.getMessage());
        }
        return null;
    }

    public Student createStudent(String name, String username, String password, String contact) {
        int newId = getNextId();
        try (FileWriter writer = new FileWriter(DataStorage.STUDENTS_FILE, true)) {
            writer.write(String.format("%d,%s,%s,%s,%s\n", newId, name, username, password, contact));
            return new Student(newId, name, username, password, contact);
        } catch (IOException e) {
            System.err.println("Error creating student: " + e.getMessage());
        }
        return null;
    }

    private int getNextId() {
        int maxId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.STUDENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        if (id > maxId) {
                            maxId = id;
                        }
                    } catch (NumberFormatException e) {
                        // Ignore lines with invalid id
                    }
                }
            }
        } catch (IOException e) {
            // Ignore if file doesn't exist yet
        }
        return maxId + 1;
    }

    public boolean deleteStudentById(int studentId) {
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.STUDENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0) {
                    if (Integer.parseInt(parts[0]) == studentId) {
                        found = true;
                        // Skip this line
                    } else {
                        lines.add(line);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading students for deletion: " + e.getMessage());
            return false;
        }

        if (found) {
            try (FileWriter writer = new FileWriter(DataStorage.STUDENTS_FILE, false)) { // Overwrite
                for (String line : lines) {
                    writer.write(line + "\n");
                }
                return true;
            } catch (IOException e) {
                System.err.println("Error writing students after deletion: " + e.getMessage());
                return false;
            }
        }

        return false; // Student not found
    }

    public List<String> getAllStudentsWithAllocationDetails() {
        List<String> studentDetails = new ArrayList<>();
        List<Student> students = getAllStudents();
        
        for (Student student : students) {
            String allocationInfo = getAllocationInfoForStudent(student.getId());
            studentDetails.add(String.format("ID: %d, Name: %s, %s", student.getId(), student.getName(), allocationInfo));
        }
        return studentDetails;
    }

    private List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.STUDENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    students.add(new Student(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3], parts[4]));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error getting students: " + e.getMessage());
        }
        return students;
    }

    private String getAllocationInfoForStudent(int studentId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.ALLOCATIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && Integer.parseInt(parts[1]) == studentId) {
                    int roomId = Integer.parseInt(parts[2]);
                    String roomNumber = getRoomNumberById(roomId);
                    return "Room: " + roomNumber;
                }
            }
        } catch (IOException | NumberFormatException e) {
            // Error reading file or no allocation found
        }
        return "Room: Not Allocated";
    }

    private String getRoomNumberById(int roomId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.ROOMS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 1 && Integer.parseInt(parts[0]) == roomId) {
                    return parts[1]; // room_number
                }
            }
        } catch (IOException | NumberFormatException e) {
            // ignore
        }
        return "Unknown";
    }

    public int getStudentCount() {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.STUDENTS_FILE))) {
            while (reader.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            System.err.println("Error counting students: " + e.getMessage());
        }
        return count;
    }
}
