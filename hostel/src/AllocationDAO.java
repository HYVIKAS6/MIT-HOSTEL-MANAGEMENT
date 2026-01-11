
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AllocationDAO {

    public Allocation allocateRoom(int studentId, int roomId) {
        if (isStudentAllocated(studentId)) {
            System.err.println("Student is already allocated a room.");
            return null;
        }

        RoomDAO roomDAO = new RoomDAO();
        Room room = roomDAO.getRoomById(roomId);
        if (room == null || room.isFull()) {
            System.err.println("Room is full or does not exist.");
            return null;
        }

        int newId = getNextId();
        String today = LocalDate.now().toString();
        try (FileWriter writer = new FileWriter(DataStorage.ALLOCATIONS_FILE, true)) {
            writer.write(String.format("%d,%d,%d,%s\n", newId, studentId, roomId, today));
            roomDAO.updateRoomOccupancy(roomId, 1);
            return new Allocation(newId, studentId, roomId, today);
        } catch (IOException e) {
            System.err.println("Error allocating room: " + e.getMessage());
        }
        return null;
    }

    public boolean isStudentAllocated(int studentId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.ALLOCATIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 1) {
                    if (Integer.parseInt(parts[1]) == studentId) {
                        return true;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            // File might not exist, or have bad data
        }
        return false;
    }

    public List<String> getAllAllocationsWithDetails() {
        List<String> allocationDetails = new ArrayList<>();
        // This is inefficient, but necessary for flat files.
        // In a real application, you'd use a database for this.
        try (BufferedReader allocReader = new BufferedReader(new FileReader(DataStorage.ALLOCATIONS_FILE))) {
            String allocLine;
            while ((allocLine = allocReader.readLine()) != null) {
                String[] allocParts = allocLine.split(",");
                if (allocParts.length == 4) {
                    int studentId = Integer.parseInt(allocParts[1]);
                    int roomId = Integer.parseInt(allocParts[2]);
                    String date = allocParts[3];

                    String studentName = getStudentNameById(studentId);
                    String roomNumber = getRoomNumberById(roomId);

                    allocationDetails.add("Student: " + studentName + ", Room: " + roomNumber + ", Date: " + date);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error getting all allocations: " + e.getMessage());
        }
        return allocationDetails;
    }

    public String getAllocationByStudentId(int studentId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.ALLOCATIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && Integer.parseInt(parts[1]) == studentId) {
                    int roomId = Integer.parseInt(parts[2]);
                    String date = parts[3];
                    String studentName = getStudentNameById(studentId);
                    String roomNumber = getRoomNumberById(roomId);
                    return "Student: " + studentName + ", Room: " + roomNumber + ", Date: " + date;
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error getting allocation for student: " + e.getMessage());
        }
        return null;
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

    private int getNextId() {
        int maxId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.ALLOCATIONS_FILE))) {
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

    public int deleteAllocationByStudentId(int studentId) {
        List<String> lines = new ArrayList<>();
        int roomId = -1;
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.ALLOCATIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    if (Integer.parseInt(parts[1]) == studentId) {
                        roomId = Integer.parseInt(parts[2]);
                        found = true;
                        // Don't add this line to the list
                    } else {
                        lines.add(line);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading allocations for deletion: " + e.getMessage());
            return -1;
        }

        if (found) {
            try (FileWriter writer = new FileWriter(DataStorage.ALLOCATIONS_FILE, false)) { // Overwrite
                for (String line : lines) {
                    writer.write(line + "\n");
                }
            } catch (IOException e) {
                System.err.println("Error writing allocations after deletion: " + e.getMessage());
                return -1;
            }
        }

        return roomId;
    }
}
