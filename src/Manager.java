import acm.graphics.GObject;

import java.awt.*;

public class Manager {
    private Graphics _graphics;
    private Factory _factory;
    private int NBRICKS_PER_ROW = 10;
    private int NBRICKS_PER_COL = 10;
    private double PROB_PLAIN_BRICK = 1;
    private double FRAME_RATE = 60;
    private Brick[][] _bricks;
    private Ball _ball;
    private Paddle _paddle;
    private double BRICK_GAP = 4;
    Manager(){
        _graphics = new Graphics();
        _graphics.linkManager(this);
        _factory = new Factory();
        _bricks = new Brick[NBRICKS_PER_ROW][NBRICKS_PER_COL];
    }

    private Brick generateSingleBrick(){
        double p = Math.random();
        if (p <= PROB_PLAIN_BRICK)
            return _factory.createRandomColorBrick();
        return _factory.createRandomColorBrick();
    }

    private Ball getBall(){
        return _ball;
    }

    private Brick getBrick(int row, int col){
        return _bricks[row][col];
    }

    private void setBrick(Brick b, int row, int col){
        _bricks[row][col] = b;
    }

    private boolean isPaddle(GObject g){
        return g == _paddle;
    }


    private boolean wallCollisionManager(){
        double ballX = _ball.getCenterX();
        double ballY = _ball.getCenterY();
        double canvasWidth = _graphics.getCanvasWidth();
        double canvasHeight = _graphics.getCanvasHeight();
        boolean tracker = true;
        if (ballX >= canvasWidth || ballX <= 0) _ball.bounceRight();
        else if (ballY <= 0) _ball.bounceUp();
        else tracker = false;
        return tracker;
    }

    public void collisionManager(){
        if (wallCollisionManager()) return;
        GObject collider = _ball.detectCollision(_graphics);
        if (collider == null) return;

        if (isPaddle(collider)){
            _ball.bounceUp();
        }

        else{
            Brick collidedBrick = (Brick) collider;
            collidedBrick.onCollision();
            _ball.bounceUp();
            if (collidedBrick.isDestroyed()){
                _graphics.remove(collidedBrick);
            }
        }
    }

    public void movePaddleXTo(double x){
        _paddle.setX(x);
    }

    private void generateBricks(){
        Brick newBrick;
        double xTrack;
        double yTrack;
        for (int col = 0; col < NBRICKS_PER_COL; col++){
            for (int row = 0; row < NBRICKS_PER_ROW; row++){
                newBrick = generateSingleBrick();
                xTrack = col * Brick.getBrickWidth();
                yTrack = row * (Brick.getBrickHeight() + BRICK_GAP);
                newBrick.setX(xTrack);
                newBrick.setY(yTrack);
                setBrick(newBrick, row, col);
                _graphics.add(getBrick(row, col));
            }
        }
    }


    private void generateBall(){
        _ball = _factory.createPlayerBall();
        _ball.putBallIn(0, _graphics.getCanvasHeight() / 2);
        _ball.setVelocityX(3);
        _ball.setVelocityY(2);
        _graphics.add(_ball);
    }

    private void generatePaddle(){
        _paddle = _factory.createPlayerPaddle(_graphics);
        _graphics.add(_paddle);
    }

    public void start(String[] args){
        generateBricks();
        generateBall();
        generatePaddle();
        _graphics.addMouseListeners();
        _graphics.start(args);

    }

    public void moveFrame(){
        _ball.moveBall();
        collisionManager();
    }


}
