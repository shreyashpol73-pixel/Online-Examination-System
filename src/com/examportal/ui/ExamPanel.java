package com.examportal.ui;

import com.examportal.dao.QuestionDAO;
import com.examportal.dao.ResultDAO;
import com.examportal.model.Exam;
import com.examportal.model.Question;
import com.examportal.model.Result;
import com.examportal.model.StudentAnswer;
import com.examportal.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main exam-taking interface.
 * Displays questions dynamically (MCQ, True/False, Fill in Blank),
 * maintains countdown timer, question palette, and calculates scoring upon submission.
 */
public class ExamPanel extends JFrame {

    private final User student;
    private final Exam exam;
    private final StudentDashboard parentDashboard;

    private final QuestionDAO questionDAO = new QuestionDAO();
    private final ResultDAO resultDAO = new ResultDAO();

    private List<Question> questions = new ArrayList<>();
    private final Map<Integer, String> studentAnswers = new HashMap<>();
    private int currentQuestionIndex = 0;

    // Timer components
    private Timer countdownTimer;
    private int remainingSeconds;
    private int totalDurationSeconds;

    // UI Components
    private JLabel lblExamTitle;
    private JLabel lblTimer;
    private JLabel lblQuestionHeader;
    private JTextArea txtQuestionText;

    private JPanel optionsContainerPanel;
    private ButtonGroup optionButtonGroup;
    private JRadioButton rbOptA;
    private JRadioButton rbOptB;
    private JRadioButton rbOptC;
    private JRadioButton rbOptD;
    private JRadioButton rbTrue;
    private JRadioButton rbFalse;
    private JTextField txtFillBlank;

    private JButton btnPrev;
    private JButton btnNext;
    private JButton btnClearAnswer;
    private JButton btnSubmitExam;

    private JPanel paletteGridPanel;
    private JButton[] paletteButtons;

    private boolean isSubmitted = false;

    // Palette Colors
    private static final Color COLOR_CURRENT = new Color(33, 150, 243);    // Blue #2196F3
    private static final Color COLOR_ANSWERED = new Color(76, 175, 80);    // Green #4CAF50
    private static final Color COLOR_UNANSWERED = new Color(244, 67, 54);  // Red #F44336

