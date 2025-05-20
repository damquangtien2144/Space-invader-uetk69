package uet.oop.spaceshootergamejavafx.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap; // Quan trọng: Import cho đầu tia laser bo tròn
import uet.oop.spaceshootergamejavafx.SpaceShooter;

public class EnemyBullet extends GameObject {

    public static final int ENEMY_BULLET_WIDTH = 4;  // Chiều rộng của hiệu ứng hào quang laser
    public static final int ENEMY_BULLET_HEIGHT = 22; // Chiều dài của tia laser
    private static final double SPEED = 6.5; // Tốc độ đạn địch (có thể điều chỉnh)

    private Color laserCoreColor;
    private Color laserGlowColor;

    /**
     * Constructor nhận màu cơ bản và tự tạo màu hào quang.
     */
    public EnemyBullet(double x, double y, Color baseColor) {
        super(x, y, ENEMY_BULLET_WIDTH, ENEMY_BULLET_HEIGHT); // (x,y) là tâm
        this.laserCoreColor = baseColor;
        // Tự động tạo màu hào quang: nhạt hơn, sáng hơn một chút và có độ mờ
        this.laserGlowColor = baseColor.deriveColor(0, 0.7, 1.1, 0.45);
    }

    @Override
    public void update() {
        if (isDead()) {
            return;
        }
        setY(getY() + SPEED); // Đạn địch di chuyển xuống dưới

        // Kiểm tra nếu đạn ra khỏi màn hình (phía dưới)
        if (getY() - getHeight() / 2 > SpaceShooter.SCREEN_HEIGHT) {
            setDead(true);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isDead()) {
            return;
        }

        // Vẽ laser bằng hai đường thẳng chồng lên nhau
        double coreLineWidth = getWidth() * 0.45; // Lõi laser chiếm % chiều rộng hiệu ứng
        double haloLineWidth = getWidth();       // Hào quang bằng chiều rộng hiệu ứng

        // Tọa độ tâm của đạn
        double centerX = getX();
        // Điểm bắt đầu và kết thúc của tia laser theo chiều dọc
        double startY = getY() - getHeight() / 2;
        double endY = getY() + getHeight() / 2;

        // 1. Vẽ Hào quang
        gc.setStroke(this.laserGlowColor);
        gc.setLineWidth(haloLineWidth);
        gc.setLineCap(StrokeLineCap.ROUND); // Đầu tia laser bo tròn
        gc.strokeLine(centerX, startY, centerX, endY);

        // 2. Vẽ Lõi
        gc.setStroke(this.laserCoreColor);
        gc.setLineWidth(coreLineWidth);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.strokeLine(centerX, startY, centerX, endY);

        // Reset LineWidth về mặc định
        gc.setLineWidth(1.0);
    }

    @Override
    public double getWidth() {
        return ENEMY_BULLET_WIDTH;
    }

    @Override
    public double getHeight() {
        return ENEMY_BULLET_HEIGHT;
    }
    // isDead và setDead được kế thừa
}