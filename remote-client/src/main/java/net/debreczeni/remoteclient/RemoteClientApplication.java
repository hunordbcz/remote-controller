package net.debreczeni.remoteclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.rsocket.RSocketSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {
        ReactiveUserDetailsServiceAutoConfiguration.class,
        SecurityAutoConfiguration.class,
        ReactiveSecurityAutoConfiguration.class,
        RSocketSecurityAutoConfiguration.class
})
public class RemoteClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(RemoteClientApplication.class, args);
    }

}
