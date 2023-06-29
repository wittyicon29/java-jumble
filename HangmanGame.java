import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HangmanGame extends JFrame implements ActionListener {
    private String[] words = {"hangman", "java", "programming", "openai"};
    private String wordToGuess;
    private int guessesLeft = 0;
    private StringBuilder hiddenWord;

    private JLabel guessesLeftLabel;
    private JLabel hiddenWordLabel;
    private JTextField guessTextField;
    private JButton guessButton;
    private JButton hintButton;

    private Timer wrongGuessTimer;
    private Timer correctGuessTimer;

    private int hintCount = 0;
    private StringBuilder hintStringBuilder = new StringBuilder();

    public HangmanGame() {
        setTitle("Hangman Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        hiddenWordLabel = new JLabel();
        hiddenWordLabel.setFont(new Font("Arial", Font.BOLD, 24));

        guessesLeftLabel = new JLabel("Guesses Left: " + guessesLeft);
        guessesLeftLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        guessTextField = new JTextField(10);
        guessButton = new JButton("Guess");
        guessButton.setFont(new Font("Arial", Font.PLAIN, 18));

        hintButton = new JButton("Hint");
        hintButton.setFont(new Font("Arial", Font.PLAIN, 18));
        hintButton.setEnabled(false);

        guessButton.addActionListener(this);
        guessTextField.addActionListener(this);
        hintButton.addActionListener(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(5, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(hiddenWordLabel);
        mainPanel.add(guessesLeftLabel);

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(guessTextField);
        inputPanel.add(guessButton);
        inputPanel.add(hintButton);
        mainPanel.add(inputPanel);

        getContentPane().add(mainPanel);
        initializeGame();
        pack();
        setPreferredSize(new Dimension(400, 350));
        setLocationRelativeTo(null);
        setVisible(true);

        initializeTimers();
    }

    private void initializeGame() {
        wordToGuess = words[(int) (Math.random() * words.length)];
        hiddenWord = new StringBuilder();
        for (int i = 0; i < wordToGuess.length(); i++) {
            hiddenWord.append("_");
        }
        hiddenWordLabel.setText(hiddenWord.toString());
        guessesLeft = 6;
        guessesLeftLabel.setText("Guesses Left: " + guessesLeft);
        hintCount = 0;
        hintButton.setEnabled(true);
    }

    private void updateHiddenWord(char guess) {
    boolean found = false;
    for (int i = 0; i < wordToGuess.length(); i++) {
        if (wordToGuess.charAt(i) == guess) {
            hiddenWord.setCharAt(i, guess);
            found = true;
        }
    }

    hiddenWordLabel.setText(hiddenWord.toString());

    if (!found) {
        guessesLeft--;
        guessesLeftLabel.setText("Guesses Left: " + guessesLeft);
        if (guessesLeft == 0) {
            endGame("You lose!! The word was: " + wordToGuess);
        } else {
            playWrongGuessAnimation();
        }
    } else if (hiddenWord.toString().equals(wordToGuess)) {
        endGame("Congratulations!! You won!");
    } else {
        playCorrectGuessAnimation();
    }
    }


    private void playWrongGuessAnimation() {
        // Perform wrong guess animation (e.g., shake the label or change color)
        Color originalColor = hiddenWordLabel.getForeground();
        wrongGuessTimer.start();

        // After a certain delay, reset the label appearance
        Timer resetTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                wrongGuessTimer.stop();
                hiddenWordLabel.setForeground(originalColor);
            }
        });
        resetTimer.setRepeats(false);
        resetTimer.start();
    }

    private void playCorrectGuessAnimation() {
        // Perform correct guess animation (e.g., fade in the label or change background color)
        Color originalColor = hiddenWordLabel.getBackground();
        correctGuessTimer.start();

        // After a certain delay, reset the label appearance
        Timer resetTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                correctGuessTimer.stop();
                hiddenWordLabel.setBackground(originalColor);
            }
        });
        resetTimer.setRepeats(false);
        resetTimer.start();
    }

    private void initializeTimers() {
        // Timer for wrong guess animation
        wrongGuessTimer = new Timer(100, new ActionListener() {
            private int count = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (count % 2 == 0) {
                    hiddenWordLabel.setForeground(Color.RED);
                } else {
                    hiddenWordLabel.setForeground(Color.BLACK);
                }
                count++;
            }
        });
        wrongGuessTimer.setRepeats(true);

        // Timer for correct guess animation
        correctGuessTimer = new Timer(200, new ActionListener() {
            private float alpha = 0.5f;

            @Override
            public void actionPerformed(ActionEvent e) {
                hiddenWordLabel.setBackground(new Color(0, 0, 255, (int) (alpha * 255)));
                alpha += 0.1f;
                if (alpha >= 1.0f) {
                    correctGuessTimer.stop();
                }
            }
        });
        correctGuessTimer.setRepeats(true);
    }

    private void endGame(String message) {
        guessTextField.setEnabled(false);
        guessButton.setEnabled(false);
        hintButton.setEnabled(false);
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        initializeGame();
        guessTextField.setEnabled(true);
        guessButton.setEnabled(true);
        hintButton.setEnabled(true);
        guessTextField.requestFocus();
    }

    private void showHint() {
        if (hintCount < 3) {
            char hintChar = wordToGuess.charAt(hintCount);
            hintStringBuilder.append(hintChar).append(" ");
            JOptionPane.showMessageDialog(this, "Hint: " + hintStringBuilder.toString(), "Hint", JOptionPane.INFORMATION_MESSAGE);
            hintCount++;
            if (hintCount == 3) {
                hintButton.setEnabled(false);
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == guessButton || e.getSource() == guessTextField) {
            String guessText = guessTextField.getText();
            if (guessText.length() > 0) {
                char guess = guessText.charAt(0);
                updateHiddenWord(guess);
                guessTextField.setText("");
            }
        } else if (e.getSource() == hintButton) {
            showHint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HangmanGame::new);
    }
}
