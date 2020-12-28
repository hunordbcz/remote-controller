package net.debreczeni.remotedesktop.ui;

import lombok.Data;
import lombok.SneakyThrows;
import net.debreczeni.remotedesktop.listeners.DisplaySelectionListener;
import net.debreczeni.remotedesktop.model.socket.RemoteImage;
import net.debreczeni.remotedesktop.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class DisplayDetails extends JPanel implements MouseListener {
    private final static Border etchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
    private final static Border raisedBorder = BorderFactory.createRaisedBevelBorder();
    private final static Border loweredBorder = BorderFactory.createLoweredBevelBorder();
    private final static Border emptyBorder = BorderFactory.createEmptyBorder();

    private final Border titledEtchedBorder;
    private final Border titledRaisedBorder;
    private final Border titledLoweredBorder;
    private final Border titledEmptyBorder;
    private final int nr;
    private final RemoteImage image;

    private final List<DisplaySelectionListener> displaySelectionListenerList;

    public DisplayDetails(int nr, RemoteImage image) throws IOException {
        this.nr = nr;
        this.image = image;
        displaySelectionListenerList = new ArrayList<>();

        titledEtchedBorder = BorderFactory.createTitledBorder(etchedBorder, getTitle());
        titledRaisedBorder = BorderFactory.createTitledBorder(raisedBorder, getTitle());
        titledLoweredBorder = BorderFactory.createTitledBorder(loweredBorder, getTitle());
        titledEmptyBorder = BorderFactory.createTitledBorder(emptyBorder, getTitle());

        init();
    }

    public void addClickListener(DisplaySelectionListener displaySelectionListener){
        displaySelectionListenerList.add(displaySelectionListener);
    }

    @SneakyThrows
    private String getTitle(){
        BufferedImage bufferedImage = image.get();
        return "#" + nr + " ("+ bufferedImage.getWidth() + "x" + bufferedImage.getHeight() +")";
    }

    private void init() throws IOException {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setVisible(true);

        JLabel image = new JLabel();
        image.setIcon(new ImageIcon(ImageUtil.resizeImage(this.image.get(), 240, 135))); //16:9 scale
        add(image);
        setBorder(titledEtchedBorder);
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        displaySelectionListenerList.forEach(listener -> {
            BufferedImage bufferedImage = null;
            try {
                bufferedImage = image.get();
                listener.selected(nr, bufferedImage.getWidth(), bufferedImage.getHeight());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        });
    }

    @Override
    public void mousePressed(MouseEvent e) {
        setBorder(titledLoweredBorder);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setBorder(titledRaisedBorder);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setBorder(titledRaisedBorder);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setBorder(titledEtchedBorder);
    }
}
