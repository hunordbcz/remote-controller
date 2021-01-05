package net.debreczenichis.remotedesktop;

import net.debreczenichis.remotedesktop.ui.Main;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.*;

@SpringBootApplication
public class RemoteApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(RemoteApplication.class);
        ConfigurableApplicationContext context = builder.headless(false).run(args);

        EventQueue.invokeLater(() -> {
            Main main = context.getBean(Main.class);
            main.setVisible(true);
        });
    }

}
