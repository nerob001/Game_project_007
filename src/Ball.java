import java.awt.*;

public class Ball extends Entity {

    private double dx, dy;
    private static final int    SIZE       = 14;
    private static final double BASE_SPEED = 4.5;
    private static final double MAX_SPEED  = 9.0;
    private double currentSpeed = BASE_SPEED;

    private boolean launched = false;

    public Ball(int x, int y) {
        super(x, y, SIZE, SIZE);
    }

    public void launch() {
        if (!launched) {
            dx = currentSpeed * 0.6;
            dy = -currentSpeed;
            launched = true;
        }
    }

    public void speedUp(double step) {
        currentSpeed = Math.min(currentSpeed + step, MAX_SPEED);
        if (launched) {
            double total = Math.sqrt(dx * dx + dy * dy);
            if (total > 0) {
                dx = dx / total * currentSpeed;
                dy = dy / total * currentSpeed;
            }
        }
    }

//    public void resetSpeed() { currentSpeed = BASE_SPEED; }

    @Override
    public void update() {
        if (!launched) return;

        int newX = (int)(getX() + dx);
        int newY = (int)(getY() + dy);

        setX(newX);
        setY(newY);

        if (getX() <= 0) {
            setX(0);
            dx = Math.abs(dx);
        }

        if (getX() + SIZE >= GamePanel.WIDTH) {
            setX(GamePanel.WIDTH - SIZE);
            dx = -Math.abs(dx);
        }

        if (getY() <= 0) {
            setY(0);
            dy = Math.abs(dy);
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillOval(getX(), getY(), SIZE, SIZE);
    }

    public void deflectOffPaddle(Paddle paddle) {
        int paddleCenterX = paddle.getX() + paddle.getWidth() / 2;
        int ballCenterX   = getX() + SIZE / 2;
        double offset = (ballCenterX - paddleCenterX) / (double)(paddle.getWidth() / 2);
        dx = currentSpeed * offset * 1.2;
        dy = -Math.abs(dy);
    }

    public void bounceY() { dy = -dy; }
    public void bounceX() { dx = -dx; }

    public boolean isOutOfBounds() { return getY() > GamePanel.HEIGHT + 20; }
    //    public boolean isLaunched()    { return launched; }
    //    public void resetLaunch()      { launched = false; dx = 0; dy = 0; }
    public int getSize()           { return SIZE; }
}