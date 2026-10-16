package com.examportal.ui;

import com.examportal.dao.QuestionDAO;
import com.examportal.model.Exam;
import com.examportal.model.Question;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for managing (view, add, edit, delete) questions of a specific exam.
 */
public class QuestionManagerDialog extends JDialog {

    private final QuestionDAO questionDAO = new QuestionDAO();

    private final Exam exam;
    private JTable questionsTable;
    private DefaultTableModel tableModel;
    private List<Question> currentQuestions = new ArrayList<>();

    private JLabel lblTotalCount;
    private JLabel lblTotalMarks;

    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnClose;

    /**
     * Constructs the QuestionManagerDialog.
     *
     * @param parent parent Frame
     * @param exam target exam entity
     */
    public QuestionManagerDialog(Frame parent, Exam exam) {
        super(parent, "Manage Questions - " + (exam != null ? exam.getTitle() : "Exam"), true);
        this.exam = exam;
        initUI();
        loadQuestions();
    }

    private void initUI() {
        setSize(780, 560);
        setMinimumSize(new Dimension(650, 450));
        setLocationRelativeTo(getParent());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(LoginFrame.COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top Summary Header Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(LoginFrame.COLOR_CARD);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
                new EmptyBorder(12, 15, 12, 15)
        ));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 3, 3));
        titlePanel.setBackground(LoginFrame.COLOR_CARD);

        JLabel lblExamTitle = new JLabel("Exam: " + (exam != null ? exam.getTitle() : "Unknown"));
        lblExamTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblExamTitle.setForeground(LoginFrame.COLOR_PRIMARY_DARK);

        JLabel lblExamDesc = new JLabel((exam != null && exam.getDescription() != null && !exam.getDescription().isEmpty())
                ? exam.getDescription() : "No description provided");
        lblExamDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblExamDesc.setForeground(LoginFrame.COLOR_TEXT_MUTED);

        titlePanel.add(lblExamTitle);
        titlePanel.add(lblExamDesc);
        topPanel.add(titlePanel, BorderLayout.WEST);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        statsPanel.setBackground(LoginFrame.COLOR_CARD);

        lblTotalCount = new JLabel("Questions: 0");
        lblTotalCount.setFont(new Font("Segoe UI", Font.BOLD, 13));

        lblTotalMarks = new JLabel("Current Total Marks: 0 / " + (exam != null ? exam.getTotalMarks() : 0));
        lblTotalMarks.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotalMarks.setForeground(new Color(46, 125, 50));

        statsPanel.add(lblTotalCount);
        statsPanel.add(lblTotalMarks);
        topPanel.add(statsPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center Table
        String[] columnNames = {"ID", "Question Text", "Type", "Marks", "Correct Answer"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only
            }
        };

        questionsTable = new JTable(tableModel);
        questionsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        questionsTable.setRowHeight(26);
        questionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        questionsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        questionsTable.getTableHeader().setBackground(new Color(238, 238, 238));

        // Column widths
        questionsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        questionsTable.getColumnModel().getColumn(1).setPreferredWidth(350);
        questionsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        questionsTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        questionsTable.getColumnModel().getColumn(4).setPreferredWidth(120);

        // Center align ID, Type, Marks
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        questionsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        questionsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        questionsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        questionsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(questionsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions Toolbar
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(LoginFrame.COLOR_BG);

        JPanel crudPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        crudPanel.setBackground(LoginFrame.COLOR_BG);

        btnAdd = new JButton("Add Question");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAdd.setBackground(LoginFrame.COLOR_PRIMARY);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnEdit = new JButton("Edit Question");
        btnEdit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnEdit.setFocusPainted(false);
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnDelete = new JButton("Delete Question");
        btnDelete.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnDelete.setForeground(new Color(198, 40, 40));
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));

        crudPanel.add(btnAdd);
        crudPanel.add(btnEdit);
        crudPanel.add(btnDelete);

        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        closePanel.setBackground(LoginFrame.COLOR_BG);
        btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnClose.setPreferredSize(new Dimension(80, 30));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closePanel.add(btnClose);

        bottomPanel.add(crudPanel, BorderLayout.WEST);
        bottomPanel.add(closePanel, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Action Listeners
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddQuestionDialog();
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openEditQuestionDialog();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedQuestion();
            }
        });

        btnClose.addActionListener(e -> dispose());

        setContentPane(mainPanel);
    }

    /**
     * Loads/refreshes the list of questions from database.
     */
    public void loadQuestions() {
        if (exam == null) return;

        tableModel.setRowCount(0);
        currentQuestions = questionDAO.getQuestionsByExamId(exam.getExamId());

        int totalMarksSum = 0;
        for (Question q : currentQuestions) {
            String truncatedText = q.getQuestionText();
            if (truncatedText != null && truncatedText.length() > 65) {
                truncatedText = truncatedText.substring(0, 62) + "...";
            }
            tableModel.addRow(new Object[]{
                    q.getQuestionId(),
                    truncatedText,
                    q.getQuestionType(),
                    q.getMarks(),
                    q.getCorrectAnswer()
            });
            totalMarksSum += q.getMarks();
        }

        lblTotalCount.setText("Questions: " + currentQuestions.size());
        lblTotalMarks.setText("Sum of Question Marks: " + totalMarksSum + " / Exam Total: " + exam.getTotalMarks());
    }

    private void openAddQuestionDialog() {
        if (exam == null) return;
        AddEditQuestionDialog dialog = new AddEditQuestionDialog(this, exam.getExamId(), null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadQuestions();
        }
    }

    private void openEditQuestionDialog() {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a question from the table to edit.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Question selectedQuestion = currentQuestions.get(selectedRow);
        AddEditQuestionDialog dialog = new AddEditQuestionDialog(this, exam.getExamId(), selectedQuestion);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadQuestions();
        }
    }

    private void deleteSelectedQuestion() {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a question from the table to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Question selectedQuestion = currentQuestions.get(selectedRow);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this question (ID: " + selectedQuestion.getQuestionId() + ")?\n" +
                "\"" + (selectedQuestion.getQuestionText().length() > 50
                        ? selectedQuestion.getQuestionText().substring(0, 47) + "..."
                        : selectedQuestion.getQuestionText()) + "\"",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = questionDAO.deleteQuestion(selectedQuestion.getQuestionId());
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Question deleted successfully.",
                        "Deleted",
                        JOptionPane.INFORMATION_MESSAGE);
                loadQuestions();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete question.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
