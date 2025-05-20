package uet.oop.spaceshootergamejavafx.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import uet.oop.spaceshootergamejavafx.SpaceShooter;

import java.util.Arrays;
import java.util.List;
// import java.util.Random; // Kế thừa 'random' từ lớp Enemy

public class BossEnemy extends Enemy {

    private int health;
    public static final double BOSS_WIDTH = 56.4;  // Kích thước cho boss.png
    public static final double BOSS_HEIGHT = 44.2;

    private double horizontalSpeed;
    private static final double VERTICAL_SPEED = 0.38;
    private int horizontalDirection = 1;
    private int shootCooldownFramesBase = 65; // Boss bắn nhanh hơn chút
    private int shootCooldownTimer;
    private int maxHealth; // Lưu máu tối đa để tính % thanh máu

    // Boss có thể có bảng màu riêng hoặc dùng chung/mở rộng từ Enemy
    private static final List<Color> BOSS_EXCLUSIVE_BULLET_COLORS = Arrays.asList(
            Color.rgb(139, 0, 255),   // Tím đậm (DarkViolet)
            Color.rgb(220, 20, 60),    // Đỏ thẫm (Crimson)
            Color.rgb(255, 215, 0),    // Vàng (Gold)
            Color.rgb(0, 255, 127)     // Xanh lá mùa xuân (SpringGreen)
    );

    public BossEnemy(double x, double y) {
        super(x, y); // Constructor của Enemy sẽ được gọi (và có thể load enemy.png)

        this.width = BOSS_WIDTH;  // Ghi đè kích thước cho getBounds() và render
        this.height = BOSS_HEIGHT;
        loadSprite("res/boss.png"); // Tải sprite riêng cho Boss, nó sẽ ghi đè sprite đã tải bởi Enemy

        this.maxHealth = 350; // Ví dụ máu tối đa cho Boss
        this.health = this.maxHealth;
        this.horizontalSpeed = 1.7;
        this.shootCooldownTimer = shootCooldownFramesBase + random.nextInt(shootCooldownFramesBase / 2);
    }

    @Override
    public void update() {
        if (isDead()) return;

        setX(getX() + horizontalDirection * horizontalSpeed);
        if (getX() - getWidth() / 2 <= 0) { setX(getWidth() / 2); horizontalDirection = 1; }
        else if (getX() + getWidth() / 2 >= SpaceShooter.SCREEN_WIDTH) { setX(SpaceShooter.SCREEN_WIDTH - getWidth() / 2); horizontalDirection = -1; }

        setY(getY() + VERTICAL_SPEED);
        if (getY() + getHeight() / 2 > SpaceShooter.SCREEN_HEIGHT * 0.5) { // Giới hạn vị trí Y của Boss
            setY(SpaceShooter.SCREEN_HEIGHT * 0.5 - getHeight() / 2);
        }
        if (getY() - getHeight() / 2 > SpaceShooter.SCREEN_HEIGHT) setDead(true); // Nếu vẫn ra khỏi màn hình

        if (shootCooldownTimer > 0) shootCooldownTimer--;
    }

    // Ghi đè attemptShoot của Enemy để Boss không dùng logic bắn đó
    @Override
    public void attemptShoot(List<GameObject> gameObjects, long currentTime) {
        // Không làm gì ở đây, Boss sẽ dùng attemptBossShooting
    }

    // Phương thức bắn riêng cho Boss (được gọi từ SpaceShooter)
    public void attemptBossShooting(List<GameObject> newObjects) {
        if (isDead() || shootCooldownTimer > 0) {
            return;
        }

        double bulletSpawnY = getY();
        double bulletXCenter = getX();

        Color baseColorForSet;
        // 60% cơ hội dùng màu đặc biệt của Boss, 40% dùng màu chung của Enemy
        if (random.nextDouble() < 0.6 && !BOSS_EXCLUSIVE_BULLET_COLORS.isEmpty()) {
            baseColorForSet = BOSS_EXCLUSIVE_BULLET_COLORS.get(random.nextInt(BOSS_EXCLUSIVE_BULLET_COLORS.size()));
        } else {
            baseColorForSet = Enemy.AVAILABLE_BULLET_COLORS.get(random.nextInt(Enemy.AVAILABLE_BULLET_COLORS.size()));
        }

        // Boss bắn 3 hoặc 5 tia tùy ngẫu nhiên
        int numberOfShots = random.nextBoolean() ? 3 : 5;

        if (numberOfShots == 3) {
            newObjects.add(new EnemyBullet(bulletXCenter, bulletSpawnY, baseColorForSet));
            newObjects.add(new EnemyBullet(bulletXCenter - getWidth() / 4, bulletSpawnY, baseColorForSet.deriveColor(0, 1, 0.8, 1))); // Màu hơi khác
            newObjects.add(new EnemyBullet(bulletXCenter + getWidth() / 4, bulletSpawnY, baseColorForSet.deriveColor(0, 1, 0.8, 1)));
        } else { // numberOfShots == 5
            newObjects.add(new EnemyBullet(bulletXCenter, bulletSpawnY, baseColorForSet));
            newObjects.add(new EnemyBullet(bulletXCenter - getWidth() / 5, bulletSpawnY, baseColorForSet.brighter()));
            newObjects.add(new EnemyBullet(bulletXCenter + getWidth() / 5, bulletSpawnY, baseColorForSet.brighter()));
            newObjects.add(new EnemyBullet(bulletXCenter - getWidth() / 2.5, bulletSpawnY, baseColorForSet.darker()));
            newObjects.add(new EnemyBullet(bulletXCenter + getWidth() / 2.5, bulletSpawnY, baseColorForSet.darker()));
        }

        shootCooldownTimer = shootCooldownFramesBase + random.nextInt(shootCooldownFramesBase / 3);
    }

    public void takeDamage(int damageAmount) {
        if (isDead()) return;
        this.health -= damageAmount;
        if (this.health <= 0) {
            this.health = 0;
            setDead(true);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isDead() && health <= 0) {
            return;
        }
        // Gọi render của lớp cha (GameObject) để vẽ sprite của Boss
        super.render(gc);

        // Vẽ thanh máu
        if (health > 0) { // Chỉ vẽ thanh máu khi còn máu
            double healthBarWidth = getWidth() * 0.85;
            double healthPercentage = Math.max(0, (double) health / this.maxHealth); // Dùng maxHealth đã lưu
            double barX = getX() - healthBarWidth / 2;
            double barY = getY() - getHeight() / 2 - 15; // Thanh máu phía trên Boss

            gc.setFill(Color.rgb(50, 50, 50, 0.75)); // Nền thanh máu
            gc.fillRect(barX, barY, healthBarWidth, 10); // Chiều cao thanh máu 10px
            gc.setFill(Color.RED.interpolate(Color.LIMEGREEN, healthPercentage)); // Màu chuyển từ đỏ sang xanh lá
            gc.fillRect(barX, barY, healthBarWidth * healthPercentage, 10);

            // Tùy chọn: Vẽ viền cho thanh máu
            gc.setStroke(Color.rgb(200,200,200,0.8));
            gc.setLineWidth(1);
            gc.strokeRect(barX, barY, healthBarWidth, 10);
        }
    }

    @Override
    public double getWidth() {
        return BOSS_WIDTH;
    }

    @Override
    public double getHeight() {
        return BOSS_HEIGHT;
    }

    public int getHealth() {
        return health;
    }
}