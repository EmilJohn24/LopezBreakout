import acm.program.GraphicsProgram;

import java.awt.event.MouseEvent;


public class Graphics extends GraphicsProgram{
    private int CANVAS_WIDTH = 640;
    private int CANVAS_HEIGHT = 720;
    private int FRAME_LEN = 15;
    private Manager management;

    Graphics(){
        setSize(CANVAS_HEIGHT, CANVAS_WIDTH);
        pause(1000);
    }

    public double getCanvasHeight(){
        return CANVAS_HEIGHT;
    }

    public double getCanvasWidth(){
        return CANVAS_WIDTH;
    }

    public void linkManager(Manager manager){
        management = manager;
    }

    public void mouseMoved(MouseEvent mouse){
        double mouseX = mouse.getX();
        management.movePaddleXTo(mouseX);
    }

    public void run(){
        while (!management.hasWon()){
            pause(FRAME_LEN);
            management.moveFrame();
            if (management.passedPaddle())
                management.lifeStrip();
            if (management.isDead()){
                management.gameOverSequence();
                return;
            }
        }


        management.victorySequence();


    }

}
