package demo.teste.trueorfalse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TrueOrFalseQuestionRepository {
    private static final Path DB_PATH = Path.of("data", "trueorfalse.db");

    private static final String SQL_CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS questions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                texte TEXT NOT NULL,
                reponse INTEGER NOT NULL CHECK (reponse IN (0, 1))
            )
            """;

    private static final String SQL_COUNT = "SELECT COUNT(*) FROM questions";
    private static final String SQL_INSERT = "INSERT INTO questions(texte, reponse) VALUES (?, ?)";
    private static final String SQL_SELECT_ALL = "SELECT texte, reponse FROM questions ORDER BY id";

    private static volatile boolean initialized;
    private static volatile boolean driverLoaded;

    public List<QuestionData> loadQuestions() {
        if (!ensureInitialized()) {
            return defaultQuestions();
        }

        List<QuestionData> loaded = new ArrayList<>();
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String texte = resultSet.getString("texte");
                boolean reponse = resultSet.getInt("reponse") == 1;
                loaded.add(new QuestionData(texte, reponse));
            }
        } catch (SQLException exception) {
            System.err.println("[TrueOrFalseQuestionRepository] Lecture DB impossible: " + exception.getMessage());
            return defaultQuestions();
        }

        return loaded.isEmpty() ? defaultQuestions() : loaded;
    }

    private synchronized boolean ensureInitialized() {
        if (initialized) {
            return true;
        }
        if (!ensureDriverLoaded()) {
            return false;
        }

        try {
            Files.createDirectories(DB_PATH.getParent());
        } catch (Exception exception) {
            System.err.println("[TrueOrFalseQuestionRepository] Creation dossier impossible: " + exception.getMessage());
            return false;
        }

        try (Connection connection = openConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(SQL_CREATE_TABLE);
            seedIfEmpty(connection);
            initialized = true;
            return true;
        } catch (SQLException exception) {
            System.err.println("[TrueOrFalseQuestionRepository] Initialisation DB impossible: " + exception.getMessage());
            return false;
        }
    }

    private boolean ensureDriverLoaded() {
        if (driverLoaded) {
            return true;
        }

        try {
            Class.forName("org.sqlite.JDBC");
            driverLoaded = true;
            return true;
        } catch (ClassNotFoundException exception) {
            System.err.println("[TrueOrFalseQuestionRepository] Driver SQLite introuvable.");
            return false;
        }
    }

    private void seedIfEmpty(Connection connection) throws SQLException {
        try (PreparedStatement countStatement = connection.prepareStatement(SQL_COUNT);
             ResultSet resultSet = countStatement.executeQuery()) {
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return;
            }
        }

        List<QuestionData> defaults = defaultQuestions();
        try (PreparedStatement insertStatement = connection.prepareStatement(SQL_INSERT)) {
            for (QuestionData question : defaults) {
                insertStatement.setString(1, question.texte());
                insertStatement.setInt(2, question.reponse() ? 1 : 0);
                insertStatement.addBatch();
            }
            insertStatement.executeBatch();
        }
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
    }

    private List<QuestionData> defaultQuestions() {
        List<QuestionData> defaults = new ArrayList<>();
        defaults.add(new QuestionData("La Terre est plate", false));
        defaults.add(new QuestionData("2 + 2 = 4", true));
        defaults.add(new QuestionData("Java est un langage compile", true));
        defaults.add(new QuestionData("Un PC peut fonctionner sans RAM", false));
        defaults.add(new QuestionData("Le ciel est bleu", true));
        defaults.add(new QuestionData("Un carre a 5 cotes", false));
        defaults.add(new QuestionData("Le JavaFX sert a faire des interfaces", true));
        defaults.add(new QuestionData("Un poisson peut respirer hors de l'eau", false));
        defaults.add(new QuestionData("Emery va nous donner + 1 point", true));
        return defaults;
    }

    public record QuestionData(String texte, boolean reponse) {
    }
}

