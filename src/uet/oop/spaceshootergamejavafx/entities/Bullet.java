package uet.oop.spaceshootergamejavafx.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import uet.oop.spaceshootergamejavafx.SpaceShooter; // For screen bounds

public class Bullet extends GameObject {

    public static final int BULLET_WIDTH = 5;
    public static final int BULLET_HEIGHT = 15;
    private static final double SPEED = 8.0;
    public static final int DAMAGE = 10; // Damage this bullet deals

    public Bullet(double x, double y) {
        super(x, y, BULLET_WIDTH, BULLET_HEIGHT); // (x,y) is center
        // this.dead is already false
    }

    @Override
    public void update() {
        if (isDead()) {
            return;
        }

        setY(getY() - SPEED); // Player's bullets always move up

        // Check if bullet is off screen (top)
        // (y is center, so y + height/2 is bottom edge of the bullet)
        if (getY() + getHeight() / 2 < 0) {
            setDead(true);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isDead()) {
            return;
        }
        gc.setFill(Color.YELLOW);
        // Draw from top-left, calculated from center (x,y)
        gc.fillRect(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
    }

    @Override
    public double getWidth() {
        return BULLET_WIDTH;
    }

    @Override
    public double getHeight() {
        return BULLET_HEIGHT;
    }
    // setDead and isDead are inherited
}