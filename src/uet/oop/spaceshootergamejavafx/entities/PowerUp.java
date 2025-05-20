package uet.oop.spaceshootergamejavafx.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import uet.oop.spaceshootergamejavafx.SpaceShooter;


public class PowerUp extends GameObject {

    public static final int POWERUP_WIDTH = 50;
    public static final int POWERUP_HEIGHT = 50;
    private static final double SPEED = 1.5;

    public enum PowerUpType {
        HEALTH_PACK, SHIELD, DOUBLE_SHOT, SPEED_BOOST
    }

    private PowerUpType type;

    public PowerUp(double x, double y, PowerUpType type) {
        super(x, y, POWERUP_WIDTH, POWERUP_HEIGHT);
        this.type = type;
        // Giả sử có sprite chung, hoặc sprite theo type
        // String spriteFileName = "res/powerup_" + type.name().toLowerCase() + ".png"; // Nếu có sprite riêng
        loadSprite("res/powerup.png"); // Đường dẫn tương đối từ package entities
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isDead()) return;
        super.render(gc); // Vẽ sprite trước


    }

    @Override
    public void update() {
        if (isDead()) {
            return;
        }
        setY(getY() + SPEED); // PowerUps fall down

        // Check if power-up is off screen (bottom)
        // (y is center, so y - height/2 is top edge)
        if (getY() - getHeight() / 2 > SpaceShooter.SCREEN_HEIGHT) {
            setDead(true);
        }
    }



    @Override
    public double getWidth() {
        return POWERUP_WIDTH;
    }

    @Override
    public double getHeight() {
        return POWERUP_HEIGHT;
    }

    public PowerUpType getType() {
        return type;
    }
    // setDead and isDead are inherited
}