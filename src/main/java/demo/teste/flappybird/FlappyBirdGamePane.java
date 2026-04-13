package demo.teste.flappybird;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class FlappyBirdGamePane extends StackPane {
    private static final int WIDTH = 480;
    private static final int HEIGHT = 640;
    private static final int BIRD_SIZE = 34;
    private static final double BIRD_X = 120.0;
    private static final double GRAVITY = 900.0;
    private static final double FLAP_VELOCITY = -320.0;
    private static final double PIPE_SPEED = 170.0;
    private static final double PIPE_SPAWN_INTERVAL = 1.45;
    private static final double PIPE_GAP_HEIGHT = 170.0;
    private static final double PIPE_MARGIN_TOP_BOTTOM = 90.0;
    private static final double PIPE_WIDTH = 84.0;
    private static final double PIPE_HITBOX_PAD = 4.0;

    private final Canvas canvas = new Canvas(WIDTH, HEIGHT);
    private final GraphicsContext gc = canvas.getGraphicsContext2D();
    private final Random random = new Random();
    private final List<PipePair> pipes = new ArrayList<>();

    private final Image background = loadImage("/img/flappybird/Bird_Background.jpg");
    private final Image birdFrame1 = loadImage("/img/flappybird/Bird_Frame_01.png");
    private final Image birdFrame2 = loadImage("/img/flappybird/Bird_Frame_02.png");
    private final Image birdFrame3 = loadImage("/img/flappybird/Bird_Frame_03.png");
    private final Image pipeDown = loadImage("/img/flappybird/Pipe_Down.png");
    private final Image pipeUp = loadImage("/img/flappybird/Pipe_Up.png");

    private double birdY;
    private double birdVelocity;
    private double birdRotation;
    private double spawnAccumulator;
    private double animationAccumulator;
    private int score;
    private boolean gameOver;
    private long lastFrameNanos;

    private final AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (lastFrameNanos == 0L) {
                lastFrameNanos = now;
                draw();
                return;
            }

            double dt = (now - lastFrameNanos) / 1_000_000_000.0;
            lastFrameNanos = now;
            update(Math.min(dt, 0.033));
            draw();
        }
    };

    public FlappyBirdGamePane() {
        setFocusTraversable(true);
        getChildren().add(canvas);
        initGame();
    }

    public void bindScene(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, this::handleMouseClicked);
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    private void initGame() {
        birdY = HEIGHT / 2.0 - BIRD_SIZE / 2.0;
        birdVelocity = 0.0;
        birdRotation = 0.0;
        spawnAccumulator = 0.0;
        animationAccumulator = 0.0;
        score = 0;
        gameOver = false;
        lastFrameNanos = 0L;
        pipes.clear();
        pipes.add(createPipe(WIDTH + 140.0));
        draw();
    }

    private void update(double dt) {
        if (gameOver) {
            return;
        }

        animationAccumulator += dt;
        birdVelocity += GRAVITY * dt;
        birdY += birdVelocity * dt;
        birdRotation = clamp(birdVelocity / 4.0, -25.0, 90.0);

        spawnAccumulator += dt;
        if (spawnAccumulator >= PIPE_SPAWN_INTERVAL) {
            spawnAccumulator -= PIPE_SPAWN_INTERVAL;
            pipes.add(createPipe(WIDTH + 40.0));
        }

        Rectangle2D birdBounds = new Rectangle2D(BIRD_X + 2.0, birdY + 2.0, BIRD_SIZE - 4.0, BIRD_SIZE - 4.0);

        Iterator<PipePair> iterator = pipes.iterator();
        while (iterator.hasNext()) {
            PipePair pipe = iterator.next();
            pipe.x -= PIPE_SPEED * dt;

            if (!pipe.scored && pipe.x + PIPE_WIDTH < BIRD_X) {
                pipe.scored = true;
                score++;
            }

            if (pipe.x + PIPE_WIDTH < -20.0) {
                iterator.remove();
                continue;
            }

            if (intersects(birdBounds, pipe)) {
                gameOver = true;
                return;
            }
        }

        if (birdY < 0.0 || birdY + BIRD_SIZE > HEIGHT) {
            gameOver = true;
        }
    }

    private void handleKeyPressed(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code == KeyCode.R && gameOver) {
            initGame();
            event.consume();
            return;
        }

        if (code == KeyCode.SPACE || code == KeyCode.UP || code == KeyCode.W) {
            triggerFlap();
            event.consume();
        }
    }

    private void handleMouseClicked(MouseEvent event) {
        triggerFlap();
        event.consume();
    }

    private void triggerFlap() {
        if (gameOver) {
            initGame();
        }
        birdVelocity = FLAP_VELOCITY;
    }

    private void draw() {
        drawBackground();
        drawPipes();
        drawBird();
        drawHud();

        if (gameOver) {
            drawGameOverOverlay();
        }
    }

    private void drawBackground() {
        if (background != null) {
            gc.drawImage(background, 0, 0, WIDTH, HEIGHT);
        } else {
            gc.setFill(Color.web("#87CEEB"));
            gc.fillRect(0, 0, WIDTH, HEIGHT);
        }

        gc.setFill(Color.rgb(255, 255, 255, 0.08));
        gc.fillRect(0, HEIGHT - 76, WIDTH, 76);
    }

    private void drawPipes() {
        for (PipePair pipe : pipes) {
            double topHeight = pipe.gapTop;
            double bottomY = pipe.gapTop + PIPE_GAP_HEIGHT;
            double bottomHeight = HEIGHT - bottomY;

            drawPipe(pipeDown, pipe.x, 0.0, PIPE_WIDTH, topHeight, false);
            drawPipe(pipeUp, pipe.x, bottomY, PIPE_WIDTH, bottomHeight, true);
        }
    }

    private void drawPipe(Image image, double x, double y, double width, double height, boolean fallbackFlip) {
        if (height <= 0.0) {
            return;
        }

        if (image != null) {
            gc.drawImage(image, x, y, width, height);
            return;
        }

        gc.setFill(Color.web("#2ecc71"));
        gc.fillRoundRect(x, y, width, height, 12, 12);
        gc.setFill(Color.web("#1e8449"));
        if (fallbackFlip) {
            gc.fillRect(x - 2, y, width + 4, 10);
        } else {
            gc.fillRect(x - 2, y + height - 10, width + 4, 10);
        }
    }

    private void drawBird() {
        Image frame = getBirdFrame();

        gc.save();
        gc.translate(BIRD_X + BIRD_SIZE / 2.0, birdY + BIRD_SIZE / 2.0);
        gc.rotate(birdRotation);

        if (frame != null) {
            gc.drawImage(frame, -BIRD_SIZE / 2.0, -BIRD_SIZE / 2.0, BIRD_SIZE, BIRD_SIZE);
        } else {
            gc.setFill(Color.web("#f1c40f"));
            gc.fillOval(-BIRD_SIZE / 2.0, -BIRD_SIZE / 2.0, BIRD_SIZE, BIRD_SIZE);
            gc.setFill(Color.web("#2c3e50"));
            gc.fillOval(4 - BIRD_SIZE / 2.0, -6 - BIRD_SIZE / 2.0, 5, 5);
        }

        gc.restore();
    }

    private Image getBirdFrame() {
        int frameIndex = ((int) (animationAccumulator / 0.1)) % 3;
        return switch (frameIndex) {
            case 0 -> birdFrame1;
            case 1 -> birdFrame2;
            default -> birdFrame3;
        };
    }

    private void drawHud() {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("System", FontWeight.BOLD, 26));
        gc.fillText("Score: " + score, 16, 34);

        gc.setFont(Font.font("System", FontWeight.BOLD, 16));
        gc.fillText("Espace / clic = voler", 16, HEIGHT - 18);
    }

    private void drawGameOverOverlay() {
        gc.setFill(Color.rgb(15, 23, 42, 0.72));
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 44));
        gc.fillText("Game Over", 118, 280);

        gc.setFont(Font.font("System", FontWeight.BOLD, 24));
        gc.fillText("Score final : " + score, 145, 324);

        gc.setFont(Font.font("System", 18));
        gc.fillText("Appuie sur R ou clique pour recommencer", 74, 365);
    }

    private PipePair createPipe(double x) {
        double available = Math.max(0.0, HEIGHT - (PIPE_MARGIN_TOP_BOTTOM * 2.0) - PIPE_GAP_HEIGHT);
        double gapTop = PIPE_MARGIN_TOP_BOTTOM + random.nextDouble() * available;
        return new PipePair(x, gapTop);
    }

    private boolean intersects(Rectangle2D birdBounds, PipePair pipe) {
        Rectangle2D topPipe = new Rectangle2D(pipe.x + PIPE_HITBOX_PAD, 0.0, PIPE_WIDTH - PIPE_HITBOX_PAD * 2.0, pipe.gapTop);
        Rectangle2D bottomPipe = new Rectangle2D(
                pipe.x + PIPE_HITBOX_PAD,
                pipe.gapTop + PIPE_GAP_HEIGHT,
                PIPE_WIDTH - PIPE_HITBOX_PAD * 2.0,
                HEIGHT - (pipe.gapTop + PIPE_GAP_HEIGHT)
        );
        return birdBounds.intersects(topPipe) || birdBounds.intersects(bottomPipe);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private Image loadImage(String resourcePath) {
        try {
            return new Image(Objects.requireNonNull(getClass().getResourceAsStream(resourcePath)));
        } catch (Exception ignored) {
            return null;
        }
    }

    private static final class PipePair {
        private double x;
        private final double gapTop;
        private boolean scored;

        private PipePair(double x, double gapTop) {
            this.x = x;
            this.gapTop = gapTop;
        }
    }
}


