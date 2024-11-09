import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Hospital1 {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hospital_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "admin"; // Replace with your MySQL password

    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    public static void main(String[] args) {
        new Hospital1().displayLogin();
    }

    // Display Login Page
    private void displayLogin() {
        frame = new JFrame("Hospital Management System - Login");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        frame.add(panel, BorderLayout.CENTER);

        JLabel logoLabel = new JLabel(new ImageIcon("assets/logo.png"), JLabel.CENTER);
        frame.add(logoLabel, BorderLayout.NORTH);

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("Role:"));
        roleBox = new JComboBox<>(new String[]{"Admin", "Doctor", "Patient"});
        panel.add(roleBox);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login());
        panel.add(loginButton);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Login Function
    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleBox.getSelectedItem();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role.toLowerCase());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                frame.dispose();
                if (role.equals("Admin")) {
                    displayAdminPage(conn);
                } else if (role.equals("Doctor")) {
                    displayDoctorPage(conn, rs.getInt("id"));
                } else {
                    displayPatientPage(conn, rs.getInt("id"));
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials or role", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Admin Page
    private void displayAdminPage(Connection conn) {
        JFrame adminFrame = new JFrame("Admin Panel");
        adminFrame.setSize(500, 400);
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));
        adminFrame.add(panel, BorderLayout.CENTER);

        JButton addDoctorButton = new JButton("Add Doctor");
        addDoctorButton.addActionListener(e -> addUser(conn, "doctor"));
        panel.add(addDoctorButton);

        JButton deleteDoctorButton = new JButton("Delete Doctor");
        deleteDoctorButton.addActionListener(e -> deleteUser(conn, "doctor"));
        panel.add(deleteDoctorButton);

        JButton addPatientButton = new JButton("Add Patient");
        addPatientButton.addActionListener(e -> addUser(conn, "patient"));
        panel.add(addPatientButton);

        JButton deletePatientButton = new JButton("Delete Patient");
        deletePatientButton.addActionListener(e -> deleteUser(conn, "patient"));
        panel.add(deletePatientButton);

        adminFrame.setLocationRelativeTo(null);
        adminFrame.setVisible(true);
    }

    // Function to Add User (Doctor or Patient)
    private void addUser(Connection conn, String role) {
        String name = JOptionPane.showInputDialog("Enter " + role + " Name:");
        String username = JOptionPane.showInputDialog("Enter " + role + " Username:");
        String password = JOptionPane.showInputDialog("Enter " + role + " Password:");

        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (name, username, password, role) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.setString(4, role);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, role + " added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Function to Delete User (Doctor or Patient)
    private void deleteUser(Connection conn, String role) {
        String username = JOptionPane.showInputDialog("Enter " + role + " Username to delete:");

        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE username = ? AND role = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, role);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame, role + " deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, role + " not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Doctor Page
    private void displayDoctorPage(Connection conn, int doctorId) {
        JFrame doctorFrame = new JFrame("Doctor Panel");
        doctorFrame.setSize(500, 400);
        doctorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton viewAppointmentsButton = new JButton("View Appointments");
        viewAppointmentsButton.addActionListener(e -> viewAppointments(conn, doctorId));
        doctorFrame.add(viewAppointmentsButton, BorderLayout.CENTER);

        doctorFrame.setLocationRelativeTo(null);
        doctorFrame.setVisible(true);
    }

    // Function to View and Update Appointments (Doctor)
    private void viewAppointments(Connection conn, int doctorId) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM appointments WHERE doctor_id = ?")) {
            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int appointmentId = rs.getInt("id");
                String patientId = rs.getString("patient_id");
                String report = JOptionPane.showInputDialog("Enter Report for Patient ID " + patientId + ":");
                
                PreparedStatement updateStmt = conn.prepareStatement("UPDATE appointments SET report = ? WHERE id = ?");
                updateStmt.setString(1, report);
                updateStmt.setInt(2, appointmentId);
                updateStmt.executeUpdate();
            }
            JOptionPane.showMessageDialog(frame, "Reports updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Patient Page
    private void displayPatientPage(Connection conn, int patientId) {
        JFrame patientFrame = new JFrame("Patient Panel");
        patientFrame.setSize(500, 400);
        patientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton bookAppointmentButton = new JButton("Book Appointment");
        bookAppointmentButton.addActionListener(e -> bookAppointment(conn, patientId));
        patientFrame.add(bookAppointmentButton, BorderLayout.CENTER);

        JButton viewReportButton = new JButton("View Report");
        viewReportButton.addActionListener(e -> viewReport(conn, patientId));
        patientFrame.add(viewReportButton, BorderLayout.SOUTH);

        patientFrame.setLocationRelativeTo(null);
        patientFrame.setVisible(true);
    }

    // Function to Book Appointment
    private void bookAppointment(Connection conn, int patientId) {
        String doctorUsername = JOptionPane.showInputDialog("Enter Doctor's Username for Appointment:");

        try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM users WHERE username = ? AND role = 'doctor'")) {
            stmt.setString(1, doctorUsername);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int doctorId = rs.getInt("id");
                PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO appointments (patient_id, doctor_id) VALUES (?, ?)");
                insertStmt.setInt(1, patientId);
                insertStmt.setInt(2, doctorId);
                insertStmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Appointment booked successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "Doctor not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Function to View Report (Patient)
    private void viewReport(Connection conn, int patientId) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT report FROM appointments WHERE patient_id = ?")) {
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String report = rs.getString("report");
                JOptionPane.showMessageDialog(frame, "Report: " + report);
            } else {
                JOptionPane.showMessageDialog(frame, "No report found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

