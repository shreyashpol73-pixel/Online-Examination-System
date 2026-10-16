package com.examportal.ui;

import com.examportal.dao.QuestionDAO;
import com.examportal.model.Question;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;

/**
 * Dialog for adding or editing an exam question.
 * Dynamically adjusts fields depending on question type (MCQ, TRUE_FALSE, FILL_BLANK).
 */
public class AddEditQuestionDialog extends JDialog {

    private final QuestionDAO questionDAO = new QuestionDAO();

    private final int examId;
    private final Question existingQuestion;
    private boolean saved = false;

    private JTextArea txtQuestionText;
    private JComboBox<String> comboQuestionType;
    private JTextField txtOptionA;
    private JTextField txtOptionB;
    private JTextField txtOptionC;
    private JTextField txtOptionD;

    private JComboBox<String> comboCorrectChoice;
    private JTextField txtCorrectBlank;
    private JPanel answerCardPanel;
    private CardLayout answerCardLayout;

    private JSpinner spinnerMarks;
    private JButton btnSave;
    private JButton btnCancel;

    private static final String CARD_COMBO = "CARD_COMBO";
    private static final String CARD_TEXT = "CARD_TEXT";

    /**
     * Constructs dialog with a Dialog parent.
     */
    public AddEditQuestionDialog(Dialog parent, int examId, Question existingQuestion) {
        super(parent, existingQuestion == null ? "Add Question" : "Edit Question", true);
        this.examId = examId;
        this.existingQuestion = existingQuestion;
        initUI();
        if (existingQuestion != null) {
            populateFields(existingQuestion);
        } else {
            updateUIForQuestionType(Question.TYPE_MCQ);
        }
    }

    /**
     * Constructs dialog with a Frame parent.
     */
    public AddEditQuestionDialog(Frame parent, int examId, Question existingQuestion) {
        super(parent, existingQuestion == null ? "Add Question" : "Edit Question", true);
        this.examId = examId;
        this.existingQuestion = existingQuestion;
        initUI();
        if (existingQuestion != null) {
            populateFields(existingQuestion);
        } else {
            updateUIForQuestionType(Question.TYPE_MCQ);
        }
    }

