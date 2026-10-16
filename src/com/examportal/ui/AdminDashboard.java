package com.examportal.ui;

import com.examportal.dao.ExamDAO;
import com.examportal.dao.QuestionDAO;
import com.examportal.dao.ResultDAO;
import com.examportal.dao.UserDAO;
import com.examportal.model.Exam;
import com.examportal.model.Result;
import com.examportal.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Administrator Dashboard for managing exams, questions, results, and student accounts.
 */
public class AdminDashboard extends JFrame {

    private final User admin;
    private final ExamDAO examDAO = new ExamDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final ResultDAO resultDAO = new ResultDAO();
    private final UserDAO userDAO = new UserDAO();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    // Tab 1 Components: Manage Exams
    private JTable examsTable;
    private DefaultTableModel examsTableModel;
    private List<Exam> currentExams = new ArrayList<>();

    // Tab 2 Components: View Results
    private JTable resultsTable;
    private DefaultTableModel resultsTableModel;
    private JComboBox<ExamFilterItem> comboExamFilter;
    private List<Result> currentResults = new ArrayList<>();

    // Tab 3 Components: Manage Students
    private JTable studentsTable;
    private DefaultTableModel studentsTableModel;
    private List<User> currentStudents = new ArrayList<>();

    /**
     * Constructs the AdminDashboard for the authenticated administrator.
     *
     * @param admin user entity with ADMIN role
     */
    public AdminDashboard(User admin) {
        this.admin = admin;
        initUI();
        refreshAllTabs();
    }

    private void initUI() {
        setTitle("Admin Dashboard - " + (admin != null ? admin.getFullName() + " (" + admin.getUsername() + ")" : "Administrator"));
        setSize(950, 680);
        setMinimumSize(new Dimension(850, 580));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Menu Bar
        setJMenuBar(createMenuBar());

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(LoginFrame.COLOR_BG);

        // Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(LoginFrame.COLOR_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("🛠️ Administration Control Center");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblAdminInfo = new JLabel("Logged in as: " + (admin != null ? admin.getFullName() : "Admin"));
        lblAdminInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblAdminInfo.setForeground(new Color(227, 242, 253));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblAdminInfo, BorderLayout.EAST);
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("📋 Manage Exams", createManageExamsTab());
        tabbedPane.addTab("📊 View Results", createViewResultsTab());
        tabbedPane.addTab("👥 Manage Students", createManageStudentsTab());

        rootPanel.add(tabbedPane, BorderLayout.CENTER);

        setContentPane(rootPanel);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JMenuItem miLogout = new JMenuItem("Logout");
        miLogout.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        miLogout.addActionListener(e -> performLogout());

        JMenuItem miExit = new JMenuItem("Exit");
        miExit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        miExit.addActionListener(e -> System.exit(0));

        fileMenu.add(miLogout);
        fileMenu.addSeparator();
        fileMenu.add(miExit);

