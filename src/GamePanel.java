import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GamePanel extends JPanel implements Runnable {

    public static final int WIDTH  = 600;
    public static final int HEIGHT = 700;
    private static final int FPS   = 60;

    // Game state
    public enum GameState { WAITING, PLAYING, GAME_OVER, WIN }
    private GameState state = GameState.WAITING;

    private final KeyHandler keyHandler = new KeyHandler();
    private Thread gameThread;
    private Paddle paddle;
    private Ball   ball;
    private List<Brick> bricks = new ArrayList<>();

    private int score = 0;
    private int lives = 3;
    private int nextSpeedUp = 50;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(15, 15, 25));
        setDoubleBuffered(true);
        addKeyListener(keyHandler);
        setFocusable(true);

        setupLevel();
        startGameThread();
    }

    private void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double delta        = 0;
        long   lastTime     = System.nanoTime();
        long   currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void setupLevel() {
        bricks.clear();
        score      = 0;
        nextSpeedUp = 50;

        int cols      = 10;
        int rows      = 5;
        int brickW    = 52;
        int brickH    = 22;
        int padding   = 4;
        int offsetX   = 14;
        int offsetY   = 80;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int bx     = offsetX + col * (brickW + padding);
                int by     = offsetY + row * (brickH + padding);
                int health = (row < 2) ? 2 : 1;
                bricks.add(new Brick(bx, by, brickW, brickH, health));
            }
        }

        resetBallAndPaddle();
        state = GameState.WAITING;
    }

    private void resetBallAndPaddle() {
        paddle = new Paddle(WIDTH / 2 - 45, HEIGHT - 60, keyHandler);
        ball   = new Ball(WIDTH / 2 - 7, HEIGHT - 80);
        state  = GameState.WAITING;
    }

    private void update() {
        if (state == GameState.WAITING) {
            ball.setX(paddle.getX() + paddle.getPaddleWidth() / 2 - ball.getSize() / 2);

            if (keyHandler.spacePressed) {
                ball.launch();
                state = GameState.PLAYING;
            }
        }

        if (state != GameState.PLAYING) return;

        paddle.update();
        ball.update();

        for (Brick b : bricks) b.update();

        // collision: ball - paddle
        if (ball.getBounds().intersects(paddle.getBounds()) && ball.getY() + ball.getSize() < paddle.getY() + paddle.getHeight()) {
            ball.deflectOffPaddle(paddle);
        }

        // collision: ball - bricks
        Iterator<Brick> it = bricks.iterator();
        while (it.hasNext()) {
            Brick brick = it.next();
            if (brick.isDestroyed()) { it.remove(); continue; }

            Rectangle bBounds = ball.getBounds();
            Rectangle brBounds = brick.getBounds();

            if (bBounds.intersects(brBounds)) {
                score += brick.getPointValue();
                brick.hit();

                //  difficulty: speed up every 50 points
                if (score >= nextSpeedUp) {
                    ball.speedUp(0.4);   // +0.4 per threshold (capped at MAX_SPEED inside Ball)
                    nextSpeedUp += 50;
                }

                // bounce direction
                int overlapLeft   = (bBounds.x + bBounds.width) - brBounds.x;
                int overlapRight  = (brBounds.x + brBounds.width) - bBounds.x;
                int overlapTop    = (bBounds.y + bBounds.height) - brBounds.y;
                int overlapBottom = (brBounds.y + brBounds.height) - bBounds.y;

                int minOverlap = Math.min(Math.min(overlapLeft, overlapRight),
                        Math.min(overlapTop,  overlapBottom));

                if (minOverlap == overlapTop || minOverlap == overlapBottom) {
                    ball.bounceY();
                } else {
                    ball.bounceX();
                }
                break;
            }
        }

        if (ball.isOutOfBounds()) {
            lives--;
            if (lives <= 0) {
                state = GameState.GAME_OVER;
            } else {
                resetBallAndPaddle();
            }
        }

        if (bricks.isEmpty()) {
            state = GameState.WIN;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        drawBackground(g2);

        for (Brick b : bricks) b.draw(g2);
        paddle.draw(g2);
        ball.draw(g2);

        drawHUD(g2);

        if (state == GameState.WAITING)   drawMessage(g2, "SPACE  to launch", null);
        if (state == GameState.GAME_OVER) drawMessage(g2, "GAME OVER", "SPACE to play again");
        if (state == GameState.WIN)       drawMessage(g2, "YOU WIN! \uD83C\uDF89", "SPACE to play again");

        if ((state == GameState.GAME_OVER || state == GameState.WIN) && keyHandler.spacePressed) {
            lives = 3;
            setupLevel();
        }

        g2.dispose();
    }

    private void drawBackground(Graphics2D g2) {
        g2.setColor(Color.black);
        g2.fillRect(0, 0, WIDTH, HEIGHT);
    }

    private void drawHUD(Graphics2D g2) {
        g2.setFont(new Font("Monospaced", Font.BOLD, 14));

        // Score
        g2.setColor(new Color(180, 180, 220));
        g2.drawString("SCORE", 16, 30);
        g2.setColor(Color.WHITE);
        g2.drawString(String.valueOf(score), 16, 50);

        // Lives
        g2.setColor(new Color(180, 180, 220));
        g2.drawString("LIVES", WIDTH - 90, 30);
        g2.setColor(Color.WHITE);
        g2.drawString(String.valueOf(lives), WIDTH - 90, 50);

        // Bottom line
        g2.setColor(new Color(50, 50, 80));
        g2.drawLine(12, HEIGHT - 30, WIDTH - 12, HEIGHT - 30);

    }

    private void drawMessage(Graphics2D g2, String title, String sub) {

        g2.setFont(new Font("Monospaced", Font.BOLD, 26));
        g2.setColor(new Color(255, 220, 80));
        FontMetrics fm = g2.getFontMetrics();
        int tx = WIDTH / 2 - fm.stringWidth(title) / 2;
        g2.drawString(title, tx, HEIGHT / 2 - 10);

    }
}