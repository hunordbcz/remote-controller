package net.debreczeni.remoteclient;

import net.debreczeni.remoteclient.ui.Main;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.rsocket.RSocketSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.*;

@SpringBootApplication(exclude = {
        ReactiveUserDetailsServiceAutoConfiguration.class,
        SecurityAutoConfiguration.class,
        ReactiveSecurityAutoConfiguration.class,
        RSocketSecurityAutoConfiguration.class
})
public class RemoteClientApplication {

    public static void main(String[] args) {
//        SpringApplication.run(RemoteClientApplication.class, args);
        SpringApplicationBuilder builder = new SpringApplicationBuilder(RemoteClientApplication.class);
        ConfigurableApplicationContext context = builder.headless(false).run(args);

        EventQueue.invokeLater(() -> {

            var ex = context.getBean(Main.class);
            ex.setVisible(true);
        });
    }

}
