import java.awt.*;

public class Brick extends Entity {

    private int health;
    private boolean destroyed = false;
    private int flashTimer    = 0;

    private static final Color COLOR_STRONG = new Color(220, 220, 220);
    private static final Color COLOR_WEAK   = new Color(140, 140, 140);
    private static final Color COLOR_FLASH  = Color.WHITE;

    public Brick(int x, int y, int width, int height, int health) {
        super(x, y, width, height);
        this.health = health;
    }

    @Override
    public void update() {
        if (flashTimer > 0) flashTimer--;
    }

    @Override
    public void draw(Graphics2D g2) {
        if (destroyed) return;

        Color fillColor;

        if (flashTimer > 0)
            fillColor = COLOR_FLASH;
        else if (health == 2)
            fillColor = COLOR_STRONG;
        else
            fillColor = COLOR_WEAK;

        g2.setColor(fillColor);
        g2.fillRect(getX(), getY(), getWidth(), getHeight());
        g2.setColor(new Color(30, 30, 30));
        g2.drawRect(getX(), getY(), getWidth(), getHeight());
    }

    public void hit() {
        health--;
        flashTimer = 6;
        if (health <= 0) destroyed = true;
    }

    public boolean isDestroyed() { return destroyed; }
    public int getPointValue()   { return health == 0 ? 10 : 20; }
}