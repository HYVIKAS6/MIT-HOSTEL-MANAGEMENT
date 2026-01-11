
public class Room {
    private int id;
    private String roomNumber;
    private int capacity;
    private int currentOccupancy;

    public Room(int id, String roomNumber, int capacity, int currentOccupancy) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.currentOccupancy = currentOccupancy;
    }

    public int getId() {
        return id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public boolean isFull() {
        return currentOccupancy >= capacity;
    }
}
