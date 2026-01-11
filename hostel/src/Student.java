
public class Student extends User {
    private String name;
    private String contact;

    public Student(int id, String name, String username, String password, String contact) {
        super(id, username, password);
        this.name = name;
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }
}
