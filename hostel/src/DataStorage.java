import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

public class DataStorage {
    public static final String DATA_DIR = "data";
    public static final String ADMINS_FILE = DATA_DIR + "/admins.csv";
    public static final String STUDENTS_FILE = DATA_DIR + "/students.csv";
    public static final String ROOMS_FILE = DATA_DIR + "/rooms.csv";
    public static final String ALLOCATIONS_FILE = DATA_DIR + "/allocations.csv";
    public static final String COMPLAINTS_FILE = DATA_DIR + "/complaints.csv";

    public static void ensureDataFilesExist() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        createFileIfNotExists(ADMINS_FILE);
        createFileIfNotExists(STUDENTS_FILE);
        createFileIfNotExists(ROOMS_FILE);
        createFileIfNotExists(ALLOCATIONS_FILE);
        createFileIfNotExists(COMPLAINTS_FILE);
        
        // Add a default admin if the file is empty
        try {
            File adminFile = new File(ADMINS_FILE);
            if (adminFile.length() == 0) {
                try (FileWriter writer = new FileWriter(adminFile)) {
                    // id,username,password
                    writer.write("1,admin,admin\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Error initializing admin data: " + e.getMessage());
        }
    }

    private static void createFileIfNotExists(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating data file: " + e.getMessage());
            }
        }
    }
}
