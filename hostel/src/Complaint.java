
public class Complaint {
    private int id;
    private int studentId;
    private String description;
    private String status;
    private String dateFiled;

    public Complaint(int id, int studentId, String description, String status, String dateFiled) {
        this.id = id;
        this.studentId = studentId;
        this.description = description;
        this.status = status;
        this.dateFiled = dateFiled;
    }

    public int getId() {
        return id;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getDateFiled() {
        return dateFiled;
    }
}
