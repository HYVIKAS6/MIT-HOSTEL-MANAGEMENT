
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AdminDAO {

    public Admin login(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DataStorage.ADMINS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[0]);
                    String storedUsername = parts[1];
                    String storedPassword = parts[2];
                    if (storedUsername.equals(username) && storedPassword.equals(password)) {
                        return new Admin(id, storedUsername, storedPassword);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Login failed: " + e.getMessage());
        }
        return null;
    }
}
