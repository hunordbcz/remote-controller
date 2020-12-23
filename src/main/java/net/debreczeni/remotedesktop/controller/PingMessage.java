package net.debreczeni.remotedesktop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
public class PingMessage {

    @MessageMapping("client-status")
    public Flux<String> statusUpdate() {
        return Flux.interval(Duration.ofSeconds(5)).map(index -> String.valueOf(Runtime.getRuntime().freeMemory()));
    }
}
