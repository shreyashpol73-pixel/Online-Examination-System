package com.examportal.ui;

import com.examportal.dao.UserDAO;
import com.examportal.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

/**
 * Modal registration dialog for new students.
 * Performs rigorous client-side and database-level validations.
 */
public class RegisterDialog extends JDialog {

    private final UserDAO userDAO = new UserDAO();

    private JTextField txtFullName;
    private JTextField txtEmail;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnRegister;
    private JButton btnCancel;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    /**
     * Constructs the registration dialog.
     *
     * @param parent the parent Frame
     */
    public RegisterDialog(Frame parent) {
        super(parent, "Student Registration", true);
        initUI();
    }

    private void initUI() {
        setSize(480, 480);
        setResizable(false);
        setLocationRelativeTo(getParent());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LoginFrame.COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(LoginFrame.COLOR_BG);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("Create Student Account");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(LoginFrame.COLOR_PRIMARY_DARK);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Fill in the details below to register");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(LoginFrame.COLOR_TEXT_MUTED);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(lblSubtitle);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Form Panel
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(LoginFrame.COLOR_CARD);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224), 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Full Name
        JLabel lblFullName = new JLabel("Full Name:");
        lblFullName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.35;
        formCard.add(lblFullName, gbc);

        txtFullName = new JTextField(15);
        txtFullName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtFullName.setPreferredSize(new Dimension(180, 28));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.65;
        formCard.add(txtFullName, gbc);

        // Email
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formCard.add(lblEmail, gbc);

        txtEmail = new JTextField(15);
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtEmail.setPreferredSize(new Dimension(180, 28));
        gbc.gridx = 1;
        gbc.gridy = 1;
        formCard.add(txtEmail, gbc);

        // Username
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formCard.add(lblUsername, gbc);

        txtUsername = new JTextField(15);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtUsername.setPreferredSize(new Dimension(180, 28));
        gbc.gridx = 1;
        gbc.gridy = 2;
        formCard.add(txtUsername, gbc);

        // Password
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 3;
        formCard.add(lblPassword, gbc);

        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtPassword.setPreferredSize(new Dimension(180, 28));
        gbc.gridx = 1;
        gbc.gridy = 3;
        formCard.add(txtPassword, gbc);

        // Confirm Password
        JLabel lblConfirmPassword = new JLabel("Confirm Password:");
        lblConfirmPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 4;
        formCard.add(lblConfirmPassword, gbc);

        txtConfirmPassword = new JPasswordField(15);
        txtConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtConfirmPassword.setPreferredSize(new Dimension(180, 28));
        gbc.gridx = 1;
        gbc.gridy = 4;
        formCard.add(txtConfirmPassword, gbc);

        mainPanel.add(formCard, BorderLayout.CENTER);

        // Bottom Actions Panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionsPanel.setBackground(LoginFrame.COLOR_BG);

        btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancel.setPreferredSize(new Dimension(90, 32));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegister.setBackground(LoginFrame.COLOR_PRIMARY);
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setPreferredSize(new Dimension(100, 32));
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCancel.addActionListener(e -> dispose());
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });

        actionsPanel.add(btnCancel);
        actionsPanel.add(btnRegister);

        mainPanel.add(actionsPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void performRegistration() {
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();

        // Validate non-empty
        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All fields are required. Please fill out all fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate email format
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address (e.g., student@example.com).",
                    "Invalid Email",
                    JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocusInWindow();
            return;
        }

        // Validate password minimum length
        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 4 characters long.",
                    "Weak Password",
                    JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocusInWindow();
            return;
        }

        // Validate password confirmation match
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match. Please re-enter your password.",
                    "Password Mismatch",
                    JOptionPane.ERROR_MESSAGE);
            txtConfirmPassword.setText("");
            txtConfirmPassword.requestFocusInWindow();
            return;
        }

        btnRegister.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            // Check username availability
            if (userDAO.isUsernameTaken(username)) {
                JOptionPane.showMessageDialog(this,
                        "Username '" + username + "' is already taken. Please choose another.",
                        "Username Taken",
                        JOptionPane.WARNING_MESSAGE);
                txtUsername.requestFocusInWindow();
                return;
            }

            // Check email availability
            if (userDAO.isEmailTaken(email)) {
                JOptionPane.showMessageDialog(this,
                        "Email '" + email + "' is already registered. Please log in or use another email.",
                        "Email Registered",
                        JOptionPane.WARNING_MESSAGE);
                txtEmail.requestFocusInWindow();
                return;
            }

            // Create and persist user
            User newUser = new User(username, password, fullName, email, "STUDENT");
            boolean success = userDAO.register(newUser);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Registration successful!\nYou can now log in using your credentials.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to register. Please check your database connection or try again later.",
                        "Registration Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "An unexpected error occurred: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            btnRegister.setEnabled(true);
            setCursor(Cursor.getDefaultCursor());
        }
    }
}
