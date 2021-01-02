package net.debreczeni.remotedesktop.configs;

import io.rsocket.core.RSocketServer;
import io.rsocket.core.Resume;
import io.rsocket.frame.decoder.PayloadDecoder;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("resumption")
@Component
public class RSocketServerResumptionConfig implements RSocketServerCustomizer {

    @Override
    public void customize(RSocketServer rSocketServer) {
        rSocketServer
                .payloadDecoder(PayloadDecoder.ZERO_COPY)
                .resume(new Resume());
    }

}