package uet.oop.spaceshootergamejavafx.entities;

// import javafx.scene.canvas.GraphicsContext; // KHÔNG CẦN NỮA NẾU KHÔNG GHI ĐÈ RENDER
// import javafx.scene.paint.Color; // KHÔNG CẦN NỮA
import uet.oop.spaceshootergamejavafx.SpaceShooter;

import java.util.List;

public class Player extends GameObject {

    public static final int PLAYER_WIDTH = 80;
    public static final int PLAYER_HEIGHT = 80;
    private static final double SPEED = 5.0;

    private boolean moveLeft;
    private boolean moveRight;
    private boolean moveForward;
    private boolean moveBackward;

    private int health;

    private long lastShotTime = 0;
    private final long shootCooldown = 300;

    public Player(double x, double y) {
        super(x, y, PLAYER_WIDTH, PLAYER_HEIGHT);
        this.health = 100;
        loadSprite("res/player.png"); // Tải sprite cho Player
    }

    @Override
    public double getWidth() {
        return PLAYER_WIDTH;
    }

    @Override
    public double getHeight() {
        return PLAYER_HEIGHT;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
        if (this.health <= 0) {
            this.health = 0;
            setDead(true);
        }
    }

    @Override
    public void update() {
        if (isDead()) {
            return;
        }

        double currentX = getX();
        double currentY = getY();

        if (moveLeft) {
            currentX -= SPEED;
        }
        if (moveRight) {
            currentX += SPEED;
        }
        if (moveForward) {
            currentY -= SPEED;
        }
        if (moveBackward) {
            currentY += SPEED;
        }

        currentX = Math.max(getWidth() / 2, Math.min(currentX, SpaceShooter.SCREEN_WIDTH - getWidth() / 2));
        currentY = Math.max(getHeight() / 2, Math.min(currentY, SpaceShooter.SCREEN_HEIGHT - getHeight() / 2));

        setX(currentX);
        setY(currentY);
    }

    // XÓA HOẶC COMMENT LẠI PHƯƠNG THỨC RENDER() Ở ĐÂY
    // @Override
    // public void render(GraphicsContext gc) {
    //     if (isDead()) {
    //         return;
    //     }
    //     // Dòng code này đang vẽ hình chữ nhật màu xanh thay vì sprite:
    //     // gc.setFill(Color.BLUE);
    //     // gc.fillRect(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
    //
    //     // Nếu bạn muốn Player render theo cách của GameObject (vẽ sprite),
    //     // chỉ cần gọi super.render(gc) hoặc xóa hoàn toàn phương thức này.
    //     // super.render(gc);
    // }


    public void setMoveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
    }

    public void setMoveRight(boolean moveRight) {
        this.moveRight = moveRight;
    }

    public void setMoveForward(boolean moveForward) {
        this.moveForward = moveForward;
    }

    public void setMoveBackward(boolean moveBackward) {
        this.moveBackward = moveBackward;
    }

    public void shoot(List<GameObject> newObjects) {
        if (isDead()) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime > shootCooldown) {
            double bulletSpawnX = getX();
            // Spawn đạn từ tâm player nếu bạn muốn (như đã thảo luận)
            double bulletSpawnY = getY();
            // Hoặc spawn từ phía trên player
            // double bulletSpawnY = getY() - getHeight() / 2 - Bullet.BULLET_HEIGHT / 2;

            newObjects.add(new Bullet(bulletSpawnX, bulletSpawnY));
            lastShotTime = currentTime;
        }
    }
}