package uet.oop.spaceshootergamejavafx; // Assuming this is the correct package for main game

// All entity imports should point to your .entities package
import uet.oop.spaceshootergamejavafx.entities.*;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node; // For Node type check in restartGame
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView; // ADDED IMPORT
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
// import javafx.scene.paint.CycleMethod; // Unused
// import javafx.scene.paint.LinearGradient; // Unused
// import javafx.scene.paint.Stop; // Unused
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

// import javafx.animation.TranslateTransition; // Unused in provided code
// import javafx.util.Duration; // Unused in provided code


public class SpaceShooter extends Application {

    // Adjusted slightly for a more typical menu aspect, can be reverted
    public static final int SCREEN_WIDTH = 510;
    public static final int SCREEN_HEIGHT = 650;
    public static int numLivesInitial = 3;

    private int score;
    private int lives;
    private boolean bossExists;
    private boolean gameRunning;
    private boolean gameOver;
    private int currentLevel = 1;
    private int scoreForNextLevel = 500;
    private int scoreForBoss = 1000;

    private Label scoreLabel;
    private Label livesLabel;
    private Label levelLabel;
    private Label tempMessageLabel;
    private AnimationTimer tempMessageTimer;
    private VBox uiOverlay;

    private List<GameObject> gameObjects;
    private Player player;
    private BossEnemy currentBoss;

    private Pane gameRoot;
    private Canvas gameCanvas;
    private GraphicsContext gc;
    private Scene gameScene;
    private Scene menuScene;
    private Stage primaryStage;

    private Random random = new Random();

    private long lastEnemySpawnTime;
    private long enemySpawnCooldown = 1500;
    private long lastPowerUpSpawnTime;
    private long powerUpSpawnCooldown = 8000;

    // --- UI Elements for the new game action menu ---
    private Button threeDotsButton;
    private VBox gameActionMenu; // The dropdown menu
    private Button pausePlayButton;
    private ImageView pausePlayImageView;
    private Button replayGameButton;
    private Button quitToMenuButton;

    // --- Images for the menu ---
    private Image imgThreeDots;
    private Image imgPause;
    private Image imgPlay;
    private Image imgReplay;
    private Image imgQuit;

    // private boolean isGamePausedForMenu = false; // To track if pause was initiated by menu



    public static void main(String[] args) {
        launch(args);
    }

