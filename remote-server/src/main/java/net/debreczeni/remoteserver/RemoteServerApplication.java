package net.debreczeni.remoteserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class RemoteServerApplication {

    public static void main(String[] args) {
//        SpringApplication.run(RemoteServerApplication.class, args);
        SpringApplicationBuilder builder = new SpringApplicationBuilder(RemoteServerApplication.class);

        builder.headless(false);

        ConfigurableApplicationContext context = builder.run(args);
    }

}
