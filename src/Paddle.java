import java.awt.*;

public class Paddle extends Entity {

    private static final int WIDTH  = 90;
    private static final int HEIGHT = 14;
    private static final int SPEED  = 6;

    private final KeyHandler keyHandler;

    public Paddle(int x, int y, KeyHandler keyHandler) {
        super(x, y, WIDTH, HEIGHT);
        this.keyHandler = keyHandler;
    }

    @Override
    public void update() {
        if (keyHandler.leftPressed)  setX(Math.max(0, getX() - SPEED));
        if (keyHandler.rightPressed) setX(Math.min(GamePanel.WIDTH - WIDTH, getX() + SPEED));
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(getX(), getY(), WIDTH, HEIGHT, 6, 6);
    }

    public int getPaddleWidth() { return WIDTH; }
}