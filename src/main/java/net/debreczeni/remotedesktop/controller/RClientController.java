package net.debreczeni.remotedesktop.controller;

import io.rsocket.metadata.WellKnownMimeType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.debreczeni.remotedesktop.listeners.DisplaySelectionListener;
import net.debreczeni.remotedesktop.listeners.ScreenShareEventListener;
import net.debreczeni.remotedesktop.model.User;
import net.debreczeni.remotedesktop.model.socket.RemoteDisplays;
import net.debreczeni.remotedesktop.model.socket.RemoteImage;
import net.debreczeni.remotedesktop.model.socket.events.RemoteEvent;
import net.debreczeni.remotedesktop.ui.DisplayDetails;
import net.debreczeni.remotedesktop.ui.ScreenShare;
import net.debreczeni.remotedesktop.util.SerializerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.Disposable;

import javax.annotation.PreDestroy;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
public class RClientController {

    @Value( "${spring.rsocket.server.port}" )
    private int serverPort;

    private static final MimeType SIMPLE_AUTH = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString());

    private static volatile Disposable screenShareDisposable;

    private RSocketRequester rsocketRequester;
    private RSocketRequester.Builder rsocketRequesterBuilder;
    private RSocketStrategies rsocketStrategies;
    private String host;

    @Autowired
    public RClientController(RSocketRequester.Builder builder,
                             @Qualifier("rSocketStrategies") RSocketStrategies strategies) {
        this.rsocketRequesterBuilder = builder;
        this.rsocketStrategies = strategies;
    }

    private boolean userCheck() {
        return null != this.rsocketRequester && !this.rsocketRequester.rsocket().isDisposed();
    }

    @SneakyThrows
    public void login(String host, String username, String password) {
        this.host = host;
        log.info("Connecting with {}", username);
        UsernamePasswordMetadata user = new UsernamePasswordMetadata(username, password);
        this.rsocketRequester = rsocketRequesterBuilder
                .setupRoute("login-server")
                .setupData(Objects.requireNonNull(SerializerUtil.toString(User.getInstance())))
                .setupMetadata(user, SIMPLE_AUTH)
                .rsocketStrategies(builder ->
                        builder.encoder(new SimpleAuthenticationEncoder())
                )
                .connectTcp(host, serverPort)
                .doOnError(error ->
                        SwingUtilities.invokeLater(()-> JOptionPane.showMessageDialog(null, error.getMessage(), "Error on connection", JOptionPane.ERROR_MESSAGE))
                )
                .onErrorStop()
                .block();

        this.rsocketRequester.rsocket()
                .onClose()
                .doOnError(error -> log.warn("Connection CLOSED"))
                .doFinally(consumer -> log.info("Disconnected from server"))
                .subscribe();
    }

    @PreDestroy
    public void preDestroy() {
        if (!userCheck()) {
            return;
        }
        screenShareDisposable.dispose();
        this.logout();
    }

    public void logout() {
        if (userCheck()) {
            this.rsocketRequester.rsocket().dispose();
            this.host = "UNKWN";
            log.info("Logged out.");
        } else {
            log.warn("Please log in to the server first");
        }
    }



    public void quitScreenShare(boolean allowControl) {
        if (userCheck() && screenShareDisposable != null) {
            screenShareDisposable.dispose();
            displays(allowControl);
        } else {
            log.warn("Please log in to the server first");
        }
    }

    public void displays(boolean allowControl) {
        if (!userCheck()) {
            log.warn("Please log in to the server first");
            return;
        }

        RemoteDisplays displays = this.rsocketRequester
                .route("displays")
                .retrieveMono(RemoteDisplays.class)
                .block();

        JFrame jFrame = new JFrame("Choose display");
        jFrame.setLayout(new FlowLayout());
        DisplaySelectionListener displaySelectionListener = (selectionNr, width, height) -> {
            jFrame.dispose();
            screenShare(selectionNr, width, height, allowControl);
        };

        assert displays != null;
        displays.getScreenshotsByDisplay().forEach((nr, image) -> {
            try {
                DisplayDetails displayDetails = new DisplayDetails(nr, image);
                displayDetails.addClickListener(displaySelectionListener);
                jFrame.add(displayDetails);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        jFrame.revalidate();
        jFrame.pack();
        jFrame.repaint();

        jFrame.setVisible(true);
    }

    public void screenShare(final int screenNr, int width, int height, boolean allowControl) {
        if (!userCheck()) {
            return;
        }

        final ScreenShare screenShare = new ScreenShare(host + (allowControl ? " - CONTROL" : " - VIEW"), screenNr, new Dimension(width, height), allowControl);
        screenShare.addEventListener(new ScreenShareEventListener() {
            @Override
            public void newRemoteEvent(RemoteEvent event) {
                sendRemoteEvent(event);
            }

            @Override
            public void quitButtonPressed() {
                quitScreenShare(allowControl);
            }
        });

        screenShareDisposable = rsocketRequester
                .route("image-stream")
                .data(screenNr)
                .retrieveFlux(RemoteImage.class)
                .subscribe(screenShare::updateImage);
    }

    public void sendRemoteEvent(RemoteEvent remoteEvent) {
        if (!userCheck() || screenShareDisposable.isDisposed()) {
            return;
        }

        log.info("Sending {}", remoteEvent);
        rsocketRequester
                .route("remote-event")
                .data(Objects.requireNonNull(SerializerUtil.toString(remoteEvent)))
                .send()
                .block();
    }
}