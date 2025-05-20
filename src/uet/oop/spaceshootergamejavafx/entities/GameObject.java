package uet.oop.spaceshootergamejavafx.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.paint.Color; // Import Color
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;   // Import Font
import javafx.scene.text.FontWeight; // Import FontWeight
import javafx.scene.text.Text;  // Import Text

import java.io.InputStream;

public abstract class GameObject {
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected boolean dead;
    protected Image sprite;

    public GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.dead = false;
        this.sprite = null;
    }

    /**
     * Tải sprite.
     * @param relativePathToRes Ví dụ: "res/player.png". Đường dẫn này là tương đối
     *                          so với package của lớp con đang gọi.
     */
    protected void loadSprite(String relativePathToRes) {
        String className = getClass().getSimpleName();
        System.out.println("[" + className + "] Attempting to load sprite: " + relativePathToRes);

        try {
            // getClass().getResourceAsStream() tìm tài nguyên tương đối với package của lớp hiện tại.
            // Nếu Player.java ở package entities, và path là "res/player.png",
            // nó sẽ tìm "entities/res/player.png" trong classpath.
            InputStream inputStream = getClass().getResourceAsStream(relativePathToRes);

            if (inputStream != null) {
                sprite = new Image(inputStream);
                if (sprite.isError()) {
                    System.err.println("[" + className + "] Error loading sprite (Image.isError() is true): " + relativePathToRes);
                    if (sprite.getException() != null) {
                        System.err.println("[" + className + "] Exception: " + sprite.getException().getMessage());
                        // sprite.getException().printStackTrace(); // Bỏ comment để xem full stack trace
                    }
                    sprite = null; // Đặt lại là null nếu có lỗi
                } else {
                    System.out.println("[" + className + "] Sprite loaded successfully: " + relativePathToRes + " (Width: " + sprite.getWidth() + ", Height: " + sprite.getHeight() + ")");
                }
                // Đóng inputStream sau khi sử dụng (quan trọng)
                try { inputStream.close(); } catch (Exception e) { /* Bỏ qua lỗi khi đóng */ }

            } else {
                System.err.println("[" + className + "] Could not find resource (InputStream is null). Path tried: " +
                        (getClass().getPackage() != null ? getClass().getPackage().getName().replace('.', '/') + "/" : "") +
                        relativePathToRes);

                // Thử một cách khác nếu cách trên thất bại (ít khi cần nếu cấu trúc đúng)
                // Dùng đường dẫn tuyệt đối từ gốc classpath
                String absolutePathInClasspath = "/uet/oop/spaceshootergamejavafx/entities/" + relativePathToRes;
                System.out.println("[" + className + "] Trying absolute classpath: " + absolutePathInClasspath);
                inputStream = GameObject.class.getResourceAsStream(absolutePathInClasspath); // Dùng GameObject.class để tránh vấn đề với lớp con
                if (inputStream != null) {
                    sprite = new Image(inputStream);
                    // ... (kiểm tra lỗi tương tự như trên)
                    System.out.println("[" + className + "] Sprite loaded successfully via ABSOLUTE path: " + absolutePathInClasspath);
                    try { inputStream.close(); } catch (Exception e) { /* ... */ }
                } else {
                    System.err.println("[" + className + "] Failed to load sprite via absolute path as well.");
                    sprite = null;
                }
            }
        } catch (Exception e) {
            System.err.println("[" + className + "] General exception loading sprite: " + relativePathToRes + " - " + e.getMessage());
            // e.printStackTrace(); // Bỏ comment để xem full stack trace
            sprite = null;
        }

        if (sprite == null) {
            System.err.println("[" + className + "] FINAL: Sprite is NULL after all attempts for: " + relativePathToRes);
        }
    }


    public abstract void update();

    public void render(GraphicsContext gc) {
        if (isDead()) {
            return;
        }
        if (sprite != null && sprite.getWidth() > 0 && sprite.getHeight() > 0) { // Kiểm tra kích thước sprite
            gc.drawImage(sprite, getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
        } else {
            Color placeholderColor = Color.GRAY;
            if (this instanceof Player) placeholderColor = Color.BLUE;
            else if (this instanceof BossEnemy) placeholderColor = Color.DARKMAGENTA;
            else if (this instanceof Enemy) placeholderColor = Color.RED;
            else if (this instanceof Bullet) placeholderColor = Color.YELLOW;
            else if (this instanceof EnemyBullet) placeholderColor = Color.LIGHTPINK;
            else if (this instanceof PowerUp) placeholderColor = Color.LIGHTGREEN;

            gc.setFill(placeholderColor);
            gc.fillRect(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());

            gc.setStroke(Color.BLACK);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            String initial = getClass().getSimpleName().length() > 0 ? getClass().getSimpleName().substring(0,1) : "?";
            Text textNode = new Text(initial);
            textNode.setFont(gc.getFont());
            double textWidth = textNode.getLayoutBounds().getWidth();
            gc.strokeText(initial, getX() - textWidth / 2, getY() + 4);

            // In ra thông báo nếu đang vẽ placeholder thay vì sprite
            // System.out.println("Rendering placeholder for " + getClass().getSimpleName() + " because sprite is null or invalid.");
        }
    }

    // ... (các getter, setter, collidesWith, getWidth, getHeight còn lại như cũ) ...
    public boolean isDead() { return dead; }
    public void setDead(boolean dead) { this.dead = dead; }
    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    public Bounds getBounds() {
        return new Rectangle(
                x - getWidth() / 2,
                y - getHeight() / 2,
                getWidth(),
                getHeight()
        ).getBoundsInLocal();
    }

    public boolean collidesWith(GameObject other) {
        if (this.isDead() || other.isDead()) {
            return false;
        }
        return this.getBounds().intersects(other.getBounds());
    }

    public abstract double getWidth();
    public abstract double getHeight();
}