    /**
     * Constructs the ExamPanel window.
     *
     * @param student student taking the exam
     * @param exam exam entity
     * @param parentDashboard parent dashboard to refresh and restore
     */
    public ExamPanel(User student, Exam exam, StudentDashboard parentDashboard) {
        this.student = student;
        this.exam = exam;
        this.parentDashboard = parentDashboard;

        this.questions = questionDAO.getQuestionsByExamId(exam.getExamId());
        if (this.questions == null || this.questions.isEmpty()) {
            JOptionPane.showMessageDialog(parentDashboard,
                    "This exam has no questions configured. Please contact the administrator.",
                    "No Questions Available",
                    JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        this.totalDurationSeconds = exam.getDurationMinutes() * 60;
        this.remainingSeconds = totalDurationSeconds;

        initUI();
        startTimer();
        displayQuestion(0);
    }

    private void initUI() {
        setTitle("Exam: " + exam.getTitle() + " - Candidate: " + student.getFullName());
        setSize(960, 680);
        setMinimumSize(new Dimension(860, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Window listener to confirm exit
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExitExam();
            }
        });

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(LoginFrame.COLOR_BG);

        // ==========================================
        // 1. TOP HEADER: Exam Title & Countdown Timer
        // ==========================================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 118, 210)); // Deep Blue
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JPanel headerLeft = new JPanel(new GridLayout(2, 1, 2, 2));
        headerLeft.setOpaque(false);

        lblExamTitle = new JLabel("📝 " + exam.getTitle());
        lblExamTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblExamTitle.setForeground(Color.WHITE);

        JLabel lblSubInfo = new JLabel("Student: " + student.getFullName() + " | Total Marks: " + exam.getTotalMarks() + " | Total Questions: " + questions.size());
        lblSubInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubInfo.setForeground(new Color(227, 242, 253));

        headerLeft.add(lblExamTitle);
        headerLeft.add(lblSubInfo);

        // Timer Panel on Right
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRight.setOpaque(false);

        lblTimer = new JLabel("⏳ Time: " + formatTime(remainingSeconds));
        lblTimer.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTimer.setForeground(Color.WHITE);
        lblTimer.setOpaque(true);
        lblTimer.setBackground(new Color(13, 71, 161));
        lblTimer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(187, 222, 251), 1),
                new EmptyBorder(6, 12, 6, 12)
        ));

        headerRight.add(lblTimer);

        headerPanel.add(headerLeft, BorderLayout.WEST);
        headerPanel.add(headerRight, BorderLayout.EAST);
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. CENTER: Question Display Area
        // ==========================================
        JPanel centerContainer = new JPanel(new BorderLayout(15, 15));
        centerContainer.setBackground(LoginFrame.COLOR_BG);
        centerContainer.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel questionCard = new JPanel(new BorderLayout(10, 10));
        questionCard.setBackground(Color.WHITE);
        questionCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224), 1, true),
                new EmptyBorder(18, 20, 18, 20)
        ));

        // Question Number & Marks Header
        JPanel questionHeaderPanel = new JPanel(new BorderLayout());
        questionHeaderPanel.setOpaque(false);

        lblQuestionHeader = new JLabel("Question 1 of " + questions.size());
        lblQuestionHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblQuestionHeader.setForeground(LoginFrame.COLOR_PRIMARY_DARK);

        questionHeaderPanel.add(lblQuestionHeader, BorderLayout.WEST);
        questionCard.add(questionHeaderPanel, BorderLayout.NORTH);

        // Center: Question Text & Options Container
        JPanel questionBodyPanel = new JPanel(new BorderLayout(10, 12));
        questionBodyPanel.setOpaque(false);

        txtQuestionText = new JTextArea(4, 40);
        txtQuestionText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtQuestionText.setEditable(false);
        txtQuestionText.setLineWrap(true);
        txtQuestionText.setWrapStyleWord(true);
        txtQuestionText.setBackground(new Color(250, 250, 250));
        txtQuestionText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(10, 12, 10, 12)
        ));
        questionBodyPanel.add(txtQuestionText, BorderLayout.NORTH);

        // Dynamic Options Container
        optionsContainerPanel = new JPanel();
        optionsContainerPanel.setOpaque(false);
        optionsContainerPanel.setLayout(new BoxLayout(optionsContainerPanel, BoxLayout.Y_AXIS));

        questionBodyPanel.add(optionsContainerPanel, BorderLayout.CENTER);
        questionCard.add(questionBodyPanel, BorderLayout.CENTER);

        centerContainer.add(questionCard, BorderLayout.CENTER);

        // ==========================================
        // 3. RIGHT SIDEBAR: Question Palette
        // ==========================================
        JPanel paletteSidebar = createPaletteSidebar();
        centerContainer.add(paletteSidebar, BorderLayout.EAST);

        rootPanel.add(centerContainer, BorderLayout.CENTER);

        // ==========================================
        // 4. BOTTOM BAR: Navigation & Submit
        // ==========================================
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(224, 224, 224)),
                new EmptyBorder(10, 20, 10, 20)
        ));

        JPanel navLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        navLeft.setOpaque(false);

        btnPrev = new JButton("◀ Previous");
        btnPrev.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnPrev.setPreferredSize(new Dimension(110, 34));
        btnPrev.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnNext = new JButton("Next ▶");
        btnNext.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnNext.setPreferredSize(new Dimension(110, 34));
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnClearAnswer = new JButton("Clear Selection");
        btnClearAnswer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnClearAnswer.setPreferredSize(new Dimension(120, 34));
        btnClearAnswer.setForeground(new Color(117, 117, 117));
        btnClearAnswer.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnPrev.addActionListener(e -> navigatePrevious());
        btnNext.addActionListener(e -> navigateNext());
        btnClearAnswer.addActionListener(e -> clearCurrentSelection());

        navLeft.add(btnPrev);
        navLeft.add(btnNext);
        navLeft.add(btnClearAnswer);

        JPanel navRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        navRight.setOpaque(false);

        btnSubmitExam = new JButton("Submit Exam 🏁");
        btnSubmitExam.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSubmitExam.setBackground(new Color(46, 125, 50)); // Green
        btnSubmitExam.setForeground(Color.WHITE);
        btnSubmitExam.setFocusPainted(false);
        btnSubmitExam.setPreferredSize(new Dimension(140, 34));
        btnSubmitExam.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmitExam.addActionListener(e -> promptSubmitExam());

        navRight.add(btnSubmitExam);

        bottomBar.add(navLeft, BorderLayout.WEST);
        bottomBar.add(navRight, BorderLayout.EAST);
        rootPanel.add(bottomBar, BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    /**
     * Builds the right-hand Question Palette sidebar with numbered buttons and color indicators.
     */
    private JPanel createPaletteSidebar() {
        JPanel paletteCard = new JPanel(new BorderLayout(8, 8));
        paletteCard.setBackground(Color.WHITE);
        paletteCard.setPreferredSize(new Dimension(240, 0));
        paletteCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224), 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel lblPaletteHeader = new JLabel("Question Palette");
        lblPaletteHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPaletteHeader.setForeground(LoginFrame.COLOR_PRIMARY_DARK);
        lblPaletteHeader.setHorizontalAlignment(SwingConstants.CENTER);
        paletteCard.add(lblPaletteHeader, BorderLayout.NORTH);

        // Grid of Question Buttons
        paletteGridPanel = new JPanel(new GridLayout(0, 4, 6, 6));
        paletteGridPanel.setBackground(Color.WHITE);

        int count = questions.size();
        paletteButtons = new JButton[count];

        for (int i = 0; i < count; i++) {
            final int index = i;
            JButton btn = new JButton(String.valueOf(i + 1));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setBackground(COLOR_UNANSWERED);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(42, 34));
            btn.addActionListener(e -> {
                saveCurrentAnswer();
                currentQuestionIndex = index;
                displayQuestion(currentQuestionIndex);
            });
            paletteButtons[i] = btn;
            paletteGridPanel.add(btn);
        }

        JScrollPane scrollPalette = new JScrollPane(paletteGridPanel);
        scrollPalette.setBorder(BorderFactory.createEmptyBorder());
        scrollPalette.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        paletteCard.add(scrollPalette, BorderLayout.CENTER);

        // Legend at bottom of palette
        JPanel legendPanel = new JPanel(new GridLayout(3, 1, 3, 3));
        legendPanel.setOpaque(false);
        legendPanel.setBorder(new EmptyBorder(8, 4, 4, 4));

        legendPanel.add(createLegendItem("Current Question", COLOR_CURRENT));
        legendPanel.add(createLegendItem("Answered", COLOR_ANSWERED));
        legendPanel.add(createLegendItem("Unanswered", COLOR_UNANSWERED));

        paletteCard.add(legendPanel, BorderLayout.SOUTH);
        return paletteCard;
    }

    private JPanel createLegendItem(String labelText, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);

        JLabel colorBadge = new JLabel("   ");
        colorBadge.setOpaque(true);
        colorBadge.setBackground(color);
        colorBadge.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        colorBadge.setPreferredSize(new Dimension(14, 14));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        p.add(colorBadge);
        p.add(lbl);
        return p;
    }

    /**
     * Starts the countdown timer.
     */
    private void startTimer() {
        countdownTimer = new Timer(1000, e -> {
            remainingSeconds--;
            lblTimer.setText("⏳ Time: " + formatTime(remainingSeconds));

            if (remainingSeconds <= 60) {
                lblTimer.setBackground(new Color(211, 47, 47)); // Red alarm
                lblTimer.setForeground(Color.WHITE);
            }

            if (remainingSeconds <= 0) {
                countdownTimer.stop();
                JOptionPane.showMessageDialog(this,
                        "Time is up! Your exam is being submitted automatically.",
                        "Time Up",
                        JOptionPane.WARNING_MESSAGE);
                submitExam();
            }
        });
        countdownTimer.start();
    }

    private String formatTime(int totalSecs) {
        if (totalSecs < 0) totalSecs = 0;
        int mins = totalSecs / 60;
        int secs = totalSecs % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    /**
     * Renders the current question and builds input fields dynamically based on question type.
     */
    private void displayQuestion(int index) {
        if (index < 0 || index >= questions.size()) return;

        currentQuestionIndex = index;
        Question q = questions.get(index);

        lblQuestionHeader.setText("Question " + (index + 1) + " of " + questions.size() +
                "  (" + q.getMarks() + " Mark" + (q.getMarks() > 1 ? "s" : "") + " | " + q.getQuestionType() + ")");
        txtQuestionText.setText(q.getQuestionText());

        // Update Prev/Next button states
        btnPrev.setEnabled(index > 0);
        btnNext.setEnabled(index < questions.size() - 1);

        optionsContainerPanel.removeAll();
        optionButtonGroup = new ButtonGroup();

        String savedAnswer = studentAnswers.get(q.getQuestionId());

        if (Question.TYPE_MCQ.equalsIgnoreCase(q.getQuestionType())) {
            rbOptA = createRadioButton("A. " + q.getOptionA(), "A");
            rbOptB = createRadioButton("B. " + q.getOptionB(), "B");
            rbOptC = createRadioButton("C. " + q.getOptionC(), "C");
            rbOptD = createRadioButton("D. " + q.getOptionD(), "D");

            optionButtonGroup.add(rbOptA);
            optionButtonGroup.add(rbOptB);
            optionButtonGroup.add(rbOptC);
            optionButtonGroup.add(rbOptD);

            if (savedAnswer != null) {
                if ("A".equalsIgnoreCase(savedAnswer)) rbOptA.setSelected(true);
                else if ("B".equalsIgnoreCase(savedAnswer)) rbOptB.setSelected(true);
                else if ("C".equalsIgnoreCase(savedAnswer)) rbOptC.setSelected(true);
                else if ("D".equalsIgnoreCase(savedAnswer)) rbOptD.setSelected(true);
            }

            optionsContainerPanel.add(rbOptA);
            optionsContainerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            optionsContainerPanel.add(rbOptB);
            optionsContainerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            optionsContainerPanel.add(rbOptC);
            optionsContainerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            optionsContainerPanel.add(rbOptD);

        } else if (Question.TYPE_TRUE_FALSE.equalsIgnoreCase(q.getQuestionType())) {
            rbTrue = createRadioButton("True", "True");
            rbFalse = createRadioButton("False", "False");

            optionButtonGroup.add(rbTrue);
            optionButtonGroup.add(rbFalse);

            if (savedAnswer != null) {
                if ("True".equalsIgnoreCase(savedAnswer)) rbTrue.setSelected(true);
                else if ("False".equalsIgnoreCase(savedAnswer)) rbFalse.setSelected(true);
            }

            optionsContainerPanel.add(rbTrue);
            optionsContainerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            optionsContainerPanel.add(rbFalse);

        } else if (Question.TYPE_FILL_BLANK.equalsIgnoreCase(q.getQuestionType())) {
            JPanel fillPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
            fillPanel.setOpaque(false);

            JLabel lblPrompt = new JLabel("Your Answer:");
            lblPrompt.setFont(new Font("Segoe UI", Font.BOLD, 13));

            txtFillBlank = new JTextField(savedAnswer != null ? savedAnswer : "", 22);
            txtFillBlank.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txtFillBlank.setPreferredSize(new Dimension(240, 32));

            txtFillBlank.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) { recordAnswer(); }
                @Override
                public void removeUpdate(DocumentEvent e) { recordAnswer(); }
                @Override
                public void changedUpdate(DocumentEvent e) { recordAnswer(); }

                private void recordAnswer() {
                    String val = txtFillBlank.getText().trim();
                    if (!val.isEmpty()) {
                        studentAnswers.put(q.getQuestionId(), val);
                    } else {
                        studentAnswers.remove(q.getQuestionId());
                    }
                    updatePaletteColors();
                }
            });

            fillPanel.add(lblPrompt);
            fillPanel.add(txtFillBlank);
            optionsContainerPanel.add(fillPanel);
        }

        optionsContainerPanel.revalidate();
        optionsContainerPanel.repaint();

        updatePaletteColors();
    }

    private JRadioButton createRadioButton(String text, String actionCommand) {
        JRadioButton rb = new JRadioButton(text);
        rb.setActionCommand(actionCommand);
        rb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rb.setOpaque(false);
        rb.setCursor(new Cursor(Cursor.HAND_CURSOR));

        rb.addActionListener(e -> {
            Question q = questions.get(currentQuestionIndex);
            studentAnswers.put(q.getQuestionId(), actionCommand);
            updatePaletteColors();
        });

        return rb;
    }

    /**
     * Saves the answer from the currently displayed inputs into the answers map.
     */
    private void saveCurrentAnswer() {
        if (currentQuestionIndex < 0 || currentQuestionIndex >= questions.size()) return;
        Question q = questions.get(currentQuestionIndex);

        if (Question.TYPE_MCQ.equalsIgnoreCase(q.getQuestionType()) || Question.TYPE_TRUE_FALSE.equalsIgnoreCase(q.getQuestionType())) {
            if (optionButtonGroup != null && optionButtonGroup.getSelection() != null) {
                studentAnswers.put(q.getQuestionId(), optionButtonGroup.getSelection().getActionCommand());
            }
        } else if (Question.TYPE_FILL_BLANK.equalsIgnoreCase(q.getQuestionType())) {
            if (txtFillBlank != null) {
                String val = txtFillBlank.getText().trim();
                if (!val.isEmpty()) {
                    studentAnswers.put(q.getQuestionId(), val);
                } else {
                    studentAnswers.remove(q.getQuestionId());
                }
            }
        }
    }

    private void clearCurrentSelection() {
        if (currentQuestionIndex < 0 || currentQuestionIndex >= questions.size()) return;
        Question q = questions.get(currentQuestionIndex);

        studentAnswers.remove(q.getQuestionId());
        if (optionButtonGroup != null) {
            optionButtonGroup.clearSelection();
        }
        if (txtFillBlank != null) {
            txtFillBlank.setText("");
        }
        updatePaletteColors();
    }

    private void navigateNext() {
        saveCurrentAnswer();
        if (currentQuestionIndex < questions.size() - 1) {
            displayQuestion(currentQuestionIndex + 1);
        }
    }

    private void navigatePrevious() {
        saveCurrentAnswer();
        if (currentQuestionIndex > 0) {
            displayQuestion(currentQuestionIndex - 1);
        }
    }

    /**
     * Updates background colors and borders for all palette buttons.
     */
    private void updatePaletteColors() {
        if (paletteButtons == null) return;

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            JButton btn = paletteButtons[i];
            String ans = studentAnswers.get(q.getQuestionId());
            boolean isAnswered = (ans != null && !ans.trim().isEmpty());

            if (i == currentQuestionIndex) {
                btn.setBackground(COLOR_CURRENT);
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createLineBorder(new Color(13, 71, 161), 2));
            } else if (isAnswered) {
                btn.setBackground(COLOR_ANSWERED);
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            } else {
                btn.setBackground(COLOR_UNANSWERED);
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            }
        }
    }

    private void promptSubmitExam() {
        saveCurrentAnswer();

        int answeredCount = studentAnswers.size();
        int totalQuestions = questions.size();
        int unansweredCount = totalQuestions - answeredCount;

        StringBuilder msg = new StringBuilder();
        msg.append("Exam Submission Confirmation:\n\n");
        msg.append("Total Questions: ").append(totalQuestions).append("\n");
        msg.append("Answered: ").append(answeredCount).append("\n");
        msg.append("Unanswered: ").append(unansweredCount).append("\n\n");

        if (unansweredCount > 0) {
            msg.append("⚠️ You have ").append(unansweredCount).append(" unanswered question(s).\n");
        }
        msg.append("Are you sure you want to finalize and submit your exam?");

        int choice = JOptionPane.showConfirmDialog(this,
                msg.toString(),
                "Confirm Submission",
                JOptionPane.YES_NO_OPTION,
                unansweredCount > 0 ? JOptionPane.WARNING_MESSAGE : JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            submitExam();
        }
    }

    private void confirmExitExam() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Warning: Leaving this window will submit your exam with current answers.\nAre you sure you want to exit?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            submitExam();
        }
    }

    /**
     * Grades all responses, inserts the Result & StudentAnswers transactionally,
     * displays result dialog, and closes the exam panel.
     */
    private synchronized void submitExam() {
        if (isSubmitted) return;
        isSubmitted = true;

        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        saveCurrentAnswer();

        int score = 0;
        List<StudentAnswer> answersList = new ArrayList<>();

        for (Question q : questions) {
            String studentAns = studentAnswers.get(q.getQuestionId());
            if (studentAns == null) studentAns = "";
            studentAns = studentAns.trim();

            boolean isCorrect = evaluateAnswer(q, studentAns);
            if (isCorrect) {
                score += q.getMarks();
            }

            StudentAnswer record = new StudentAnswer(q.getQuestionId(), studentAns, isCorrect);
            answersList.add(record);
        }

        int timeTakenSeconds = totalDurationSeconds - remainingSeconds;
        if (timeTakenSeconds < 0) timeTakenSeconds = 0;
        if (timeTakenSeconds > totalDurationSeconds) timeTakenSeconds = totalDurationSeconds;

        double percentage = exam.getTotalMarks() > 0 ? ((double) score / exam.getTotalMarks()) * 100.0 : 0.0;
        percentage = Math.round(percentage * 100.0) / 100.0;

        String status = score >= exam.getPassingMarks() ? "PASS" : "FAIL";

        Result result = new Result(
                student.getUserId(),
                exam.getExamId(),
                score,
                exam.getTotalMarks(),
                percentage,
                status,
                timeTakenSeconds
        );

        boolean saved = resultDAO.saveResult(result, answersList);

        if (!saved) {
            JOptionPane.showMessageDialog(this,
                    "There was an issue saving your result to the database.\nPlease contact the administrator.",
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        // Show Result Summary Dialog
        showResultSummaryDialog(score, percentage, status, timeTakenSeconds);

        // Close exam panel and refresh parent dashboard
        dispose();
        if (parentDashboard != null) {
            parentDashboard.loadAvailableExams();
            parentDashboard.loadMyResults();
            parentDashboard.setVisible(true);
        }
    }

    /**
     * Evaluates student answer against question's correct answer.
     */
    private boolean evaluateAnswer(Question q, String studentAns) {
        if (studentAns == null || studentAns.isEmpty()) return false;
        String correct = q.getCorrectAnswer() != null ? q.getCorrectAnswer().trim() : "";

        if (Question.TYPE_MCQ.equalsIgnoreCase(q.getQuestionType())) {
            if (studentAns.equalsIgnoreCase(correct)) return true;
            // Handle if correct answer was stored as text or letter
            if ("A".equalsIgnoreCase(studentAns) && q.getOptionA() != null && q.getOptionA().trim().equalsIgnoreCase(correct)) return true;
            if ("B".equalsIgnoreCase(studentAns) && q.getOptionB() != null && q.getOptionB().trim().equalsIgnoreCase(correct)) return true;
            if ("C".equalsIgnoreCase(studentAns) && q.getOptionC() != null && q.getOptionC().trim().equalsIgnoreCase(correct)) return true;
            if ("D".equalsIgnoreCase(studentAns) && q.getOptionD() != null && q.getOptionD().trim().equalsIgnoreCase(correct)) return true;
            return false;
        } else if (Question.TYPE_TRUE_FALSE.equalsIgnoreCase(q.getQuestionType())) {
            if (studentAns.equalsIgnoreCase(correct)) return true;
            if ("True".equalsIgnoreCase(studentAns) && "A".equalsIgnoreCase(correct)) return true;
            if ("False".equalsIgnoreCase(studentAns) && "B".equalsIgnoreCase(correct)) return true;
            return false;
        } else if (Question.TYPE_FILL_BLANK.equalsIgnoreCase(q.getQuestionType())) {
            return studentAns.equalsIgnoreCase(correct);
        }
        return false;
    }

    private void showResultSummaryDialog(int score, double percentage, String status, int timeTakenSeconds) {
        int mins = timeTakenSeconds / 60;
        int secs = timeTakenSeconds % 60;

        StringBuilder sb = new StringBuilder();
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("       🎉 EXAM COMPLETED 🎉       \n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        sb.append("Exam Title:    ").append(exam.getTitle()).append("\n");
        sb.append("Candidate:     ").append(student.getFullName()).append("\n");
        sb.append("Score Achieved: ").append(score).append(" / ").append(exam.getTotalMarks()).append("\n");
        sb.append("Percentage:    ").append(String.format("%.2f%%", percentage)).append("\n");
        sb.append("Passing Marks: ").append(exam.getPassingMarks()).append("\n");
        sb.append("Final Status:  ").append(status).append("\n");
        sb.append("Time Taken:    ").append(mins).append(" min ").append(secs).append(" sec\n\n");
        sb.append("Your responses have been successfully submitted.");

        JOptionPane.showMessageDialog(this,
                sb.toString(),
                "Exam Results - " + status,
                "PASS".equalsIgnoreCase(status) ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }
}
