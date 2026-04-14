package demo.teste.trueorfalse;

import demo.teste.score.LocalBestScoreManager;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class TrueOrFalseGamePane extends StackPane {
    private static final Duration NEXT_QUESTION_DELAY = Duration.millis(750);

    private final List<Question> questions = new ArrayList<>();
    private final VBox layout = new VBox(18);
    private final Label titleLabel = new Label("Vrai ou Faux");
    private final Label progressLabel = new Label();
    private final Label scoreLabel = new Label();
    private final Label questionLabel = new Label();
    private final Label feedbackLabel = new Label();
    private final Label endLabel = new Label();
    private final Button trueButton = new Button("Vrai");
    private final Button falseButton = new Button("Faux");
    private final Button replayButton = new Button("Rejouer");
    private final PauseTransition nextQuestionDelay = new PauseTransition(NEXT_QUESTION_DELAY);
    private final LocalBestScoreManager scoreManager =
            new LocalBestScoreManager("trueorfalse-best-score.txt", "TrueOrFalseScoreManager");
    private final TrueOrFalseQuestionRepository questionRepository = new TrueOrFalseQuestionRepository();

    private int currentQuestionIndex;
    private int score;
    private int bestScore;
    private boolean gameFinished;
    private boolean scoreSaved;

    public TrueOrFalseGamePane() {
        setFocusTraversable(true);
        setPrefSize(640, 420);
        setMinSize(640, 420);
        setStyle("-fx-background-color: linear-gradient(to bottom, #1f2937, #0f172a);");

        buildUi();
        initialiserQuestions();
        startNewGame();

        nextQuestionDelay.setOnFinished(event -> showNextQuestion());
    }

    public void bindScene(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKey);
    }

    public void start() {
        requestFocus();
    }

    public void stop() {
        nextQuestionDelay.stop();
    }

    private void buildUi() {
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(28));
        layout.setMaxWidth(560);

        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 30));

        progressLabel.setTextFill(Color.web("#d1d5db"));
        progressLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        scoreLabel.setTextFill(Color.web("#d1d5db"));
        scoreLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        questionLabel.setWrapText(true);
        questionLabel.setTextFill(Color.WHITE);
        questionLabel.setFont(Font.font("System", FontWeight.BOLD, 26));
        questionLabel.setAlignment(Pos.CENTER);
        questionLabel.setMaxWidth(540);

        feedbackLabel.setTextFill(Color.web("#fbbf24"));
        feedbackLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        feedbackLabel.setAlignment(Pos.CENTER);

        endLabel.setTextFill(Color.web("#86efac"));
        endLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        endLabel.setAlignment(Pos.CENTER);
        endLabel.setVisible(false);
        endLabel.setManaged(false);

        styleAnswerButton(trueButton, "#16a34a");
        styleAnswerButton(falseButton, "#dc2626");
        styleAnswerButton(replayButton, "#2563eb");

        trueButton.setOnAction(event -> answer(true));
        falseButton.setOnAction(event -> answer(false));
        replayButton.setOnAction(event -> startNewGame());
        replayButton.setVisible(false);
        replayButton.setManaged(false);

        HBox infoRow = new HBox(28, progressLabel, scoreLabel);
        infoRow.setAlignment(Pos.CENTER);

        HBox answerRow = new HBox(18, trueButton, falseButton);
        answerRow.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(titleLabel, infoRow, questionLabel, feedbackLabel, answerRow, endLabel, replayButton);
        getChildren().add(layout);
    }

    private void styleAnswerButton(Button button, String color) {
        button.setMinWidth(150);
        button.setMinHeight(48);
        button.setFont(Font.font("System", FontWeight.BOLD, 18));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: " + color + "; -fx-cursor: hand; -fx-background-radius: 10px;");
    }

    public void initialiserQuestions() {
        questions.clear();

        List<TrueOrFalseQuestionRepository.QuestionData> loadedQuestions = questionRepository.loadQuestions();
        for (TrueOrFalseQuestionRepository.QuestionData loadedQuestion : loadedQuestions) {
            questions.add(new Question(loadedQuestion.texte(), loadedQuestion.reponse()));
        }
    }

    private void startNewGame() {
        nextQuestionDelay.stop();
        currentQuestionIndex = 0;
        score = 0;
        bestScore = scoreManager.chargerMeilleurScore();
        gameFinished = false;
        scoreSaved = false;

        feedbackLabel.setText("");
        endLabel.setVisible(false);
        endLabel.setManaged(false);
        replayButton.setVisible(false);
        replayButton.setManaged(false);

        setButtonsEnabled(true);
        showQuestion();
    }

    private void showQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            finishGame();
            return;
        }

        Question question = questions.get(currentQuestionIndex);
        scoreLabel.setText("Score: " + score);
        progressLabel.setText("Question " + (currentQuestionIndex + 1) + " / " + questions.size() + "  |  Record: " + bestScore);
        questionLabel.setText(question.getTexte());
        feedbackLabel.setText("");
    }

    private void answer(boolean userAnswer) {
        if (gameFinished || trueButton.isDisabled()) {
            return;
        }

        Question question = questions.get(currentQuestionIndex);
        boolean correct = question.getReponse() == userAnswer;

        if (correct) {
            score += 10;
            feedbackLabel.setTextFill(Color.web("#86efac"));
            feedbackLabel.setText("Bravo ! Bonne reponse.");
        } else {
            feedbackLabel.setTextFill(Color.web("#fca5a5"));
            feedbackLabel.setText("Mauvaise reponse.");
        }

        scoreLabel.setText("Score: " + score);
        setButtonsEnabled(false);
        nextQuestionDelay.playFromStart();
    }

    private void showNextQuestion() {
        currentQuestionIndex++;

        if (currentQuestionIndex >= questions.size()) {
            finishGame();
            return;
        }

        setButtonsEnabled(true);
        feedbackLabel.setTextFill(Color.web("#fbbf24"));
        showQuestion();
    }

    private void finishGame() {
        gameFinished = true;
        nextQuestionDelay.stop();
        setButtonsEnabled(false);

        feedbackLabel.setTextFill(Color.web("#fbbf24"));
        feedbackLabel.setText("Quiz termine !");
        if (!scoreSaved) {
            scoreSaved = true;
            scoreManager.sauvegarderMeilleurScore(score);
            bestScore = scoreManager.chargerMeilleurScore();
        }
        endLabel.setText("Score final : " + score + " / " + (questions.size() * 10));
        endLabel.setVisible(true);
        endLabel.setManaged(true);
        replayButton.setVisible(true);
        replayButton.setManaged(true);
    }

    private void setButtonsEnabled(boolean enabled) {
        trueButton.setDisable(!enabled);
        falseButton.setDisable(!enabled);
    }

    private void handleKey(KeyEvent event) {
        KeyCode code = event.getCode();

        if (code == KeyCode.R && gameFinished) {
            startNewGame();
            event.consume();
            return;
        }

        if (trueButton.isDisabled()) {
            return;
        }

        if (code == KeyCode.V || code == KeyCode.ENTER || code == KeyCode.RIGHT) {
            answer(true);
            event.consume();
        } else if (code == KeyCode.F || code == KeyCode.LEFT || code == KeyCode.DOWN) {
            answer(false);
            event.consume();
        }
    }

    private static final class Question {
        private final String texte;
        private final boolean reponse;

        private Question(String texte, boolean reponse) {
            this.texte = texte;
            this.reponse = reponse;
        }

        private String getTexte() {
            return texte;
        }

        private boolean getReponse() {
            return reponse;
        }
    }
}