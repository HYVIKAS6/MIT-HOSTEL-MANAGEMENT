
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDAO {

    public Complaint fileComplaint(int studentId, String description) {
        int newId = getNextId();
        String today = LocalDate.now().toString();
        String status = "Pending";
        try (FileWriter writer = new FileWriter(DataStorage.COMPLAINTS_FILE, true)) {
            writer.write(String.format("%d,%d,%s,%s,%s\n", newId, studentId, description, status, today));
            return new Complaint(newId, studentId, description, status, today);
        } catch (IOException e) {
            System.err.println("Error filing complaint: " + e.getMessage());
        }
        return null;
    }

    public List<String> getAllComplaintsWithDetails() {
        List<String> complaintDetails = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.COMPLAINTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    int complaintId = Integer.parseInt(parts[0]);
                    int studentId = Integer.parseInt(parts[1]);
                    String studentName = getStudentNameById(studentId);
                    String description = parts[2];
                    String status = parts[3];
                    String date = parts[4];
                    complaintDetails.add("ID: " + complaintId + ", Student: " + studentName + ", Complaint: " + description +
                                     ", Status: " + status + ", Date: " + date);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error getting all complaints: " + e.getMessage());
        }
        return complaintDetails;
    }

    public boolean updateComplaintStatus(int complaintId, String newStatus) {
        List<String> lines = new ArrayList<>();
        boolean found = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.COMPLAINTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    if (Integer.parseInt(parts[0]) == complaintId) {
                        parts[3] = newStatus; // Update status
                        line = String.join(",", parts);
                        found = true;
                    }
                }
                lines.add(line);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading complaints for update: " + e.getMessage());
            return false;
        }

        if (found) {
            try (FileWriter writer = new FileWriter(DataStorage.COMPLAINTS_FILE, false)) { // Overwrite
                for (String line : lines) {
                    writer.write(line + "\n");
                }
                return true;
            } catch (IOException e) {
                System.err.println("Error writing complaints after update: " + e.getMessage());
                return false;
            }
        }
        return false; // Not found
    }


    public List<String> getComplaintsByStudentId(int studentId) {
        List<String> complaintDetails = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.COMPLAINTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5 && Integer.parseInt(parts[1]) == studentId) {
                    String description = parts[2];
                    String status = parts[3];
                    String date = parts[4];
                    complaintDetails.add("Complaint: " + description + ", Status: " +
                                     status + ", Date: " + date);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error getting complaints for student: " + e.getMessage());
        }
        return complaintDetails;
    }
    
    private String getStudentNameById(int studentId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.STUDENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 1 && Integer.parseInt(parts[0]) == studentId) {
                    return parts[1]; // name
                }
            }
        } catch (IOException | NumberFormatException e) {
            // ignore
        }
        return "Unknown";
    }

    private int getNextId() {
        int maxId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.COMPLAINTS_FILE))) {
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
}