        menuBar.add(fileMenu);
        return menuBar;
    }

    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }

    // ==========================================
    // TAB 1: MANAGE EXAMS
    // ==========================================

    private JPanel createManageExamsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(LoginFrame.COLOR_BG);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Table
        String[] columns = {"ID", "Title", "Duration (mins)", "Total Marks", "Pass Marks", "Status", "Questions Count"};
        examsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        examsTable = new JTable(examsTableModel);
        examsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        examsTable.setRowHeight(26);
        examsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        examsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        examsTable.getTableHeader().setBackground(new Color(238, 238, 238));

        // Alignment
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        examsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        examsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        examsTable.getColumnModel().getColumn(1).setPreferredWidth(260);
        examsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        examsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        examsTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        examsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        examsTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        examsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        examsTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        examsTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        examsTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        examsTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(examsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Action Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        toolbar.setBackground(LoginFrame.COLOR_BG);

        JButton btnAddExam = new JButton("Add Exam");
        btnAddExam.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddExam.setBackground(LoginFrame.COLOR_PRIMARY);
        btnAddExam.setForeground(Color.WHITE);
        btnAddExam.setFocusPainted(false);
        btnAddExam.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnEditExam = new JButton("Edit Exam");
        btnEditExam.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnEditExam.setFocusPainted(false);
        btnEditExam.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnDeleteExam = new JButton("Delete Exam");
        btnDeleteExam.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnDeleteExam.setForeground(new Color(198, 40, 40));
        btnDeleteExam.setFocusPainted(false);
        btnDeleteExam.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnToggleStatus = new JButton("Toggle Active/Inactive");
        btnToggleStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnToggleStatus.setFocusPainted(false);
        btnToggleStatus.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnManageQuestions = new JButton("Manage Questions");
        btnManageQuestions.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnManageQuestions.setBackground(new Color(67, 160, 71));
        btnManageQuestions.setForeground(Color.WHITE);
        btnManageQuestions.setFocusPainted(false);
        btnManageQuestions.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnRefreshExams = new JButton("Refresh");
        btnRefreshExams.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefreshExams.setFocusPainted(false);
        btnRefreshExams.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnAddExam.addActionListener(e -> openAddExamDialog());
        btnEditExam.addActionListener(e -> openEditExamDialog());
        btnDeleteExam.addActionListener(e -> deleteSelectedExam());
        btnToggleStatus.addActionListener(e -> toggleSelectedExamStatus());
        btnManageQuestions.addActionListener(e -> openQuestionManager());
        btnRefreshExams.addActionListener(e -> loadExams());

        toolbar.add(btnAddExam);
        toolbar.add(btnEditExam);
        toolbar.add(btnDeleteExam);
        toolbar.add(btnToggleStatus);
        toolbar.add(btnManageQuestions);
        toolbar.add(btnRefreshExams);

        panel.add(toolbar, BorderLayout.SOUTH);
        return panel;
    }

    public void loadExams() {
        examsTableModel.setRowCount(0);
        currentExams = examDAO.getAllExams();

        for (Exam ex : currentExams) {
            int qCount = questionDAO.getQuestionCount(ex.getExamId());
            examsTableModel.addRow(new Object[]{
                    ex.getExamId(),
                    ex.getTitle(),
                    ex.getDurationMinutes(),
                    ex.getTotalMarks(),
                    ex.getPassingMarks(),
                    ex.isActive() ? "Active" : "Inactive",
                    qCount
            });
        }
    }

    private void openAddExamDialog() {
        AddEditExamDialog dialog = new AddEditExamDialog(this, null, admin != null ? admin.getUserId() : 1);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadExams();
            updateExamFilterCombo();
        }
    }

    private void openEditExamDialog() {
        int selectedRow = examsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select an exam from the table to edit.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Exam selectedExam = currentExams.get(selectedRow);
        AddEditExamDialog dialog = new AddEditExamDialog(this, selectedExam, admin != null ? admin.getUserId() : 1);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadExams();
            updateExamFilterCombo();
        }
    }

    private void deleteSelectedExam() {
        int selectedRow = examsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select an exam to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Exam selectedExam = currentExams.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete exam: \"" + selectedExam.getTitle() + "\" (ID: " + selectedExam.getExamId() + ")?\n" +
                "All associated questions and student results will also be deleted!",
                "Confirm Delete Exam",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = examDAO.deleteExam(selectedExam.getExamId());
            if (success) {
                JOptionPane.showMessageDialog(this, "Exam deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadExams();
                updateExamFilterCombo();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete exam.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleSelectedExamStatus() {
        int selectedRow = examsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select an exam to toggle its status.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Exam selectedExam = currentExams.get(selectedRow);
        boolean newStatus = !selectedExam.isActive();
        boolean success = examDAO.toggleExamStatus(selectedExam.getExamId(), newStatus);
        if (success) {
            selectedExam.setActive(newStatus);
            loadExams();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update exam status.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openQuestionManager() {
        int selectedRow = examsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select an exam to manage its questions.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Exam selectedExam = currentExams.get(selectedRow);
        QuestionManagerDialog dialog = new QuestionManagerDialog(this, selectedExam);
        dialog.setVisible(true);
        // Refresh question count in exams table upon dialog close
        loadExams();
    }

    // ==========================================
    // TAB 2: VIEW RESULTS
    // ==========================================

    private JPanel createViewResultsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(LoginFrame.COLOR_BG);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Top Filter Bar
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterPanel.setBackground(LoginFrame.COLOR_BG);

        JLabel lblFilter = new JLabel("Filter by Exam:");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));

        comboExamFilter = new JComboBox<>();
        comboExamFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboExamFilter.setPreferredSize(new Dimension(280, 28));

        JButton btnRefreshResults = new JButton("Refresh");
        btnRefreshResults.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefreshResults.setFocusPainted(false);
        btnRefreshResults.setCursor(new Cursor(Cursor.HAND_CURSOR));

        comboExamFilter.addActionListener(e -> filterResults());
        btnRefreshResults.addActionListener(e -> {
            updateExamFilterCombo();
            filterResults();
        });

        filterPanel.add(lblFilter);
        filterPanel.add(comboExamFilter);
        filterPanel.add(btnRefreshResults);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Results Table
        String[] columns = {"Result ID", "Student Name", "Exam Title", "Score", "Total Marks", "Percentage", "Status", "Date"};
        resultsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        resultsTable = new JTable(resultsTableModel);
        resultsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultsTable.setRowHeight(26);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        resultsTable.getTableHeader().setBackground(new Color(238, 238, 238));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        resultsTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        resultsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        resultsTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        resultsTable.getColumnModel().getColumn(2).setPreferredWidth(220);
        resultsTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        resultsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        resultsTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        resultsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        resultsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        resultsTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        resultsTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        resultsTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        resultsTable.getColumnModel().getColumn(7).setPreferredWidth(130);
        resultsTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private static class ExamFilterItem {
        final int examId;
        final String title;

        ExamFilterItem(int examId, String title) {
            this.examId = examId;
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    private void updateExamFilterCombo() {
        comboExamFilter.removeAllItems();
        comboExamFilter.addItem(new ExamFilterItem(-1, "All Exams"));

        List<Exam> exams = examDAO.getAllExams();
        for (Exam ex : exams) {
            comboExamFilter.addItem(new ExamFilterItem(ex.getExamId(), ex.getTitle()));
        }
    }

    private void filterResults() {
        ExamFilterItem selectedItem = (ExamFilterItem) comboExamFilter.getSelectedItem();
        if (selectedItem == null) return;

        resultsTableModel.setRowCount(0);
        if (selectedItem.examId == -1) {
            currentResults = resultDAO.getAllResults();
        } else {
            currentResults = resultDAO.getResultsByExamId(selectedItem.examId);
        }

        for (Result r : currentResults) {
            String dateStr = r.getAttemptedAt() != null ? dateFormat.format(r.getAttemptedAt()) : "N/A";
            resultsTableModel.addRow(new Object[]{
                    r.getResultId(),
                    r.getStudentName() != null ? r.getStudentName() : "Student #" + r.getUserId(),
                    r.getExamTitle() != null ? r.getExamTitle() : "Exam #" + r.getExamId(),
                    r.getScore(),
                    r.getTotalMarks(),
                    String.format("%.2f%%", r.getPercentage()),
                    r.getStatus(),
                    dateStr
            });
        }
    }

    // ==========================================
    // TAB 3: MANAGE STUDENTS
    // ==========================================

    private JPanel createManageStudentsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(LoginFrame.COLOR_BG);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        String[] columns = {"ID", "Name", "Username", "Email", "Registered Date"};
        studentsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        studentsTable = new JTable(studentsTableModel);
        studentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studentsTable.setRowHeight(26);
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        studentsTable.getTableHeader().setBackground(new Color(238, 238, 238));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        studentsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        studentsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        studentsTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        studentsTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        studentsTable.getColumnModel().getColumn(3).setPreferredWidth(220);
        studentsTable.getColumnModel().getColumn(4).setPreferredWidth(140);
        studentsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(studentsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bottomPanel.setBackground(LoginFrame.COLOR_BG);

        JButton btnRefreshStudents = new JButton("Refresh Students");
        btnRefreshStudents.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefreshStudents.setFocusPainted(false);
        btnRefreshStudents.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefreshStudents.addActionListener(e -> loadStudents());

        bottomPanel.add(btnRefreshStudents);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    public void loadStudents() {
        studentsTableModel.setRowCount(0);
        currentStudents = userDAO.getAllStudents();

        for (User u : currentStudents) {
            String registeredDate = u.getCreatedAt() != null ? dateFormat.format(u.getCreatedAt()) : "N/A";
            studentsTableModel.addRow(new Object[]{
                    u.getUserId(),
                    u.getFullName(),
                    u.getUsername(),
                    u.getEmail(),
                    registeredDate
            });
        }
    }

    private void refreshAllTabs() {
        loadExams();
        updateExamFilterCombo();
        filterResults();
        loadStudents();
    }

    // ==========================================
    // INNER CLASS: ADD/EDIT EXAM MODAL DIALOG
    // ==========================================

    private static class AddEditExamDialog extends JDialog {
        private final ExamDAO examDAO = new ExamDAO();
        private final Exam existingExam;
        private final int adminId;
        private boolean saved = false;

        private JTextField txtTitle;
        private JTextArea txtDescription;
        private JSpinner spinnerDuration;
        private JSpinner spinnerTotalMarks;
        private JSpinner spinnerPassingMarks;
        private JCheckBox chkActive;

        private JButton btnSave;
        private JButton btnCancel;

        AddEditExamDialog(Frame parent, Exam existingExam, int adminId) {
            super(parent, existingExam == null ? "Add New Exam" : "Edit Exam Details", true);
            this.existingExam = existingExam;
            this.adminId = adminId;
            initUI();
            if (existingExam != null) {
                populate(existingExam);
            }
        }

        private void initUI() {
            setSize(500, 480);
            setResizable(false);
            setLocationRelativeTo(getParent());

            JPanel main = new JPanel(new BorderLayout());
            main.setBackground(LoginFrame.COLOR_BG);
            main.setBorder(new EmptyBorder(15, 20, 15, 20));

            JLabel lblTitleHeader = new JLabel(existingExam == null ? "Create New Exam" : "Update Exam Information");
            lblTitleHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblTitleHeader.setForeground(LoginFrame.COLOR_PRIMARY_DARK);
            lblTitleHeader.setBorder(new EmptyBorder(0, 0, 10, 0));
            main.add(lblTitleHeader, BorderLayout.NORTH);

            JPanel form = new JPanel(new GridBagLayout());
            form.setBackground(LoginFrame.COLOR_CARD);
            form.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(224, 224, 224), 1, true),
                    new EmptyBorder(12, 15, 12, 15)
            ));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Title
            JLabel lblTitle = new JLabel("Exam Title:");
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 0.3;
            form.add(lblTitle, gbc);

            txtTitle = new JTextField();
            txtTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 0.7;
            form.add(txtTitle, gbc);

            // Description
            JLabel lblDesc = new JLabel("Description:");
            lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 12));
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            form.add(lblDesc, gbc);

            txtDescription = new JTextArea(3, 20);
            txtDescription.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            txtDescription.setLineWrap(true);
            txtDescription.setWrapStyleWord(true);
            JScrollPane scrollDesc = new JScrollPane(txtDescription);
            scrollDesc.setPreferredSize(new Dimension(240, 65));
            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.fill = GridBagConstraints.BOTH;
            form.add(scrollDesc, gbc);

            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.CENTER;

            // Duration
            JLabel lblDuration = new JLabel("Duration (mins):");
            lblDuration.setFont(new Font("Segoe UI", Font.BOLD, 12));
            gbc.gridx = 0;
            gbc.gridy = 2;
            form.add(lblDuration, gbc);

            spinnerDuration = new JSpinner(new SpinnerNumberModel(30, 5, 300, 5));
            spinnerDuration.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            gbc.gridx = 1;
            gbc.gridy = 2;
            form.add(spinnerDuration, gbc);

            // Total Marks
            JLabel lblTotal = new JLabel("Total Marks:");
            lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 12));
            gbc.gridx = 0;
            gbc.gridy = 3;
            form.add(lblTotal, gbc);

            spinnerTotalMarks = new JSpinner(new SpinnerNumberModel(100, 5, 1000, 5));
            spinnerTotalMarks.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            gbc.gridx = 1;
            gbc.gridy = 3;
            form.add(spinnerTotalMarks, gbc);

            // Passing Marks
            JLabel lblPass = new JLabel("Passing Marks:");
            lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
            gbc.gridx = 0;
            gbc.gridy = 4;
            form.add(lblPass, gbc);

            spinnerPassingMarks = new JSpinner(new SpinnerNumberModel(40, 1, 1000, 5));
            spinnerPassingMarks.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            gbc.gridx = 1;
            gbc.gridy = 4;
            form.add(spinnerPassingMarks, gbc);

            // Is Active Checkbox
            JLabel lblActive = new JLabel("Status:");
            lblActive.setFont(new Font("Segoe UI", Font.BOLD, 12));
            gbc.gridx = 0;
            gbc.gridy = 5;
            form.add(lblActive, gbc);

            chkActive = new JCheckBox("Active (Visible to Students)", true);
            chkActive.setBackground(LoginFrame.COLOR_CARD);
            chkActive.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            gbc.gridx = 1;
            gbc.gridy = 5;
            form.add(chkActive, gbc);

            main.add(form, BorderLayout.CENTER);

            // Buttons
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            actions.setBackground(LoginFrame.COLOR_BG);

            btnCancel = new JButton("Cancel");
            btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btnCancel.setPreferredSize(new Dimension(85, 32));
            btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnSave = new JButton(existingExam == null ? "Create Exam" : "Save Changes");
            btnSave.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnSave.setBackground(LoginFrame.COLOR_PRIMARY);
            btnSave.setForeground(Color.WHITE);
            btnSave.setFocusPainted(false);
            btnSave.setPreferredSize(new Dimension(120, 32));
            btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnCancel.addActionListener(e -> dispose());
            btnSave.addActionListener(e -> saveExam());

            actions.add(btnCancel);
            actions.add(btnSave);
            main.add(actions, BorderLayout.SOUTH);

            setContentPane(main);
        }

        private void populate(Exam ex) {
            txtTitle.setText(ex.getTitle());
            txtDescription.setText(ex.getDescription());
            spinnerDuration.setValue(ex.getDurationMinutes());
            spinnerTotalMarks.setValue(ex.getTotalMarks());
            spinnerPassingMarks.setValue(ex.getPassingMarks());
            chkActive.setSelected(ex.isActive());
        }

        private void saveExam() {
            String title = txtTitle.getText().trim();
            String desc = txtDescription.getText().trim();
            int duration = (Integer) spinnerDuration.getValue();
            int total = (Integer) spinnerTotalMarks.getValue();
            int pass = (Integer) spinnerPassingMarks.getValue();
            boolean active = chkActive.isSelected();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Exam Title cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                txtTitle.requestFocusInWindow();
                return;
            }

            if (pass > total) {
                JOptionPane.showMessageDialog(this, "Passing marks cannot exceed total marks.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                spinnerPassingMarks.requestFocusInWindow();
                return;
            }

            btnSave.setEnabled(false);
            try {
                if (existingExam == null) {
                    Exam newExam = new Exam(title, desc, duration, total, pass, active, adminId);
                    int generatedId = examDAO.addExam(newExam);
                    if (generatedId > 0) {
                        saved = true;
                        JOptionPane.showMessageDialog(this, "Exam created successfully with ID: " + generatedId, "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to create exam. Please check database connectivity.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    existingExam.setTitle(title);
                    existingExam.setDescription(desc);
                    existingExam.setDurationMinutes(duration);
                    existingExam.setTotalMarks(total);
                    existingExam.setPassingMarks(pass);
                    existingExam.setActive(active);

                    boolean success = examDAO.updateExam(existingExam);
                    if (success) {
                        saved = true;
                        JOptionPane.showMessageDialog(this, "Exam updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update exam.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                btnSave.setEnabled(true);
            }
        }

        boolean isSaved() {
            return saved;
        }
    }
}
