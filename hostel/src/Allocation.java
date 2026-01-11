
public class Allocation {
    private int id;
    private int studentId;
    private int roomId;
    private String allocationDate;

    public Allocation(int id, int studentId, int roomId, String allocationDate) {
        this.id = id;
        this.studentId = studentId;
        this.roomId = roomId;
        this.allocationDate = allocationDate;
    }

    public int getId() {
        return id;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getAllocationDate() {
        return allocationDate;
    }
}
