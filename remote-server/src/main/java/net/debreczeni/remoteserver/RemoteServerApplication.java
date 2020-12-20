package net.debreczeni.remoteserver;

import net.debreczeni.remoteclient.ui.Main;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.awt.*;

@SpringBootApplication
public class RemoteServerApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(RemoteServerApplication.class);
        var context = builder.headless(false).run(args);

//        EventQueue.invokeLater(() -> {
//
//            var ex = context.getBean(Main.class);
//            ex.setVisible(true);
//        });
    }

}
