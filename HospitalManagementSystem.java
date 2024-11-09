import java.sql.*;
import javax.swing.*;

public class HospitalManagementSystem {
    private static Connection connect;

    // Establish a connection with the MySQL database
    public static void connectDatabase() {
        try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db?useSSL=false&serverTimezone=UTC", "root", "admin");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection failed!");
        }
    }

    // Login Form
    public static void showLoginForm() {
        JFrame loginFrame = new JFrame("Hospital Management System - Login");
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = authenticateUser(username, password);
            if (role != null) {
                loginFrame.dispose();
                switch (role) {
                    case "Admin":
                        showAdminPanel();
                        break;
                    case "Doctor":
                        showDoctorPanel();
                        break;
                    case "Patient":
                        showPatientPanel();
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid username or password!");
            }
        });

        loginFrame.setLayout(null);
        loginFrame.add(new JLabel("Username:")).setBounds(30, 30, 100, 30);
        loginFrame.add(usernameField).setBounds(120, 30, 150, 30);
        loginFrame.add(new JLabel("Password:")).setBounds(30, 70, 100, 30);
        loginFrame.add(passwordField).setBounds(120, 70, 150, 30);
        loginFrame.add(loginButton).setBounds(100, 110, 100, 30);

        loginFrame.setSize(300, 200);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setVisible(true);
    }

    // Authenticate user
    public static String authenticateUser(String username, String password) {
        try {
            String query = "SELECT Role FROM User WHERE Username=? AND Password=?";
            PreparedStatement stmt = connect.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Admin Panel
    public static void showAdminPanel() {
        JFrame adminFrame = new JFrame("Admin Panel");
        adminFrame.setSize(500, 400);
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        adminFrame.setVisible(true);

        JButton addPatient = new JButton("Add Patient");
        JButton addDoctor = new JButton("Add Doctor");
        adminFrame.setLayout(null);
        adminFrame.add(addPatient).setBounds(150, 100, 200, 30);
        adminFrame.add(addDoctor).setBounds(150, 150, 200, 30);

        addPatient.addActionListener(e -> JOptionPane.showMessageDialog(adminFrame, "Patient added!"));
        addDoctor.addActionListener(e -> JOptionPane.showMessageDialog(adminFrame, "Doctor added!"));
    }

    // Doctor Panel
    public static void showDoctorPanel() {
        JFrame doctorFrame = new JFrame("Doctor Panel");
        doctorFrame.setSize(500, 400);
        doctorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        doctorFrame.setVisible(true);

        JButton viewAppointments = new JButton("View Appointments");
        JButton addPrescription = new JButton("Add Prescription");
        doctorFrame.setLayout(null);
        doctorFrame.add(viewAppointments).setBounds(150, 100, 200, 30);
        doctorFrame.add(addPrescription).setBounds(150, 150, 200, 30);

        viewAppointments.addActionListener(e -> JOptionPane.showMessageDialog(doctorFrame, "Viewing appointments"));
        addPrescription.addActionListener(e -> JOptionPane.showMessageDialog(doctorFrame, "Prescription added!"));
    }

    // Patient Panel
    public static void showPatientPanel() {
        JFrame patientFrame = new JFrame("Patient Panel");
        patientFrame.setSize(500, 400);
        patientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        patientFrame.setVisible(true);

        JButton bookAppointment = new JButton("Book Appointment");
        JButton viewPrescription = new JButton("View Prescription");
        patientFrame.setLayout(null);
        patientFrame.add(bookAppointment).setBounds(150, 100, 200, 30);
        patientFrame.add(viewPrescription).setBounds(150, 150, 200, 30);

        bookAppointment.addActionListener(e -> JOptionPane.showMessageDialog(patientFrame, "Appointment booked!"));
        viewPrescription.addActionListener(e -> JOptionPane.showMessageDialog(patientFrame, "Viewing prescription"));
    }

    public static void main(String[] args) {
        connectDatabase();
        showLoginForm();
    }
}
