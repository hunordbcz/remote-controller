package net.debreczeni.remotedesktop.controller;

import io.rsocket.SocketAcceptor;
import io.rsocket.metadata.WellKnownMimeType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.debreczeni.remotedesktop.model.Message;
import net.debreczeni.remotedesktop.model.User;
import net.debreczeni.remotedesktop.model.socket.RemoteDisplays;
import net.debreczeni.remotedesktop.model.socket.RemoteImage;
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
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PreDestroy;
import javax.naming.AuthenticationException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.rmi.Remote;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@ShellComponent
public class RClientController {

    private static final String CLIENT = "Client";
    private static final String REQUEST = "Request";
    private static final String FIRE_AND_FORGET = "Fire-And-Forget";
    private static final String STREAM = "Stream";
    private static final String CLIENT_ID = UUID.randomUUID().toString();
    private static final MimeType SIMPLE_AUTH = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString());
    private static Disposable disposable;

    private RSocketRequester rsocketRequester;
    private RSocketRequester.Builder rsocketRequesterBuilder;
    private RSocketStrategies rsocketStrategies;

    @Autowired
    public RClientController(RSocketRequester.Builder builder,
                             @Qualifier("rSocketStrategies") RSocketStrategies strategies) {
        this.rsocketRequesterBuilder = builder;
        this.rsocketStrategies = strategies;
    }

    @ShellMethod("Login with your username and password.")
    @SneakyThrows
    public void login(String username, String password) {
        log.info("Connecting using client ID: {} and username: {}", CLIENT_ID, username);
        SocketAcceptor responder = RSocketMessageHandler.responder(rsocketStrategies, new PingMessage());
        UsernamePasswordMetadata user = new UsernamePasswordMetadata(username, password);
        this.rsocketRequester = rsocketRequesterBuilder
                .setupRoute("shell-client")
                .setupData(SerializerUtil.toString(User.getInstance()))
                .setupMetadata(user, SIMPLE_AUTH)
                .rsocketStrategies(builder ->
                        builder.encoder(new SimpleAuthenticationEncoder())
                )
                .rsocketConnector(connector -> connector.acceptor(responder))
                .connectTcp("localhost", 7000)
                .doOnError(error -> JOptionPane.showMessageDialog(null, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE))
                .onErrorStop()
                .block();

        this.rsocketRequester.rsocket()
                .onClose()
                .doOnError(error -> log.warn("Connection CLOSED"))
                .doFinally(consumer -> log.info("Client DISCONNECTED"))
                .subscribe();
    }

    @PreDestroy
