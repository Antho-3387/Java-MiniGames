package demo.teste.moreless.controller;

import demo.teste.moreless.dao.DatabaseManager;
import demo.teste.moreless.model.Produit;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.geometry.Rectangle2D;

public class GameController implements Initializable {

    @FXML private StackPane rootPane;
    @FXML private ImageView backgroundView;
    @FXML private ImageView productImageView;
    @FXML private Label productNameLabel;
    @FXML private Label categoryLabel;
    @FXML private Label hintLabel;
    @FXML private Label resultLabel;
    @FXML private Label attemptsLabel;
    @FXML private Label scoreLabel;
    @FXML private Label timerLabel;
    @FXML private TextField priceField;
    @FXML private Button validateBtn;
    @FXML private Pane screenOverlay;

    private static final int MAX_ATTEMPTS  = 10;
    private static final int MAX_ATT_BONUS = 1000; // (MAX_ATTEMPTS - attempts) * 200
    private static final int MAX_TIME_BONUS = 1000; // décroit de 10 pts/s, 0 après 100 s

    private Produit currentProduit;
    private int     attempts;
    private int     score;

    private Timeline timer;
    private long     startTimeMs;

    // ---------------------------------------------------------------

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        URL bgUrl = getClass().getResource("/img/moreless/main_scene.png");
        if (bgUrl != null) {
            backgroundView.setImage(new Image(bgUrl.toExternalForm()));
        }
        priceField.setOnAction(e -> handleValidate());
        startNewGame();
    }

    // ---------------------------------------------------------------

    @FXML
    public void startNewGame() {
        currentProduit = DatabaseManager.findRandom();
        attempts = 0;

        hintLabel.setText("");
        resultLabel.setText("");
        priceField.clear();
        priceField.setDisable(false);
        validateBtn.setDisable(false);

        updateHud();
        resetTimer();

        if (currentProduit == null) {
            productNameLabel.setText("Aucun produit");
            categoryLabel.setText("");
            productImageView.setImage(null);
            hintLabel.setText("Ajoutez des produits via ADMIN");
            resultLabel.setText(DatabaseManager.getDebugInfo());
            priceField.setDisable(true);
            validateBtn.setDisable(true);
            stopTimer();
            return;
        }

        productNameLabel.setText(currentProduit.getNom());
        categoryLabel.setText(currentProduit.getCategorie());

        String imgPath = currentProduit.getImagePath();
        if (imgPath != null && !imgPath.isBlank()) {
            File imgFile = new File(imgPath);
            if (imgFile.exists()) {
                applyImageCover(new Image(imgFile.toURI().toString()));
            } else {
                productImageView.setImage(null);
                productImageView.setViewport(null);
            }
        } else {
            productImageView.setImage(null);
            productImageView.setViewport(null);
        }

        playFadeIn(productImageView);
    }

    @FXML
    public void handleValidate() {
        if (currentProduit == null) return;

        String input = priceField.getText().trim().replace(",", ".");
        double guess;
        try {
            guess = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            hintLabel.setText("Entrez un nombre valide !");
            playShake(priceField);
            return;
        }

        double price   = currentProduit.getPrix();
        double diff    = Math.abs(guess - price);
        double pct     = diff / price * 100.0;

        if (diff < 0.01) {
            stopTimer();

            int elapsedSec  = getElapsedSeconds();
            int attemptBonus = (MAX_ATTEMPTS - attempts) * (MAX_ATT_BONUS / MAX_ATTEMPTS);
            int timeBonus    = Math.max(0, MAX_TIME_BONUS - elapsedSec * 10);
            int bonus        = attemptBonus + timeBonus;
            score += bonus;

            updateHud();
            hintLabel.setText(
                "BRAVO ! C'était %.2f €  —  +%d pts  (tentatives +%d  |  temps +%d en %ds)"
                .formatted(price, bonus, attemptBonus, timeBonus, elapsedSec)
            );
            resultLabel.setText("");
            flashOverlay(true);
            playPulse(productNameLabel);

            priceField.setDisable(true);
            validateBtn.setDisable(true);

        } else {
            attempts++;
            updateHud();
            playShake(priceField);

            String direction = guess < price ? "▲ PLUS" : "▼ MOINS";
            String heat;
            if      (pct < 5)  heat = "BRÛLANT !";
            else if (pct < 15) heat = "CHAUD !";
            else if (pct < 30) heat = "TIÈDE";
            else               heat = "FROID !";

            hintLabel.setText(direction + "  —  " + heat);
            flashOverlay(false);

            if (attempts >= MAX_ATTEMPTS) {
                stopTimer();
                resultLabel.setText("Perdu ! Le prix était %.2f €".formatted(price));
                priceField.setDisable(true);
                validateBtn.setDisable(true);
            }
        }

        priceField.clear();
        priceField.requestFocus();
    }

    @FXML
    public void openAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/demo/teste/moreless/fxml/admin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Administration — Produits");
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------------
    // Image cover (remplit le cadre sans déformation)
    // ---------------------------------------------------------------

    private static final double FRAME_W = 550;
    private static final double FRAME_H = 320;

    private void applyImageCover(Image img) {
        double iw = img.getWidth();
        double ih = img.getHeight();
        if (iw == 0 || ih == 0) {
            productImageView.setImage(img);
            return;
        }
        // Scale minimum pour couvrir le cadre (cover)
        double scale = Math.max(FRAME_W / iw, FRAME_H / ih);

        // Dimensions de la portion de l'image originale à afficher
        double vpW = FRAME_W / scale;
        double vpH = FRAME_H / scale;

        // Centrage du crop dans l'image originale
        double vpX = (iw - vpW) / 2.0;
        double vpY = (ih - vpH) / 2.0;

        productImageView.setImage(img);
        productImageView.setViewport(new Rectangle2D(vpX, vpY, vpW, vpH));
        productImageView.setFitWidth(FRAME_W);
        productImageView.setFitHeight(FRAME_H);
        productImageView.setPreserveRatio(false);
        productImageView.setClip(null);
        productImageView.setTranslateX(0);
        productImageView.setTranslateY(0);
    }

    // ---------------------------------------------------------------
    // Timer
    // ---------------------------------------------------------------

    private void resetTimer() {
        stopTimer();
        startTimeMs = System.currentTimeMillis();
        timerLabel.setText("Temps : 0s");
        timerLabel.setStyle("");

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            int s = getElapsedSeconds();
            timerLabel.setText("Temps : " + s + "s");
            if      (s < 20) timerLabel.setStyle("-fx-text-fill: #00DD00;");
            else if (s < 50) timerLabel.setStyle("-fx-text-fill: #FFB800;");
            else             timerLabel.setStyle("-fx-text-fill: #FF4444;");
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    private int getElapsedSeconds() {
        return (int) ((System.currentTimeMillis() - startTimeMs) / 1000L);
    }

    // ---------------------------------------------------------------
    // HUD
    // ---------------------------------------------------------------

    private void updateHud() {
        scoreLabel.setText("Score : " + score);
        attemptsLabel.setText("Essais : " + (MAX_ATTEMPTS - attempts) + "/" + MAX_ATTEMPTS);
    }

    // ---------------------------------------------------------------
    // Animations
    // ---------------------------------------------------------------

    private void playFadeIn(javafx.scene.Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(600), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    private void playShake(javafx.scene.Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(60), node);
        tt.setFromX(-10);
        tt.setToX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.setOnFinished(e -> node.setTranslateX(0));
        tt.play();
    }

    private void playPulse(javafx.scene.Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(180), node);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.35);  st.setToY(1.35);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }

    private void flashOverlay(boolean success) {
        String color = success
            ? "-fx-background-color: rgba(0,220,0,0.45);"
            : "-fx-background-color: rgba(220,0,0,0.35);";
        screenOverlay.setStyle(color);
        FadeTransition ft = new FadeTransition(Duration.millis(500), screenOverlay);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setOnFinished(e -> screenOverlay.setStyle("-fx-background-color: transparent;"));
        ft.play();
    }
}
