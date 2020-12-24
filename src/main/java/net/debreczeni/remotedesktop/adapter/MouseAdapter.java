package net.debreczeni.remotedesktop.adapter;
import lombok.SneakyThrows;
import net.debreczeni.remotedesktop.ui.ScreenView;
import net.debreczeni.remotedesktop.util.SingletonRobot;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MouseAdapter extends java.awt.event.MouseAdapter {

    private final Robot robot;
    private final ScreenView screenView;

    @SneakyThrows
    public MouseAdapter(ScreenView screenView) {
        this.screenView = screenView;
        this.robot = SingletonRobot.getInstance();
    }

    public void moveMouse(Point point) {
//        point = Display.getPointByScreen(point, screenView.getDisplay().getNr());
        robot.mouseMove(point.x, point.y);
    }

    public void pressMouse() {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    }

    public void releaseMouse() {
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public void clickMouse() {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public void rotateMouseWheel(int amount) {
        robot.mouseWheel(amount);
    }


    @Override
    public void mousePressed(MouseEvent e) {
        //todo remove after mouse movement enabled
        moveMouse(e.getPoint());
        pressMouse();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
//        moveMouse(e.getPoint());
        releaseMouse();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
//        rotateMouseWheel(e.getScrollAmount());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        moveMouse(e.getPoint());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        clickMouse();
    }

    /*@Override
    public void mouseEntered(MouseEvent e) {
        System.err.println("mouseEntered");
        super.mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        System.err.println("mouseExited");
        super.mouseExited(e);
    }*/
}
