package demo.teste.score;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class HighScoreService {
    private static final String DB_FILE = "scores.db";
    private static final String APP_DIR = "data";
    private static final String DB_PATH_PROPERTY = "java.minigames.db.path";

    private static final String SQL_CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS best_scores (
                game_key TEXT PRIMARY KEY,
                score INTEGER NOT NULL,
                updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """;

    private static final String SQL_MIGRATE_OLD_TABLE = """
            INSERT INTO best_scores(game_key, score)
            SELECT game_key, MAX(score)
            FROM high_scores
            GROUP BY game_key
            ON CONFLICT(game_key) DO UPDATE SET
                score = MAX(best_scores.score, excluded.score),
                updated_at = CURRENT_TIMESTAMP
            """;

    private static final String SQL_DROP_OLD_TABLE = "DROP TABLE IF EXISTS high_scores";

    private static final String SQL_UPSERT_BEST = """
            INSERT INTO best_scores(game_key, score)
            VALUES (?, ?)
            ON CONFLICT(game_key) DO UPDATE SET
                score = MAX(best_scores.score, excluded.score),
                updated_at = CURRENT_TIMESTAMP
            """;

    private static final String SQL_BEST = "SELECT COALESCE(score, 0) FROM best_scores WHERE game_key = ?";

    private static volatile boolean initialized;
    private static volatile boolean driverLoaded;

    private HighScoreService() {
    }

    public static void saveScore(String gameKey, int score) {
        if (score < 0 || gameKey == null || gameKey.isBlank()) {
            return;
        }

        if (!ensureInitialized()) {
            return;
        }

        try (Connection connection = openConnection();
             PreparedStatement upsertStatement = connection.prepareStatement(SQL_UPSERT_BEST)) {
            upsertStatement.setString(1, gameKey);
            upsertStatement.setInt(2, score);
            upsertStatement.executeUpdate();
        } catch (SQLException exception) {
            System.err.println("[HighScoreService] Erreur sauvegarde score: " + exception.getMessage());
        }
    }

    public static int getBestScore(String gameKey) {
        if (gameKey == null || gameKey.isBlank()) {
            return 0;
        }

        if (!ensureInitialized()) {
            return 0;
        }

        try (Connection connection = openConnection();
             PreparedStatement bestStatement = connection.prepareStatement(SQL_BEST)) {
            bestStatement.setString(1, gameKey);
            try (ResultSet resultSet = bestStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException exception) {
            System.err.println("[HighScoreService] Erreur lecture score: " + exception.getMessage());
        }

        return 0;
    }

    private static synchronized boolean ensureInitialized() {
        if (initialized) {
            return true;
        }

        if (!ensureDriverLoaded()) {
            return false;
        }

        try {
            Files.createDirectories(getDatabasePath().getParent());
        } catch (IOException exception) {
            System.err.println("[HighScoreService] Impossible de creer le dossier de DB: " + exception.getMessage());
            return false;
        }

        try (Connection connection = openConnection();
             PreparedStatement createTableStatement = connection.prepareStatement(SQL_CREATE_TABLE)) {
            createTableStatement.execute();
            migrateLegacyData(connection);
            initialized = true;
            return true;
        } catch (SQLException exception) {
            System.err.println("[HighScoreService] Erreur initialisation DB: " + exception.getMessage());
            return false;
        }
    }

    private static boolean ensureDriverLoaded() {
        if (driverLoaded) {
            return true;
        }

        try {
            Class.forName("org.sqlite.JDBC");
            driverLoaded = true;
            return true;
        } catch (ClassNotFoundException exception) {
            System.err.println("[HighScoreService] Driver SQLite introuvable. Verifie la dependance sqlite-jdbc.");
            return false;
        }
    }

    private static void migrateLegacyData(Connection connection) {
        try (PreparedStatement migrationStatement = connection.prepareStatement(SQL_MIGRATE_OLD_TABLE);
             PreparedStatement dropLegacyStatement = connection.prepareStatement(SQL_DROP_OLD_TABLE)) {
            migrationStatement.executeUpdate();
            dropLegacyStatement.execute();
        } catch (SQLException ignored) {
            // Ancien schema absent: rien a migrer.
        }
    }

    private static Connection openConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + getDatabasePath());
    }

    private static Path getDatabasePath() {
        String configuredPath = System.getProperty(DB_PATH_PROPERTY, "").trim();
        if (!configuredPath.isEmpty()) {
            return Path.of(configuredPath);
        }

        String workingDirectory = System.getProperty("user.dir", ".");
        return Path.of(workingDirectory, APP_DIR, DB_FILE);
    }
}