    private String getResourcePath(String fileName) {
        // Assuming entities/res is directly under uet/oop/spaceshootergamejavafx package folder in classpath
        return "/uet/oop/spaceshootergamejavafx/entities/res/" + fileName;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Space Shooter");

        String iconPath = getResourcePath("game_icon.png");
        try {
            Image appIcon = new Image(getClass().getResourceAsStream(iconPath));
            if (appIcon != null && !appIcon.isError()) {
                primaryStage.getIcons().add(appIcon);
            } else {
                System.err.println("Could not load application icon: " + iconPath);
            }
        } catch (Exception e) {
            System.err.println("Exception loading application icon: " + e.getMessage());
        }

        // --- Load images for the game action menu ---
        try {
            imgThreeDots = new Image(getClass().getResourceAsStream(getResourcePath("Three_dots.png")));
            imgPause = new Image(getClass().getResourceAsStream(getResourcePath("Pause_game.png")));
            imgPlay = new Image(getClass().getResourceAsStream(getResourcePath("Play_game.png")));
            imgReplay = new Image(getClass().getResourceAsStream(getResourcePath("Replay_game.png")));
            imgQuit = new Image(getClass().getResourceAsStream(getResourcePath("Quit_game.png")));

            if (imgThreeDots == null || imgThreeDots.isError() ||
                    imgPause == null || imgPause.isError() ||
                    imgPlay == null || imgPlay.isError() ||
                    imgReplay == null || imgReplay.isError() ||
                    imgQuit == null || imgQuit.isError()) {
                System.err.println("Error loading one or more game action menu icons. Menu might not work correctly.");
            }
        } catch (Exception e) {
            System.err.println("Exception loading game action menu icons: " + e.getMessage());
        }


        gameRoot = new Pane();
        gameCanvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        gc = gameCanvas.getGraphicsContext2D();
        gameRoot.getChildren().add(gameCanvas);

        scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        scoreLabel.setTextFill(Color.LIGHTGREEN);

        livesLabel = new Label("Lives: " + numLivesInitial);
        livesLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        livesLabel.setTextFill(Color.TOMATO);

        levelLabel = new Label("Level: 1");
        levelLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        levelLabel.setTextFill(Color.CYAN);

        threeDotsButton = new Button();
        if (imgThreeDots != null && !imgThreeDots.isError()) {
            ImageView threeDotsImageView = new ImageView(imgThreeDots);
            threeDotsImageView.setFitWidth(40); // Kích thước ảnh
            threeDotsImageView.setFitHeight(40);
            threeDotsButton.setGraphic(threeDotsImageView);
        } else {
            threeDotsButton.setText(":::");
        }
// QUAN TRỌNG: Giảm padding của nút ba chấm xuống mức tối thiểu
        threeDotsButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;"); // Padding 0
        threeDotsButton.setFocusTraversable(false);
// Hành động sẽ được định nghĩa sau


        // --- Create Game Action Menu Elements ---
        // ... (threeDotsButton creation remains the same) ...

        // --- Create Game Action Menu Elements ---
        // ... (threeDotsButton creation remains the same) ...

        gameActionMenu = new VBox(5); // Tăng khoảng cách dọc giữa các nút thành 5px
        gameActionMenu.setAlignment(Pos.CENTER); // Căn giữa các nút bên trong VBox theo chiều ngang

        // Style cho gameActionMenu để có hình viên thuốc (pill-shape)
        // Các nút bên trong (ImageViews) có kích thước 40x40.
        // Padding 6px mỗi bên -> chiều rộng VBox khoảng 40 + 2*6 = 52px.
        // background-radius khoảng 26px (nửa chiều rộng) sẽ tạo hình viên thuốc.
        gameActionMenu.setStyle("-fx-background-color: rgba(30, 30, 60, 0.9); -fx-padding: 3px; -fx-background-radius: 5px; -fx-border-color: rgba(150,150,200,0.7); -fx-border-radius: 5px; -fx-border-width: 1px;");
        gameActionMenu.setVisible(false); // Ban đầu ẩn

        // ... (pausePlayButton, replayGameButton, quitToMenuButton creation remains the same) ...



        pausePlayImageView = new ImageView(); // Image will be set based on game state
        if (imgPause != null && !imgPause.isError()) {
            pausePlayImageView.setImage(imgPause); // Default to Pause (implies game is running or ready to run)
        }
        pausePlayImageView.setFitWidth(40);
        pausePlayImageView.setFitHeight(40);
        pausePlayButton = new Button();
        pausePlayButton.setGraphic(pausePlayImageView);
        pausePlayButton.setStyle("-fx-background-color: transparent; -fx-padding: 1;");
        pausePlayButton.setFocusTraversable(false);

        replayGameButton = new Button();
        if (imgReplay != null && !imgReplay.isError()) {
            ImageView replayImageView = new ImageView(imgReplay);
            replayImageView.setFitWidth(40);
            replayImageView.setFitHeight(40);
            replayGameButton.setGraphic(replayImageView);
        } else {
            replayGameButton.setText("R"); // Fallback
        }
        replayGameButton.setStyle("-fx-background-color: transparent; -fx-padding: 1;");
        replayGameButton.setFocusTraversable(false);

        quitToMenuButton = new Button();
        if (imgQuit != null && !imgQuit.isError()) {
            ImageView quitImageView = new ImageView(imgQuit);
            quitImageView.setFitWidth(40);
            quitImageView.setFitHeight(40);
            quitToMenuButton.setGraphic(quitImageView);
        } else {
            quitToMenuButton.setText("Q"); // Fallback
        }
        quitToMenuButton.setStyle("-fx-background-color: transparent; -fx-padding: 1;");
        quitToMenuButton.setFocusTraversable(false);

        gameActionMenu.getChildren().addAll(pausePlayButton, replayGameButton, quitToMenuButton);
        // Add to gameRoot so it can overlay other elements and be positioned absolutely
        gameRoot.getChildren().add(gameActionMenu);


        // --- UI Layout Setup ---
        tempMessageLabel = new Label("");
        tempMessageLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        tempMessageLabel.setTextFill(Color.YELLOW);
        tempMessageLabel.setAlignment(Pos.CENTER);
        tempMessageLabel.setTextAlignment(TextAlignment.CENTER);
        tempMessageLabel.setMinWidth(SCREEN_WIDTH);

        // Group scoreLabel and threeDotsButton vertically
        VBox scoreArea = new VBox(0); // Spacing between score and three-dots button
        scoreArea.setAlignment(Pos.TOP_LEFT); // Align content to top-left
        scoreArea.getChildren().addAll(scoreLabel, threeDotsButton);

        HBox topBar = new HBox(20, scoreArea, levelLabel, livesLabel);
        topBar.setPadding(new Insets(10));
        // THAY ĐỔI DÒNG TIẾP THEO:
        topBar.setAlignment(Pos.BASELINE_LEFT); // Căn chỉnh các con theo đường cơ sở văn bản của chúng

        HBox.setHgrow(scoreArea, Priority.ALWAYS);
        HBox.setHgrow(levelLabel, Priority.ALWAYS);
        HBox.setHgrow(livesLabel, Priority.ALWAYS);
        scoreArea.setMaxWidth(Double.MAX_VALUE); // VBox containing score and menu button
        levelLabel.setMaxWidth(Double.MAX_VALUE);
        livesLabel.setMaxWidth(Double.MAX_VALUE);

        // scoreLabel itself should still be left aligned within its part of scoreArea
        scoreLabel.setAlignment(Pos.CENTER_LEFT);
        levelLabel.setAlignment(Pos.CENTER);
        livesLabel.setAlignment(Pos.CENTER_RIGHT);

        VBox centerMessageLayout = new VBox(tempMessageLabel);
        centerMessageLayout.setAlignment(Pos.CENTER);
        centerMessageLayout.setPrefHeight(SCREEN_HEIGHT);
        centerMessageLayout.setMouseTransparent(true);

        uiOverlay = new VBox(topBar, centerMessageLayout);
        uiOverlay.setSpacing(5);
        // uiOverlay should allow clicks on its interactive children (threeDotsButton)
        // but pass through clicks elsewhere if it's covering the whole screen.
        // Default behavior for VBox means it's not mouse transparent.
        // The pickOnBounds=false means only children are targeted.
        uiOverlay.setPickOnBounds(false);
        topBar.setPickOnBounds(false); // HBox children are targeted
        scoreArea.setPickOnBounds(false); // VBox children are targeted
        centerMessageLayout.setPickOnBounds(false);


        gameRoot.getChildren().add(uiOverlay);

        gameScene = new Scene(gameRoot, SCREEN_WIDTH, SCREEN_HEIGHT);
        gameScene.setFill(Color.rgb(10, 10, 40));

        menuScene = new Scene(createMenu(), SCREEN_WIDTH, SCREEN_HEIGHT);
        gameObjects = new ArrayList<>();

        // Trong phương thức start(Stage primaryStage), bên trong phần // --- Event Handlers for Game Action Menu ---

        threeDotsButton.setOnAction(event -> {
            if (gameActionMenu.isVisible()) {
                gameActionMenu.setVisible(false);
            } else {
                // Lấy vị trí của threeDotsButton để tham chiếu
                Bounds buttonBoundsInScene = threeDotsButton.localToScene(threeDotsButton.getBoundsInLocal());

                // Tọa độ X của cạnh TRÁI của threeDotsButton (so với gameRoot)
                double threeDotsButtonLeftEdgeX = gameRoot.sceneToLocal(
                        buttonBoundsInScene.getMinX(), // Lấy cạnh trái X
                        buttonBoundsInScene.getMinY()
                ).getX();

                // Tọa độ Y của cạnh DƯỚI của threeDotsButton (so với gameRoot)
                double threeDotsButtonBottomEdgeY = gameRoot.sceneToLocal(
                        buttonBoundsInScene.getMinX(), // MinX không quan trọng ở đây cho Y
                        buttonBoundsInScene.getMaxY()  // Lấy cạnh dưới Y
                ).getY();

                // Đặt layoutX cho gameActionMenu BẰNG với layoutX (cạnh trái) của threeDotsButton.
                gameActionMenu.setLayoutX(threeDotsButtonLeftEdgeX);

                // Đặt layoutY cho gameActionMenu ngay bên dưới threeDotsButton.
                double verticalSpacing = 5; // Khoảng cách dọc
                gameActionMenu.setLayoutY(threeDotsButtonBottomEdgeY + verticalSpacing);

                // Đảm bảo các nút con được thêm vào lại
                gameActionMenu.getChildren().clear();
                gameActionMenu.getChildren().addAll(pausePlayButton, replayGameButton, quitToMenuButton);

                gameActionMenu.setVisible(true);
                gameActionMenu.toFront();
            }
        });



        pausePlayButton.setOnAction(event -> {
            if (gameOver) { // Don't allow pause/play if game is over
                gameActionMenu.setVisible(false);
                return;
            }
            if (gameRunning) {
                gameRunning = false;
                // isGamePausedForMenu = true;
                if (imgPlay != null && !imgPlay.isError()) pausePlayImageView.setImage(imgPlay);
                showTempMessage("GAME PAUSED", SCREEN_WIDTH / 2.0, SCREEN_HEIGHT / 2.0, 9999); // Show until unpaused
            } else {
                gameRunning = true;
                // isGamePausedForMenu = false;
                if (imgPause != null && !imgPause.isError()) pausePlayImageView.setImage(imgPause);
                tempMessageLabel.setVisible(false); // Clear "GAME PAUSED" message
            }
            gameActionMenu.setVisible(false);
        });

        replayGameButton.setOnAction(event -> {
            gameRoot.getChildren().removeIf(node -> "losingScreenWrapper".equals(node.getId())); // Remove losing screen if present
            tempMessageLabel.setVisible(false); // Clear any messages

            restartGame(); // This sets gameRunning = true

            if (imgPause != null && !imgPause.isError()) pausePlayImageView.setImage(imgPause);
            // isGamePausedForMenu = false;
            gameActionMenu.setVisible(false);
        });

        quitToMenuButton.setOnAction(event -> {
            gameRunning = false;
            gameOver = false; // Reset game over state as we are going to menu
            // isGamePausedForMenu = false;

            gameRoot.getChildren().removeIf(node -> "losingScreenWrapper".equals(node.getId()));
            tempMessageLabel.setVisible(false);

            primaryStage.setScene(menuScene);
            if (imgPause != null && !imgPause.isError()) pausePlayImageView.setImage(imgPause); // Reset icon
            gameActionMenu.setVisible(false);
        });

        // Hide menu if clicking elsewhere on the game scene (not on menu or its toggle button)
        gameScene.setOnMouseClicked(event -> {
            if (gameActionMenu.isVisible()) {
                Point2D clickInMenuNode = gameActionMenu.sceneToLocal(event.getSceneX(), event.getSceneY());
                Point2D clickInThreeDotsButtonNode = threeDotsButton.sceneToLocal(event.getSceneX(), event.getSceneY());

                boolean clickInsideMenu = gameActionMenu.contains(clickInMenuNode);
                boolean clickInsideThreeDots = threeDotsButton.contains(clickInThreeDotsButtonNode);

                if (!clickInsideMenu && !clickInsideThreeDots) {
                    gameActionMenu.setVisible(false);
                }
            }
        });


        AnimationTimer gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                lastUpdate = now;

                if (gameRunning && !gameOver) {
                    updateGame();
                }
                // Always render, even if paused, to show current state
                // Or only render if running: if (gameRunning && !gameOver) renderGame();
                // Current setup renders always, which is fine.
                // If paused, updateGame() isn't called, so state doesn't change.
                renderGame(); // Render regardless of gameRunning to show paused screen.
            }
        };

        primaryStage.setScene(menuScene);
        primaryStage.setResizable(false);
        primaryStage.show();
        gameLoop.start();
    }

    private void initializeNewGame() {
        score = 0;
        lives = numLivesInitial;
        currentLevel = 1;
        enemySpawnCooldown = 1500;
        Enemy.SPEED = 1.0;
        scoreForNextLevel = 500 * currentLevel;
        scoreForBoss = 1000;

        bossExists = false;
        currentBoss = null;
        gameOver = false;
        // gameRunning will be set by the caller (startGame or restartGame)

        gameObjects.clear();
        player = new Player(SCREEN_WIDTH / 2.0, SCREEN_HEIGHT - 70);
        gameObjects.add(player);

        lastEnemySpawnTime = System.currentTimeMillis();
        lastPowerUpSpawnTime = System.currentTimeMillis();

        updateUI();
        initEventHandlers(gameScene); // Re-initialize if necessary, or ensure they are robust

        // Reset menu state
        gameActionMenu.setVisible(false);
        if (imgPause != null && !imgPause.isError()) {
            pausePlayImageView.setImage(imgPause); // Default to "Pause" icon
        }
        tempMessageLabel.setVisible(false); // Clear any lingering messages
    }

    private void updateGame() {
        // This method is only called if gameRunning is true and !gameOver
        List<GameObject> newObjects = new ArrayList<>();

        for (GameObject obj : new ArrayList<>(gameObjects)) {
            obj.update();
            if (obj instanceof Enemy && !(obj instanceof BossEnemy)) {
                ((Enemy) obj).attemptShoot(newObjects, System.currentTimeMillis());
            }
            // Không gọi attemptShoot cho Boss ở đây nữa nếu Boss có logic riêng
        }
        gameObjects.addAll(newObjects);
        newObjects.clear(); // Clear sau khi thêm đạn của Enemy thường

        // Xử lý bắn cho Boss (sau vòng lặp chính hoặc tách riêng)
        if (currentBoss != null && !currentBoss.isDead()) {
            currentBoss.attemptBossShooting(newObjects); // Gọi phương thức bắn riêng của Boss
            gameObjects.addAll(newObjects); // Thêm đạn của Boss
        }

        spawnEnemy();
        spawnPowerUp();
        checkCollisions();
        checkEnemiesReachingBottom();

        gameObjects.removeIf(GameObject::isDead);

        if (currentBoss != null && currentBoss.isDead()) {
            score += 500 * currentLevel;
            showTempMessage("BOSS DEFEATED!", SCREEN_WIDTH / 2.0, SCREEN_HEIGHT / 2.0, 3);
            currentBoss = null;
            bossExists = false;
            scoreForBoss += 1000 * (currentLevel + 1);
        }

        if (score >= scoreForNextLevel && currentLevel < 10) {
            currentLevel++;
            scoreForNextLevel += 750 * currentLevel;
            enemySpawnCooldown = Math.max(400, enemySpawnCooldown - 100);
            Enemy.SPEED = Math.min(4.0, Enemy.SPEED + 0.25);
            showTempMessage("LEVEL " + currentLevel + "!", SCREEN_WIDTH / 2.0, SCREEN_HEIGHT / 2.0, 3);
        }

        updateUI();

        if (lives <= 0 && !gameOver) {
            gameOver = true; // Set gameOver flag
            resetGame();     // Call resetGame to handle game over sequence
        }
    }

    private void renderGame() {
        gc.setFill(Color.rgb(10, 10, 40));
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        for (GameObject obj : gameObjects) {
            obj.render(gc);
        }
    }

    private void updateUI() {
        scoreLabel.setText("Score: " + score);
        livesLabel.setText("Lives: " + lives);
        levelLabel.setText("Level: " + currentLevel);
    }

    private void spawnEnemy() {
        long currentTime = System.currentTimeMillis();
        if (bossExists || currentBoss != null) return;

        if (score >= scoreForBoss && !bossExists) {
            spawnBossEnemy();
            return;
        }

        if (currentTime - lastEnemySpawnTime > enemySpawnCooldown) {
            double spawnX = random.nextDouble() * (SCREEN_WIDTH - Enemy.ENEMY_WIDTH) + Enemy.ENEMY_WIDTH / 2.0;
            double spawnY = -Enemy.ENEMY_HEIGHT / 2.0;
            gameObjects.add(new Enemy(spawnX, spawnY));
            lastEnemySpawnTime = currentTime;
        }
    }

    private void spawnPowerUp() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPowerUpSpawnTime > powerUpSpawnCooldown) {
            if (random.nextDouble() < 0.20) {
                double spawnX = random.nextDouble() * (SCREEN_WIDTH - PowerUp.POWERUP_WIDTH) + PowerUp.POWERUP_WIDTH / 2.0;
                double spawnY = -PowerUp.POWERUP_HEIGHT / 2.0;
                PowerUp.PowerUpType randomType = PowerUp.PowerUpType.values()[random.nextInt(PowerUp.PowerUpType.values().length)];
                gameObjects.add(new PowerUp(spawnX, spawnY, randomType));
            }
            lastPowerUpSpawnTime = currentTime;
        }
    }

    private void spawnBossEnemy() {
        if (!bossExists && currentBoss == null) {
            currentBoss = new BossEnemy(SCREEN_WIDTH / 2.0, BossEnemy.BOSS_HEIGHT * 1.5);
            gameObjects.add(currentBoss);
            bossExists = true;
            showTempMessage("!!! BOSS APPEARS !!!", SCREEN_WIDTH / 2.0, SCREEN_HEIGHT / 2.0, 3.5);
        }
    }

    private void checkCollisions() {
        List<Bullet> playerBullets = gameObjects.stream()
                .filter(obj -> obj instanceof Bullet)
                .map(obj -> (Bullet) obj)
                .collect(Collectors.toList());
        List<EnemyBullet> enemyBullets = gameObjects.stream()
                .filter(obj -> obj instanceof EnemyBullet)
                .map(obj -> (EnemyBullet) obj)
                .collect(Collectors.toList());
        List<Enemy> enemies = gameObjects.stream()
                .filter(obj -> obj instanceof Enemy && !(obj instanceof BossEnemy))
                .map(obj -> (Enemy) obj)
                .collect(Collectors.toList());
        List<PowerUp> powerUps = gameObjects.stream()
                .filter(obj -> obj instanceof PowerUp)
                .map(obj -> (PowerUp) obj)
                .collect(Collectors.toList());

        for (Bullet pBullet : playerBullets) {
            if (pBullet.isDead()) continue;
            for (Enemy enemy : enemies) {
                if (!enemy.isDead() && pBullet.collidesWith(enemy)) {
                    pBullet.setDead(true);
                    enemy.setDead(true);
                    score += 10 * currentLevel;
                    break;
                }
            }
            if (currentBoss != null && !currentBoss.isDead() && pBullet.collidesWith(currentBoss)) {
                pBullet.setDead(true);
                currentBoss.takeDamage(Bullet.DAMAGE);
            }
        }

        if (player != null && !player.isDead()) {
            for (EnemyBullet eBullet : enemyBullets) {
                if (eBullet.isDead()) continue;
                if (eBullet.collidesWith(player)) {
                    eBullet.setDead(true);
                    playerHit();
                }
            }
        }

        if (player != null && !player.isDead()) {
            for (Enemy enemy : enemies) {
                if (!enemy.isDead() && player.collidesWith(enemy)) {
                    enemy.setDead(true);
                    playerHit();
                }
            }
            if (currentBoss != null && !currentBoss.isDead() && player.collidesWith(currentBoss)) {
                playerHit();
            }
        }

        if (player != null && !player.isDead()) {
            for (PowerUp powerUp : powerUps) {
                if (!powerUp.isDead() && player.collidesWith(powerUp)) {
                    powerUp.setDead(true);
                    applyPowerUp(powerUp);
                }
            }
        }
    }

    private void playerHit() {
        if (player.isDead()) return;
        lives--;
        // player.setHealth(player.getHealth() - 25); // Assuming Player class has health, or simplify to lives
        if (lives < 0) lives = 0;
        showTempMessage("HIT! Lives: " + lives, SCREEN_WIDTH / 2.0, SCREEN_HEIGHT - 100, 1.5);
        updateUI();
        if (lives <= 0) {
            player.setDead(true);
            // gameOver will be set in updateGame()
        }
    }

    private void applyPowerUp(PowerUp powerUp) {
        if (player.isDead()) return;
        String message = "";
        switch (powerUp.getType()) {
            case HEALTH_PACK: // Assuming this maps to gaining a life for simplicity, or player health
                if (player.getHealth() < 100) { // Example: if player has health
                    int oldHealth = player.getHealth();
                    player.setHealth(Math.min(100, oldHealth + 35));
                    message = (player.getHealth() > oldHealth) ? "HEALTH + " + (player.getHealth() - oldHealth) : "MAX HEALTH!";
                } else {
                    message = "MAX HEALTH!"; // Or give points if health is full
                }
                score += 15 * currentLevel;
                break;
            case SHIELD: // Maps to extra life
                if (lives < numLivesInitial + 2) { // Max 2 extra lives
                    lives++;
                    message = "EXTRA LIFE!";
                } else {
                    message = "MAX LIVES!";
                }
                score += 25 * currentLevel;
                break;
            case DOUBLE_SHOT:
                message = "DOUBLE SHOT! (WIP)"; // Placeholder
                score += 30 * currentLevel;
                break;
            case SPEED_BOOST:
                message = "SPEED BOOST! (WIP)"; // Placeholder
                score += 20 * currentLevel;
                break;
        }
        showTempMessage(message, SCREEN_WIDTH / 2.0, SCREEN_HEIGHT / 2.0, 2);
        updateUI();
    }

    private void checkEnemiesReachingBottom() {
        for (GameObject obj : new ArrayList<>(gameObjects)) {
            if (obj instanceof Enemy && !obj.isDead() && !(obj instanceof BossEnemy)) {
                if (obj.getY() - obj.getHeight() / 2 >= SCREEN_HEIGHT) {
                    if (!gameOver) {
                        lives--;
                        if (lives < 0) lives = 0;
                        showTempMessage("ENEMY SLIPPED! Lives: " + lives, SCREEN_WIDTH / 2.0, SCREEN_HEIGHT - 100, 2);
                        updateUI();
                        if (lives <= 0) {
                            // gameOver will be set in updateGame()
                        }
                    }
                    obj.setDead(true);
                }
            }
        }
    }

    private void showLosingScreen() {
        VBox contentPane = new VBox(30);
        contentPane.setId("losingContentPane");
        contentPane.setAlignment(Pos.CENTER);

        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        gameOverLabel.setTextFill(Color.RED);

        Label finalScoreLabel = new Label("Your Score: " + score);
        finalScoreLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 36));
        finalScoreLabel.setTextFill(Color.WHITE);
        VBox.setMargin(gameOverLabel, new Insets(0, 0, 30, 0));
        VBox.setMargin(finalScoreLabel, new Insets(0, 0, 40, 0));

        Button tryAgainButton = new Button("Try Again");
        styleLosingScreenButton(tryAgainButton, Color.rgb(68, 68, 68), Color.rgb(85, 85, 85));
        tryAgainButton.setOnAction(e -> restartGame());

        Button exitGameButton = new Button("Exit Game");
        styleLosingScreenButton(exitGameButton, Color.rgb(204, 51, 51), Color.rgb(220, 61, 61));
        exitGameButton.setOnAction(e -> {
            gameRoot.getChildren().removeIf(node -> "losingScreenWrapper".equals(node.getId()));
            primaryStage.setScene(menuScene);
        });
        VBox.setMargin(tryAgainButton, new Insets(0, 0, 15, 0));
        contentPane.getChildren().addAll(gameOverLabel, finalScoreLabel, tryAgainButton, exitGameButton);

        StackPane losingScreenWrapper = new StackPane();
        losingScreenWrapper.setId("losingScreenWrapper");
        losingScreenWrapper.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        losingScreenWrapper.setStyle("-fx-background-color: black;");
        losingScreenWrapper.getChildren().add(contentPane);
        StackPane.setAlignment(contentPane, Pos.CENTER);

        gameRoot.getChildren().removeIf(node -> "losingScreenWrapper".equals(node.getId()));
        gameRoot.getChildren().add(losingScreenWrapper);
        losingScreenWrapper.toFront(); // Make sure it's on top
    }

    private void styleLosingScreenButton(Button button, Color normalColor, Color hoverColor) {
        button.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        button.setTextFill(Color.WHITE);
        button.setMinWidth(200);
        button.setMinHeight(50);
        button.setPadding(new Insets(10));
        CornerRadii radii = new CornerRadii(5);
        button.setBackground(new Background(new BackgroundFill(normalColor, radii, Insets.EMPTY)));
        button.setOnMouseEntered(e -> button.setBackground(new Background(new BackgroundFill(hoverColor, radii, Insets.EMPTY))));
        button.setOnMouseExited(e -> button.setBackground(new Background(new BackgroundFill(normalColor, radii, Insets.EMPTY))));
    }

    private void restartGame() {
        // Remove game-specific overlays like losing screen or instructions if they were part of gameRoot
        gameRoot.getChildren().removeIf(node ->
                "losingScreenWrapper".equals(node.getId()) ||
                        "instructionsPane".equals(node.getId()) // Assuming instructions might be shown over game
        );
        // Keep gameCanvas, uiOverlay, and gameActionMenu as they are part of the base game scene

        initializeNewGame(); // Resets score, lives, player, objects, etc.
        gameRunning = true;  // Start the game logic

        // Ensure the scene is the gameScene (it should be if restarting from losing screen)
        if (primaryStage.getScene() != gameScene) {
            primaryStage.setScene(gameScene);
        }

        // Reset menu state for a new game
        gameActionMenu.setVisible(false);
        if (imgPause != null && !imgPause.isError()) {
            pausePlayImageView.setImage(imgPause); // Game is running, so show "Pause" icon
        }
        // isGamePausedForMenu = false;
        tempMessageLabel.setVisible(false); // Clear any temporary messages
    }

    private void resetGame() {
        gameRunning = false; // Stop game logic updates
        // gameOver is already true if this is called from updateGame due to lives <= 0

        showLosingScreen(); // Display the game over UI

        // Update in-game menu state for game over
        gameActionMenu.setVisible(false);
        if (imgPlay != null && !imgPlay.isError()) { // Show "Play" icon as game is over, effectively paused
            pausePlayImageView.setImage(imgPlay);
        }
        // isGamePausedForMenu = false; // Reset this flag
        // tempMessageLabel might be showing "GAME PAUSED", clear it if showLosingScreen doesn't.
        // Or let showLosingScreen take precedence.
    }


    private void initEventHandlers(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (!gameRunning || player == null || player.isDead() || gameOver) return; // Check gameOver
            if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.A) player.setMoveLeft(true);
            else if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.D) player.setMoveRight(true);
            else if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.W) player.setMoveForward(true);
            else if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.S) player.setMoveBackward(true);
            else if (event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.CONTROL) {
                List<GameObject> playerBullets = new ArrayList<>();
                player.shoot(playerBullets);
                gameObjects.addAll(playerBullets);
            }
        });
        scene.setOnKeyReleased(event -> {
            if (player == null) return; // No need to check gameRunning for key release
            if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.A) player.setMoveLeft(false);
            else if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.D) player.setMoveRight(false);
            else if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.W) player.setMoveForward(false);
            else if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.S) player.setMoveBackward(false);
        });
    }

    private StackPane createMenu() {
        VBox menuElementsLayout = new VBox(25);
        menuElementsLayout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Welcome to\nSpace Shooter!");
        titleLabel.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 56));
        titleLabel.setTextFill(Color.rgb(160, 255, 255));
        DropShadow titleGlow = new DropShadow();
        titleGlow.setColor(Color.rgb(0, 255, 255, 0.9));
        titleGlow.setRadius(25);
        titleGlow.setSpread(0.3);
        titleGlow.setBlurType(BlurType.GAUSSIAN);
        titleLabel.setEffect(titleGlow);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setTextAlignment(TextAlignment.CENTER);

        Button startButton = new Button("START");
        styleMenuButton(startButton);
        startButton.setOnAction(e -> startGame());

        Button instructionsButton = new Button("INSTRUCTIONS");
        styleMenuButton(instructionsButton);
        instructionsButton.setOnAction(e -> showInstructions());

        Button exitButton = new Button("QUIT");
        styleMenuButton(exitButton);
        exitButton.setOnAction(e -> primaryStage.close());

        menuElementsLayout.getChildren().addAll(titleLabel, startButton, instructionsButton, exitButton);
        StackPane menuRootStackPane = new StackPane();
        menuRootStackPane.setStyle("-fx-background-color: #0D47A1;");
        menuRootStackPane.getChildren().add(menuElementsLayout);
        return menuRootStackPane;
    }


    private void styleMenuButton(Button button) {
        button.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        String baseStyle =
                "-fx-background-radius: 50em; " +
                        "-fx-border-radius: 50em; " +
                        "-fx-border-width: 2px; " +
                        "-fx-padding: 12 40 12 40; " +
                        "-fx-text-fill: white; ";
        String normalBg = "-fx-background-color: linear-gradient(to right, #5e35b1, #1e88e5);";
        String hoverBg = "-fx-background-color: linear-gradient(to right, #7e57c2, #42a5f5);";
        String normalBorder = "-fx-border-color: white;";
        String hoverBorder = "-fx-border-color: #e3f2fd;";

        button.setStyle(baseStyle + normalBg + normalBorder);
        button.setOnMouseEntered(e -> button.setStyle(baseStyle + hoverBg + hoverBorder));
        button.setOnMouseExited(e -> button.setStyle(baseStyle + normalBg + normalBorder));
    }

    private void showInstructions() {
        // First, ensure no existing instructionsPane is there from a previous click
        if (menuScene.getRoot() instanceof StackPane) {
            StackPane menuRoot = (StackPane) menuScene.getRoot();
            menuRoot.getChildren().removeIf(node -> "instructionsPane".equals(node.getId()));
        }

        VBox instructionsPane = new VBox(20);
        instructionsPane.setId("instructionsPane"); // Assign an ID for removal
        instructionsPane.setAlignment(Pos.CENTER);
        instructionsPane.setPadding(new Insets(50));
        instructionsPane.setStyle("-fx-background-color: rgba(13, 71, 161, 0.9); -fx-border-color: #90caf9; -fx-border-width: 2; -fx-background-radius: 15; -fx-border-radius: 15;");

        Label title = new Label("Space Shooter \n  Instructions");
        title.setFont(Font.font("Arial Black", FontWeight.BOLD, 30));
        title.setTextFill(Color.LIGHTCYAN);

        Label controls = new Label(
                "Use the A, W, S, and D keys or the arrow keys \nto move your spaceship.\n" +
                        "Press SPACE to shoot bullets and destroy \nthe enemies.\n" +
                        "If an enemy reaches the bottom of the screen, \nyou lose a life.\n" +
                        "The game resets if you lose all lives.\n" +
                        "Collect power-ups (Health, Shield/Extra Life).\n" + // Updated power-up info
                        "Defeat the boss enemy to level up and \nincrease the difficulty.\n\n" +
                        "Good luck and have fun!"
        );
        controls.setFont(Font.font("Arial", 18));
        controls.setTextFill(Color.WHITE);
        controls.setTextAlignment(TextAlignment.CENTER);

        Button backButton = new Button("BACK");
        styleMenuButton(backButton);
        backButton.setOnAction(e -> {
            if (menuScene.getRoot() instanceof StackPane) {
                ((StackPane) menuScene.getRoot()).getChildren().remove(instructionsPane);
            }
        });
        instructionsPane.getChildren().addAll(title, controls, backButton);

        if (menuScene.getRoot() instanceof StackPane) {
            StackPane menuRoot = (StackPane) menuScene.getRoot();
            menuRoot.getChildren().add(instructionsPane); // Add instructionsPane to the menu's root
        }
    }

    private void showTempMessage(String message, double x, double y, double durationSeconds) {
        tempMessageLabel.setText(message);
        tempMessageLabel.setVisible(true);
        tempMessageLabel.toFront(); // Ensure it's on top of other UI elements like gameActionMenu if needed

        if (tempMessageTimer != null) {
            tempMessageTimer.stop();
        }

        if (durationSeconds >= 9999) { // Persistent message (e.g. "GAME PAUSED")
            // Don't start a timer to hide it automatically
            tempMessageTimer = null;
        } else {
            tempMessageTimer = new AnimationTimer() {
                private long startTime = -1;
                @Override
                public void handle(long now) {
                    if (startTime == -1) startTime = now;
                    if (now - startTime > durationSeconds * 1_000_000_000L) {
                        tempMessageLabel.setVisible(false);
                        tempMessageLabel.setText("");
                        this.stop();
                        tempMessageTimer = null;
                    }
                }
            };
            tempMessageTimer.start();
        }
    }

    private void startGame() {
        initializeNewGame(); // Sets up lives, score, player, etc.
        gameRunning = true;  // Critical: game logic should start
        primaryStage.setScene(gameScene);
    }
}