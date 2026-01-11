
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public Room addRoom(String roomNumber, int capacity) {
        int newId = getNextId();
        try (FileWriter writer = new FileWriter(DataStorage.ROOMS_FILE, true)) {
            writer.write(String.format("%d,%s,%d,0\n", newId, roomNumber, capacity));
            return new Room(newId, roomNumber, capacity, 0);
        } catch (IOException e) {
            System.err.println("Error adding room: " + e.getMessage());
        }
        return null;
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.ROOMS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    rooms.add(new Room(Integer.parseInt(parts[0]), parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3])));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error getting rooms: " + e.getMessage());
        }
        return rooms;
    }

    public Room getRoomById(int id) {
        for (Room room : getAllRooms()) {
            if (room.getId() == id) {
                return room;
            }
        }
        return null;
    }

    public void updateRoomOccupancy(int roomId, int change) {
        List<Room> rooms = getAllRooms();
        for (Room room : rooms) {
            if (room.getId() == roomId) {
                // This is not how Room objects work. I need to create a new one.
                // Let's create a new list of strings to write to the file.
            }
        }

        List<String> lines = new ArrayList<>();
        boolean updated = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.ROOMS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    int id = Integer.parseInt(parts[0]);
                    if (id == roomId) {
                        int newOccupancy = Integer.parseInt(parts[3]) + change;
                        lines.add(String.format("%d,%s,%d,%d", id, parts[1], Integer.parseInt(parts[2]), newOccupancy));
                        updated = true;
                    } else {
                        lines.add(line);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading rooms for update: " + e.getMessage());
            return;
        }

        if (updated) {
            try (FileWriter writer = new FileWriter(DataStorage.ROOMS_FILE, false)) { // false to overwrite
                for (String line : lines) {
                    writer.write(line + "\n");
                }
            } catch (IOException e) {
                System.err.println("Error updating room occupancy: " + e.getMessage());
            }
        }
    }

    private int getNextId() {
        int maxId = 0;
        for (Room room : getAllRooms()) {
            if (room.getId() > maxId) {
                maxId = room.getId();
            }
        }
        return maxId + 1;
    }

    public int deleteRoomById(int roomId) {
        Room room = getRoomById(roomId);
        if (room == null) {
            return 1; // Room not found
        }
        if (room.getCurrentOccupancy() > 0) {
            return 2; // Room not empty
        }

        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.ROOMS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0) {
                    if (Integer.parseInt(parts[0]) == roomId) {
                        found = true;
                        // Skip this line
                    } else {
                        lines.add(line);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading rooms for deletion: " + e.getMessage());
            return -1; // General error
        }

        if (found) {
            try (FileWriter writer = new FileWriter(DataStorage.ROOMS_FILE, false)) { // Overwrite
                for (String line : lines) {
                    writer.write(line + "\n");
                }
                return 0; // Success
            } catch (IOException e) {
                System.err.println("Error writing rooms after deletion: " + e.getMessage());
                return -1; // General error
            }
        }

        return 1; // Should not happen if found before, but as a fallback
    }
    
    public List<String> getRoomAllocationDetails() {
        List<String> details = new ArrayList<>();
        List<Room> rooms = getAllRooms();
        List<Allocation> allocations = getAllAllocations();
        List<Student> students = getAllStudents();

        for (Room room : rooms) {
            StringBuilder roomDetails = new StringBuilder();
            roomDetails.append(String.format("ID: %d | Room %s (%d/%d):", 
                room.getId(), room.getRoomNumber(), room.getCurrentOccupancy(), room.getCapacity()));
            
            boolean empty = true;
            for (Allocation alloc : allocations) {
                if (alloc.getRoomId() == room.getId()) {
                    for (Student student : students) {
                        if (student.getId() == alloc.getStudentId()) {
                            roomDetails.append("\n - ").append(student.getName());
                            empty = false;
                            break;
                        }
                    }
                }
            }
            if (empty) {
                roomDetails.append("\n - (Empty)");
            }
            details.add(roomDetails.toString());
        }
        return details;
    }

    private List<Allocation> getAllAllocations() {
        List<Allocation> allocations = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.ALLOCATIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    allocations.add(new Allocation(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), 
                                    Integer.parseInt(parts[2]), parts[3]));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error getting allocations: " + e.getMessage());
        }
        return allocations;
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
}
