package net.debreczeni.remotedesktop;

import net.debreczeni.remotedesktop.ui.Main;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.naming.Context;
import java.awt.*;

@SpringBootApplication
public class RemoteServerApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(RemoteServerApplication.class);
        ConfigurableApplicationContext context = builder.headless(false).run(args);

        EventQueue.invokeLater(() -> {
            Main main = context.getBean(Main.class);
            main.setVisible(true);
        });
    }

}
