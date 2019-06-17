import acm.graphics.GImage;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.util.RandomGenerator;

import java.awt.Color;

interface Solid {
    void onCollision();
}

public class Factory {
    static String pngDirectory = "./PNG/";
    private final int NUM_COLORS = ColorBrick.COLORS.values().length;
    private RandomGenerator colorPicker = RandomGenerator.getInstance();
    private ColorBrick.COLORS[] colors = ColorBrick.COLORS.values();

    public Ball createPlayerBall(){
        Ball b = new Ball();
        return b;
    }



    public ColorBrick createRandomColorBrick(){
        int NCOLOR = ColorBrick.COLORS.values().length;
        int colorID = colorPicker.nextInt(1, NCOLOR - 1);
        ColorBrick newColoredBrick = new ColorBrick(colors[colorID]);
        return newColoredBrick;
    }

    public Paddle createPlayerPaddle(Graphics g){
        Paddle p = new Paddle(g);
        return p;
    }

}

class Ball extends GOval {
    static private final double DEFAULT_BALL_RADIUS = 12;
    static private final double DEFAULT_VELOCITY_Y = 3.0;
    static private final double DEFAULT_VELOCITY_X_MIN = 1.0;
    static private final double DEFAULT_VELOCITY_X_MAX = 3.0;
    static private final Color DEFAULT_COLOR = Color.BLACK;

    private double[] _velocity; //per frame

    Ball(){
        super(DEFAULT_BALL_RADIUS, DEFAULT_BALL_RADIUS);
        setFilled(true);
        setFillColor(DEFAULT_COLOR);
        _velocity = new double[2];
    }

    public void putBallIn(double x, double y){
        setX(x);
        setY(y);
    }

    public void setVelocityX(double velocity){
        _velocity[0] = velocity;
    }

    public void setVelocityY(double velocity){
        _velocity[1] = velocity;
    }

    public void setPolarVelocity(double velocity, double degrees){
        double radians = Math.toRadians(degrees);
        double newVelocityX = velocity * Math.cos(radians);
        double newVelocityY = -velocity * Math.sin(radians);
        setVelocityX(Math.min(DEFAULT_VELOCITY_X_MAX, newVelocityX));
        setVelocityX(Math.max(DEFAULT_VELOCITY_X_MIN, newVelocityX));
        setVelocityY(newVelocityY);

    }

    public void moveBall(){
        move(_velocity[0], _velocity[1]);
    }

    public GObject detectCollision (Graphics g){
        GObject upperLeft = g.getElementAt(getX(), getY()) ;
        GObject lowerLeft = g.getElementAt(getX(), getBottomY());
        GObject upperRight = g.getElementAt(getRightX(), getY());
        GObject lowerRight = g.getElementAt(getRightX(), getBottomY());
        if (upperLeft != null) return upperLeft;
        if (lowerLeft != null) return lowerLeft;
        if (upperRight != null) return upperRight;
        if (lowerRight != null) return lowerRight;
        return null;

    }

    public void bounce(){
        bounceUp();
        bounceRight();
    }
    public void bounceUp(){
        setVelocityY(-_velocity[1]);
    }

    public void bounceRight(){
        setVelocityX(-_velocity[0]);
    }


}

abstract class Brick extends GImage implements Solid{
    private static double BRICK_WIDTH = 40;
    private static double BRICK_HEIGHT = 12;
    public static String fileExt = ".png";
    Brick(String img, double x, double y){
        super(img, x, y);
        setSize(BRICK_WIDTH, BRICK_HEIGHT);

    }

    static public double getBrickWidth(){
        return BRICK_WIDTH;
    }

    static public double getBrickHeight(){
        return BRICK_HEIGHT;
    }

    public boolean isDestroyed(){
        if (health <= 0) return true;
        else return false;
    }

    public void setHealth(int health){
        this.health = health;
    }

    public int getHealth(){
        return health;
    }

    public void removeHealth(){
        health--;
    }
    
    abstract public void onCollision();
    private int health;
}

class ColorBrick extends Brick {
    static public int STARTING_HEALTH = 2;
    static public String convertColorToID(COLORS color){
        return String.valueOf(color.ordinal());
    }
    ColorBrick(COLORS color){
        super( Factory.pngDirectory + convertColorToID(color)
                        + Brick.fileExt, 0, 0);
        _color = color;
        setHealth(STARTING_HEALTH);
        broken = "b";
    }

    public enum COLORS {NONE, BLUE, YELLOWGREEN,
                                PURPLE, RED, ORANGE, CYAN,
                                    YELLOW, GREEN, GREY, BROWN};

    public void onCollision(){
        //handle collision for colored bricks
        removeHealth();
        String newImg = Factory.pngDirectory + getIDString()
                                    + broken + fileExt;
        if (getHealth() == 1) {
            setImage(newImg);
            setSize(getBrickWidth(), getBrickHeight());

        }

    }

    private String getIDString(){
        return convertColorToID(_color);
    }


    private String broken;
    private COLORS _color;
}

class Paddle extends GRect{
    private static double DEFAULT_PADDLE_WIDTH = 80;
    private static double DEFAULT_PADDLE_HEIGHT = 12;
    private static double DEFAULT_PADDLE_Y_OFFSET = 30; //from the bottom


    Paddle(Graphics g){
        super(DEFAULT_PADDLE_WIDTH, DEFAULT_PADDLE_HEIGHT);
        double bottomY = g.getSize().height - DEFAULT_PADDLE_Y_OFFSET;
        setBottomY(bottomY);
    }



}