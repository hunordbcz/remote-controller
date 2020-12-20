package net.debreczeni.remoteclient.ui.elements;

import net.debreczeni.remoteclient.image.Display;
import net.debreczeni.remoteclient.model.socket.SocketImage;

import javax.swing.*;
import java.io.IOException;

public class ScreenView extends JLabel {

    //todo start listener for images
//    public ScreenView(Client client){

    public ScreenView() {
//        initImageProcessor();
//        imageProcessor.start();
        setVisible(true);
    }

//    @Deprecated
//    private void initImageProcessor() {
//        this.imageProcessor = new Thread(() -> {
//            while (true) {
//                ImageIcon image = new ImageIcon(Display.takeScreenshot());
//                setIcon(image);
//
//                if (LATENCY > 0) {
//                    try {
//                        Thread.sleep(LATENCY);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }

    public void updateImage(ImageIcon image){
//        SwingUtilities.invokeLater(() -> setIcon(image));
        setIcon(image);
    }

    public void updateImage(SocketImage image){
        try {
            setIcon(image.get());
        } catch (IOException ignored) {
        }
    }
}
