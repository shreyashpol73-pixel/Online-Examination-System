package com.examportal.ui;

import com.examportal.dao.ExamDAO;
import com.examportal.dao.QuestionDAO;
import com.examportal.dao.ResultDAO;
import com.examportal.model.Exam;
import com.examportal.model.Question;
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
 * Student Dashboard providing interfaces to view available exams, start an exam attempt,
 * and review personal exam performance and history.
 */
public class StudentDashboard extends JFrame {

    private final User student;
    private final ExamDAO examDAO = new ExamDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final ResultDAO resultDAO = new ResultDAO();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    // Tab 1: Available Exams
    private JTable examsTable;
    private DefaultTableModel examsTableModel;
    private List<Exam> currentActiveExams = new ArrayList<>();

    // Tab 2: My Results
    private JTable resultsTable;
    private DefaultTableModel resultsTableModel;
    private List<Result> myResults = new ArrayList<>();

    /**
     * Constructs the StudentDashboard.
     *
     * @param student authenticated user entity with STUDENT role
     */
    public StudentDashboard(User student) {
        this.student = student;
        initUI();
        loadAvailableExams();
        loadMyResults();
    }

    private void initUI() {
        setTitle("Student Dashboard - " + (student != null ? student.getFullName() + " (" + student.getUsername() + ")" : "Student"));
        setSize(880, 620);
        setMinimumSize(new Dimension(800, 520));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Menu Bar
        setJMenuBar(createMenuBar());

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(LoginFrame.COLOR_BG);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(LoginFrame.COLOR_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("🎓 Student Examination Portal");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblStudentInfo = new JLabel("Welcome, " + (student != null ? student.getFullName() : "Student"));
        lblStudentInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStudentInfo.setForeground(new Color(227, 242, 253));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblStudentInfo, BorderLayout.EAST);
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("📝 Available Exams", createAvailableExamsTab());
        tabbedPane.addTab("📈 My Results", createMyResultsTab());

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
    // TAB 1: AVAILABLE EXAMS
    // ==========================================

    private JPanel createAvailableExamsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(LoginFrame.COLOR_BG);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        String[] columns = {"ID", "Title", "Description", "Duration (mins)", "Total Marks", "Pass Marks", "Status"};
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

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        examsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        examsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        examsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        examsTable.getColumnModel().getColumn(2).setPreferredWidth(260);
        examsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        examsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        examsTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        examsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        examsTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        examsTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        examsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        examsTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(examsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbar.setBackground(LoginFrame.COLOR_BG);

        JButton btnStartExam = new JButton("Start Exam 🚀");
        btnStartExam.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnStartExam.setBackground(new Color(46, 125, 50)); // Green
        btnStartExam.setForeground(Color.WHITE);
        btnStartExam.setFocusPainted(false);
        btnStartExam.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnStartExam.setPreferredSize(new Dimension(140, 34));

        JButton btnRefresh = new JButton("Refresh Exams");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setPreferredSize(new Dimension(120, 34));

        btnStartExam.addActionListener(e -> startSelectedExam());
        btnRefresh.addActionListener(e -> loadAvailableExams());

        toolbar.add(btnStartExam);
        toolbar.add(btnRefresh);

        panel.add(toolbar, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Loads active exams and indicates whether student has already completed them.
     */
    public void loadAvailableExams() {
        examsTableModel.setRowCount(0);
        currentActiveExams = examDAO.getActiveExams();

        for (Exam ex : currentActiveExams) {
            boolean attempted = (student != null) && resultDAO.hasStudentAttemptedExam(student.getUserId(), ex.getExamId());
            examsTableModel.addRow(new Object[]{
                    ex.getExamId(),
                    ex.getTitle(),
                    ex.getDescription() != null ? ex.getDescription() : "",
                    ex.getDurationMinutes(),
                    ex.getTotalMarks(),
                    ex.getPassingMarks(),
                    attempted ? "Attempted" : "Available"
            });
        }
    }

    private void startSelectedExam() {
        int selectedRow = examsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select an exam from the list to start.",
                    "No Exam Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Exam selectedExam = currentActiveExams.get(selectedRow);

        // 1. Check if already attempted
        if (student != null && resultDAO.hasStudentAttemptedExam(student.getUserId(), selectedExam.getExamId())) {
            JOptionPane.showMessageDialog(this,
                    "You have already attempted the exam: \"" + selectedExam.getTitle() + "\".\nMultiple attempts are not permitted.",
                    "Already Attempted",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Check if questions exist
        List<Question> questions = questionDAO.getQuestionsByExamId(selectedExam.getExamId());
        if (questions == null || questions.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "This exam currently has no questions available. Please check back later or notify your instructor.",
                    "No Questions",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 3. Confirm start
        String message = String.format(
                "Are you ready to begin the exam?\n\n" +
                "Title: %s\n" +
                "Duration: %d minutes\n" +
                "Total Marks: %d\n" +
                "Passing Marks: %d\n" +
                "Number of Questions: %d\n\n" +
                "⚠️ Note: Once started, the timer will countdown continuously.\n" +
                "Do not close or minimize the window.",
                selectedExam.getTitle(),
                selectedExam.getDurationMinutes(),
                selectedExam.getTotalMarks(),
                selectedExam.getPassingMarks(),
                questions.size()
        );

        int confirm = JOptionPane.showConfirmDialog(this,
                message,
                "Start Exam Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Open ExamPanel
            SwingUtilities.invokeLater(() -> {
                ExamPanel examPanel = new ExamPanel(student, selectedExam, this);
                examPanel.setVisible(true);
            });
        }
    }

    // ==========================================
    // TAB 2: MY RESULTS
    // ==========================================

    private JPanel createMyResultsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(LoginFrame.COLOR_BG);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        String[] columns = {"Result ID", "Exam Title", "Score", "Total Marks", "Percentage", "Status", "Time Taken", "Date"};
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
        resultsTable.getColumnModel().getColumn(1).setPreferredWidth(240);
        resultsTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        resultsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        resultsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        resultsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        resultsTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        resultsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        resultsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        resultsTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        resultsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        resultsTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        resultsTable.getColumnModel().getColumn(7).setPreferredWidth(130);
        resultsTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bottomPanel.setBackground(LoginFrame.COLOR_BG);

        JButton btnRefreshResults = new JButton("Refresh Results");
        btnRefreshResults.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefreshResults.setFocusPainted(false);
        btnRefreshResults.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefreshResults.addActionListener(e -> loadMyResults());

        bottomPanel.add(btnRefreshResults);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Loads and populates all completed exam results for this student.
     */
    public void loadMyResults() {
        if (student == null) return;

        resultsTableModel.setRowCount(0);
        myResults = resultDAO.getResultsByUserId(student.getUserId());

        for (Result r : myResults) {
            int mins = r.getTimeTakenSeconds() / 60;
            int secs = r.getTimeTakenSeconds() % 60;
            String timeTakenStr = mins + "m " + secs + "s";
            String dateStr = r.getAttemptedAt() != null ? dateFormat.format(r.getAttemptedAt()) : "N/A";

            resultsTableModel.addRow(new Object[]{
                    r.getResultId(),
                    r.getExamTitle() != null ? r.getExamTitle() : "Exam #" + r.getExamId(),
                    r.getScore(),
                    r.getTotalMarks(),
                    String.format("%.2f%%", r.getPercentage()),
                    r.getStatus(),
                    timeTakenStr,
                    dateStr
            });
        }
    }
}
