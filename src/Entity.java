import java.awt.*;

public abstract class Entity {

    private int x, y;
    private int width, height;

    public Entity(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void update();
    public abstract void draw(Graphics2D g2);

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX()        { return x; }
    public int getY()        { return y; }
    public int getWidth()    { return width; }
    public int getHeight()   { return height; }
    public void setX(int x)  { this.x = x; }
    public void setY(int y)  { this.y = y; }
}