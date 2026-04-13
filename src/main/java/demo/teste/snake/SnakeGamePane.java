package demo.teste.snake;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class SnakeGamePane extends StackPane {
    private static final int CELL_SIZE = 24;
    private static final int GRID_WIDTH = 26;
    private static final int GRID_HEIGHT = 22;
    private static final long TICK_NANOS = 140_000_000L;
    private static final int OBSTACLE_COUNT = 28;

    private final Canvas canvas = new Canvas(GRID_WIDTH * CELL_SIZE, GRID_HEIGHT * CELL_SIZE);
    private final GraphicsContext gc = canvas.getGraphicsContext2D();
    private final Deque<Cell> snake = new ArrayDeque<>();
    private final Set<Cell> obstacles = new HashSet<>();
    private final Random random = new Random();
    private final SnakeDifficulty difficulty;

    private final Image snakeHead = loadImage("/img/snake/snake_head.png");
    private final Image snakeBody = loadImage("/img/snake/snake_body.png");
    private final Image snakeTail = loadImage("/img/snake/snake_tail.png");
    private final Image apple = loadImage("/img/snake/snake_apple.png");
    private final Image border = loadImage("/img/snake/Snake_border.png");
    private final Image borderDownLeft = loadImage("/img/snake/Snake_border_down_left.png");
    private final Image borderDownRight = loadImage("/img/snake/Snake_border_down_right.png");
    private final Image borderUpLeft = loadImage("/img/snake/Snake_border_up_left.png");
    private final Image borderUpRight = loadImage("/img/snake/Snake_border_up_right.png");

    private Direction direction = Direction.RIGHT;
    private Direction nextDirection = Direction.RIGHT;
    private Cell food;
    private int score;
    private boolean gameOver;
    private long lastTick;

    private final AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (lastTick == 0L) {
                lastTick = now;
                draw();
                return;
            }
            if (now - lastTick >= TICK_NANOS) {
                tick();
                draw();
                lastTick = now;
            }
        }
    };

    public SnakeGamePane() {
        this(SnakeDifficulty.HARD);
    }

    public SnakeGamePane(SnakeDifficulty difficulty) {
        this.difficulty = difficulty;
        setFocusTraversable(true);
        getChildren().add(canvas);
        initGame();
    }

    public void bindScene(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKey);
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    private void initGame() {
        snake.clear();
        int centerX = GRID_WIDTH / 2;
        int centerY = GRID_HEIGHT / 2;
        snake.addFirst(new Cell(centerX, centerY));
        snake.addLast(new Cell(centerX - 1, centerY));
        snake.addLast(new Cell(centerX - 2, centerY));
        direction = Direction.RIGHT;
        nextDirection = Direction.RIGHT;
        score = 0;
        gameOver = false;
        lastTick = 0L;
        if (hasObstacles()) {
            initObstacles();
        } else {
            obstacles.clear();
        }
        food = spawnFood();
        draw();
    }

    private void tick() {
        if (gameOver) {
            return;
        }

        if (!isOpposite(direction, nextDirection)) {
            direction = nextDirection;
        }

        Cell head = snake.peekFirst();
        Cell next = move(head, direction);

        if (isWall(next) || isObstacle(next) || snake.contains(next)) {
            gameOver = true;
            return;
        }

        snake.addFirst(next);

        if (next.equals(food)) {
            score += 10;
            food = spawnFood();
        } else {
            snake.removeLast();
        }
    }

    private void handleKey(KeyEvent event) {
        KeyCode key = event.getCode();
        switch (key) {
            case UP, Z -> nextDirection = Direction.UP;
            case DOWN, S -> nextDirection = Direction.DOWN;
            case LEFT, Q -> nextDirection = Direction.LEFT;
            case RIGHT, D -> nextDirection = Direction.RIGHT;
            case R -> {
                if (gameOver) {
                    initGame();
                }
            }
            default -> {
            }
        }
    }

    private void draw() {
        gc.setFill(Color.web("#0f172a"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawGrid();
        drawWalls();
        if (hasObstacles()) {
            drawObstacles();
        }
        drawFood();
        drawSnake();
        drawHud();

        if (gameOver) {
            drawGameOver();
        }
    }

    private void drawGrid() {
        gc.setStroke(Color.web("#1e293b"));
        gc.setLineWidth(1);
        for (int x = 0; x <= GRID_WIDTH; x++) {
            double px = x * CELL_SIZE;
            gc.strokeLine(px, 0, px, GRID_HEIGHT * CELL_SIZE);
        }
        for (int y = 0; y <= GRID_HEIGHT; y++) {
            double py = y * CELL_SIZE;
            gc.strokeLine(0, py, GRID_WIDTH * CELL_SIZE, py);
        }
    }

    private void drawWalls() {
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                Cell cell = new Cell(x, y);
                if (!isWall(cell)) {
                    continue;
                }
                drawImageOrRect(border, cell, Color.web("#334155"));
            }
        }
    }

    private void drawFood() {
        drawImageOrRect(apple, food, Color.web("#ef4444"));
    }

    private void drawObstacles() {
        for (Cell obstacle : obstacles) {
            Image tile = getObstacleTile(obstacle);
            drawImageOrRect(tile, obstacle, Color.web("#64748b"));
        }
    }

    private void drawSnake() {
        List<Cell> segments = new ArrayList<>(snake);
        for (int i = 0; i < segments.size(); i++) {
            Cell cell = segments.get(i);
            if (i == 0) {
                drawImageOrRect(snakeHead, cell, Color.web("#22c55e"));
            } else if (i == segments.size() - 1) {
                drawImageOrRect(snakeTail, cell, Color.web("#15803d"));
            } else {
                drawImageOrRect(snakeBody, cell, Color.web("#16a34a"));
            }
        }
    }

    private void drawHud() {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("System", FontWeight.BOLD, 20));
        gc.fillText("Score: " + score, 12, 28);
        gc.setFont(Font.font("System", FontWeight.BOLD, 16));
        gc.fillText("Niveau: " + difficulty.getLabel(), 12, 50);
        gc.setFont(Font.font("System", 14));
        gc.fillText("Controles: Fleches/WASD - R pour rejouer", 12, canvas.getHeight() - 12);
    }

    private void drawGameOver() {
        gc.setFill(Color.rgb(15, 23, 42, 0.75));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 44));
        gc.fillText("Game Over", canvas.getWidth() / 2 - 125, canvas.getHeight() / 2 - 10);
        gc.setFont(Font.font("System", FontWeight.BOLD, 24));
        gc.fillText("Score final: " + score, canvas.getWidth() / 2 - 80, canvas.getHeight() / 2 + 28);
        gc.setFont(Font.font("System", 18));
        gc.fillText("Appuie sur R pour recommencer", canvas.getWidth() / 2 - 130, canvas.getHeight() / 2 + 60);
    }

    private void drawImageOrRect(Image image, Cell cell, Color fallback) {
        double x = cell.x * CELL_SIZE;
        double y = cell.y * CELL_SIZE;
        if (image != null) {
            gc.drawImage(image, x, y, CELL_SIZE, CELL_SIZE);
        } else {
            gc.setFill(fallback);
            gc.fillRoundRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4, 8, 8);
        }
    }

    private Cell move(Cell cell, Direction dir) {
        return switch (dir) {
            case UP -> new Cell(cell.x, cell.y - 1);
            case DOWN -> new Cell(cell.x, cell.y + 1);
            case LEFT -> new Cell(cell.x - 1, cell.y);
            case RIGHT -> new Cell(cell.x + 1, cell.y);
        };
    }

    private boolean isWall(Cell cell) {
        return cell.x <= 0 || cell.y <= 0 || cell.x >= GRID_WIDTH - 1 || cell.y >= GRID_HEIGHT - 1;
    }

    private Cell spawnFood() {
        Cell candidate;
        do {
            int x = 1 + random.nextInt(GRID_WIDTH - 2);
            int y = 1 + random.nextInt(GRID_HEIGHT - 2);
            candidate = new Cell(x, y);
        } while (snake.contains(candidate) || isObstacle(candidate));
        return candidate;
    }

    private void initObstacles() {
        obstacles.clear();
        Cell spawn = snake.peekFirst();
        while (obstacles.size() < OBSTACLE_COUNT) {
            int x = 1 + random.nextInt(GRID_WIDTH - 2);
            int y = 1 + random.nextInt(GRID_HEIGHT - 2);
            Cell candidate = new Cell(x, y);

            if (snake.contains(candidate)) {
                continue;
            }

            // Garde un peu d'espace libre autour du spawn pour eviter une mort immediate.
            if (Math.abs(candidate.x - spawn.x) + Math.abs(candidate.y - spawn.y) <= 4) {
                continue;
            }

            obstacles.add(candidate);
        }
    }

    private Image getObstacleTile(Cell cell) {
        Image[] tiles = {border, borderDownLeft, borderDownRight, borderUpLeft, borderUpRight};
        int start = Math.floorMod(cell.x * 31 + cell.y * 17, tiles.length);

        for (int i = 0; i < tiles.length; i++) {
            Image candidate = tiles[(start + i) % tiles.length];
            if (candidate != null) {
                return candidate;
            }
        }
        return border;
    }

    private boolean hasObstacles() {
        return difficulty == SnakeDifficulty.HARD;
    }

    private boolean isObstacle(Cell cell) {
        return hasObstacles() && obstacles.contains(cell);
    }

    private boolean isOpposite(Direction first, Direction second) {
        return (first == Direction.UP && second == Direction.DOWN)
                || (first == Direction.DOWN && second == Direction.UP)
                || (first == Direction.LEFT && second == Direction.RIGHT)
                || (first == Direction.RIGHT && second == Direction.LEFT);
    }

    private Image loadImage(String resourcePath) {
        try {
            return new Image(Objects.requireNonNull(getClass().getResourceAsStream(resourcePath)));
        } catch (Exception ignored) {
            return null;
        }
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private record Cell(int x, int y) {
    }
}