//    @ShellMethod("Logout and close your connection")
    public void logout() {
        if (userIsLoggedIn()) {
            this.s();
            this.rsocketRequester.rsocket().dispose();
            log.info("Logged out.");
        }
    }

    private boolean userIsLoggedIn() {
        if (null == this.rsocketRequester || this.rsocketRequester.rsocket().isDisposed()) {
            log.info("No connection. Did you login?");
            return false;
        }
        return true;
    }

    //    @ShellMethod("Send one request. One response will be printed.")
    public void requestResponse() throws InterruptedException {
        if (userIsLoggedIn()) {
            log.info("\nSending one request. Waiting for one response...");
            Message message = this.rsocketRequester
                    .route("request-response")
                    .data(new Message(CLIENT, REQUEST))
                    .retrieveMono(Message.class)
                    .block();
            log.info("\nResponse was: {}", message);
        }
    }

    @ShellMethod("get displays")
    public void displays() {
        userCheck();

        RemoteDisplays displays = this.rsocketRequester
                .route("displays")
                .retrieveMono(RemoteDisplays.class)
                .block();

        JFrame jFrame = new JFrame("Choose display");
        jFrame.setLayout(new FlowLayout());

        displays.getScreenshotsByDisplay().forEach((nr, image) -> {
            try {
                jFrame.add(new DisplayDetails(nr, image));
                jFrame.add(new DisplayDetails(nr, image));
                jFrame.add(new DisplayDetails(nr, image));
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

    @SneakyThrows
    private void userCheck() {
        if (null == this.rsocketRequester || this.rsocketRequester.rsocket().isDisposed()) {
            log.warn("Please log in to the server first");
            throw new AuthenticationException("Not logged in");
        }
    }

    //    @ShellMethod("Send one request. No response will be returned.")
    public void fireAndForget() throws InterruptedException {
        if (userIsLoggedIn()) {
            log.info("\nFire-And-Forget. Sending one request. Expect no response (check server console log)...");
            this.rsocketRequester
                    .route("fire-and-forget")
                    .data(new Message(CLIENT, FIRE_AND_FORGET))
                    .send()
                    .block();
        }
    }

    //    @ShellMethod("Send one request. Many responses (stream) will be printed.")
    public void stream(final int screenNr) {
        if (userIsLoggedIn()) {
            log.info("\n\n**** Request-Stream\n**** Send one request.\n**** Log responses.\n**** Type 's' to stop.");

            JFrame jFrame = new JFrame();
            var quitButton = new JButton("Quit");
            quitButton.addActionListener((ActionEvent event) -> {
                jFrame.dispose();
                s();
            });
            ScreenShare screenShare = new ScreenShare();

            jFrame.addComponentListener(new ComponentListener() {
                @Override
                public void componentResized(ComponentEvent e) {
                    java.awt.Component component = e.getComponent();
                    screenShare.setSize(component.getWidth(), component.getHeight());
                }

                @Override
                public void componentMoved(ComponentEvent e) {

                }

                @Override
                public void componentShown(ComponentEvent e) {

                }

                @Override
                public void componentHidden(ComponentEvent e) {

                }
            });

            jFrame.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {
                    rsocketRequester
                            .route("remote-event")
//                            .data(new RemoteEvent(RemoteEvent.KEYBOARD.PRESS, e.getKeyCode()))
                            .send()
                            .block();
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    rsocketRequester
                            .route("remote-event")
//                            .data(new RemoteEvent(RemoteEvent.KEYBOARD.RELEASE, e.getKeyCode()))
                            .send()
                            .block();
                }
            });
            jFrame.setFocusable(true);
            createLayout(jFrame, quitButton);
            jFrame.setTitle("Quit button");
            jFrame.setLayout(new FlowLayout());
            jFrame.setLocationRelativeTo(null);
            jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            jFrame.add(screenShare);

            EventQueue.invokeLater(() -> {
                jFrame.setVisible(true);
            });

            disposable = this.rsocketRequester
                    .route("image-stream")
                    .data(0)
                    .retrieveFlux(RemoteImage.class)
                    .subscribe(image -> {
                        log.info("Response: {} ms \n(Type 's' to stop.)", System.currentTimeMillis() - image.getCreated());
                        screenShare.updateImage(image);
                    });
        }
    }

    private void createLayout(JFrame jFrame, JComponent... arg) {
        var pane = jFrame.getContentPane();
        var gl = new GroupLayout(pane);
        pane.setLayout(gl);

        Arrays.stream(arg).forEach(obj -> gl.setHorizontalGroup(gl.createSequentialGroup().addComponent(obj)));
        Arrays.stream(arg).forEach(obj -> gl.setVerticalGroup(gl.createSequentialGroup().addComponent(obj)));
    }

    //    @ShellMethod("Stream some settings to the server. Stream of responses will be printed.")
    public void channel() {
        if (userIsLoggedIn()) {
            log.info("\n\n***** Channel (bi-directional streams)\n***** Asking for a stream of messages.\n***** Type 's' to stop.\n\n");

            Mono<Duration> setting1 = Mono.just(Duration.ofSeconds(1));
            Mono<Duration> setting2 = Mono.just(Duration.ofSeconds(3)).delayElement(Duration.ofSeconds(5));
            Mono<Duration> setting3 = Mono.just(Duration.ofSeconds(5)).delayElement(Duration.ofSeconds(15));

            Flux<Duration> settings = Flux.concat(setting1, setting2, setting3)
                    .doOnNext(d -> log.info("\nSending setting for a {}-second interval.\n", d.getSeconds()));

            disposable = this.rsocketRequester
                    .route("channel")
                    .data(settings)
                    .retrieveFlux(Message.class)
                    .subscribe(message -> log.info("Received: {} \n(Type 's' to stop.)", message));
        }
    }

    //    @ShellMethod("Stops Streams or Channels.")
    public void s() {
        if (userIsLoggedIn() && null != disposable) {
            log.info("Stopping the current stream.");
            disposable.dispose();
            log.info("Stream stopped.");
        }
    }
}