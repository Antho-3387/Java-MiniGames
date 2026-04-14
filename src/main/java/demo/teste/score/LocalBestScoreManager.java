package demo.teste.score;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LocalBestScoreManager {
    private final Path scoreFile;
    private final String managerName;

    public LocalBestScoreManager(String fileName, String managerName) {
        this.scoreFile = Path.of("data", fileName);
        this.managerName = managerName;
    }

    public int chargerMeilleurScore() {
        try {
            if (!Files.exists(scoreFile)) {
                return 0;
            }

            String content = Files.readString(scoreFile).trim();
            if (content.isEmpty()) {
                return 0;
            }

            return Integer.parseInt(content);
        } catch (Exception exception) {
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
            Files.createDirectories(scoreFile.getParent());
            Files.writeString(scoreFile, String.valueOf(score));
        } catch (IOException exception) {
            System.err.println("[" + managerName + "] Impossible de sauvegarder le record: " + exception.getMessage());
        }
    }
}

