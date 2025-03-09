
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connect {

    public static Connection con;

    public static final String dbName = "feedback";
    public static final String username = "Administrator";
    public static final String password = "Admin";
    public static final String createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS " + dbName;

    private static final String WAMP_PROCESS_NAME = "mysqld";

    // Method to start WAMPP server if it's not already running
    public static void startWAMPP() throws IOException, InterruptedException {
        if (!isWAMPPRunning()) {
            System.out.println("WAMPP server is not running. Starting it...");
            String wampPath = "C:\\wamp64\\wampmanager.exe";
            ProcessBuilder processBuilder = new ProcessBuilder(wampPath);
            processBuilder.start();
            System.out.println("WAMPP server started.");
        } else {
            System.out.println("WAMPP server is already running.");
        }
    }

    // Method to check if WAMPP server is running
    private static boolean isWAMPPRunning() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("tasklist.exe");
        Process process = processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(WAMP_PROCESS_NAME)) {
                    return true; // WAMPP server process found
                }
            }
        }
        return false; // WAMPP server process not found
    }

    // Method to create the database and tables if they don't exist
    public static void initializeDatabase() throws SQLException {

        String createFeedbackQuery, createUserQuery, createInsertAdminQuery;

        createFeedbackQuery
                = """
        CREATE TABLE IF NOT EXISTS customer (        
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          Gender text NOT   NULL,
                          Email text   NOT NULL,
                          FirstName text   NOT NULL,
                          LastName text    NULL,
                          Comment text    NULL,
                          createdBy text ,
                          createdAt timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                        )""";
        
        createUserQuery
                = """
                CREATE TABLE IF NOT EXISTS `User` (
                          id int NOT NULL AUTO_INCREMENT,
                          user_pass TEXT NOT NULL,
                          email TEXT DEFAULT NULL,
                          username TEXT NOT NULL,   
                          createdAt timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (id),
                          UNIQUE KEY email (email(100))
                        )                                  
                  """;

        
        
        createInsertAdminQuery
                = """
                  INSERT IGNORE INTO `User` (id, user_pass, email, username)
                  SELECT 1, '335b202e18be9e2030a3d3ec6f6a355035e6ea47a0ab813230dcbe48dc18c7d0', 'musazapatrick369@gmail.com', 'Admin'
                  FROM dual
                  WHERE NOT EXISTS (SELECT * FROM `User` WHERE id = 1)
                  """;

        try {
            Statement statement1 = con.createStatement();
            Statement statement2 = con.createStatement();
            Statement statement3 = con.createStatement();
            statement1.executeUpdate(createFeedbackQuery);
            statement2.executeUpdate(createUserQuery);
            statement3.executeUpdate(createInsertAdminQuery);
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to open local server connection
    public static void openLocalServerConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/";
            Connection conn = DriverManager.getConnection(url, username, password);

            // Creating the database if it doesn't exist
            try (var stmt = conn.createStatement()) {
                stmt.executeUpdate(createDatabaseQuery);
                System.out.println("Database created successfully.");
            }
            con = DriverManager.getConnection(url + dbName, username, password);

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {

            String url = "jdbc:mysql://localhost:3306/";
            Class.forName("com.mysql.cj.jdbc.Driver");
            String dbUrl = url + dbName;
            return DriverManager.getConnection(dbUrl, username, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Failed to load database driver", e);
        }
    }

    public void main(String[] args) throws SQLException, IOException, InterruptedException {
        try {
            startWAMPP();

            openLocalServerConnection();

            initializeDatabase();

        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }
}
