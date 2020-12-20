package net.debreczeni.remoteclient.ui;

import net.debreczeni.remotecommon.util.SingletonRobot;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardAdapter implements KeyListener {

    private final Robot robot;

    public KeyboardAdapter() {
        this.robot = SingletonRobot.getInstance();
    }

    public void pressKey(int keyCode){
        robot.keyPress(keyCode);
    }

    public void releaseKey(int keyCode){
        robot.keyPress(keyCode);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        System.err.println("keyTyped");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.err.println("keyPressed");
        pressKey(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        System.err.println("keyReleased");
        releaseKey(e.getKeyCode());
    }
}
