package net.debreczeni.remotedesktop.ui;

import lombok.Data;
import net.debreczeni.remotedesktop.model.socket.RemoteImage;
import net.debreczeni.remotedesktop.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

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

    public DisplayDetails(int nr, RemoteImage image) throws IOException {
        this.nr = nr;
        this.image = image;
        titledEtchedBorder = BorderFactory.createTitledBorder(etchedBorder, "Screen #" + nr);
        titledRaisedBorder = BorderFactory.createTitledBorder(raisedBorder, "Screen #" + nr);
        titledLoweredBorder = BorderFactory.createTitledBorder(loweredBorder, "Screen #" + nr);
        titledEmptyBorder = BorderFactory.createTitledBorder(emptyBorder, "Screen #" + nr);

        init();
    }

    private void init() throws IOException {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setVisible(true);

        JLabel image = new JLabel();
        image.setIcon(new ImageIcon(ImageUtil.resizeImage(this.image.get(), 160, 90)));
        add(image);
        setBorder(titledEtchedBorder);
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

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