    private void initUI() {
        setSize(580, 600);
        setResizable(false);
        setLocationRelativeTo(getParent());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LoginFrame.COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Title Header
        JLabel lblHeader = new JLabel(existingQuestion == null ? "Add New Question" : "Edit Question Details");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(LoginFrame.COLOR_PRIMARY_DARK);
        lblHeader.setBorder(new EmptyBorder(0, 0, 12, 0));
        mainPanel.add(lblHeader, BorderLayout.NORTH);

        // Center Form
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(LoginFrame.COLOR_CARD);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224), 1, true),
                new EmptyBorder(12, 15, 12, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Question Type
        JLabel lblType = new JLabel("Question Type:");
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.25;
        formCard.add(lblType, gbc);

        comboQuestionType = new JComboBox<>(new String[]{
                Question.TYPE_MCQ,
                Question.TYPE_TRUE_FALSE,
                Question.TYPE_FILL_BLANK
        });
        comboQuestionType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboQuestionType.setPreferredSize(new Dimension(200, 28));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.75;
        formCard.add(comboQuestionType, gbc);

        // 2. Question Text
        JLabel lblText = new JLabel("Question Text:");
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formCard.add(lblText, gbc);

        txtQuestionText = new JTextArea(4, 25);
        txtQuestionText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtQuestionText.setLineWrap(true);
        txtQuestionText.setWrapStyleWord(true);
        JScrollPane scrollQuestionText = new JScrollPane(txtQuestionText);
        scrollQuestionText.setPreferredSize(new Dimension(300, 80));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        formCard.add(scrollQuestionText, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // 3. Option A
        JLabel lblOptA = new JLabel("Option A:");
        lblOptA.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formCard.add(lblOptA, gbc);

        txtOptionA = new JTextField();
        txtOptionA.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1;
        gbc.gridy = 2;
        formCard.add(txtOptionA, gbc);

        // 4. Option B
        JLabel lblOptB = new JLabel("Option B:");
        lblOptB.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 3;
        formCard.add(lblOptB, gbc);

        txtOptionB = new JTextField();
        txtOptionB.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1;
        gbc.gridy = 3;
        formCard.add(txtOptionB, gbc);

        // 5. Option C
        JLabel lblOptC = new JLabel("Option C:");
        lblOptC.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 4;
        formCard.add(lblOptC, gbc);

        txtOptionC = new JTextField();
        txtOptionC.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1;
        gbc.gridy = 4;
        formCard.add(txtOptionC, gbc);

        // 6. Option D
        JLabel lblOptD = new JLabel("Option D:");
        lblOptD.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 5;
        formCard.add(lblOptD, gbc);

        txtOptionD = new JTextField();
        txtOptionD.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1;
        gbc.gridy = 5;
        formCard.add(txtOptionD, gbc);

        // 7. Correct Answer (Dynamic Card Layout: Combo or TextField)
        JLabel lblCorrect = new JLabel("Correct Answer:");
        lblCorrect.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 6;
        formCard.add(lblCorrect, gbc);

        answerCardLayout = new CardLayout();
        answerCardPanel = new JPanel(answerCardLayout);
        answerCardPanel.setBackground(LoginFrame.COLOR_CARD);

        comboCorrectChoice = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        comboCorrectChoice.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        answerCardPanel.add(comboCorrectChoice, CARD_COMBO);

        txtCorrectBlank = new JTextField();
        txtCorrectBlank.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        answerCardPanel.add(txtCorrectBlank, CARD_TEXT);

        gbc.gridx = 1;
        gbc.gridy = 6;
        formCard.add(answerCardPanel, gbc);

        // 8. Marks
        JLabel lblMarks = new JLabel("Marks:");
        lblMarks.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 7;
        formCard.add(lblMarks, gbc);

        spinnerMarks = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spinnerMarks.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1;
        gbc.gridy = 7;
        formCard.add(spinnerMarks, gbc);

        mainPanel.add(formCard, BorderLayout.CENTER);

        // Bottom Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionsPanel.setBackground(LoginFrame.COLOR_BG);

        btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancel.setPreferredSize(new Dimension(85, 32));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave = new JButton("Save Question");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSave.setBackground(LoginFrame.COLOR_PRIMARY);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setPreferredSize(new Dimension(130, 32));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveQuestion();
            }
        });

        actionsPanel.add(btnCancel);
        actionsPanel.add(btnSave);

        mainPanel.add(actionsPanel, BorderLayout.SOUTH);

        // Type change listener
        comboQuestionType.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selectedType = (String) e.getItem();
                updateUIForQuestionType(selectedType);
            }
        });

        setContentPane(mainPanel);
    }

    /**
     * Dynamically updates field states and options depending on question type.
     */
    private void updateUIForQuestionType(String type) {
        if (Question.TYPE_MCQ.equalsIgnoreCase(type)) {
            txtOptionA.setEnabled(true);
            txtOptionB.setEnabled(true);
            txtOptionC.setEnabled(true);
            txtOptionD.setEnabled(true);

            comboCorrectChoice.removeAllItems();
            comboCorrectChoice.addItem("A");
            comboCorrectChoice.addItem("B");
            comboCorrectChoice.addItem("C");
            comboCorrectChoice.addItem("D");

            answerCardLayout.show(answerCardPanel, CARD_COMBO);
        } else if (Question.TYPE_TRUE_FALSE.equalsIgnoreCase(type)) {
            txtOptionA.setText("True");
            txtOptionA.setEnabled(false);

            txtOptionB.setText("False");
            txtOptionB.setEnabled(false);

            txtOptionC.setText("");
            txtOptionC.setEnabled(false);

            txtOptionD.setText("");
            txtOptionD.setEnabled(false);

            comboCorrectChoice.removeAllItems();
            comboCorrectChoice.addItem("True");
            comboCorrectChoice.addItem("False");

            answerCardLayout.show(answerCardPanel, CARD_COMBO);
        } else if (Question.TYPE_FILL_BLANK.equalsIgnoreCase(type)) {
            txtOptionA.setText("");
            txtOptionA.setEnabled(false);

            txtOptionB.setText("");
            txtOptionB.setEnabled(false);

            txtOptionC.setText("");
            txtOptionC.setEnabled(false);

            txtOptionD.setText("");
            txtOptionD.setEnabled(false);

            answerCardLayout.show(answerCardPanel, CARD_TEXT);
        }
        answerCardPanel.revalidate();
        answerCardPanel.repaint();
    }

    /**
     * Populates fields when editing an existing question.
     */
    private void populateFields(Question q) {
        txtQuestionText.setText(q.getQuestionText());
        comboQuestionType.setSelectedItem(q.getQuestionType());
        updateUIForQuestionType(q.getQuestionType());

        if (Question.TYPE_MCQ.equalsIgnoreCase(q.getQuestionType())) {
            txtOptionA.setText(q.getOptionA());
            txtOptionB.setText(q.getOptionB());
            txtOptionC.setText(q.getOptionC());
            txtOptionD.setText(q.getOptionD());
            comboCorrectChoice.setSelectedItem(q.getCorrectAnswer());
        } else if (Question.TYPE_TRUE_FALSE.equalsIgnoreCase(q.getQuestionType())) {
            comboCorrectChoice.setSelectedItem(q.getCorrectAnswer());
        } else if (Question.TYPE_FILL_BLANK.equalsIgnoreCase(q.getQuestionType())) {
            txtCorrectBlank.setText(q.getCorrectAnswer());
        }

        spinnerMarks.setValue(q.getMarks() > 0 ? q.getMarks() : 1);
    }

    private void saveQuestion() {
        String text = txtQuestionText.getText().trim();
        String type = (String) comboQuestionType.getSelectedItem();
        int marks = (Integer) spinnerMarks.getValue();

        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Question text cannot be empty.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            txtQuestionText.requestFocusInWindow();
            return;
        }

        String optA = null;
        String optB = null;
        String optC = null;
        String optD = null;
        String correctAnswer = null;

        if (Question.TYPE_MCQ.equalsIgnoreCase(type)) {
            optA = txtOptionA.getText().trim();
            optB = txtOptionB.getText().trim();
            optC = txtOptionC.getText().trim();
            optD = txtOptionD.getText().trim();

            if (optA.isEmpty() || optB.isEmpty() || optC.isEmpty() || optD.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill out all 4 options (A, B, C, and D) for multiple choice questions.",
                        "Incomplete Options",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            correctAnswer = (String) comboCorrectChoice.getSelectedItem();
        } else if (Question.TYPE_TRUE_FALSE.equalsIgnoreCase(type)) {
            optA = "True";
            optB = "False";
            optC = "";
            optD = "";
            correctAnswer = (String) comboCorrectChoice.getSelectedItem();
        } else if (Question.TYPE_FILL_BLANK.equalsIgnoreCase(type)) {
            optA = "";
            optB = "";
            optC = "";
            optD = "";
            correctAnswer = txtCorrectBlank.getText().trim();

            if (correctAnswer.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please specify the correct answer for the blank.",
                        "Missing Correct Answer",
                        JOptionPane.WARNING_MESSAGE);
                txtCorrectBlank.requestFocusInWindow();
                return;
            }
        }

        btnSave.setEnabled(false);
        try {
            boolean success;
            if (existingQuestion == null) {
                Question newQ = new Question(examId, text, type, optA, optB, optC, optD, correctAnswer, marks);
                success = questionDAO.addQuestion(newQ);
            } else {
                existingQuestion.setQuestionText(text);
                existingQuestion.setQuestionType(type);
                existingQuestion.setOptionA(optA);
                existingQuestion.setOptionB(optB);
                existingQuestion.setOptionC(optC);
                existingQuestion.setOptionD(optD);
                existingQuestion.setCorrectAnswer(correctAnswer);
                existingQuestion.setMarks(marks);
                success = questionDAO.updateQuestion(existingQuestion);
            }

            if (success) {
                saved = true;
                JOptionPane.showMessageDialog(this,
                        "Question saved successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to save question. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "An error occurred: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            btnSave.setEnabled(true);
        }
    }

    /**
     * Checks if the question was successfully saved.
     *
     * @return true if saved; false otherwise
     */
    public boolean isSaved() {
        return saved;
    }
}
