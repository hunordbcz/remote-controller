package net.debreczeni.remotedesktop.controller;

import io.rsocket.SocketAcceptor;
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
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.Disposable;

import javax.annotation.PreDestroy;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
//@ShellComponent
public class RClientController {

    private static final String CLIENT = "Client";
    private static final String REQUEST = "Request";
    private static final String CLIENT_ID = UUID.randomUUID().toString();
    private static final MimeType SIMPLE_AUTH = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString());
    private static Disposable disposable;

    private static volatile Disposable screenShareDisposable;

    private RSocketRequester rsocketRequester;
    private RSocketRequester.Builder rsocketRequesterBuilder;
    private RSocketStrategies rsocketStrategies;

    @Autowired
    public RClientController(RSocketRequester.Builder builder,
                             @Qualifier("rSocketStrategies") RSocketStrategies strategies) {
        this.rsocketRequesterBuilder = builder;
        this.rsocketStrategies = strategies;
    }

    private boolean userCheck() {
        if (null == this.rsocketRequester || this.rsocketRequester.rsocket().isDisposed()) {
            log.warn("Please log in to the server first");
            return false;
        }
        return true;
    }

    //    @ShellMethod("Login with your username and password.")
    @SneakyThrows
    public void login(String host, String username, String password) {
        log.info("Connecting using client ID: {} and username: {}", CLIENT_ID, username);
        SocketAcceptor responder = RSocketMessageHandler.responder(rsocketStrategies, new PingMessage());
        UsernamePasswordMetadata user = new UsernamePasswordMetadata(username, password);
        this.rsocketRequester = rsocketRequesterBuilder
                .setupRoute("shell-client")
                .setupData(Objects.requireNonNull(SerializerUtil.toString(User.getInstance())))
                .setupMetadata(user, SIMPLE_AUTH)
                .rsocketStrategies(builder ->
                        builder.encoder(new SimpleAuthenticationEncoder())
                )
                .rsocketConnector(connector -> connector.acceptor(responder))
                .connectTcp(host, 7000)
                .doOnError(error -> JOptionPane.showMessageDialog(null, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE))
                .doOnSuccess(data -> log.info(String.valueOf(data)))
                .onErrorStop()
                .block();

        this.rsocketRequester.rsocket()
                .onClose()
                .doOnError(error -> log.warn("Connection CLOSED"))
                .doFinally(consumer -> log.info("Client DISCONNECTED"))
                .subscribe();
    }

    @PreDestroy
    public void preDestroy(){
        this.quitScreenShare();
        this.logout();
    }

    public void logout() {
        if (userCheck()) {
            this.rsocketRequester.rsocket().dispose();
            log.info("Logged out.");
        }
    }

    public void quitScreenShare() {
        if (userCheck() && screenShareDisposable != null) {
            log.info("Stopping screen share.");
            screenShareDisposable.dispose();
            displays();
        }
    }

    //    @ShellMethod("get displays")
    public void displays() {
        if (!userCheck()) {
            return;
        }

        RemoteDisplays displays = this.rsocketRequester
                .route("displays")
                .retrieveMono(RemoteDisplays.class)
                .block();

        JFrame jFrame = new JFrame("Choose display");
        jFrame.setLayout(new FlowLayout());

        DisplaySelectionListener displaySelectionListener = new DisplaySelectionListener() {
            @Override
            public void selected(int selectionNr, int width, int height) {
                jFrame.dispose();
                screenShare(selectionNr, width, height);
            }
        };

        displays.getScreenshotsByDisplay().forEach((nr, image) -> {
            try {
                DisplayDetails displayDetails = new DisplayDetails(nr, image);
                displayDetails.addClickListener(displaySelectionListener);
                jFrame.add(displayDetails);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
//        jFrame.setSize(500,110);
        jFrame.revalidate();
        jFrame.pack();
        jFrame.repaint();

        jFrame.setVisible(true);

//        return displays;
    }

    public void screenShare(final int screenNr, int width, int height) {
        if (!userCheck()) {
            return;
        }

        final ScreenShare screenShare = new ScreenShare("Test server", screenNr,true);
        screenShare.addEventListener(new ScreenShareEventListener() {
            @Override
            public void newRemoteEvent(RemoteEvent event) {
                sendRemoteEvent(event);
            }

            @Override
            public void quitButtonPressed() {
                quitScreenShare();
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

        log.info("\nSending {}", remoteEvent);
        rsocketRequester
                .route("remote-event")
                .data(Objects.requireNonNull(SerializerUtil.toString(remoteEvent)))
                .send()
                .block();
    }
}