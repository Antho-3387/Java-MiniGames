package demo.teste.roadrush;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RoadRushGamePane extends StackPane {
    private static final int WIDTH = 640;
    private static final int HEIGHT = 420;
    private static final int LANE_COUNT = 3;

    private static final double ROAD_LEFT = 120.0;
    private static final double ROAD_RIGHT = WIDTH - 120.0;
    private static final double ROAD_WIDTH = ROAD_RIGHT - ROAD_LEFT;
    private static final double LANE_WIDTH = ROAD_WIDTH / LANE_COUNT;

    private static final double PLAYER_WIDTH = 58.0;
    private static final double PLAYER_HEIGHT = 88.0;
    private static final double PLAYER_Y = HEIGHT - 110.0;

    private static final double BASE_SCROLL_SPEED = 220.0;
    private static final double MAX_SCROLL_SPEED = 520.0;
    private static final double SPEED_RAMP_PER_SEC = 7.5;
    private static final double BASE_SPAWN_INTERVAL = 1.0;
    private static final double MIN_SPAWN_INTERVAL = 0.34;

    private final Canvas canvas = new Canvas(WIDTH, HEIGHT);
    private final GraphicsContext gc = canvas.getGraphicsContext2D();
    private final Random random = new Random();
    private final RoadRushScoreManager scoreManager = new RoadRushScoreManager();

    private final Image playerNeutral = loadImage("/img/cars/Cars_Position_Neutre.png");
    private final Image playerLeft = loadImage("/img/cars/Cars_Position_Left.png");
    private final Image playerRight = loadImage("/img/cars/Cars_Position_Right.png");
    private final Image enemy1 = loadImage("/img/cars/Cars_1.png");
    private final Image enemy2 = loadImage("/img/cars/Cars_2.png");
    private final Image enemy3 = loadImage("/img/cars/Cars_3.png");

    private final List<SpawnedObject> objects = new ArrayList<>();

    private int playerLane;
    private LanePose lanePose = LanePose.NEUTRAL;
    private double poseTimeLeft;

    private double scrollSpeed;
    private double roadStripeOffset;
    private double spawnCooldown;
    private double elapsedSeconds;
    private int score;
    private int bestScore;
    private boolean gameOver;
    private boolean scoreSaved;
    private long lastFrameNanos;

    private final AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (lastFrameNanos == 0L) {
                lastFrameNanos = now;
                draw();
                return;
            }

            double dt = Math.min((now - lastFrameNanos) / 1_000_000_000.0, 0.033);
            lastFrameNanos = now;

            update(dt);
            draw();
        }
    };

    public RoadRushGamePane() {
        setFocusTraversable(true);
        getChildren().add(canvas);
        initGame();
    }

    public void bindScene(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::onKeyPressed);
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    private void initGame() {
        objects.clear();
        playerLane = 1;
        lanePose = LanePose.NEUTRAL;
        poseTimeLeft = 0.0;
        scrollSpeed = BASE_SCROLL_SPEED;
        roadStripeOffset = 0.0;
        spawnCooldown = 0.8;
        elapsedSeconds = 0.0;
        score = 0;
        bestScore = scoreManager.chargerMeilleurScore();
        gameOver = false;
        scoreSaved = false;
        lastFrameNanos = 0L;
        draw();
    }

    private void update(double dt) {
        if (gameOver) {
            return;
        }

        elapsedSeconds += dt;
        scrollSpeed = Math.min(MAX_SCROLL_SPEED, scrollSpeed + SPEED_RAMP_PER_SEC * dt);
        score = (int) (elapsedSeconds * 100.0);

        roadStripeOffset += scrollSpeed * dt;
        if (roadStripeOffset >= 80.0) {
            roadStripeOffset -= 80.0;
        }

        if (poseTimeLeft > 0.0) {
            poseTimeLeft -= dt;
            if (poseTimeLeft <= 0.0) {
                lanePose = LanePose.NEUTRAL;
            }
        }

        double currentSpawnInterval = Math.max(MIN_SPAWN_INTERVAL, BASE_SPAWN_INTERVAL - elapsedSeconds * 0.025);
        spawnCooldown -= dt;
        if (spawnCooldown <= 0.0) {
            spawnObject();
            spawnCooldown += currentSpawnInterval;
        }

        Rectangle2D playerBounds = getPlayerBounds();

        Iterator<SpawnedObject> iterator = objects.iterator();
        while (iterator.hasNext()) {
            SpawnedObject object = iterator.next();
            object.y += scrollSpeed * object.speedFactor * dt;

            if (object.y > HEIGHT + 120.0) {
                iterator.remove();
                continue;
            }

            if (playerBounds.intersects(object.getBounds())) {
                onGameOver();
                return;
            }
        }
    }

    private void spawnObject() {
        int lane = random.nextInt(LANE_COUNT);

        if (objects.stream().anyMatch(obj -> obj.lane == lane && obj.y < 140.0)) {
            return;
        }

        ObjectType type = random.nextDouble() < 0.72 ? ObjectType.ENEMY_CAR : ObjectType.OBSTACLE;
        double width = type == ObjectType.ENEMY_CAR ? 56.0 : 50.0;
        double height = type == ObjectType.ENEMY_CAR ? 84.0 : 56.0;
        double speedFactor = type == ObjectType.ENEMY_CAR ? (0.95 + random.nextDouble() * 0.35) : (1.05 + random.nextDouble() * 0.4);
        Image sprite = type == ObjectType.ENEMY_CAR ? pickEnemySprite() : null;

        double x = laneToX(lane, width);
        double y = -height - random.nextDouble() * 160.0;
        objects.add(new SpawnedObject(type, lane, x, y, width, height, speedFactor, sprite));
    }

    private Image pickEnemySprite() {
        int idx = random.nextInt(3);
        return switch (idx) {
            case 0 -> enemy1;
            case 1 -> enemy2;
            default -> enemy3;
        };
    }

    private void onKeyPressed(KeyEvent event) {
        KeyCode code = event.getCode();

        if (code == KeyCode.R && gameOver) {
            initGame();
            event.consume();
            return;
        }

        if (gameOver) {
            return;
        }

        if (code == KeyCode.LEFT || code == KeyCode.Q || code == KeyCode.A) {
            if (playerLane > 0) {
                playerLane--;
                lanePose = LanePose.LEFT;
                poseTimeLeft = 0.12;
            }
            event.consume();
        } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
            if (playerLane < LANE_COUNT - 1) {
                playerLane++;
                lanePose = LanePose.RIGHT;
                poseTimeLeft = 0.12;
            }
            event.consume();
        }
    }

    private void draw() {
        drawBackground();
        drawRoad();
        drawObjects();
        drawPlayer();
        drawHud();

        if (gameOver) {
            drawGameOverOverlay();
        }
    }

    private void drawBackground() {
        gc.setFill(Color.web("#0f172a"));
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        gc.setFill(Color.web("#1e3a8a"));
        gc.fillRect(0, 0, ROAD_LEFT, HEIGHT);

        gc.setFill(Color.web("#1e3a8a"));
        gc.fillRect(ROAD_RIGHT, 0, WIDTH - ROAD_RIGHT, HEIGHT);
    }

    private void drawRoad() {
        gc.setFill(Color.web("#1f2937"));
        gc.fillRect(ROAD_LEFT, 0, ROAD_WIDTH, HEIGHT);

        gc.setStroke(Color.web("#f8fafc"));
        gc.setLineWidth(5.0);
        gc.strokeLine(ROAD_LEFT + 2, 0, ROAD_LEFT + 2, HEIGHT);
        gc.strokeLine(ROAD_RIGHT - 2, 0, ROAD_RIGHT - 2, HEIGHT);

        gc.setStroke(Color.web("#e2e8f0"));
        gc.setLineWidth(3.0);
        for (int divider = 1; divider < LANE_COUNT; divider++) {
            double x = ROAD_LEFT + divider * LANE_WIDTH;
            for (double y = -80 + roadStripeOffset; y < HEIGHT + 80; y += 80) {
                gc.strokeLine(x, y, x, y + 42);
            }
        }
    }

    private void drawObjects() {
        for (SpawnedObject object : objects) {
            if (object.type == ObjectType.ENEMY_CAR && object.sprite != null) {
                gc.drawImage(object.sprite, object.x, object.y, object.width, object.height);
                continue;
            }

            if (object.type == ObjectType.ENEMY_CAR) {
                gc.setFill(Color.web("#ef4444"));
                gc.fillRoundRect(object.x, object.y, object.width, object.height, 12, 12);
                gc.setFill(Color.web("#991b1b"));
                gc.fillRect(object.x + 8, object.y + 10, object.width - 16, 16);
            } else {
                gc.setFill(Color.web("#f59e0b"));
                gc.fillRoundRect(object.x, object.y, object.width, object.height, 10, 10);
                gc.setFill(Color.web("#b45309"));
                gc.fillRect(object.x + 5, object.y + object.height * 0.55, object.width - 10, 10);
            }
        }
    }

    private void drawPlayer() {
        Image sprite = switch (lanePose) {
            case LEFT -> playerLeft != null ? playerLeft : playerNeutral;
            case RIGHT -> playerRight != null ? playerRight : playerNeutral;
            case NEUTRAL -> playerNeutral;
        };

        double x = laneToX(playerLane, PLAYER_WIDTH);
        if (sprite != null) {
            gc.drawImage(sprite, x, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT);
            return;
        }

        gc.setFill(Color.web("#22c55e"));
        gc.fillRoundRect(x, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT, 12, 12);
        gc.setFill(Color.web("#166534"));
        gc.fillRect(x + 8, PLAYER_Y + 12, PLAYER_WIDTH - 16, 18);
    }

    private void drawHud() {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("System", FontWeight.BOLD, 24));
        gc.fillText("Score: " + score, 16, 34);

        gc.setFont(Font.font("System", FontWeight.BOLD, 16));
        gc.fillText("Survie: " + String.format("%.1f", elapsedSeconds) + " s", 16, 58);
        gc.fillText("Record: " + bestScore, 16, 80);

        gc.setFont(Font.font("System", 14));
        gc.fillText("Controles: Gauche/Droite (ou Q/D) - R pour rejouer", 16, HEIGHT - 14);
    }

    private void onGameOver() {
        gameOver = true;
        if (scoreSaved) {
            return;
        }
        scoreSaved = true;
        scoreManager.sauvegarderMeilleurScore(score);
        bestScore = scoreManager.chargerMeilleurScore();
    }

    private void drawGameOverOverlay() {
        gc.setFill(Color.rgb(2, 6, 23, 0.78));
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 48));
        gc.fillText("Crash !", WIDTH / 2.0 - 86, HEIGHT / 2.0 - 22);

        gc.setFont(Font.font("System", FontWeight.BOLD, 24));
        gc.fillText("Score final: " + score, WIDTH / 2.0 - 92, HEIGHT / 2.0 + 18);

        gc.setFont(Font.font("System", 18));
        gc.fillText("Appuie sur R pour recommencer", WIDTH / 2.0 - 138, HEIGHT / 2.0 + 52);
    }

    private Rectangle2D getPlayerBounds() {
        double x = laneToX(playerLane, PLAYER_WIDTH);
        return new Rectangle2D(x + 7, PLAYER_Y + 6, PLAYER_WIDTH - 14, PLAYER_HEIGHT - 12);
    }

    private double laneToX(int lane, double objectWidth) {
        return ROAD_LEFT + lane * LANE_WIDTH + (LANE_WIDTH - objectWidth) / 2.0;
    }

    private Image loadImage(String resourcePath) {
        try {
            return new Image(Objects.requireNonNull(getClass().getResourceAsStream(resourcePath)));
        } catch (Exception ignored) {
            return null;
        }
    }

    private enum LanePose {
        LEFT,
        RIGHT,
        NEUTRAL
    }

    private enum ObjectType {
        ENEMY_CAR,
        OBSTACLE
    }

    private static final class SpawnedObject {
        private final ObjectType type;
        private final int lane;
        private final double x;
        private double y;
        private final double width;
        private final double height;
        private final double speedFactor;
        private final Image sprite;

        private SpawnedObject(ObjectType type, int lane, double x, double y, double width, double height,
                              double speedFactor, Image sprite) {
            this.type = type;
            this.lane = lane;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.speedFactor = speedFactor;
            this.sprite = sprite;
        }

        private Rectangle2D getBounds() {
            return new Rectangle2D(x + 6, y + 6, width - 12, height - 12);
        }
    }
}

