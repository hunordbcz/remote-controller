package net.debreczeni.remotedesktop.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.debreczeni.remotedesktop.factory.AbstractEventFactory;
import net.debreczeni.remotedesktop.image.Display;
import net.debreczeni.remotedesktop.model.User;
import net.debreczeni.remotedesktop.model.socket.RemoteDisplays;
import net.debreczeni.remotedesktop.model.socket.RemoteImage;
import net.debreczeni.remotedesktop.model.socket.events.RemoteEvent;
import net.debreczeni.remotedesktop.util.SerializerUtil;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
public class RServerController {

    private final List<RSocketRequester> CLIENTS = new ArrayList<>();

    @PreDestroy
    void shutdown() {
        log.info("Removing connected clients...");
        CLIENTS.forEach(requester -> requester.rsocket().dispose());
        log.info("Shutting down.");
    }

    @SneakyThrows
    @ConnectMapping("login-server")
    void connectAndAskForTelemetry(RSocketRequester requester,
                                   @Payload String client) {

        User user = SerializerUtil.fromString(client);

        Objects.requireNonNull(requester.rsocket())
                .onClose()
                .doFirst(() -> {
                    log.info("Client: {} CONNECTED.", user.getName());
                    CLIENTS.add(requester);
                })
                .doOnError(error -> log.warn("Channel to client {} CLOSED", user.getName()))
                .doFinally(consumer -> log.info("Client {} DISCONNECTED", user.getName()))
                .subscribe();
    }

    @PreAuthorize("hasRole('VIEW')")
    @MessageMapping("displays")
    Mono<RemoteDisplays> displays(@AuthenticationPrincipal UserDetails user) {
        RemoteDisplays displays = new RemoteDisplays();

        for (int i = 0; i < Display.getDisplayNumbers(); i++) {
            displays.setImage(i, new RemoteImage(Display.getInstance(i).takeScreenshot()));
        }

        return Mono.just(displays);
    }

    @PreAuthorize("hasRole('CONTROL')")
    @MessageMapping("remote-event")
    public Mono<Void> remoteEvent(final String remoteEvent, @AuthenticationPrincipal UserDetails user) throws IOException, ClassNotFoundException {
        RemoteEvent event = SerializerUtil.fromString(remoteEvent);
        AbstractEventFactory factory = AbstractEventFactory.getFactory(event);

        factory.process();
        log.info("Received remoteEvent request: {}", (RemoteEvent) SerializerUtil.fromString(remoteEvent));
        log.info("remoteEvent initiated by '{}' in the role '{}'", user.getUsername(), user.getAuthorities());
        return Mono.empty();
    }

    @PreAuthorize("hasRole('VIEW')")
    @MessageMapping("image-stream")
    Flux<RemoteImage> imageStream(final int screenNr) {
        final Display display = Display.getInstance(screenNr);
        return Flux
                .interval(Duration.ofMillis(100))
                .map(index -> new RemoteImage(display.takeScreenshot()));
    }
}