package uet.oop.spaceshootergamejavafx.entities;

// import javafx.scene.canvas.GraphicsContext; // Không cần nếu render kế thừa từ GameObject
import javafx.scene.paint.Color;
import uet.oop.spaceshootergamejavafx.SpaceShooter;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Enemy extends GameObject {

    public static final double ENEMY_WIDTH = 70.5;
    public static final double ENEMY_HEIGHT = 55.25;
    public static double SPEED = 1.0;

    private long lastShotTime = 0;
    private long shootCooldownBase = 2300; //ms
    private long currentShootCooldown;
    protected Random random = new Random(); // protected để BossEnemy có thể dùng lại

    // Danh sách các màu cơ bản có thể có cho đạn của Enemy
    public static final List<Color> AVAILABLE_BULLET_COLORS = Arrays.asList(
            Color.rgb(255, 80, 80),   // Đỏ hồng
            Color.rgb(80, 255, 80),   // Xanh lá cây sáng
            Color.rgb(80, 180, 255),  // Xanh dương sáng
            Color.rgb(255, 180, 80),  // Cam
            Color.rgb(230, 80, 255),  // Tím hồng
            Color.rgb(0, 230, 230)    // Cyan sáng
    );

    public Enemy(double x, double y) {
        super(x, y, ENEMY_WIDTH, ENEMY_HEIGHT);
        resetShootCooldown();
        loadSprite("res/enemy.png"); // Tải sprite cho Enemy
    }

    protected void resetShootCooldown() {
        this.currentShootCooldown = shootCooldownBase + random.nextInt(1800); // 2300ms đến 4100ms
    }

    @Override
    public double getWidth() {
        return ENEMY_WIDTH;
    }

    @Override
    public double getHeight() {
        return ENEMY_HEIGHT;
    }

    @Override
    public void update() {
        if (isDead()) return;
        setY(getY() + SPEED);

        if (getY() - getHeight() / 2 > SpaceShooter.SCREEN_HEIGHT) {
            setDead(true);
        }
    }

    public void attemptShoot(List<GameObject> gameObjects, long currentTime) {
        if (isDead()) return;

        // Xác suất bắn, ví dụ 1.8% mỗi frame
        if (random.nextDouble() < 0.018) {
            if (currentTime - lastShotTime > currentShootCooldown) {
                double bulletX = getX();
                double bulletY = getY(); // Bắn từ tâm Enemy

                // Chọn một màu ngẫu nhiên từ danh sách
                Color randomBaseColor = AVAILABLE_BULLET_COLORS.get(random.nextInt(AVAILABLE_BULLET_COLORS.size()));

                gameObjects.add(new EnemyBullet(bulletX, bulletY, randomBaseColor));

                lastShotTime = currentTime;
                resetShootCooldown();
            }
        }
    }

    // render() được kế thừa từ GameObject, sẽ tự động vẽ sprite nếu sprite được tải thành công.
    // Nếu bạn xóa/comment lại dòng loadSprite("res/enemy.png"), nó sẽ vẽ placeholder.
}