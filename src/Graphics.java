import acm.program.GraphicsProgram;

import java.awt.event.MouseEvent;


public class Graphics extends GraphicsProgram{
    private int CANVAS_WIDTH = 640;
    private int CANVAS_HEIGHT = 720;
    private int FRAME_LEN = 30;
    private Manager management;

    Graphics(){
        resize(CANVAS_WIDTH, CANVAS_HEIGHT);
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
        while (true){
            pause(FRAME_LEN);
            management.moveFrame();
        }
    }

}
