package demo.teste.snake;

public enum SnakeDifficulty {
    EASY("Facile"),
    HARD("Difficile");

    private final String label;

    SnakeDifficulty(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}

