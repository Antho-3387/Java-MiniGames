package demo.teste.roadrush;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RoadRushScoreManager {
    private static final Path SCORE_FILE = Path.of("data", "roadrush-best-score.txt");

    public int chargerMeilleurScore() {
        try {
            if (!Files.exists(SCORE_FILE)) {
                return 0;
            }

            String content = Files.readString(SCORE_FILE).trim();
            if (content.isEmpty()) {
                return 0;
            }

            return Integer.parseInt(content);
        } catch (Exception exception) {
            // Si le fichier est corrompu ou illisible, on repart de 0.
            return 0;
        }
    }

    public void sauvegarderMeilleurScore(int score) {
        if (score < 0) {
            return;
        }

        int meilleurScoreActuel = chargerMeilleurScore();
        if (score <= meilleurScoreActuel) {
            return;
        }

        try {
            Files.createDirectories(SCORE_FILE.getParent());
            Files.writeString(SCORE_FILE, String.valueOf(score));
        } catch (IOException exception) {
            System.err.println("[RoadRushScoreManager] Impossible de sauvegarder le record: " + exception.getMessage());
        }
    }
}

