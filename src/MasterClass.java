
import java.awt.Color;
import java.awt.Window;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MasterClass {

    // Check email validity
    public boolean checkEmail(String email) {
        return Pattern.matches("^[a-zA-Z0-9._%+-]+@(gmail\\.com|yahoo\\.com|hotmail\\.com)$", email);
    }

    // Get the length of a string
    public int getStringLength(String txt) {
        return txt.length();
    }

    // Convert a string to uppercase
    public String changeToUpper(String txt) {
        return txt.toUpperCase();
    }

    // Validate the password
    public boolean validatePassword(String password) {
        return password.length() >= 8 && password.length() <= 12 && password.matches("[A-Z]+");
    }

    //Hash the Password
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] hashedPasswordBytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPasswordBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Email available
    public boolean isUsernameAvailable(String username) {
        try {
            Connection con = Connect.getConnection();
            Statement st = con.createStatement();
            String query = "SELECT * FROM `User` WHERE username = '" + username + "'";
            ResultSet rs = st.executeQuery(query);
            return !rs.next(); // Return true if username is available (no rows returned)
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false; // Return false in case of error
        }
    }

    public static void showLoader() {
        JFrame loaderFrame = new JFrame();
        loaderFrame.setUndecorated(true);
        loaderFrame.setBackground(new Color(0, 0, 0, 0));
        loaderFrame.setVisible(true);
        JLabel loadingLabel = new JLabel();
        loadingLabel.setIcon(new ImageIcon(MasterClass.class.getResource("/upload.gif")));
        loaderFrame.add(loadingLabel);
        loaderFrame.getContentPane().add(loadingLabel);
        loaderFrame.pack();
        loaderFrame.setSize(500, 500);
        loaderFrame.setLocationRelativeTo(null);

        /* Set the application icon
        Image logo = new ImageIcon(Login.class.getResource("/Logo.png")).getImage();
        loaderFrame.setIconImage(logo);
         */
    }

}
