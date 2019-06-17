import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.util.MediaTools;
import acm.util.RandomGenerator;

import java.applet.AudioClip;
import java.awt.*;


class Score extends GLabel {
    private int score;
    private final String label = "Score: ";
    Score(Graphics g){
        super("" , g.getCanvasWidth(), g.getCanvasHeight() + 30);
        Font font = new Font("Times New Roman", Font.BOLD, 500);
        score = 0;
        update();
    }

    public String stringify(){
        return String.valueOf(score);
    }
    public void add(){
        score++;
    }

    public void update(){
        setText(label + stringify());
    }

    public int getScore(){
        return score;
    }
}

class Lives extends GLabel {
    private int _lives = 3;
    private final String label = "Lives: ";

    Lives(int lives, Graphics g){
        super("", g.getCanvasWidth(), g.getCanvasHeight());
        _lives = lives;
        update();
    }

    public void removeLife(){
        _lives--;
    }

    public String stringify(){
        return String.valueOf(_lives);
    }

    public void update(){
        setText(label + stringify());
    }

    public boolean isDead(){
        return _lives == 0;
    }

    public int getLife(){
        return _lives;
    }
}


public class Manager {
    private Graphics _graphics;
    private Factory _factory;
    private int NBRICKS_PER_ROW = 10;
    private int NBRICKS_PER_COL = 10;
    private double PROB_PLAIN_BRICK = 1;
    private RandomGenerator velocityGenerator = RandomGenerator.getInstance();
    private AudioClip _bounce;
    private String audioDir = "AUDIO/";
    private Brick[][] _bricks;
    private Ball _ball;
    private Paddle _paddle;
    private Score _score;
    private Lives _lives;
    private double BRICK_GAP = 4;
    Manager(){
        _graphics = new Graphics();
        _graphics.linkManager(this);
        _factory = new Factory();
        _bricks = new Brick[NBRICKS_PER_ROW][NBRICKS_PER_COL];
        _paddle = new Paddle(_graphics);
        _score = new Score(_graphics);
        _lives = new Lives(3, _graphics);
        loadBounceAudio("bounce.wav");

    }

    public void loadBounceAudio(String filename){
        String location = audioDir + filename;
        _bounce =  MediaTools.loadAudioClip(location);
    }

    public void playBounceSound(){
        _bounce.play();
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

        playBounceSound();
        if (isPaddle(collider)){
            _ball.bounceUp();
        }

        else{
            Brick collidedBrick = (Brick) collider;
            collidedBrick.onCollision();
            _ball.bounceUp();
            if (collidedBrick.isDestroyed()){
                _graphics.remove(collidedBrick);
                _score.add();
                _score.update();
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
        _ball.putBallIn(    100,150);
        _ball.setVelocityX(velocityGenerator.nextDouble(1,10));
        _ball.setVelocityY(velocityGenerator.nextDouble(1, 10));
        _graphics.add(_ball);
    }


    private void playBounceAudio(){

    }
    private void generatePaddle(){
        _paddle = _factory.createPlayerPaddle(_graphics);
        _graphics.add(_paddle);
    }

    private void addScore(){
        _graphics.add(_score);
    }

    private void addLives(){
        _graphics.add(_lives);
    }

    public void start(String[] args){
        generateBricks();
        generateBall();
        generatePaddle();
        addScore();
        addLives();
        _graphics.addMouseListeners();
        _graphics.start(args);

    }

    public void moveFrame(){
        _ball.moveBall();
        collisionManager();
    }

    public void victorySequence(){
        return;
    }

    public void gameOverSequence(){
        return;
    }
    public boolean lifeStrip(){
        _lives.removeLife();
        _ball.reset();
        return _lives.isDead();

    }

    public boolean hasWon(){
        return _score.getScore() == NBRICKS_PER_ROW * NBRICKS_PER_COL;
    }

    public boolean passedPaddle(){
        return _ball.getY() >= _graphics.getCanvasHeight();
    }

    public boolean isDead(){
        return _lives.isDead();
    }



}
