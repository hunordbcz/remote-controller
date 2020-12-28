package net.debreczeni.remotedesktop.controller;

import io.netty.util.internal.StringUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.debreczeni.remotedesktop.factory.AbstractEventFactory;
import net.debreczeni.remotedesktop.image.Display;
import net.debreczeni.remotedesktop.model.Message;
import net.debreczeni.remotedesktop.model.User;
import net.debreczeni.remotedesktop.model.socket.RemoteDisplays;
import net.debreczeni.remotedesktop.model.socket.RemoteImage;
import net.debreczeni.remotedesktop.model.socket.events.KeyboardEvent;
import net.debreczeni.remotedesktop.model.socket.events.RemoteEvent;
import net.debreczeni.remotedesktop.util.SerializerUtil;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
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
import java.rmi.Remote;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
public class RServerController {

    static final String SERVER = "Server";
    static final String RESPONSE = "Response";
    static final String STREAM = "Stream";
    static final String CHANNEL = "Channel";

    private final List<RSocketRequester> CLIENTS = new ArrayList<>();

    @PreDestroy
    void shutdown() {
        log.info("Detaching all remaining clients...");
        CLIENTS.stream().forEach(requester -> requester.rsocket().dispose());
        log.info("Shutting down.");
    }

    @SneakyThrows
    @ConnectMapping("shell-client")
    void connectShellClientAndAskForTelemetry(RSocketRequester requester,
                                              @Payload String client) {

        User user = SerializerUtil.fromString(client);
//        log.error(user.getName(), user.getViewToken(), user.getControlToken());

        Objects.requireNonNull(requester.rsocket())
                .onClose()
                .doFirst(() -> {
                    log.info("Client: {} CONNECTED.", user.getName());
                    CLIENTS.add(requester);
                })
                .doOnError(error -> {
                    log.warn("Channel to client {} CLOSED", client);
                })
//                .doFinally(consumer -> {
//                    CLIENTS.remove(requester);
//                    log.info("Client {} DISCONNECTED", client);
//                })
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                    }

                    @Override
                    public void onNext(Void unused) {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        log.error("onError");
                    }

                    @Override
                    public void onComplete() {
                        CLIENTS.remove(requester);
                        log.info("Client {} DISCONNECTED", client);
                    }
                });


        User user1 = SerializerUtil.fromString(client);

        // Callback to client, confirming connection
        requester.route("client-status")
                .data("OPEN")
                .retrieveFlux(String.class)
                .doOnNext(s -> log.info("Client: {} Free Memory: {}.", user1.getName(), s))
                .subscribe();
    }

    /**
     * This @MessageMapping is intended to be used "request --> response" style.
     * For each Message received, a new Message is returned with ORIGIN=Server and INTERACTION=Request-Response.
     *
     * @param request
     * @return Message
     */
    @PreAuthorize("hasRole('USER')")
    @MessageMapping("request-response")
    Mono<Message> requestResponse(final Message request, @AuthenticationPrincipal UserDetails user) {
        log.info("Received request-response request: {}", request);
        log.info("Request-response initiated by '{}' in the role '{}'", user.getUsername(), user.getAuthorities());
        // create a single Message and return it
        return Mono.just(new Message(SERVER, RESPONSE));
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


    @PreAuthorize("hasRole('USER')")
    @MessageMapping("fire-and-forget")
    public Mono<Void> fireAndForget(final Message request, @AuthenticationPrincipal UserDetails user) {
        log.info("Received fire-and-forget request: {}", request);
        log.info("Fire-And-Forget initiated by '{}' in the role '{}'", user.getUsername(), user.getAuthorities());
        return Mono.empty();
    }

    @PreAuthorize("hasRole('CONTROL')")
    @MessageMapping("remote-event")
    public Mono<Void> remoteEvent(final String remoteEvent, @AuthenticationPrincipal UserDetails user) throws IOException, ClassNotFoundException {
        RemoteEvent event = SerializerUtil.fromString(remoteEvent);
        AbstractEventFactory factory = AbstractEventFactory.getFactory(event);

        factory.process();
        log.info("Received remoteEvent request: {}", (RemoteEvent)SerializerUtil.fromString(remoteEvent));
        log.info("remoteEvent initiated by '{}' in the role '{}'", user.getUsername(), user.getAuthorities());
        return Mono.empty();
    }

    /**
     * This @MessageMapping is intended to be used "subscribe --> stream" style.
     * When a new request command is received, a new stream of events is started and returned to the client.
     *
     * @param request
     * @return
     */
    @PreAuthorize("hasRole('VIEW')")
    @MessageMapping("image-stream")
    Flux<RemoteImage> imageStream(final int screenNr) {
        final Display display = Display.getInstance(screenNr);
        return Flux
                .interval(Duration.ofMillis(100))
                .map(index -> new RemoteImage(display.takeScreenshot()));
    }


    /**
     * This @MessageMapping is intended to be used "stream <--> stream" style.
     * The incoming stream contains the interval settings (in seconds) for the outgoing stream of messages.
     *
     * @param settings
     * @return
     */
    @PreAuthorize("hasRole('USER')")
    @MessageMapping("channel")
    Flux<Message> channel(final Flux<Duration> settings, @AuthenticationPrincipal UserDetails user) {
        log.info("Received channel request...");
        log.info("Channel initiated by '{}' in the role '{}'", user.getUsername(), user.getAuthorities());

        return settings
                .doOnNext(setting -> log.info("Channel frequency setting is {} second(s).", setting.getSeconds()))
                .doOnCancel(() -> log.warn("The client cancelled the channel."))
                .switchMap(setting -> Flux.interval(setting)
                        .map(index -> new Message(SERVER, CHANNEL, index)));
    }
}